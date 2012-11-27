package org.openflexo.foundation.ontology;

import java.util.List;

// test
public interface IFlexoOntologyIndividual extends IFlexoOntologyConcept {

	public List<? extends IFlexoOntologyClass> getTypes();

	/**
	 * Add type to this individual
	 * 
	 * @param type
	 */
	public Object addType(IFlexoOntologyClass type);

}
