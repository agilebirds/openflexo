package org.openflexo.technologyadapter.xsd.model;

import java.util.ArrayList;
import java.util.List;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.foundation.ontology.OntologicDataType;
import org.openflexo.foundation.ontology.IFlexoOntologyDataProperty;

public class XSOntDataProperty extends XSOntProperty implements IFlexoOntologyDataProperty {

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
	public List<XSOntDataProperty> getSubProperties(IFlexoOntology context) {
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
