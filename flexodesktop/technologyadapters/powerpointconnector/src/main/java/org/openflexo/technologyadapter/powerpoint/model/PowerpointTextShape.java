package org.openflexo.technologyadapter.powerpoint.model;

import org.apache.poi.hslf.model.TextShape;
import org.openflexo.technologyadapter.powerpoint.PowerpointTechnologyAdapter;

/**
 * 
 * @author vincent, sylvain
 * 
 */
abstract public class PowerpointTextShape extends PowerpointSimpleShape {

	public PowerpointTextShape(TextShape shape, PowerpointSlide powerpointSlide, PowerpointTechnologyAdapter adapter) {
		super(shape, powerpointSlide, adapter);
	}

}
