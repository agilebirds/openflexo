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
import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.foundation.ontology.IFlexoOntologyConceptVisitor;
import org.openflexo.foundation.ontology.IFlexoOntologyObjectProperty;
import org.openflexo.technologyadapter.xsd.XSDTechnologyAdapter;

public class XSOntObjectProperty extends XSOntProperty implements IFlexoOntologyObjectProperty {

	private XSOntClass range;
	private boolean noRangeFoundYet = true;

	private List<XSOntObjectProperty> superProperties;

	protected XSOntObjectProperty(XSOntology ontology, String name, String uri, XSDTechnologyAdapter adapter) {
		super(ontology, name, uri, adapter);
		range = ontology.getRootConcept();
		superProperties = new ArrayList<XSOntObjectProperty>();
	}

	protected XSOntObjectProperty(XSOntology ontology, String name, XSDTechnologyAdapter adapter) {
		this(ontology, name, XS_ONTOLOGY_URI + "#" + name, adapter);
	}

	public void addSuperProperty(XSOntObjectProperty parent) {
		superProperties.add(parent);
	}

	public void clearSuperProperties() {
		superProperties.clear();
	}

	@Override
	public List<XSOntObjectProperty> getSuperProperties() {
		return superProperties;
	}

	@Override
	public List<XSOntObjectProperty> getSubProperties(IFlexoOntology context) {
		// TODO
		return new ArrayList<XSOntObjectProperty>();
	}

	@Override
	public XSOntClass getRange() {
		return range;
	}

	public void newRangeFound(XSOntClass range) {
		if (noRangeFoundYet) {
			this.range = range;
			noRangeFoundYet = false;
		} else {
			this.range = getOntology().getRootConcept();
		}
	}

	public void resetRange() {
		this.range = getOntology().getRootConcept();
		noRangeFoundYet = true;
	}

	@Override
	public String getDisplayableDescription() {
		return getName();
	}

	@Override
	public boolean isOntologyObjectProperty() {
		return true;
	}

	@Override
	public String getClassNameKey() {
		return "XSD_ontology_object_property";
	}

	@Override
	public String getInspectorName() {
		if (getIsReadOnly()) {
			return Inspectors.VE.ONTOLOGY_OBJECT_PROPERTY_READ_ONLY_INSPECTOR;
		} else {
			return Inspectors.VE.ONTOLOGY_OBJECT_PROPERTY_INSPECTOR;
		}
	}

	@Override
	public <T> T accept(IFlexoOntologyConceptVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
