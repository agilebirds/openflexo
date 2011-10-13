/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
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
package org.openflexo.model.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ParameterizedTypeImpl implements ParameterizedType {

	public ParameterizedTypeImpl(Class rawType, Type[] actualTypeArguments) 
	{
		this(rawType,null,actualTypeArguments);
	}
	
	public ParameterizedTypeImpl(Class rawType, Type actualTypeArgument) 
	{
		this(rawType,null,makeTypeArray(actualTypeArgument));
	}
	
	private static Type[] makeTypeArray(Type t)
	{
		Type[] returned = new Type[1];
		returned[0] = t;
		return returned;
	}
	
	public ParameterizedTypeImpl(Class rawType, Type ownerType, Type[] actualTypeArguments) {
		super();
		this.rawType = rawType;
		this.ownerType = ownerType;
		this.actualTypeArguments = actualTypeArguments;
	}

	private Class rawType;
	private Type ownerType;
	private Type[] actualTypeArguments;
	
	@Override
	public Type[] getActualTypeArguments() {
		return actualTypeArguments;
	}

	@Override
	public Type getOwnerType() {
		return ownerType;
	}

	@Override
	public Type getRawType() {
		return rawType;
	}

	@Override
	public String toString() 
	{
		StringBuffer sb = new StringBuffer();
		sb.append(rawType.getSimpleName()+"<");
		boolean isFirst = true;
		for (Type t : getActualTypeArguments()) {
			sb.append((isFirst?"":",")+TypeUtils.simpleRepresentation(t));
			isFirst = false;
		}
		sb.append(">");
		return sb.toString();
	}
	
	public String fullQualifiedRepresentation() 
	{
		StringBuffer sb = new StringBuffer();
		sb.append(rawType.getName()+"<");
		boolean isFirst = true;
		for (Type t : getActualTypeArguments()) {
			sb.append((isFirst?"":",")+TypeUtils.fullQualifiedRepresentation(t));
			isFirst = false;
		}
		sb.append(">");
		return sb.toString();
	}
	
	@Override
	public int hashCode() 
	{
		return fullQualifiedRepresentation().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if (obj instanceof Type) {
			return TypeUtils.fullQualifiedRepresentation(this).equals(TypeUtils.fullQualifiedRepresentation((Type)obj));
		}
		else {
			return super.equals(obj);
		}
		/*if (obj instanceof ParameterizedType) {
			if (getRawType() == null) return false;
			if (getActualTypeArguments() == null) return false;
			if (! (((getOwnerType() == null && ((ParameterizedType)obj).getOwnerType() == null)
					|| (getOwnerType() != null && getOwnerType().equals(((ParameterizedType)obj).getOwnerType())))
					&& getRawType().equals(((ParameterizedType)obj).getRawType())))
				return false;
			// Now check all args
			for (int i=0; i<getActualTypeArguments().length; i++) {
				if (getActualTypeArguments()[i] == null) {
					if (((ParameterizedType)obj).getActualTypeArguments()[i] != null) return false;
				}
				else if (!getActualTypeArguments()[i].equals(((ParameterizedType)obj).getActualTypeArguments()[i])) return false;
			}
			return true;
		}
		else return super.equals(obj);*/
	}
}
