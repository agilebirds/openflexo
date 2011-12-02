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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.openflexo.foundation.dm.DMClassLibrary;

import com.thoughtworks.qdox.model.AbstractJavaEntity;
import com.thoughtworks.qdox.model.ClassLibrary;
import com.thoughtworks.qdox.model.DefaultDocletTagFactory;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.DocletTagFactory;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaClassParent;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.ModelBuilder;
import com.thoughtworks.qdox.model.Type;
import com.thoughtworks.qdox.parser.structs.ClassDef;
import com.thoughtworks.qdox.parser.structs.FieldDef;
import com.thoughtworks.qdox.parser.structs.MethodDef;
import com.thoughtworks.qdox.parser.structs.TagDef;

/**
 * Code duplicated from ModelBuilder Redefined createType() method (use of FJPType instead of Type)
 * 
 * @author sylvain
 * 
 */
public class FJPModelBuilder extends ModelBuilder {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(FJPModelBuilder.class.getPackage().getName());

	private final ClassLibrary classLibrary;
	private final JavaSource source;
	private JavaClassParent currentParent;
	private JavaClass currentClass;
	private String lastComment;
	private List<TagDef> lastTagSet;
	private DocletTagFactory docletTagFactory;

	public FJPModelBuilder() {
		this(new ClassLibrary(null), new DefaultDocletTagFactory());
	}

	public FJPModelBuilder(ClassLibrary classLibrary, DocletTagFactory docletTagFactory) {
		super(classLibrary, docletTagFactory);
		this.classLibrary = classLibrary;
		this.docletTagFactory = docletTagFactory;
		source = new JavaSource();
		source.setClassLibrary(classLibrary);
		currentParent = source;
	}

	@Override
	public void addPackage(String packageName) {
		source.setPackage(packageName);
	}

	@Override
	public void addImport(String importName) {
		source.addImport(importName);
	}

	@Override
	public void addJavaDoc(String text) {
		lastComment = text;
		lastTagSet = new LinkedList<TagDef>();
	}

	@Override
	public void addJavaDocTag(TagDef tagDef) {
		lastTagSet.add(tagDef);
	}

	@Override
	public void beginClass(ClassDef def) {
		currentClass = new JavaClass();
		currentClass.setParent(currentParent);
		currentClass.setLineNumber(def.lineNumber);

		// basic details
		currentClass.setName(def.name);
		currentClass.setInterface(ClassDef.INTERFACE.equals(def.type));
		currentClass.setEnum(ClassDef.ENUM.equals(def.type));

		// superclass
		if (currentClass.isInterface()) {
			currentClass.setSuperClass(null);
		} else if (!currentClass.isEnum()) {
			currentClass.setSuperClass(def.extendz.size() > 0 ? createType((String) def.extendz.toArray()[0], 0) : null);
		}

		// implements
		{
			Set implementSet = currentClass.isInterface() ? def.extendz : def.implementz;
			Iterator implementIt = implementSet.iterator();
			Type[] implementz = new Type[implementSet.size()];
			for (int i = 0; i < implementz.length && implementIt.hasNext(); i++) {
				implementz[i] = createType((String) implementIt.next(), 0);
			}
			currentClass.setImplementz(implementz);
		}

		// modifiers
		{
			String[] modifiers = new String[def.modifiers.size()];
			def.modifiers.toArray(modifiers);
			currentClass.setModifiers(modifiers);
		}

		// javadoc
		addJavaDoc(currentClass);

		// ignore annotation types (for now)
		if (ClassDef.ANNOTATION_TYPE.equals(def.type)) {
			return;
		}

		currentParent.addClass(currentClass);
		currentParent = currentClass;
		classLibrary.add(currentClass.getFullyQualifiedName());

		if (classLibrary instanceof DMClassLibrary) {
			((DMClassLibrary) classLibrary).registerClassForName(currentClass);
		}
	}

	@Override
	public void endClass() {
		currentParent = currentClass.getParent();
		if (currentParent instanceof JavaClass) {
			currentClass = (JavaClass) currentParent;
		} else {
			currentClass = null;
		}
	}

	private Type createType(String typeName, int dimensions) {
		// logger.info("createType "+typeName+" dimensions: "+dimensions);
		if (typeName == null || typeName.equals("")) {
			return null;
		}
		return FJPType.createUnresolved(typeName, dimensions, currentClass);
	}

	private void addJavaDoc(AbstractJavaEntity entity) {
		if (lastComment == null) {
			return;
		}

		entity.setComment(lastComment);

		Iterator tagDefIterator = lastTagSet.iterator();
		List<DocletTag> tagList = new ArrayList<DocletTag>();
		while (tagDefIterator.hasNext()) {
			TagDef tagDef = (TagDef) tagDefIterator.next();
			tagList.add(docletTagFactory.createDocletTag(tagDef.name, tagDef.text, entity, tagDef.lineNumber));
		}
		entity.setTags(tagList);

		lastComment = null;
	}

	@Override
	public void addMethod(MethodDef def) {
		JavaMethod currentMethod = new JavaMethod();
		currentMethod.setParentClass(currentClass);
		currentMethod.setLineNumber(def.lineNumber);

		// basic details
		currentMethod.setName(def.name);
		currentMethod.setReturns(createType(def.returns, def.dimensions));
		currentMethod.setConstructor(def.constructor);

		// parameters
		{
			JavaParameter[] params = new JavaParameter[def.params.size()];
			int i = 0;
			for (Iterator iterator = def.params.iterator(); iterator.hasNext();) {
				FieldDef fieldDef = (FieldDef) iterator.next();
				params[i++] = new JavaParameter(createType(fieldDef.type, fieldDef.dimensions), fieldDef.name, fieldDef.isVarArgs);
			}
			currentMethod.setParameters(params);
		}

		// exceptions
		{
			Type[] exceptions = new Type[def.exceptions.size()];
			int index = 0;
			for (Iterator iter = def.exceptions.iterator(); iter.hasNext();) {
				exceptions[index++] = createType((String) iter.next(), 0);
			}
			currentMethod.setExceptions(exceptions);
		}

		// modifiers
		{
			String[] modifiers = new String[def.modifiers.size()];
			def.modifiers.toArray(modifiers);
			currentMethod.setModifiers(modifiers);
		}

		currentMethod.setSourceCode(def.body);

		// javadoc
		addJavaDoc(currentMethod);

		currentClass.addMethod(currentMethod);
	}

	@Override
	public void addField(FieldDef def) {
		JavaField currentField = new JavaField();
		currentField.setParent(currentClass);
		currentField.setLineNumber(def.lineNumber);

		currentField.setName(def.name);
		currentField.setType(createType(def.type, def.dimensions));

		// modifiers
		{
			String[] modifiers = new String[def.modifiers.size()];
			def.modifiers.toArray(modifiers);
			currentField.setModifiers(modifiers);
		}

		// code body
		currentField.setInitializationExpression(def.body);

		// javadoc
		addJavaDoc(currentField);

		currentClass.addField(currentField);
	}

	@Override
	public JavaSource getSource() {
		return source;
	}

}
