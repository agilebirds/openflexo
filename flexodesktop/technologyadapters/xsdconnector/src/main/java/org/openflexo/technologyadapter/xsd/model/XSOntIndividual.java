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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyIndividual;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.technologyadapter.xsd.XSDTechnologyAdapter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XSOntIndividual extends AbstractXSOntObject implements IFlexoOntologyIndividual, XSOntologyURIDefinitions {

	private XSOntClass type;
	private Map<IFlexoOntologyStructuralProperty, Object> values = new HashMap<IFlexoOntologyStructuralProperty, Object>();
	private Set<XSOntIndividual> children = new HashSet<XSOntIndividual>();
	private XSOntIndividual parent;

	protected XSOntIndividual(XSOntology ontology, String name, String uri, XSDTechnologyAdapter adapter) {
		super(ontology, name, uri, adapter);
	}

	public XSOntClass getType() {
		return type;
	}

	public void setType(XSOntClass type) {
		this.type = type;
	}

	@Override
	public List<XSOntClass> getTypes() {
		List<XSOntClass> result = new ArrayList<XSOntClass>();
		if (getType() != null) {
			result.add(getType());
		}
		return result;
	}

	@Override
	public Object addType(IFlexoOntologyClass type) {
		// Can only have one type.
		if (type instanceof XSOntClass) {
			setType((XSOntClass) type);
		}
		return null;
	}

	@Override
	public Object getPropertyValue(IFlexoOntologyStructuralProperty property) {
		if (property.getURI().equals(XS_HASCHILD_PROPERTY_NAME)) {
			return children;
		}
		if (property.getURI().equals(XS_HASPARENT_PROPERTY_NAME)) {
			return parent;
		}
		return values.get(property);
	}

	@Override
	public void setPropertyValue(IFlexoOntologyStructuralProperty property, Object newValue) {
		if (property.getURI().equals(XS_HASCHILD_PROPERTY_NAME)) {
			// adds a child instead of a regular set
			// TODO make sure that's the way to do it
			if (newValue instanceof XSOntIndividual) {
				addChild((XSOntIndividual) newValue);
			}
		} else if (property.getURI().equals(XS_HASPARENT_PROPERTY_NAME)) {
			if (newValue instanceof XSOntIndividual) {
				setParent((XSOntIndividual) newValue);
			}
		} else {
			values.put(property, newValue);
		}
	}

	@Override
	public String getDisplayableDescription() {
		return getName();
	}

	protected void setParent(XSOntIndividual parent) {
		this.parent = parent;
	}

	protected XSOntIndividual getParent() {
		return parent;
	}

	public void addChild(XSOntIndividual child) {
		children.add(child);
		child.setParent(this);
	}

	protected Element toXML(Document doc) {
		Element element = doc.createElement(getType().getName());
		for (XSOntIndividual child : children) {
			Element childElement = child.toXML(doc);
			element.appendChild(childElement);
		}
		for (IFlexoOntologyStructuralProperty property : values.keySet()) {
			if (property instanceof XSOntDataProperty) {
				if (((XSOntDataProperty) property).getIsFromAttribute()) {
					element.setAttribute(property.getName(), values.get(property).toString());
				} else {
					Element dataElement = doc.createElement(property.getName());
					dataElement.setTextContent(values.get(property).toString());
					element.appendChild(dataElement);
				}
			}
		}
		return element;
	}

	@Override
	public boolean isOntologyIndividual() {
		return true;
	}

	@Override
	public String getClassNameKey() {
		return "XSD_ontology_individual";
	}

	@Override
	public String getInspectorName() {
		if (getIsReadOnly()) {
			return Inspectors.VE.ONTOLOGY_INDIVIDUAL_READ_ONLY_INSPECTOR;
		} else {
			return Inspectors.VE.ONTOLOGY_INDIVIDUAL_INSPECTOR;
		}
	}
}
