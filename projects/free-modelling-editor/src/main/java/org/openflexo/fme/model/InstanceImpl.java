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
package org.openflexo.fme.model;

import java.util.List;

/**
 * Default implementation for instance
 * 
 * @author sylvain
 * 
 */
public abstract class InstanceImpl implements Instance {

	@Override
	public boolean containsKeyNamed(String keyName){
		for(PropertyValue pv : getPropertyValues()){
			if(pv.getKey().equals(keyName))
				return true;
		}
		return false;
	}
	
	@Override
	public String buildDescription(){
		StringBuilder sb = new StringBuilder();
		// All properties are printed like this "myProperty=myValue"
		List<PropertyValue> propertyValues = getPropertyValues();
		for(PropertyValue propertyValue: propertyValues){
			sb.append(propertyValue.getKey());sb.append("=");
			sb.append(propertyValue.getValue());sb.append("\n");
		}
		setDescription(sb.toString());
		return sb.toString();
	}
}
