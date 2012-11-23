package org.openflexo.foundation.ontology.owl;

import org.openflexo.foundation.ontology.ProjectOWLOntology;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.viewpoint.AbstractModelSlot;
import org.openflexo.foundation.viewpoint.TechnologicalSpace;

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
	public ProjectOWLOntology createEmptyModel(View view) {
		// TODO this is a stub
		return ProjectOWLOntology.createNewProjectOntology(view.getProject());
	}

	@Override
	public String getFullyQualifiedName() {
		return "OWLModelSlot";
	}

	@Override
	public String getClassNameKey() {
		return "owl_model_slot";
	}

}
