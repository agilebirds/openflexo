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
package org.openflexo.antar.binding;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

public class GenericArrayTypeImpl implements GenericArrayType {

	private Type componentType;

	public GenericArrayTypeImpl(Type componentType) {
		this.componentType = componentType;
	}

	@Override
	public Type getGenericComponentType() {
		return componentType;
	}

	@Override
	public String toString() {
		return TypeUtils.simpleRepresentation(getGenericComponentType()) + "[]";
	}

	public String fullQualifiedRepresentation() {
		return TypeUtils.fullQualifiedRepresentation(getGenericComponentType()) + "[]";
	}

	@Override
	public int hashCode() {
		return fullQualifiedRepresentation().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GenericArrayType) {
			return getGenericComponentType() == null && ((GenericArrayType) obj).getGenericComponentType() == null
					|| getGenericComponentType() != null
					&& getGenericComponentType().equals(((GenericArrayType) obj).getGenericComponentType());
		} else {
			return super.equals(obj);
		}
	}

}
