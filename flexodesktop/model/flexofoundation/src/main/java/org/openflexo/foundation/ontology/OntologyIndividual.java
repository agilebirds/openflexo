package org.openflexo.foundation.ontology;

import java.util.List;

public interface OntologyIndividual extends OntologyObject {

	public List<? extends OntologyClass> getTypes();

	/**
	 * Add type to this individual
	 * 
	 * @param type
	 */
	public Object addType(OntologyClass type);

}
