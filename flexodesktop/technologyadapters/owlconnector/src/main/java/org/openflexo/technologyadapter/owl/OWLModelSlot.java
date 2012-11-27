package org.openflexo.technologyadapter.owl;

import org.openflexo.foundation.technologyadapter.ModelSlotImpl;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.technologyadapter.owl.model.OWLMetaModel;
import org.openflexo.technologyadapter.owl.model.OWLModel;

/**
 * <p>
 * Implementation of the ModelSlot class for the OWL technology adapter
 * 
 * @author Luka Le Roux
 * 
 */
public class OWLModelSlot extends ModelSlotImpl<OWLModel, OWLMetaModel> {

	public OWLModelSlot(ViewPoint viewPoint) {
		super(viewPoint);
	}

	public OWLModelSlot(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public OWLModel createEmptyModel(View view) {
		// TODO this is a stub
		return OWLModel.createNewOWLModel(view.getProject());
	}

	@Override
	public String getFullyQualifiedName() {
		return "OWLModelSlot";
	}

	@Override
	public String getClassNameKey() {
		return "owl_model_slot";
	}

	@Override
	public OWLTechnologyAdapter getTechnologyAdapter() {
		return TechnologyAdapter.getTechnologyAdapter(OWLTechnologyAdapter.class);
	}

}
