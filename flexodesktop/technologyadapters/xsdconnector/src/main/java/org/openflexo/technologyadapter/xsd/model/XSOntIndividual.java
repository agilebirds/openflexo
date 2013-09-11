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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import org.openflexo.technologyadapter.xml.model.IXMLAttribute;
import org.openflexo.technologyadapter.xml.model.IXMLIndividual;
import org.openflexo.technologyadapter.xsd.XSDTechnologyAdapter;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntClass;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntDataProperty;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntObjectProperty;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntProperty;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XSOntIndividual extends AbstractXSOntConcept implements IFlexoOntologyIndividual, XSOntologyURIDefinitions, IXMLIndividual<XSOntIndividual,XSOntProperty> {


	private XSOntClass type;
	private Map<XSOntProperty, XSPropertyValue> values = new HashMap<XSOntProperty, XSPropertyValue>();

	// TODO : check if this is actually useful ?!?
	private Set<XSOntIndividual> children = new HashSet<XSOntIndividual>();
	private XSOntIndividual parent;

	private String uuid;


	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(XSOntIndividual.class
			.getPackage().getName());

	/**
	 * Default Constructor
	 * 
	 * @param adapter
	 */

	protected XSOntIndividual(XSDTechnologyAdapter adapter) {
		super(adapter);
		uuid = UUID.randomUUID().toString();
	}


	@Override
	public String getURI() {

		// FIXME URI is a non-sense here, as it is calculated by the ModelSlot
		return uuid;
	}



	@Override
	public String getName() {
		// The name of the Tag
		return type.getName();
	}

	@Override
	public void setName(String name) {
		// name is calculated, it cannot be set
		// in fact, it has no sense in the general case
		// FIXME URI and Name are a non-sense here, as it is calculated by the ModelSlot
		// This should be removed when inheritance to FlexoOntology will be fixed
	}


	public Type getType() {
		return (Type) type;
	}

	public void setType(Type type) {
		this.type = (XSOntClass) type;
	}

	@Override
	public boolean isIndividualOf(IFlexoOntologyClass aClass) {
		return OntologyUtils.getAllSuperClasses(this).contains(aClass);
	}

	@Override
	public List<XSOntClass> getTypes() {
		List<XSOntClass> result = new ArrayList<XSOntClass>();
		if (getType() != null) {
			result.add((XSOntClass) getType());
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
		if (((XSOntClass)getType()).equalsToConcept(aType)) {
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


	@Override
	public String getContentDATA() {
		XSOntProperty propCDATA = ((XSOntClass)this.getType()).getPropertyByName(CDATA_ATTR_NAME);
		if (propCDATA != null){
			XSPropertyValue val= this.getPropertyValue(propCDATA);
			if (val != null) return val.getValues().toString();
		}
		return null;
	}


	@Override
	public Object getAttributeValue(String attributeName) {

		XSOntProperty property = ((XSOntClass)this.getType()).getPropertyByName(attributeName);
		if (property != null){
			XSPropertyValue val= this.getPropertyValue(property);	
			if (val != null) return val.getValues();
		}
		return null;
	}

	@Override
	public String getAttributeStringValue(IXMLAttribute a) {

		XSOntProperty property = (XSOntProperty) a;

		if (property != null){
			XSPropertyValue val= this.getPropertyValue(property);	
			if (val != null)
				return val.getValues().get(0).toString();
		}
		return null;
	}



	@Override
	public XSOntProperty getAttributeByName(String aName) {
		return ((XSOntClass)this.getType()).getPropertyByName(aName);
	}

	@Override
	public Set<XSOntIndividual> getChildren() {

		return children;
	}


	@Override
	public String getUUID() {
		return uuid;
	}


	@Override
	public Collection<? extends XSOntProperty> getAttributes() {

		return   (Collection<? extends XSOntProperty>) ((XSOntClass) this.getType()).getStructuralFeatureAssociations();
	}


	@Override
	public Object createAttribute(String attrLName, Type aType, String value) {

		XSOntProperty property = this.type.getPropertyByName(attrLName);
		XSPropertyValue propValue = null;

		if (property != null) {
			propValue = this.addToPropertyValue(property, value);
		}
		else {
			logger.info("So Many Things to do...Should Add something to create Attribute: " + attrLName);
		}

		return propValue;
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
	
		XSPropertyValue returned = values.get(property);

		logger.info(" PROPERTY VALUE IS: " + returned);
		
		if (returned == null) {
			if (property instanceof XSOntObjectProperty ) {
				if (newValue == null) {
					returned = new XSObjectPropertyValue((XSOntObjectProperty) property);
				}
				else if (newValue instanceof XSOntIndividual) {
					returned = new XSObjectPropertyValue((XSOntObjectProperty) property, (XSOntIndividual) newValue);
					this.addChild((XSOntIndividual) newValue);
				} 
				values.put((XSOntObjectProperty) property, returned);
				logger.info(" CREATED PROPERTY VALUE IS: " + returned);

				return returned; 
			}
			else if (property instanceof XSOntDataProperty) {
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

	public XSOntIndividual  getParent() {
		return parent;
	}

	@Override
	public void addChild(IXMLIndividual<XSOntIndividual,XSOntProperty> child) {
		children.add((XSOntIndividual) child);
		((XSOntIndividual) child).setParent(this);
	}


	public Element toXML(Document doc) {
		Element element = doc.createElement(((XSOntClass) getType()).getName());
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
	public <T> T accept(IFlexoOntologyConceptVisitor<T> visitor) {
		return visitor.visit(this);
	}

	@Override
	public XSOntology getContainer() {
		return getOntology();
	}

	@Override
	public List<XSOntProperty> getStructuralFeatureAssociations() {
		return Collections.emptyList();
	}



}
