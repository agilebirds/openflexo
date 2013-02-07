package org.openflexo.foundation.technologyadapter;

import java.util.logging.Logger;

import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.VirtualModel.VirtualModelBuilder;

/**
 * Implementation of a ModelSlot in a technology conform to FlexoOntology layer
 * 
 */
public abstract class FlexoOntologyModelSlot<M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> extends ModelSlot<M, MM> {

	private static final Logger logger = Logger.getLogger(FlexoOntologyModelSlot.class.getPackage().getName());

	protected FlexoOntologyModelSlot(ViewPoint viewPoint, TechnologyAdapter<M, MM> technologyAdapter) {
		super(viewPoint, technologyAdapter);
	}

	protected FlexoOntologyModelSlot(VirtualModel<?> virtualModel, TechnologyAdapter<M, MM> technologyAdapter) {
		super(virtualModel, technologyAdapter);
	}

	protected FlexoOntologyModelSlot(VirtualModelBuilder builder) {
		super(builder);
	}

	public FlexoOntologyModelSlot(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public ModelSlotInstanceConfiguration<FlexoOntologyModelSlot<M, MM>> createConfiguration(CreateVirtualModelInstance action) {
		return new FlexoOntologyModelSlotInstanceConfiguration<FlexoOntologyModelSlot<M, MM>>(this, action);
	}
}
