
/*
 * (c) Copyright 2010-2012 AgileBirds
 * (c) Copyright 2012-2013 Openflexo
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

package org.openflexo.technologyadapter.xml.model;

import java.lang.reflect.Type;

import org.openflexo.foundation.FlexoObject;


/**
 * 
 * Represents an XML Attribute in an XMLModel
 * 
 * @author xtof
 *
 */
public class XMLAttribute extends FlexoObject implements IXMLAttribute {
	
	// Properties
	private String name;
	private Object value;
	private Type myType;
	private IXMLIndividual<?, ?> container;
	
	
	public XMLAttribute (String aName, Type aType, String aValue){
		super();
		name = aName;
		setType(aType);
		value = aValue;
	}

	public XMLAttribute (String aName){
		super();
		name = aName;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String aName) {
		name = aName;
	}
	

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(Object aValue) {
		this.value = aValue;
	}

	/**
	 * @return the myType
	 */
	public Type getType() {
		return myType;
	}

	/**
	 * @param myType the myType to set
	 */
	public void setType(Type myType) {
		this.myType = myType;
	}

	/**
	 * @return the containedIn
	 */
	public IXMLIndividual<?, ?> getContainer() {
		return container;
	}

	/**
	 * @param containedIn the containedIn to set
	 */
	public void setContainer(IXMLIndividual<?, ?> containedIn) {
		this.container = containedIn;
	}

	@Override
	public String getFullyQualifiedName() {
		return name;
	}

	@Override
	public boolean isSimpleAttribute() {
		return true;
	}

	@Override
	public void addValue(IXMLIndividual<?, ?> indiv, Object value) {
		setValue(value);
		
	}

	@Override
	public Type getAttributeType() {
		return myType;
	}

	

}
