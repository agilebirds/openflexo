package org.openflexo.foundation.ontology.xsd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyIndividual;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XSOntIndividual extends XSOntObject implements OntologyIndividual, XSOntologyURIDefinitions {

	private XSOntClass type;
	private Map<OntologyProperty, Object> values = new HashMap<OntologyProperty, Object>();
	private Set<XSOntIndividual> children = new HashSet<XSOntIndividual>();
	private XSOntIndividual parent;

	protected XSOntIndividual(XSOntology ontology, String name, String uri) {
		super(ontology, name, uri);
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
	public Object addType(OntologyClass type) {
		// Can only have one type.
		if (type instanceof XSOntClass) {
			setType((XSOntClass) type);
		}
		return null;
	}

	@Override
	public Object getPropertyValue(OntologyProperty property) {
		if (property.getURI().equals(XS_HASCHILD_PROPERTY_NAME)) {
			return children;
		}
		if (property.getURI().equals(XS_HASPARENT_PROPERTY_NAME)) {
			return parent;
		}
		return values.get(property);
	}

	@Override
	public void setPropertyValue(OntologyProperty property, Object newValue) {
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

	protected void addChild(XSOntIndividual child) {
		children.add(child);
		child.setParent(this);
	}

	protected Element toXML(Document doc) {
		Element element = doc.createElement(getType().getName());
		for (XSOntIndividual child : children) {
			Element childElement = child.toXML(doc);
			element.appendChild(childElement);
		}
		for (OntologyProperty property : values.keySet()) {
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

}
