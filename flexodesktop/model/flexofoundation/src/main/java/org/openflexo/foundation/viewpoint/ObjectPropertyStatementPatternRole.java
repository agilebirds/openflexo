package org.openflexo.foundation.viewpoint;

import org.openflexo.foundation.ontology.ObjectPropertyStatement;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;

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
		if (getViewPoint() != null) {
			getViewPoint().loadWhenUnloaded();
		}
		if (getOntologyLibrary() != null) {
			return getOntologyLibrary().getObjectProperty(_getObjectPropertyURI());
		}
		return null;
	}

	public void setObjectProperty(OntologyProperty p) {
		_setObjectPropertyURI(p != null ? p.getURI() : null);
	}

	public static class ObjectPropertyStatementPatternRoleMustDefineAValidProperty extends
			ValidationRule<ObjectPropertyStatementPatternRoleMustDefineAValidProperty, ObjectPropertyStatementPatternRole> {
		public ObjectPropertyStatementPatternRoleMustDefineAValidProperty() {
			super(ObjectPropertyStatementPatternRole.class, "pattern_role_must_define_a_valid_object_property");
		}

		@Override
		public ValidationIssue<ObjectPropertyStatementPatternRoleMustDefineAValidProperty, ObjectPropertyStatementPatternRole> applyValidation(
				ObjectPropertyStatementPatternRole patternRole) {
			if (patternRole.getObjectProperty() == null) {
				return new ValidationError<ObjectPropertyStatementPatternRoleMustDefineAValidProperty, ObjectPropertyStatementPatternRole>(
						this, patternRole, "pattern_role_does_not_define_any_valid_object_property");
			}
			return null;
		}
	}

}
