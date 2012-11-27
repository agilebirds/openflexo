package org.openflexo.technologyadapter.emf;

import org.openflexo.foundation.technologyadapter.ModelSlotImpl;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.technologyadapter.emf.model.EMFMetaModel;
import org.openflexo.technologyadapter.emf.model.EMFModel;

/**
 * Implementation of the ModelSlot class for the EMF technology adapter
 * 
 * @author sylvain
 * 
 */
public class EMFModelSlot extends ModelSlotImpl<EMFModel, EMFMetaModel> {

	public EMFModelSlot(ViewPoint viewPoint) {
		super(viewPoint);
	}

	public EMFModelSlot(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public String getFullyQualifiedName() {
		return "DiagramModelSlot";
	}

	@Override
	public String getClassNameKey() {
		return "xsd_model_slot";
	}

	@Override
	public EMFTechnologyAdapter getTechnologyAdapter() {
		return TechnologyAdapter.getTechnologyAdapter(EMFTechnologyAdapter.class);
	}

}
