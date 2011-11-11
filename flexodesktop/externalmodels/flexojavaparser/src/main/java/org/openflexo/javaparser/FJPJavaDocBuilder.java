/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.javaparser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.Searcher;
import com.thoughtworks.qdox.directorywalker.DirectoryScanner;
import com.thoughtworks.qdox.directorywalker.FileVisitor;
import com.thoughtworks.qdox.directorywalker.SuffixFilter;
import com.thoughtworks.qdox.model.ClassLibrary;
import com.thoughtworks.qdox.model.DefaultDocletTagFactory;
import com.thoughtworks.qdox.model.DocletTagFactory;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaClassCache;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.parser.Lexer;
import com.thoughtworks.qdox.parser.ParseException;
import com.thoughtworks.qdox.parser.impl.JFlexLexer;
import com.thoughtworks.qdox.parser.impl.Parser;
import com.thoughtworks.qdox.parser.structs.ClassDef;
import com.thoughtworks.qdox.parser.structs.FieldDef;
import com.thoughtworks.qdox.parser.structs.MethodDef;

/**
 * Code duplicated from JavaDocBuilder with following modifications: 1. Allow to use a custom class library 2. Use FJPModelBuilder instead
 * of ModelBuilder
 * 
 * @author sylvain
 * 
 */
public class FJPJavaDocBuilder extends JavaDocBuilder implements Serializable, JavaClassCache {

	private Map classes = new HashMap();
	private ClassLibrary classLibrary;
	private List<JavaSource> sources = new ArrayList<JavaSource>();
	private DocletTagFactory docletTagFactory;
	private String encoding = System.getProperty("file.encoding");
	private boolean debugLexer;
	private boolean debugParser;

	public FJPJavaDocBuilder(ClassLibrary classLibrary) {
		this(new DefaultDocletTagFactory(), classLibrary);
	}

	public FJPJavaDocBuilder(DocletTagFactory docletTagFactory, ClassLibrary classLibrary) {
		super(docletTagFactory);
		this.docletTagFactory = docletTagFactory;
		this.classLibrary = classLibrary;
	}

	private void addClasses(JavaSource source) {
		Set resultSet = new HashSet();
		addClassesRecursive(source, resultSet);
		JavaClass[] javaClasses = (JavaClass[]) resultSet.toArray(new JavaClass[resultSet.size()]);
		for (int classIndex = 0; classIndex < javaClasses.length; classIndex++) {
			JavaClass cls = javaClasses[classIndex];
			addClass(cls);
		}
	}

	private void addClass(JavaClass cls) {
		classes.put(cls.getFullyQualifiedName(), cls);
		cls.setJavaClassCache(this);
	}

	@Override
	public JavaClass getClassByName(String name) {
		if (name == null) {
			return null;
		}
		JavaClass result = (JavaClass) classes.get(name);
		if (result == null) {
			result = classLibrary.getClassByName(name);
		}
		if (result == null) {
			// Try to make a binary class out of it
			result = createBinaryClass(name);
			if (result != null) {
				addClass(result);
			} else {
				result = createUnknownClass(name);
			}
		}
		return result;
	}

	private JavaClass createUnknownClass(String name) {
		FJPModelBuilder unknownBuilder = new FJPModelBuilder(classLibrary, docletTagFactory);
		ClassDef classDef = new ClassDef();
		classDef.name = name;
		unknownBuilder.beginClass(classDef);
		unknownBuilder.endClass();
		JavaSource unknownSource = unknownBuilder.getSource();
		JavaClass result = unknownSource.getClasses()[0];
		return result;
	}

