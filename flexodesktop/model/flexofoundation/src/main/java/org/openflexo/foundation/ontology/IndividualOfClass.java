package org.openflexo.foundation.ontology;

import java.lang.reflect.Type;

import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.viewpoint.TechnologySpecificCustomType;

public class IndividualOfClass implements TechnologySpecificCustomType {

	public static IndividualOfClass getIndividualOfClass(IFlexoOntologyClass anOntologyClass) {
		if (anOntologyClass == null) {
			return null;
		}
		return anOntologyClass.getTechnologyAdapter().getTechnologyContextManager().getIndividualOfClass(anOntologyClass);
	}

	private IFlexoOntologyClass ontologyClass;

	public IndividualOfClass(IFlexoOntologyClass anOntologyClass) {
		this.ontologyClass = anOntologyClass;
	}

	public IFlexoOntologyClass getOntologyClass() {
		return ontologyClass;
	}

	@Override
	public Class getBaseClass() {
		return IFlexoOntologyIndividual.class;
	}

	@Override
	public boolean isTypeAssignableFrom(Type aType, boolean permissive) {
		// System.out.println("isTypeAssignableFrom " + aType + " (i am a " + this + ")");
		if (aType instanceof IndividualOfClass) {
			return ontologyClass.isSuperConceptOf(((IndividualOfClass) aType).getOntologyClass());
		}
		return false;
	}

	@Override
	public String simpleRepresentation() {
		return "Individual" + ":" + ontologyClass.getName();
	}

	@Override
	public String fullQualifiedRepresentation() {
		return "Individual" + ":" + ontologyClass.getURI();
	}

	@Override
	public String toString() {
		return simpleRepresentation();
	}

	@Override
	public TechnologyAdapter getTechnologyAdapter() {
		if (getOntologyClass() != null) {
			return getOntologyClass().getTechnologyAdapter();
		}
		return null;
	}
}
