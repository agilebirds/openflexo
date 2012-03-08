package org.openflexo.foundation.viewpoint;

import org.openflexo.foundation.ontology.ObjectPropertyStatement;
import org.openflexo.foundation.ontology.OntologyProperty;

public class ObjectPropertyStatementPatternRole extends StatementPatternRole {

	@Override
	public PatternRoleType getType() {
		return PatternRoleType.ObjectPropertyStatement;
	}

	@Override
	public String getPreciseType() {
		if (getObjectProperty() != null) {
			return getObjectProperty().getName();
		}
		return "";
	}

	@Override
	public Class<ObjectPropertyStatement> getAccessedClass() {
		return ObjectPropertyStatement.class;
	}

	private String objectPropertyURI;

	public String _getObjectPropertyURI() {
		return objectPropertyURI;
	}

	public void _setObjectPropertyURI(String objectPropertyURI) {
		this.objectPropertyURI = objectPropertyURI;
	}

	public OntologyProperty getObjectProperty() {
		if (getCalc() != null) {
			getCalc().loadWhenUnloaded();
		}
		if (getOntologyLibrary() != null) {
			return getOntologyLibrary().getObjectProperty(_getObjectPropertyURI());
		}
		return null;
	}

	public void setObjectProperty(OntologyProperty p) {
		_setObjectPropertyURI(p != null ? p.getURI() : null);
	}

}
