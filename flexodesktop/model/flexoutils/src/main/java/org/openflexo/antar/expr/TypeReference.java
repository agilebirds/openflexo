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
package org.openflexo.antar.expr;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.ParameterizedTypeImpl;

/**
 * Represents a type reference, as formed by a static access and some optional parameters
 * 
 * @author sylvain
 * 
 */
public class TypeReference {

	private static final Logger logger = Logger.getLogger(TypeReference.class.getPackage().getName());

	private String baseType;
	private List<TypeReference> parameters;
	private Type type;

	public TypeReference(String baseType) {
		super();
		this.baseType = baseType;
		parameters = new ArrayList<TypeReference>();
	}

	public TypeReference(String baseType, List<TypeReference> someParameters) {
		super();
		this.baseType = baseType;
		parameters = new ArrayList<TypeReference>(someParameters);
	}

	public String getBaseType() {
		return baseType;
	}

	public List<TypeReference> getParameters() {
		return parameters;
	}

	public Type getType() {
		if (type == null) {
			try {
				type = makeType();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return type;
	}

	private Type makeType() throws ClassNotFoundException {
		Class baseClass = Class.forName(baseType);
		if (parameters.size() > 0) {
			Type[] params = new Type[parameters.size()];
			int i = 0;
			for (TypeReference r : parameters) {
				params[i++] = r.getType();
			}
			return new ParameterizedTypeImpl(baseClass, params);
		} else {
			return baseClass;
		}
	}
}
