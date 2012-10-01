package org.openflexo.foundation.viewpoint;

import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyIndividual;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.toolbox.StringUtils;

public class IndividualPatternRole extends OntologicObjectPatternRole {

	public IndividualPatternRole(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public PatternRoleType getType() {
		return PatternRoleType.Individual;
	}

	@Override
	public String getPreciseType() {
		if (getOntologicType() != null) {
			return getOntologicType().getName();
		}
		return "";
	}

	@Override
	public Class<?> getAccessedClass() {
		return OntologyIndividual.class;
	}

	private String conceptURI;

	public String _getConceptURI() {
		return conceptURI;
	}

	public void _setConceptURI(String conceptURI) {
		this.conceptURI = conceptURI;
	}

	public OntologyClass getOntologicType() {
		if (getViewPoint() != null) {
			getViewPoint().loadWhenUnloaded();
			if (getViewPoint().getViewpointOntology() != null) {
				return getViewPoint().getViewpointOntology().getClass(_getConceptURI());
			}
		}
		return null;
	}

	public void setOntologicType(OntologyClass ontologyClass) {
		conceptURI = ontologyClass != null ? ontologyClass.getURI() : null;
	}

	@Override
	public String getLanguageRepresentation() {
		// Voir du cote de GeneratorFormatter pour formatter tout ca
		StringBuffer sb = new StringBuffer();
		sb.append("PatternRole " + getName() + " as individual of " + getOntologicType().getName() + " in defaultModelSlot");
		sb.append(" {" + StringUtils.LINE_SEPARATOR);
		sb.append(StringUtils.LINE_SEPARATOR);
		/*for (EditionPattern ep : getEditionPatterns()) {
			sb.append(ep.getLanguageRepresentation());
			sb.append(StringUtils.LINE_SEPARATOR);
		}*/
		sb.append("}" + StringUtils.LINE_SEPARATOR);
		return sb.toString();
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
