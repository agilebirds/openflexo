package org.openflexo.technologyadapter.owl.viewpoint;

import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.technologyadapter.owl.model.DataPropertyStatement;

public class DataPropertyStatementPatternRole extends StatementPatternRole {

	public DataPropertyStatementPatternRole(ViewPointBuilder builder) {
		super(builder);
	}

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
		return getViewPoint().getViewpointOntology().getDataProperty(_getDataPropertyURI());
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
				return new ValidationError<DataPropertyStatementPatternRoleMustDefineAValidProperty, DataPropertyStatementPatternRole>(
						this, patternRole, "pattern_role_does_not_define_any_valid_data_property");
			}
			return null;
		}
	}

}
