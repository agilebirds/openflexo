package org.openflexo.foundation.viewpoint;

import org.openflexo.foundation.ontology.DataPropertyStatement;
import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;

public class DataPropertyStatementPatternRole extends StatementPatternRole {

	@Override
	public PatternRoleType getType() {
		return PatternRoleType.DataPropertyStatement;
	}

	@Override
	public String getPreciseType() {
		if (getDataProperty() != null) {
			return getDataProperty().getName();
		}
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
		getViewPoint().loadWhenUnloaded();
		return getOntologyLibrary().getDataProperty(_getDataPropertyURI());
	}

	public void setDataProperty(OntologyProperty p) {
		_setDataPropertyURI(p != null ? p.getURI() : null);
	}

	public static class DataPropertyStatementPatternRoleMustDefineAValidProperty extends
			ValidationRule<DataPropertyStatementPatternRoleMustDefineAValidProperty, DataPropertyStatementPatternRole> {
		public DataPropertyStatementPatternRoleMustDefineAValidProperty() {
			super(DataPropertyStatementPatternRole.class, "pattern_role_must_define_a_valid_data_property");
		}

		@Override
		public ValidationIssue<DataPropertyStatementPatternRoleMustDefineAValidProperty, DataPropertyStatementPatternRole> applyValidation(
				DataPropertyStatementPatternRole patternRole) {
			if (patternRole.getDataProperty() == null) {
				return new ValidationError<DataPropertyStatementPatternRoleMustDefineAValidProperty, DataPropertyStatementPatternRole>(this,
						patternRole, "pattern_role_does_not_define_any_valid_data_property");
			}
			return null;
		}
	}

}
