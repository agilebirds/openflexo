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

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.javaparser.ParsedJavaField;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;

public class FJPJavaField extends FJPJavaEntity implements ParsedJavaField {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(FJPJavaField.class.getPackage().getName());

	private JavaField _qdJavaField;

	public FJPJavaField(JavaField qdJavaField, FJPJavaSource aJavaSource) {
		super(qdJavaField, aJavaSource);
		_qdJavaField = qdJavaField;
	}

	public String getCallSignature() {
		return _qdJavaField.getCallSignature();
	}

	public String getDeclarationSignature(boolean withModifiers) {
		return _qdJavaField.getDeclarationSignature(withModifiers);
	}

	public String getDeclarationSignature() {
		return _qdJavaField.getDeclarationSignature(true);
	}

	@Override
	public String getInitializationExpression() {
		return _qdJavaField.getInitializationExpression();
	}

	@Override
	public String getName() {
		return _qdJavaField.getName();
	}

	public FJPJavaClass getParentClass() {
		return getClass((JavaClass) _qdJavaField.getParent());
	}

	@Override
	public String getInspectorName() {
		return Inspectors.CG.JAVA_FIELD_INSPECTOR;
	}

	@Override
	public int getLinesCount() {
		return 1;
	}

	@Override
	public DMType getType() {
		return (DMType) _qdJavaField.getType();
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public String getUniqueIdentifier() {
		return getName();
	}

}
