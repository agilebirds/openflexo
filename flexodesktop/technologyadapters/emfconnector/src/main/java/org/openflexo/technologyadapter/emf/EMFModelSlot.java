package org.openflexo.technologyadapter.emf;

import org.openflexo.foundation.technologyadapter.ModelSlotImpl;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.model.EMFModel;

/**
 * Implementation of the ModelSlot class for the EMF technology adapter
 * 
 * @author sylvain
 * 
 */
public class EMFModelSlot extends ModelSlotImpl<EMFModel, EMFMetaModel> {

	public EMFModelSlot(ViewPoint viewPoint, EMFTechnologyAdapter adapter) {
		super(viewPoint, adapter);
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

}
