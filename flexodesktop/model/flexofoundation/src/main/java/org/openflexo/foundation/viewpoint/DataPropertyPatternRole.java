package org.openflexo.foundation.viewpoint;

import org.openflexo.foundation.ontology.OntologicDataType;
import org.openflexo.foundation.ontology.IFlexoOntologyDataProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyObjectProperty;
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
		return IFlexoOntologyObjectProperty.class;
	}

	@Override
	public IFlexoOntologyDataProperty getParentProperty() {
		return (IFlexoOntologyDataProperty) super.getParentProperty();
	}

	public void setParentProperty(IFlexoOntologyDataProperty ontologyProperty) {
		super.setParentProperty(ontologyProperty);
	}

	public OntologicDataType getDataType() {
		return dataType;
	}

	public void setDataType(OntologicDataType dataType) {
		this.dataType = dataType;
	}

}
