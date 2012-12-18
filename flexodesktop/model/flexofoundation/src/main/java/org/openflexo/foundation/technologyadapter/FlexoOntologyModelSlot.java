package org.openflexo.foundation.technologyadapter;

import java.util.logging.Logger;

import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;

/**
 * Implementation of a ModelSlot in a technology conform to FlexoOntology layer
 * 
 */
public abstract class FlexoOntologyModelSlot<M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> extends ModelSlot<M, MM> {

	private static final Logger logger = Logger.getLogger(FlexoOntologyModelSlot.class.getPackage().getName());

	protected FlexoOntologyModelSlot(ViewPoint viewPoint, TechnologyAdapter<M, MM> technologyAdapter) {
		super(viewPoint, technologyAdapter);
	}

	protected FlexoOntologyModelSlot(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public abstract <PR extends PatternRole<?>> PR makePatternRole(Class<PR> patternRoleClass);

}
