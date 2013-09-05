package org.openflexo.foundation.ontology;

import java.lang.reflect.Type;

import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.viewpoint.TechnologySpecificCustomType;

public class SubClassOfClass implements TechnologySpecificCustomType {

	public static SubClassOfClass getSubClassOfClass(IFlexoOntologyClass anOntologyClass) {
		if (anOntologyClass == null) {
			return null;
		}
		return anOntologyClass.getTechnologyAdapter().getTechnologyContextManager().getSubClassOfClass(anOntologyClass);
	}

	private IFlexoOntologyClass ontologyClass;

	public SubClassOfClass(IFlexoOntologyClass anOntologyClass) {
		this.ontologyClass = anOntologyClass;
	}

	public IFlexoOntologyClass getOntologyClass() {
		return ontologyClass;
	}

	@Override
	public Class getBaseClass() {
		return IFlexoOntologyClass.class;
	}

	@Override
	public boolean isTypeAssignableFrom(Type aType, boolean permissive) {
		// System.out.println("isTypeAssignableFrom " + aType + " (i am a " + this + ")");
		if (aType instanceof SubClassOfClass) {
			return ontologyClass.isSuperConceptOf(((SubClassOfClass) aType).getOntologyClass());
		}
		return false;
	}

	@Override
	public String simpleRepresentation() {
		return "Class" + ":" + ontologyClass.getName();
	}

	@Override
	public String fullQualifiedRepresentation() {
		return "Class" + ":" + ontologyClass.getURI();
	}

	@Override
	public TechnologyAdapter getTechnologyAdapter() {
		if (getOntologyClass() != null) {
			return getOntologyClass().getTechnologyAdapter();
		}
		return null;
	}

}
