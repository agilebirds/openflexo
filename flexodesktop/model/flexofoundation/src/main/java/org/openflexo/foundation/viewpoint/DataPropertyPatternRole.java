package org.openflexo.foundation.viewpoint;

import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.ontology.OntologyObjectProperty;

public class DataPropertyPatternRole extends PropertyPatternRole {

	@Override
	public PatternRoleType getType() {
		return PatternRoleType.DataProperty;
	}

	@Override
	public String getPreciseType() {
		if (type != null)
			return type.getName();
		return "";
	}

	@Override
	public Class<?> getAccessedClass() {
		return OntologyObjectProperty.class;
	}

	private OntologyDataProperty type;

	public OntologyDataProperty getOntologicType() {
		return type;
	}

	public void setOntologicType(OntologyDataProperty ontologyProperty) {
		type = ontologyProperty;
	}

}
