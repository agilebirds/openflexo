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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.dm.DMClassLibrary;
import org.openflexo.toolbox.FileUtils;

import com.thoughtworks.qdox.model.AbstractJavaEntity;
import com.thoughtworks.qdox.model.ClassLibrary;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaSource;

public class FJPJavaSource extends FJPJavaElement {
	private static final Logger logger = Logger.getLogger(FJPJavaSource.class.getPackage().getName());

	private final JavaParser _javaParser;
	private final JavaSource _qdSource;
	private final String _sourceCode;
	private final String _fileName;
	private FJPImportDeclarations _importDeclarations;
	private FJPPackageDeclaration _packageDeclaration;

	public FJPJavaSource(File sourceFile, ClassLibrary classLibrary) throws FileNotFoundException, IOException {
		this(sourceFile, new JavaParser(classLibrary));
	}

	public FJPJavaSource(String sourceName, String sourceString, ClassLibrary classLibrary) {
		this(sourceName, sourceString, new JavaParser(classLibrary));
	}

	public FJPJavaSource(File sourceFile, JavaParser javaParser) throws FileNotFoundException, IOException {
		super(null);
		_javaParser = javaParser;
		_qdSource = javaParser.parseFileForSource(sourceFile, this);
		javaSource = this;
		_sourceCode = FileUtils.fileContents(sourceFile);
		_fileName = sourceFile.getName();
		init();
	}

	public FJPJavaSource(String sourceName, String sourceString, JavaParser javaParser) {
		super(null);
		_javaParser = javaParser;
		_qdSource = javaParser.parseStringForSource(sourceString, this, sourceName);
		javaSource = this;
		_sourceCode = sourceString;
		_fileName = sourceName;
		init();
	}

	@Override
	public JavaParser getJavaParser() {
		return _javaParser;
	}

	// private Hashtable<AbstractJavaEntity,FJPJavaElement> _elements;
	private Hashtable<String, FJPJavaElement> _classesElements;
	private Hashtable<String, FJPJavaElement> _methodsElements;
	private Hashtable<String, FJPJavaElement> _fieldsElements;

	private void init() {
		// _elements = new Hashtable<AbstractJavaEntity,FJPJavaElement>();
		_classesElements = new Hashtable<String, FJPJavaElement>();
		_methodsElements = new Hashtable<String, FJPJavaElement>();
		_fieldsElements = new Hashtable<String, FJPJavaElement>();
		for (JavaClass aClass : _qdSource.getClasses()) {
			registerClass(aClass);
		}
		_packageDeclaration = new FJPPackageDeclaration();
		_importDeclarations = new FJPImportDeclarations();
	}

	private void registerClass(JavaClass aClass) {
		// Date date1 = new Date();
		FJPJavaClass newJavaClass = new FJPJavaClass(aClass, this);
		// Date date01 = new Date();
		registerElements(aClass, newJavaClass);
		// Date date02 = new Date();
		// logger.info("Time1 : "+(date02.getTime()-date01.getTime())+" ms");
		// t1 = 0;
		// t2 = 0;
		// t3 = 0;
		// t4 = 0;
		for (JavaMethod aMethod : aClass.getMethods()) {
			registerMethod(aMethod);
		}
		// Date date03 = new Date();
		// logger.info("Time2 : "+(date03.getTime()-date02.getTime())+" ms t1="+t1+" t2="+t2+" t3="+t3+" t4="+t4);
		for (JavaField aField : aClass.getFields()) {
			registerField(aField);
		}
		// Date date04 = new Date();
		// logger.info("Time3 : "+(date04.getTime()-date03.getTime())+" ms");
		for (JavaClass aNestedClass : aClass.getNestedClasses()) {
			registerClass(aNestedClass);
		}
		// Date date2 = new Date();
		// logger.info("Time4 : "+(date2.getTime()-date04.getTime())+" ms");
		// logger.info("Time for registerClass("+aClass.getName()+") : "+(date2.getTime()-date1.getTime())+" ms");
	}

	/*private long t1 = 0;
	private long t2 = 0;
	private long t3 = 0;
	private long t4 = 0;*/

	private void registerMethod(JavaMethod aMethod) {
		// Date date1 = new Date();
		FJPJavaMethod newJavaMethod = new FJPJavaMethod(aMethod, this);
		// Date date2 = new Date();
		registerElements(aMethod, newJavaMethod);
		// Date date3 = new Date();
		// aMethod.hashCode();
		// Date date4 = new Date();
		// aMethod.getCallSignature();
		// Date date5 = new Date();
		// t1 += (date2.getTime()-date1.getTime());
		// t2 += (date3.getTime()-date2.getTime());
		// t3 += (date4.getTime()-date3.getTime());
		// t4 += (date5.getTime()-date4.getTime());
	}

	private void registerField(JavaField aField) {
		FJPJavaField newJavaField = new FJPJavaField(aField, this);
		registerElements(aField, newJavaField);
	}

	/**
	 * Implementation of hashCode() method for JavaMethod in QDox library is unexpectedally very very slow. Consequence of that was that
	 * managing hashtable structures with JavaMethod keys was a very bad idea and resulted to very poor performances. I replaced this by a
	 * String key, obtained by following computing, and used in hashtable structures
	 * 
	 * @param entity
	 * @return
	 */
	private String keyFor(AbstractJavaEntity entity) {
		if (entity instanceof JavaClass) {
			return ((JavaClass) entity).getPackage() + "." + ((JavaClass) entity).getName();
		} else if (entity instanceof JavaMethod) {
			return keyFor(((JavaMethod) entity).getParentClass()) + "." + ((JavaMethod) entity).getCallSignature();
		}
		if (entity instanceof JavaField) {
			return keyFor((JavaClass) ((JavaField) entity).getParent()) + "." + ((JavaField) entity).getName();
		}
		return null;
	}

