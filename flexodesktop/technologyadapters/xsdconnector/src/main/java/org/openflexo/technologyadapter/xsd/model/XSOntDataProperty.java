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

import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.foundation.ontology.IFlexoOntologyConceptVisitor;
import org.openflexo.foundation.ontology.IFlexoOntologyDataProperty;
import org.openflexo.technologyadapter.xsd.XSDTechnologyAdapter;

public class XSOntDataProperty extends XSOntProperty implements IFlexoOntologyDataProperty {

	private XSDDataType dataType;
	private boolean isFromAttribute = false;

	protected XSOntDataProperty(XSOntology ontology, String name, String uri, XSDTechnologyAdapter adapter) {
		super(ontology, name, uri, adapter);
	}

	@Override
	public List<XSOntDataProperty> getSuperProperties() {
		// TODO Make sure it's always empty
		return new ArrayList<XSOntDataProperty>();
	}

	@Override
	public List<XSOntDataProperty> getSubProperties(IFlexoOntology context) {
		// TODO Make sure it's always empty
		return new ArrayList<XSOntDataProperty>();
	}

	@Override
	public XSDDataType getRange() {
		return getDataType();
	}

	public XSDDataType getDataType() {
		return dataType;
	}

	public void setDataType(XSDDataType dataType) {
		this.dataType = dataType;
	}

	public void setIsFromAttribute(boolean isFromAttribute) {
		this.isFromAttribute = isFromAttribute;
	}

	public boolean getIsFromAttribute() {
		return isFromAttribute;
	}

	@Override
	public String getDisplayableDescription() {
		StringBuffer buffer = new StringBuffer(getName());
		if (getIsFromAttribute()) {
			buffer.append(" (attribute)");
		}
		return buffer.toString();
	}

	@Override
	public <T> T accept(IFlexoOntologyConceptVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