	private JavaClass createBinaryClass(String name) {
		// First see if the class exists at all.
		Class clazz = classLibrary.getClass(name);
		if (clazz == null) {
			return null;
		} else {
			// Create a new builder and mimic the behaviour of the parser.
			// We're getting all the information we need via reflection instead.
			FJPModelBuilder binaryBuilder = new FJPModelBuilder(classLibrary, docletTagFactory);

			// Set the package name and class name
			String packageName = getPackageName(name);
			binaryBuilder.addPackage(packageName);

			ClassDef classDef = new ClassDef();
			classDef.name = getClassName(name);

			// Set the extended class and interfaces.
			Class[] interfaces = clazz.getInterfaces();
			if (clazz.isInterface()) {
				// It's an interface
				classDef.type = ClassDef.INTERFACE;
				for (int i = 0; i < interfaces.length; i++) {
					Class anInterface = interfaces[i];
					classDef.extendz.add(anInterface.getName());
				}
			} else {
				// It's a class
				for (int i = 0; i < interfaces.length; i++) {
					Class anInterface = interfaces[i];
					classDef.implementz.add(anInterface.getName());
				}
				Class superclass = clazz.getSuperclass();
				if (superclass != null) {
					classDef.extendz.add(superclass.getName());
				}
			}

			addModifiers(classDef.modifiers, clazz.getModifiers());

			binaryBuilder.beginClass(classDef);

			// add the constructors
			Constructor[] constructors = clazz.getConstructors();
			for (int i = 0; i < constructors.length; i++) {
				addMethodOrConstructor(constructors[i], binaryBuilder);
			}

			// add the methods
			Method[] methods = clazz.getMethods();
			for (int i = 0; i < methods.length; i++) {
				// Ignore methods defined in superclasses
				if (methods[i].getDeclaringClass() == clazz) {
					addMethodOrConstructor(methods[i], binaryBuilder);
				}
			}

			Field[] fields = clazz.getFields();
			for (int i = 0; i < fields.length; i++) {
				if (fields[i].getDeclaringClass() == clazz) {
					addField(fields[i], binaryBuilder);
				}
			}

			binaryBuilder.endClass();
			JavaSource binarySource = binaryBuilder.getSource();
			// There is always only one class in a "binary" source.
			JavaClass result = binarySource.getClasses()[0];
			return result;
		}
	}

	private void addModifiers(Set set, int modifier) {
		String modifierString = Modifier.toString(modifier);
		for (StringTokenizer stringTokenizer = new StringTokenizer(modifierString); stringTokenizer.hasMoreTokens();) {
			set.add(stringTokenizer.nextToken());
		}
	}

	private void addField(Field field, FJPModelBuilder binaryBuilder) {
		FieldDef fieldDef = new FieldDef();
		Class fieldType = field.getType();
		fieldDef.name = field.getName();
		fieldDef.type = getTypeName(fieldType);
		fieldDef.dimensions = getDimension(fieldType);
		binaryBuilder.addField(fieldDef);
	}

	@SuppressWarnings("unchecked")
	// because call library in java 1.4 code style
	private void addMethodOrConstructor(Member member, FJPModelBuilder binaryBuilder) {
		MethodDef methodDef = new MethodDef();
		// The name of constructors are qualified. Need to strip it.
		// This will work for regular methods too, since -1 + 1 = 0
		int lastDot = member.getName().lastIndexOf('.');
		methodDef.name = member.getName().substring(lastDot + 1);

		addModifiers(methodDef.modifiers, member.getModifiers());
		Class[] exceptions;
		Class[] parameterTypes;
		if (member instanceof Method) {
			methodDef.constructor = false;

			// For some stupid reason, these methods are not defined in Member,
			// but in both Method and Construcotr.
			exceptions = ((Method) member).getExceptionTypes();
			parameterTypes = ((Method) member).getParameterTypes();

			Class returnType = ((Method) member).getReturnType();
			methodDef.returns = getTypeName(returnType);
			methodDef.dimensions = getDimension(returnType);

		} else {
			methodDef.constructor = true;

			exceptions = ((Constructor) member).getExceptionTypes();
			parameterTypes = ((Constructor) member).getParameterTypes();
		}
		for (int j = 0; j < exceptions.length; j++) {
			Class exception = exceptions[j];
			methodDef.exceptions.add(exception.getName());
		}
		for (int j = 0; j < parameterTypes.length; j++) {
			FieldDef param = new FieldDef();
			Class parameterType = parameterTypes[j];
			param.name = "p" + j;
			param.type = getTypeName(parameterType);
			param.dimensions = getDimension(parameterType);
			methodDef.params.add(param);
		}
		binaryBuilder.addMethod(methodDef);
	}