	private void registerElements(AbstractJavaEntity entity, FJPJavaElement element) {
		if (entity instanceof JavaClass) {
			_classesElements.put(keyFor(entity), element);
		} else if (entity instanceof JavaMethod) {
			_methodsElements.put(keyFor(entity), element);
		} else if (entity instanceof JavaField) {
			_fieldsElements.put(keyFor(entity), element);
		} else {
			logger.warning("Inconsistant data in registerElements() " + entity);
		}
		// _elements.put(entity, element);
	}

	@Override
	public FJPJavaElement getFJPJavaElement(AbstractJavaEntity entity) {
		if (entity == null) {
			return null;
		}
		if (entity instanceof JavaClass) {
			return _classesElements.get(keyFor(entity));
		} else if (entity instanceof JavaMethod) {
			return _methodsElements.get(keyFor(entity));
		} else if (entity instanceof JavaField) {
			return _fieldsElements.get(keyFor(entity));
		} else {
			logger.warning("Inconsistant data in registerElements() " + entity);
			return null;
		}
		// return _elements.get(abstractJavaEntity);
	}

	@Override
	public FJPJavaClass getClass(JavaClass aClass) {
		return (FJPJavaClass) getFJPJavaElement(aClass);
	}

	@Override
	public FJPJavaMethod getMethod(JavaMethod aMethod) {
		return (FJPJavaMethod) getFJPJavaElement(aMethod);
	}

	@Override
	public FJPJavaField getField(JavaField aField) {
		return (FJPJavaField) getFJPJavaElement(aField);
	}

	public String getSourceCode() {
		return _sourceCode;
	}

	@Override
	protected FJPJavaClass[] retrieveClasses(JavaClass[] someClasses) {
		if (someClasses == null) {
			return new FJPJavaClass[0];
		}
		Vector<FJPJavaClass> vector = new Vector<FJPJavaClass>();
		for (JavaClass c : someClasses) {
			vector.add((FJPJavaClass) getFJPJavaElement(c));
		}
		return vector.toArray(new FJPJavaClass[vector.size()]);

	}

	@Override
	protected FJPJavaMethod[] retrieveMethods(JavaMethod[] someMethods) {
		if (someMethods == null) {
			return new FJPJavaMethod[0];
		}
		Vector<FJPJavaMethod> vector = new Vector<FJPJavaMethod>();
		for (JavaMethod m : someMethods) {
			vector.add((FJPJavaMethod) getFJPJavaElement(m));
		}
		return vector.toArray(new FJPJavaMethod[vector.size()]);
	}

	@Override
	protected FJPJavaField[] retrieveFields(JavaField[] someFields) {
		if (someFields == null) {
			return new FJPJavaField[0];
		}
		Vector<FJPJavaField> vector = new Vector<FJPJavaField>();
		for (JavaField f : someFields) {
			vector.add((FJPJavaField) getFJPJavaElement(f));
		}
		return vector.toArray(new FJPJavaField[vector.size()]);
	}

	private FJPJavaClass[] _classes;

	public FJPJavaClass[] getClasses() {
		if (_classes == null) {
			_classes = retrieveClasses(_qdSource.getClasses());
		}
		return _classes;
	}

	public FJPJavaClass getRootClass() {
		return getClasses()[0];
	}

	@Override
	public DMClassLibrary getClassLibrary() {
		return (DMClassLibrary) _qdSource.getClassLibrary();
	}

	@Override
	public String getClassNamePrefix() {
		return _qdSource.getClassNamePrefix();
	}

	public String[] getImports() {
		return _qdSource.getImports();
	}

	public String getPackage() {
		return _qdSource.getPackage();
	}

	public String getFileName() {
		return _fileName;
	}

	@Override
	public String getInspectorName() {
		return Inspectors.CG.JAVA_SOURCE_INSPECTOR;
	}

	public class FJPImportDeclarations extends FJPJavaElement {
		private final Vector<FJPImportDeclaration> _importDeclarations;

		public FJPImportDeclarations() {
			super(FJPJavaSource.this);
			_importDeclarations = new Vector<FJPImportDeclaration>();
			for (String i : getImports()) {
				_importDeclarations.add(new FJPImportDeclaration(i));
			}
		}

		@Override
		public String getInspectorName() {
			return Inspectors.CG.JAVA_IMPORTS_INSPECTOR;
		}

		public class FJPImportDeclaration extends FJPJavaElement {
			private final String _importDeclaration;

			private FJPImportDeclaration(String importDeclaration) {
				super(FJPJavaSource.this);
				_importDeclaration = importDeclaration;
			}

			public String getImportDeclaration() {
				return _importDeclaration;
			}

			/*public Icon getIcon()
			{
				return CGIconLibrary.FJP_IMPORT_ICON;
			}*/

			@Override
			public String getInspectorName() {
				return Inspectors.CG.JAVA_IMPORT_INSPECTOR;
			}

		}

		public Vector<FJPImportDeclaration> getImportDeclarations() {
			return _importDeclarations;
		}
	}

	public FJPImportDeclarations getImportDeclarations() {
		return _importDeclarations;
	}

	public class FJPPackageDeclaration extends FJPJavaElement {
		private FJPPackageDeclaration() {
			super(FJPJavaSource.this);
		}

		public String getPackage() {
			return FJPJavaSource.this.getPackage();
		}

		@Override
		public String getInspectorName() {
			return Inspectors.CG.JAVA_PACKAGE_INSPECTOR;
		}

	}

	public FJPPackageDeclaration getPackageDeclaration() {
		return _packageDeclaration;
	}

	public JavaSource getQDSource() {
		return _qdSource;
	}

}
