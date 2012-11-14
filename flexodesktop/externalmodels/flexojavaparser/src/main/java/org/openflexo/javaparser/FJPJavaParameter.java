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

import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.javaparser.ParsedJavaMethod.ParsedJavaMethodParameter;

import com.thoughtworks.qdox.model.JavaParameter;

public class FJPJavaParameter extends FJPJavaElement implements ParsedJavaMethodParameter {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(FJPJavaParameter.class.getPackage().getName());

	private JavaParameter _qdJavaParameter;

	public FJPJavaParameter(JavaParameter qdJavaParameter, FJPJavaSource aJavaSource) {
		super(aJavaSource);
		_qdJavaParameter = qdJavaParameter;
	}

	@Override
	public String getInspectorName() {
		// not inspectable alone
		return null;
	}

	@Override
	public String getName() {
		return _qdJavaParameter.getName();
	}

	public FJPJavaMethod getParentMethod() {
		return getMethod(_qdJavaParameter.getParentMethod());
	}

	@Override
	public DMType getType() {
		return (DMType) _qdJavaParameter.getType();
	}

	public String getTypeAsString() {
		return getType().toString();
	}

	public boolean isVarArgs() {
		return _qdJavaParameter.isVarArgs();
	}

	public boolean isArray() {
		return getType() != null ? getType().isArray() : false;
	}

}
