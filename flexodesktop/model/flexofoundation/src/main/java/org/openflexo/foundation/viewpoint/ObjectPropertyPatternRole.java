package org.openflexo.foundation.viewpoint;

import org.openflexo.foundation.ontology.OntologyObjectProperty;

public class ObjectPropertyPatternRole extends PropertyPatternRole {

	@Override
	public PatternRoleType getType() {
		return PatternRoleType.ObjectProperty;
	}

	@Override
	public String getPreciseType() {
		if (type != null) {
			return type.getName();
		}
		return "";
	}

	@Override
	public Class<?> getAccessedClass() {
		return OntologyObjectProperty.class;
	}

	private OntologyObjectProperty type;

	public OntologyObjectProperty getOntologicType() {
		return type;
	}

	public void setOntologicType(OntologyObjectProperty ontologyProperty) {
		type = ontologyProperty;
	}

}
