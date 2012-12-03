package org.openflexo.technologyadapter.emf;

import org.openflexo.foundation.technologyadapter.ModelSlotImpl;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
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

	/**
	 * Constructor.
	 * 
	 * @param viewPoint
	 */
	public EMFModelSlot(ViewPoint viewPoint, EMFTechnologyAdapter adapter) {
		super(viewPoint, adapter);
	}

	/**
	 * Constructor.
	 * 
	 * @param builder
	 */
	public EMFModelSlot(ViewPointBuilder builder) {
		super(builder);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.viewpoint.ViewPointObject#getFullyQualifiedName()
	 */
	@Override
	public String getFullyQualifiedName() {
		return "DiagramModelSlot";
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.TemporaryFlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "emf_model_slot";
	}
}
