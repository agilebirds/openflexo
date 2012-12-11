package org.openflexo.foundation.technologyadapter;

import java.util.logging.Logger;

import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;

/**
 * Implementation of a ModelSlot in a technology conform to FlexoOntology layer
 * 
 */
public abstract class FlexoOntologyModelSlotImpl<M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> extends ModelSlotImpl<M, MM>
		implements FlexoOntologyModelSlot<M, MM> {

	private static final Logger logger = Logger.getLogger(FlexoOntologyModelSlotImpl.class.getPackage().getName());

	protected FlexoOntologyModelSlotImpl(ViewPoint viewPoint, TechnologyAdapter<M, MM> technologyAdapter) {
		super(viewPoint, technologyAdapter);
	}

	protected FlexoOntologyModelSlotImpl(ViewPointBuilder builder) {
		super(builder);
	}

}
