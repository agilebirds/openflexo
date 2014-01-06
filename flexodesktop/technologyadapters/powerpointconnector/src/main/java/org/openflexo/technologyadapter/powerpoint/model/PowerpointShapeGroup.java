package org.openflexo.technologyadapter.powerpoint.model;

import org.apache.poi.hslf.model.ShapeGroup;
import org.openflexo.technologyadapter.powerpoint.PowerpointTechnologyAdapter;

/**
 * 
 * @author vincent, sylvain
 * 
 */
public class PowerpointShapeGroup extends PowerpointShape {

	public PowerpointShapeGroup(ShapeGroup shape, PowerpointSlide powerpointSlide, PowerpointTechnologyAdapter adapter) {
		super(shape, powerpointSlide, adapter);
	}

}
