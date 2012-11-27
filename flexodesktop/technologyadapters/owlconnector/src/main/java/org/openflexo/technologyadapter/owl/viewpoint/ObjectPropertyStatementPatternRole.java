package org.openflexo.technologyadapter.owl.viewpoint;

import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.technologyadapter.owl.model.ObjectPropertyStatement;

public class ObjectPropertyStatementPatternRole extends StatementPatternRole {

	public ObjectPropertyStatementPatternRole(ViewPointBuilder builder) {
		super(builder);
	}

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

	public IFlexoOntologyStructuralProperty getObjectProperty() {
		if (getViewPoint() != null) {
			getViewPoint().loadWhenUnloaded();
		}
		if (getViewPoint().getViewpointOntology() != null) {
			return getViewPoint().getViewpointOntology().getObjectProperty(_getObjectPropertyURI());
		}
		return null;
	}

	public void setObjectProperty(IFlexoOntologyStructuralProperty p) {
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
