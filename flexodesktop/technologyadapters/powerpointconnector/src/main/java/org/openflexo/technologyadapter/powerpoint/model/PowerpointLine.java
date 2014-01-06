package org.openflexo.technologyadapter.powerpoint.model;

import org.apache.poi.hslf.model.Line;
import org.openflexo.technologyadapter.powerpoint.PowerpointTechnologyAdapter;

/**
 * 
 * @author vincent, sylvain
 * 
 */
public class PowerpointLine extends PowerpointSimpleShape {

	public PowerpointLine(Line line, PowerpointSlide powerpointSlide, PowerpointTechnologyAdapter adapter) {
		super(line, powerpointSlide, adapter);
	}

}
