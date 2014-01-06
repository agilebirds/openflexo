package org.openflexo.technologyadapter.powerpoint.model;

import org.apache.poi.hslf.model.AutoShape;
import org.openflexo.technologyadapter.powerpoint.PowerpointTechnologyAdapter;

/**
 * 
 * @author vincent, sylvain
 * 
 */
public class PowerpointAutoShape extends PowerpointTextShape {

	public PowerpointAutoShape(AutoShape autoShape, PowerpointSlide powerpointSlide, PowerpointTechnologyAdapter adapter) {
		super(autoShape, powerpointSlide, adapter);
	}

}
