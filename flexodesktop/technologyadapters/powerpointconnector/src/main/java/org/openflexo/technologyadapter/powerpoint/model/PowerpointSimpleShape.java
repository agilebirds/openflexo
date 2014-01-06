package org.openflexo.technologyadapter.powerpoint.model;

import org.apache.poi.hslf.model.SimpleShape;
import org.openflexo.technologyadapter.powerpoint.PowerpointTechnologyAdapter;

/**
 * 
 * @author vincent, sylvain
 * 
 */
abstract public class PowerpointSimpleShape extends PowerpointShape {

	public PowerpointSimpleShape(SimpleShape shape, PowerpointSlide powerpointSlide, PowerpointTechnologyAdapter adapter) {
		super(shape, powerpointSlide, adapter);
	}

}
