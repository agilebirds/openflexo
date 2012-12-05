package org.openflexo.foundation.viewpoint;

import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.view.ConceptActorReference;
import org.openflexo.foundation.view.EditionPatternReference;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;

public class ClassPatternRole extends OntologicObjectPatternRole<OntologyClass> {

	public ClassPatternRole(ViewPointBuilder builder) {
		super(builder);
	}

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
	public Class<OntologyClass> getAccessedClass() {
		return IFlexoOntologyClass.class;
	}

	private String conceptURI;

	public String _getConceptURI() {
		return conceptURI;
	}

	public void _setConceptURI(String conceptURI) {
		this.conceptURI = conceptURI;
	}

	public IFlexoOntologyClass getOntologicType() {
		if (getViewPoint() != null) {
			getViewPoint().loadWhenUnloaded();
		}
		return getViewPoint().getOntologyClass(_getConceptURI());
	}

	public void setOntologicType(IFlexoOntologyClass ontologyClass) {
		conceptURI = ontologyClass != null ? ontologyClass.getURI() : null;
	}

	@Override
	public boolean defaultBehaviourIsToBeDeleted() {
		return false;
	}

	public static class ClassPatternRoleMustDefineAValidConceptClass extends
			ValidationRule<ClassPatternRoleMustDefineAValidConceptClass, ClassPatternRole> {
		public ClassPatternRoleMustDefineAValidConceptClass() {
			super(ClassPatternRole.class, "pattern_role_must_define_a_valid_concept_class");
		}

		@Override
		public ValidationIssue<ClassPatternRoleMustDefineAValidConceptClass, ClassPatternRole> applyValidation(ClassPatternRole patternRole) {
			if (patternRole.getOntologicType() == null) {
				return new ValidationError<ClassPatternRoleMustDefineAValidConceptClass, ClassPatternRole>(this, patternRole,
						"pattern_role_does_not_define_any_concept_class");
			}
			return null;
		}
	}

	@Override
	public ConceptActorReference<OntologyClass> makeActorReference(OntologyClass object, EditionPatternReference epRef) {
		return new ConceptActorReference<OntologyClass>(object, this, epRef);
	}
}
