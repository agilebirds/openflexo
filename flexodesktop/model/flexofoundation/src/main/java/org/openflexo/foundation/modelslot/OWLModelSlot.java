package org.openflexo.foundation.modelslot;

import org.openflexo.foundation.ontology.ProjectOWLOntology;
import org.openflexo.foundation.ontology.owl.OWLOntology;

/**
 * <p>
 * Implementation of the ModelSlot class for the OWL technoligical space.
 * 
 * @author Luka Le Roux
 * 
 */
public class OWLModelSlot extends AbstractModelSlot<OWLOntology> {

	@Override
	public TechnologicalSpace getTechnologicalSpace() {
		return TechnologicalSpace.OWL;
	}

	@Override
	public ProjectOWLOntology createEmptyModel() {
		// TODO Auto-generated method stub
		return null;
	}

}
