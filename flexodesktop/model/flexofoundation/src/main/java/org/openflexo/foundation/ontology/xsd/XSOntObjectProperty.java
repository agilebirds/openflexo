package org.openflexo.foundation.ontology.xsd;

import java.util.ArrayList;
import java.util.List;

import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.OntologyObjectProperty;

public class XSOntObjectProperty extends XSOntProperty implements OntologyObjectProperty {

	protected XSOntObjectProperty(XSOntology ontology, String name) {
		super(ontology, name, XS_ONTOLOGY_URI + "#" + name);
	}

	@Override
	public List<XSOntObjectProperty> getSuperProperties() {
		// TODO if element specific properties are added
		return new ArrayList<XSOntObjectProperty>();
	}

	@Override
	public List<XSOntObjectProperty> getSubProperties(FlexoOntology context) {
		// TODO if element specific properties are added
		return new ArrayList<XSOntObjectProperty>();
	}

	@Override
	public OntologyObject getDomain() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OntologyObject getRange() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isLiteralRange() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getDisplayableDescription() {
		return getName();
	}

	@Override
	public boolean isOntologyObjectProperty() {
		return true;
	}

}
