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

public class XSOntIndividual extends XSOntObject implements OntologyIndividual, XSOntologyURIDefinitions {

	private List<XSOntClass> types = new ArrayList<XSOntClass>();
	private Set<String> typeUris = new HashSet<String>();
	private Map<OntologyProperty, Object> values = new HashMap<OntologyProperty, Object>();

	protected XSOntIndividual(XSOntology ontology, String name, String uri) {
		super(ontology, name, uri);
	}

	@Override
	public List<XSOntClass> getTypes() {
		return types;
	}

	@Override
	public Object addType(OntologyClass type) {
		if (type instanceof XSOntClass && typeUris.add(type.getURI())) {
			types.add((XSOntClass) type);
			return true;
		}
		return false;
	}

	@Override
	public Object getPropertyValue(OntologyProperty property) {
		return values.get(property);
	}

	@Override
	public void setPropertyValue(OntologyProperty property, Object newValue) {
		// TODO How can one define several values for a given property?
		values.put(property, newValue);
	}

}
