package org.openflexo.foundation.viewpoint;

import org.openflexo.foundation.ontology.OntologyClass;

public class ClassPatternRole extends OntologicObjectPatternRole {

	@Override
	public PatternRoleType getType() {
		return PatternRoleType.Class;
	}

	@Override
	public String getPreciseType() {
		if (getOntologicType() != null) {
			return getOntologicType().getName();
		}
		return "";
	}

	@Override
	public Class<?> getAccessedClass() {
		return OntologyClass.class;
	}

	private String conceptURI;

	public String _getConceptURI() {
		return conceptURI;
	}

	public void _setConceptURI(String conceptURI) {
		this.conceptURI = conceptURI;
	}

	public OntologyClass getOntologicType() {
		if (getViewPoint() != null) {
			getViewPoint().loadWhenUnloaded();
		}
		if (getOntologyLibrary() != null) {
			return getOntologyLibrary().getClass(_getConceptURI());
		}
		return null;
	}

	public void setOntologicType(OntologyClass ontologyClass) {
		conceptURI = (ontologyClass != null ? ontologyClass.getURI() : null);
	}

}
