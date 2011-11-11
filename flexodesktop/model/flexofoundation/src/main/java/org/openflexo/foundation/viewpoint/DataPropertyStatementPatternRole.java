package org.openflexo.foundation.viewpoint;

import org.openflexo.foundation.ontology.DataPropertyStatement;
import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.ontology.OntologyProperty;

public class DataPropertyStatementPatternRole extends StatementPatternRole {

	@Override
	public PatternRoleType getType() {
		return PatternRoleType.DataPropertyStatement;
	}

	@Override
	public String getPreciseType() {
		if (getDataProperty() != null)
			return getDataProperty().getName();
		return "";
	}

	@Override
	public Class<DataPropertyStatement> getAccessedClass() {
		return DataPropertyStatement.class;
	}

	private String dataPropertyURI;

	public String _getDataPropertyURI() {
		return dataPropertyURI;
	}

	public void _setDataPropertyURI(String dataPropertyURI) {
		this.dataPropertyURI = dataPropertyURI;
	}

	public OntologyDataProperty getDataProperty() {
		getCalc().loadWhenUnloaded();
		return getOntologyLibrary().getDataProperty(_getDataPropertyURI());
	}

	public void setDataProperty(OntologyProperty p) {
		_setDataPropertyURI(p != null ? p.getURI() : null);
	}

}
