package org.openflexo.foundation.ontology;

import java.lang.reflect.Type;

import org.openflexo.antar.binding.CustomType;

public class IndividualOfClass implements CustomType {

	public static IndividualOfClass getIndividualOfClass(OntologyClass anOntologyClass) {
		if (anOntologyClass == null) {
			return null;
		}
		if (anOntologyClass.getOntologyLibrary() != null) {
			if (anOntologyClass.getOntologyLibrary().individualsOfClass.get(anOntologyClass) != null) {
				return anOntologyClass.getOntologyLibrary().individualsOfClass.get(anOntologyClass);
			} else {
				IndividualOfClass returned = new IndividualOfClass(anOntologyClass);
				anOntologyClass.getOntologyLibrary().individualsOfClass.put(anOntologyClass, returned);
				return returned;
			}
		}
		return null;
	}

	private OntologyClass ontologyClass;

	private IndividualOfClass(OntologyClass anOntologyClass) {
		this.ontologyClass = anOntologyClass;
	}

	public OntologyClass getOntologyClass() {
		return ontologyClass;
	}

	@Override
	public Class getBaseClass() {
		return OntologyIndividual.class;
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

}
