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
package org.openflexo.velocity;

import java.lang.reflect.Field;

import org.apache.velocity.util.introspection.VelPropertyGet;


public class FlexoVelocityPropertyGet implements VelPropertyGet {
	
	private Field objField;
	
	public FlexoVelocityPropertyGet(Class objClass, String identifier) throws SecurityException, NoSuchFieldException
	{
		objField = objClass.getField(identifier);
	}

	@Override
	public String getMethodName() {
		return objField.getName();
	}

	@Override
	public Object invoke(Object obj) throws Exception {
		return objField.get(obj);
	}

	@Override
	public boolean isCacheable() {
		return true;
	}

}