	private static final int getDimension(Class c) {
		return c.getName().lastIndexOf('[') + 1;
	}

	private static String getTypeName(Class c) {
		return c.getComponentType() != null ? c.getComponentType().getName() : c.getName();
	}

	private String getPackageName(String fullClassName) {
		int lastDot = fullClassName.lastIndexOf('.');
		return lastDot == -1 ? "" : fullClassName.substring(0, lastDot);
	}

	private String getClassName(String fullClassName) {
		int lastDot = fullClassName.lastIndexOf('.');
		return lastDot == -1 ? fullClassName : fullClassName.substring(lastDot + 1);
	}

	@Override
	public JavaSource addSource(Reader reader, String sourceInfo) {
		FJPModelBuilder builder = new FJPModelBuilder(classLibrary, docletTagFactory);
		Lexer lexer = new JFlexLexer(reader);
		Parser parser = new Parser(lexer, builder);
		parser.setDebugLexer(debugLexer);
		parser.setDebugParser(debugParser);
		try {
			parser.parse();
		} catch (ParseException e) {
			e.setSourceInfo(sourceInfo);
			throw e;
		}
		JavaSource source = builder.getSource();
		sources.add(source);
		addClasses(source);
		return source;
	}

	@Override
	public JavaSource addSource(URL url) throws IOException, FileNotFoundException {
		JavaSource source = addSource(new InputStreamReader(url.openStream(), encoding), url.toExternalForm());
		source.setURL(url);
		return source;
	}

	@Override
	public JavaSource[] getSources() {
		return sources.toArray(new JavaSource[sources.size()]);
	}

	private void addClassesRecursive(JavaSource javaSource, Set resultSet) {
		JavaClass[] classes = javaSource.getClasses();
		for (int j = 0; j < classes.length; j++) {
			JavaClass javaClass = classes[j];
			addClassesRecursive(javaClass, resultSet);
		}
	}

	private void addClassesRecursive(JavaClass javaClass, Set set) {
		// Add the class...
		set.add(javaClass);

		// And recursively all of its inner classes
		JavaClass[] innerClasses = javaClass.getNestedClasses();
		for (int i = 0; i < innerClasses.length; i++) {
			JavaClass innerClass = innerClasses[i];
			addClassesRecursive(innerClass, set);
		}
	}

	@Override
	public void addSourceTree(File file) {
		DirectoryScanner scanner = new DirectoryScanner(file);
		scanner.addFilter(new SuffixFilter(".java"));
		scanner.scan(new FileVisitor() {
			@Override
			public void visitFile(File currentFile) {
				try {
					addSource(currentFile);
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException("Unsupported encoding : " + encoding);
				} catch (IOException e) {
					throw new RuntimeException("Cannot read file : " + currentFile.getName());
				}
			}
		});
	}

	@Override
	public List<JavaClass> search(Searcher searcher) {
		List<JavaClass> results = new LinkedList<JavaClass>();
		for (Iterator iterator = classLibrary.all().iterator(); iterator.hasNext();) {
			String clsName = (String) iterator.next();
			JavaClass cls = getClassByName(clsName);
			if (searcher.eval(cls)) {
				results.add(cls);
			}
		}
		return results;
	}

	@Override
	public ClassLibrary getClassLibrary() {
		return classLibrary;
	}

	/**
	 * Note that after loading JavaDocBuilder classloaders need to be re-added.
	 */
	public static FJPJavaDocBuilder load(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream in = new ObjectInputStream(fis);
		FJPJavaDocBuilder builder = null;
		try {
			builder = (FJPJavaDocBuilder) in.readObject();
		} catch (ClassNotFoundException e) {
			throw new Error("Couldn't load class : " + e.getMessage());
		} finally {
			in.close();
			fis.close();
		}
		return builder;
	}

	@Override
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * Forces QDox to dump tokens returned from lexer to System.err.
	 */
	@Override
	public void setDebugLexer(boolean debugLexer) {
		this.debugLexer = debugLexer;
	}

	/**
	 * Forces QDox to dump parser states to System.out.
	 */
	@Override
	public void setDebugParser(boolean debugParser) {
		this.debugParser = debugParser;
	}
}