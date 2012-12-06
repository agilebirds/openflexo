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

import org.openflexo.foundation.ontology.IFlexoOntologyObjectPropertyValue;

/**
 * Implementation of an Object Property values in XSD/XML technology.<br>
 * Value is an instance of {@link XSOntIndividual}
 * 
 * @author sylvain
 */
public class XSObjectPropertyValue extends XSPropertyValue implements IFlexoOntologyObjectPropertyValue {

	private XSOntObjectProperty property;
	private List<XSOntIndividual> values;

	public XSObjectPropertyValue(XSOntObjectProperty property, XSOntIndividual value) {
		super();
		this.property = property;
		this.values = new ArrayList<XSOntIndividual>();
		this.values.add(value);
	}

	@Override
	public XSOntObjectProperty getObjectProperty() {
		return property;
	}

	@Override
	public XSOntProperty getProperty() {
		return getObjectProperty();
	}

	@Override
	public List<XSOntIndividual> getValues() {
		return values;
	}

	public void addToValues(XSOntIndividual value) {
		values.add(value);
	}

	public void removeFromValues(XSOntIndividual value) {
		values.remove(value);
	}
}
