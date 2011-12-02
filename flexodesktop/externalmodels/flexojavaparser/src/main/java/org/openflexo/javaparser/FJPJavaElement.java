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

import java.util.logging.Logger;

import org.openflexo.foundation.TemporaryFlexoModelObject;
import org.openflexo.inspector.InspectableObject;

import com.thoughtworks.qdox.model.AbstractJavaEntity;
import com.thoughtworks.qdox.model.ClassLibrary;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.Type;

public abstract class FJPJavaElement extends TemporaryFlexoModelObject implements InspectableObject {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(FJPJavaElement.class.getPackage().getName());

	protected FJPJavaSource javaSource;

	public FJPJavaElement(FJPJavaSource aJavaSource) {
		super();
		javaSource = aJavaSource;
	}

	public JavaParser getJavaParser() {
		return getJavaSource().getJavaParser();
	}

	public FJPJavaSource getJavaSource() {
		return javaSource;
	}

	public FJPJavaClass getClass(JavaClass aClass) {
		return getJavaSource().getClass(aClass);
	}

	public FJPJavaMethod getMethod(JavaMethod aMethod) {
		return getJavaSource().getMethod(aMethod);
	}

	public FJPJavaField getField(JavaField aField) {
		return getJavaSource().getField(aField);
	}

	public FJPJavaElement getFJPJavaElement(AbstractJavaEntity abstractJavaEntity) {
		return getJavaSource().getFJPJavaElement(abstractJavaEntity);
	}

	protected FJPJavaClass[] retrieveClasses(JavaClass[] someClasses) {
		return getJavaSource().retrieveClasses(someClasses);
	}

	protected FJPJavaMethod[] retrieveMethods(JavaMethod[] someMethods) {
		return getJavaSource().retrieveMethods(someMethods);
	}

	protected FJPJavaField[] retrieveFields(JavaField[] someFields) {
		return getJavaSource().retrieveFields(someFields);
	}

	public ClassLibrary getClassLibrary() {
		return getJavaSource().getClassLibrary();
	}

	public String getClassNamePrefix() {
		return getJavaSource().getClassNamePrefix();
	}

	public Type getType(String fullQualifiedName) {
		return getJavaParser().getType(fullQualifiedName);
	}

	public Type getType(Class aClass) {
		return getJavaParser().getType(aClass);
	}

	public FJPJavaClass getClassByName(String fullQualifiedName) {
		return getJavaParser().getClassByName(fullQualifiedName);
	}

	@Override
	public abstract String getInspectorName();

}
