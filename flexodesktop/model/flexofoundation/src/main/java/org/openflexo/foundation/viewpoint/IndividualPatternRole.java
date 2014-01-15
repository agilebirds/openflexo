package org.openflexo.foundation.viewpoint;

import java.lang.reflect.Type;

import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyIndividual;
import org.openflexo.foundation.ontology.IndividualOfClass;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.view.ConceptActorReference;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.viewpoint.FMLRepresentationContext.FMLRepresentationOutput;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;

@ModelEntity(isAbstract = true)
@ImplementationClass(IndividualPatternRole.IndividualPatternRoleImpl.class)
public interface IndividualPatternRole<I extends IFlexoOntologyIndividual> extends OntologicObjectPatternRole<I> {

	@PropertyIdentifier(type = String.class)
	public static final String CONCEPT_URI_KEY = "conceptURI";

	@Getter(value = CONCEPT_URI_KEY)
	@XMLAttribute(xmlTag = "ontologicType")
	public String _getConceptURI();

	@Setter(CONCEPT_URI_KEY)
	public void _setConceptURI(String conceptURI);

	public IFlexoOntologyClass getOntologicType();

	public void setOntologicType(IFlexoOntologyClass ontologyClass);

	public static abstract class IndividualPatternRoleImpl<I extends IFlexoOntologyIndividual> extends OntologicObjectPatternRoleImpl<I>
			implements IndividualPatternRole<I> {

		public IndividualPatternRoleImpl() {
			super();
		}

		@Override
		public String getFMLRepresentation(FMLRepresentationContext context) {
			FMLRepresentationOutput out = new FMLRepresentationOutput(context);
			out.append("PatternRole " + getName() + " as Individual conformTo " + getPreciseType() + " from " + getModelSlot().getName()
					+ " ;", context);
			return out.toString();
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

		@Override
		public String _getConceptURI() {
			return conceptURI;
		}

		@Override
		public void _setConceptURI(String conceptURI) {
			this.conceptURI = conceptURI;
		}

		@Override
		public IFlexoOntologyClass getOntologicType() {
			if (getVirtualModel() != null) {
				return getVirtualModel().getOntologyClass(_getConceptURI());
			}
			return null;
		}

		@Override
		public void setOntologicType(IFlexoOntologyClass ontologyClass) {
			conceptURI = ontologyClass != null ? ontologyClass.getURI() : null;
		}

		@Override
		public ConceptActorReference<I> makeActorReference(I object, EditionPatternInstance epi) {
			return new ConceptActorReference<I>(object, this, epi);
		}

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

}
