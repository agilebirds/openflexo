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

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

public class WilcardTypeImpl implements WildcardType {

	private Type[] upperBounds = null;
	private Type[] lowerBounds = null;
	
	public WilcardTypeImpl(Type[] upperBounds, Type[] lowerBounds) 
	{
		this.upperBounds = upperBounds;
		this.lowerBounds = lowerBounds;
	}
	
	public WilcardTypeImpl(Type upperBound) 
	{
		upperBounds = new Type[1];
		upperBounds[0] = upperBound;
		lowerBounds = null;
	}
	
	@Override
	public Type[] getLowerBounds()
	{
		return lowerBounds;
	}
	
	@Override
	public Type[] getUpperBounds()
	{
		return upperBounds;
	}
	
	@Override
	public String toString() 
	{
		StringBuffer sb = new StringBuffer();
		sb.append("?");

		if (getUpperBounds() != null && getUpperBounds().length > 0) {
			sb.append(" extends ");
			boolean isFirst = true;
			for (Type t : getUpperBounds()) {
				sb.append((isFirst?"":",")+TypeUtils.simpleRepresentation(t));
				isFirst = false;
			}
		}
		
		if (getLowerBounds() != null && getLowerBounds().length > 0) {
			sb.append(" super ");
			boolean isFirst = true;
			for (Type t : getLowerBounds()) {
				sb.append((isFirst?"":",")+TypeUtils.simpleRepresentation(t));
				isFirst = false;
			}
		}
		
		return sb.toString();
	}
	
}
