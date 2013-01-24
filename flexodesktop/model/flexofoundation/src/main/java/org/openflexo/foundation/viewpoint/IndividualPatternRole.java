package org.openflexo.foundation.viewpoint;

import java.lang.reflect.Type;

import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyIndividual;
import org.openflexo.foundation.ontology.IndividualOfClass;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.view.ConceptActorReference;
import org.openflexo.foundation.view.EditionPatternReference;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;

public abstract class IndividualPatternRole<I extends IFlexoOntologyIndividual> extends OntologicObjectPatternRole<I> {

	public IndividualPatternRole(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public boolean defaultBehaviourIsToBeDeleted() {
		return true;
	}

	@Override
	public Type getType() {
		if (getOntologicType() == null) {
			return IFlexoOntologyIndividual.class;
		}
		return IndividualOfClass.getIndividualOfClass(getOntologicType());
	}

	@Override
	public String getPreciseType() {
		if (getOntologicType() != null) {
			return getOntologicType().getName();
		}
		return "";
	}

	private String conceptURI;

	public String _getConceptURI() {
		return conceptURI;
	}

	public void _setConceptURI(String conceptURI) {
		this.conceptURI = conceptURI;
	}

	public IFlexoOntologyClass getOntologicType() {
		if (getVirtualModel() != null) {
			return getVirtualModel().getOntologyClass(_getConceptURI());
		}
		return null;
	}

	public void setOntologicType(IFlexoOntologyClass ontologyClass) {
		conceptURI = ontologyClass != null ? ontologyClass.getURI() : null;
	}

	public static class IndividualPatternRoleMustDefineAValidConceptClass extends
			ValidationRule<IndividualPatternRoleMustDefineAValidConceptClass, IndividualPatternRole> {
		public IndividualPatternRoleMustDefineAValidConceptClass() {
			super(IndividualPatternRole.class, "pattern_role_must_define_a_valid_concept_class");
		}

		@Override
		public ValidationIssue<IndividualPatternRoleMustDefineAValidConceptClass, IndividualPatternRole> applyValidation(
				IndividualPatternRole patternRole) {
			if (patternRole.getOntologicType() == null) {
				return new ValidationError<IndividualPatternRoleMustDefineAValidConceptClass, IndividualPatternRole>(this, patternRole,
						"pattern_role_does_not_define_any_concept_class");
			}
			return null;
		}
	}

	@Override
	public ConceptActorReference<I> makeActorReference(I object, EditionPatternReference epRef) {
		return new ConceptActorReference<I>(object, this, epRef);
	}

}
