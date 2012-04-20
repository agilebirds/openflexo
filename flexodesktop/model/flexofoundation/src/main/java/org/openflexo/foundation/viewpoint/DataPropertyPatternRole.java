package org.openflexo.foundation.viewpoint;

import org.openflexo.foundation.ontology.OntologicDataType;
import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.ontology.OntologyObjectProperty;

public class DataPropertyPatternRole extends PropertyPatternRole {

	private OntologicDataType dataType;

	@Override
	public PatternRoleType getType() {
		return PatternRoleType.DataProperty;
	}

	@Override
	public String getPreciseType() {
		if (getParentProperty() != null) {
			return getParentProperty().getName();
		}
		return "";
	}

	@Override
	public Class<?> getAccessedClass() {
		return OntologyObjectProperty.class;
	}

	@Override
	public OntologyDataProperty getParentProperty() {
		return (OntologyDataProperty) super.getParentProperty();
	}

	public void setParentProperty(OntologyDataProperty ontologyProperty) {
		super.setParentProperty(ontologyProperty);
	}

	public OntologicDataType getDataType() {
		return dataType;
	}

	public void setDataType(OntologicDataType dataType) {
		this.dataType = dataType;
	}

}
