package org.openflexo.foundation.viewpoint;

import java.lang.reflect.Type;

import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.SubClassOfClass;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.view.ConceptActorReference;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.viewpoint.FMLRepresentationContext.FMLRepresentationOutput;


@ModelEntity(isAbstract = true)
@ImplementationClass(ClassPatternRole.ClassPatternRoleImpl.class)
public abstract interface ClassPatternRole<C extends IFlexoOntologyClass> extends OntologicObjectPatternRole<IFlexoOntologyClass>{

@PropertyIdentifier(type=String.class)
public static final String CONCEPT_URI_KEY = "conceptURI";

@Getter(value=CONCEPT_URI_KEY)
@XMLAttribute(xmlTag="ontologicType")
public String _getConceptURI();

@Setter(CONCEPT_URI_KEY)
public void _setConceptURI(String conceptURI);


public static abstract  abstract class ClassPatternRole<CImpl extends IFlexoOntologyClass> extends OntologicObjectPatternRole<IFlexoOntologyClass>Impl implements ClassPatternRole<C
{

	public ClassPatternRoleImpl() {
		super();
	}

	@Override
	public String getFMLRepresentation(FMLRepresentationContext context) {
		FMLRepresentationOutput out = new FMLRepresentationOutput(context);
		out.append("PatternRole " + getName() + " as Class conformTo " + getPreciseType() + " from " + "\""
				+ getModelSlot().getMetaModelURI() + "\"" + " ;", context);
		return out.toString();
	}

	@Override
	public Type getType() {
		if (getOntologicType() == null) {
			return IFlexoOntologyClass.class;
		}
		return SubClassOfClass.getSubClassOfClass(getOntologicType());
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
		return getVirtualModel().getOntologyClass(_getConceptURI());
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
		public ClassPatternRoleImplMustDefineAValidConceptClass() {
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
	public ConceptActorReference<IFlexoOntologyClass> makeActorReference(IFlexoOntologyClass object, EditionPatternInstance epi) {
		return new ConceptActorReference<IFlexoOntologyClass>(object, this, epi);
	}
}
}
