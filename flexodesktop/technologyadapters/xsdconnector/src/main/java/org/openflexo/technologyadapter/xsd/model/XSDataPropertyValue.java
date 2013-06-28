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
package org.openflexo.technologyadapter.xsd.model;

import java.util.ArrayList;
import java.util.List;

import org.openflexo.foundation.ontology.IFlexoOntologyDataPropertyValue;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntDataProperty;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntProperty;

/**
 * Implementation of an Data Property values in XSD/XML technology.<br>
 * 
 * @author sylvain
 */
public class XSDataPropertyValue extends XSPropertyValue implements IFlexoOntologyDataPropertyValue {

	private XSOntDataProperty property;
	private List<Object> values;

	public XSDataPropertyValue(XSOntDataProperty property, Object value) {
		super();
		this.property = property;
		this.values = new ArrayList<Object>();
		this.values.add(value);
	}

	@Override
	public XSOntDataProperty getDataProperty() {
		return property;
	}

	@Override
	public XSOntProperty getProperty() {
		return getDataProperty();
	}

	@Override
	public List<Object> getValues() {
		return values;
	}

	public void addToValues(Object value) {
		values.add(value);
	}

	public void removeFromValues(Object value) {
		values.remove(value);
	}

	@Override
	public String toString() {
		String result = new String();

		// TODO Work in progress
		
		for (Object o : values){
			if (o instanceof String) {
				result = result.concat((String) o);
			}
		}
		return result;
	}
	
}
