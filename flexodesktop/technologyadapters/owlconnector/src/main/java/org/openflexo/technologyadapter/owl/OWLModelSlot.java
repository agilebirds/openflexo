package org.openflexo.technologyadapter.owl;

import org.openflexo.foundation.technologyadapter.ModelSlotImpl;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.technologyadapter.owl.model.OWLOntology;

/**
 * <p>
 * Implementation of the ModelSlot class for the OWL technology adapter
 * 
 * @author Luka Le Roux
 * 
 */
public class OWLModelSlot extends ModelSlotImpl<OWLOntology, OWLOntology> {

	public OWLModelSlot(ViewPoint viewPoint, OWLTechnologyAdapter adapter) {
		super(viewPoint, adapter);
	}

	public OWLModelSlot(ViewPointBuilder builder) {
		super(builder);
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
