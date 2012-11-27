package org.openflexo.technologyadapter.emf;

import org.openflexo.foundation.technologyadapter.ModelSlotImpl;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.view.View;
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

	/**
	 * Instantiate and returns an empty model to be used as model for this slot.
	 * 
	 * @param view
	 * @param metaModel
	 * 
	 * @return a newly created model conform to supplied meta model
	 */
	@Override
	public EMFModel createEmptyModel(View view, EMFMetaModel metaModel) {
		return getTechnologyAdapter().createNewModel(view.getProject(), metaModel);
	}

	@Override
	public String getFullyQualifiedName() {
		return "EMFModelSlot";
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
