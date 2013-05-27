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
import java.util.UUID;
import java.util.logging.Level;

import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyConceptVisitor;
import org.openflexo.foundation.ontology.IFlexoOntologyFeatureAssociation;
import org.openflexo.foundation.ontology.IFlexoOntologyIndividual;
import org.openflexo.foundation.ontology.IFlexoOntologyPropertyValue;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.ontology.OntologyUtils;
import org.openflexo.technologyadapter.xsd.XSDTechnologyAdapter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XSOntIndividual extends AbstractXSOntConcept implements IFlexoOntologyIndividual, XSOntologyURIDefinitions {


	private XSOntClass type;
	private Map<XSOntProperty, XSPropertyValue> values = new HashMap<XSOntProperty, XSPropertyValue>();
	private Set<XSOntIndividual> children = new HashSet<XSOntIndividual>();
	private XSOntIndividual parent;
	

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(AbstractXSOntObject.class
			.getPackage().getName());

	/**
	 * Default Constructor
	 * 
	 * @param adapter
	 */

	protected XSOntIndividual(XSDTechnologyAdapter adapter) {
		super(adapter);
	}

	

	@Override
	public String getName() {
		// calculated, as it as no actual Meaning for an XML Individual
		// so give the last part of the URI
		String objURI = getURI();
		if (objURI != null )
			return objURI.substring(objURI.lastIndexOf("#"));
		else
			return null;
		
	}

	@Override
	public void setName(String name) {
		// name is calculated, it cannot be set
		// in fact, it has no sense in the general case
		logger.warning("Name of an Individual can not be set");
	}

	
	public XSOntClass getType() {
		return type;
	}

	public void setType(XSOntClass type) {
		this.type = type;
	}

	@Override
	public boolean isIndividualOf(IFlexoOntologyClass aClass) {
		return OntologyUtils.getAllSuperClasses(this).contains(aClass);
	}

	@Override
	public List<XSOntClass> getTypes() {
		List<XSOntClass> result = new ArrayList<XSOntClass>();
		if (getType() != null) {
			result.add(getType());
		}
		return result;
	}

	public void addToTypes(IFlexoOntologyClass type) {
		// Can only have one type.
		if (type instanceof XSOntClass) {
			setType((XSOntClass) type);
		}
	}

	public void removeFromTypes(IFlexoOntologyClass aType) {
		if (getType().equalsToConcept(aType)) {
			setType(null);
		}
	}

	@Override
	public List<XSPropertyValue> getPropertyValues() {
		ArrayList<XSPropertyValue> returned = new ArrayList<XSPropertyValue>();
		returned.addAll(values.values());
		return returned;
	}

	/**
	 * Return the {@link IFlexoOntologyPropertyValue} matching supplied property and defined for this individual<br>
	 * If no values were defined for supplied property, return null
	 * 
	 * @param property
	 * @return
	 */
	@Override
	public XSPropertyValue getPropertyValue(IFlexoOntologyStructuralProperty property) {
		/*if (property.getURI().equals(XS_HASCHILD_PROPERTY_NAME)) {
		return children;
		}
		if (property.getURI().equals(XS_HASPARENT_PROPERTY_NAME)) {
		return parent;
		}*/
		return values.get(property);
	}

	/**
	 * Add newValue as a value for supplied property<br>
	 * Return the {@link IFlexoOntologyPropertyValue} matching supplied property and defined for this individual<br>
	 * 
	 * @param property
	 * @param newValue
	 * @return
	 */
	@Override
	public XSPropertyValue addToPropertyValue(IFlexoOntologyStructuralProperty property, Object newValue) {
		/*if (property.getURI().equals(XS_HASCHILD_PROPERTY_NAME)) {
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
		}*/
		if (newValue != null) {
			XSPropertyValue returned = values.get(property);
			if (returned == null) {
				if (property instanceof XSOntObjectProperty && newValue instanceof XSOntIndividual) {
					returned = new XSObjectPropertyValue((XSOntObjectProperty) property, (XSOntIndividual) newValue);
					values.put((XSOntObjectProperty) property, returned);
					return returned;
				} else if (property instanceof XSOntDataProperty) {
					returned = new XSDataPropertyValue((XSOntDataProperty) property, newValue);
					values.put((XSOntDataProperty) property, returned);
					return returned;
				}
			} else {
				if (returned instanceof XSObjectPropertyValue && newValue instanceof XSOntIndividual) {
					((XSObjectPropertyValue) returned).addToValues((XSOntIndividual) newValue);
					return returned;
				} else if (property instanceof XSOntDataProperty) {
					((XSDataPropertyValue) returned).addToValues(newValue);
					return returned;
				}
			}
		}
		return null;

	}

	/**
	 * Remove valueToRemove from list of values for supplied property<br>
	 * Return the {@link IFlexoOntologyPropertyValue} matching supplied property and defined for this individual<br>
	 * If the supplied valueToRemove parameter was the only value defined for supplied property for this individual, return null
	 * 
	 * @param property
	 * @param valueToRemove
	 * @return
	 */
	@Override
	public IFlexoOntologyPropertyValue removeFromPropertyValue(IFlexoOntologyStructuralProperty property, Object valueToRemove) {
		if (valueToRemove != null) {
			XSPropertyValue returned = values.get(property);
			if (returned == null) {
				return null;
			} else {
				if (returned instanceof XSObjectPropertyValue && valueToRemove instanceof XSOntIndividual) {
					((XSObjectPropertyValue) returned).removeFromValues((XSOntIndividual) valueToRemove);
				} else if (property instanceof XSOntDataProperty) {
					((XSDataPropertyValue) returned).removeFromValues(valueToRemove);
				}
				if (returned.getValues().size() == 0) {
					// No more values registered, delete from entries
					values.remove(returned);
					return null;
				}
				return returned;

			}
		}
		return null;

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
	public <T> T accept(IFlexoOntologyConceptVisitor<T> visitor) {
		return visitor.visit(this);
	}

	@Override
	public XSOntology getContainer() {
		return getOntology();
	}

	@Override
	public List<IFlexoOntologyFeatureAssociation> getFeatureAssociations() {
		return null;
	}

}
