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

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.foundation.ontology.OntologicDataType;
import org.openflexo.foundation.ontology.OntologyDataProperty;

public class XSOntDataProperty extends XSOntProperty implements OntologyDataProperty {

	private OntologicDataType dataType;
	private boolean isFromAttribute = false;

	protected XSOntDataProperty(XSOntology ontology, String name, String uri) {
		super(ontology, name, uri);
	}

	@Override
	public List<XSOntDataProperty> getSuperProperties() {
		// TODO Make sure it's always empty
		return new ArrayList<XSOntDataProperty>();
	}

	@Override
	public List<XSOntDataProperty> getSubProperties(FlexoOntology context) {
		// TODO Make sure it's always empty
		return new ArrayList<XSOntDataProperty>();
	}

	public void setDataType(OntologicDataType dataType) {
		this.dataType = dataType;
	}

	@Override
	public OntologicDataType getDataType() {
		return dataType;
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
	public boolean isOntologyDataProperty() {
		return true;
	}

	@Override
	public String getClassNameKey() {
		return "XSD_ontology_data_property";
	}

	@Override
	public String getInspectorName() {
		if (getIsReadOnly()) {
			return Inspectors.VE.ONTOLOGY_DATA_PROPERTY_READ_ONLY_INSPECTOR;
		} else {
			return Inspectors.VE.ONTOLOGY_DATA_PROPERTY_INSPECTOR;
		}
	}

}
