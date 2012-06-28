package org.openflexo.foundation.viewpoint;

import org.openflexo.foundation.ontology.OntologicDataType;
import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.ontology.OntologyObjectProperty;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;

public class DataPropertyPatternRole extends PropertyPatternRole {

	private OntologicDataType dataType;

	public DataPropertyPatternRole(ViewPointBuilder builder) {
		super(builder);
	}

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
