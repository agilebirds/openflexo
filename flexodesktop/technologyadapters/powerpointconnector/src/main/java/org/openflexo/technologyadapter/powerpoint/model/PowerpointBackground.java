package org.openflexo.technologyadapter.powerpoint.model;

import org.apache.poi.hslf.model.Background;
import org.openflexo.technologyadapter.powerpoint.PowerpointTechnologyAdapter;

/**
 * 
 * @author vincent, sylvain
 * 
 */
public class PowerpointBackground extends PowerpointShape {

	public PowerpointBackground(Background shape, PowerpointSlide powerpointSlide, PowerpointTechnologyAdapter adapter) {
		super(shape, powerpointSlide, adapter);
	}

}
