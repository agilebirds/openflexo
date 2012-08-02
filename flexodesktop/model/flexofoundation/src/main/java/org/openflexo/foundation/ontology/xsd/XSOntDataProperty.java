package org.openflexo.foundation.ontology.xsd;

import java.util.ArrayList;
import java.util.List;

import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.foundation.ontology.OntologicDataType;
import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.ontology.OntologyObject;

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

	@Override
	public OntologyObject getDomain() {
		// TODO
		return null;
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

}
