package org.openflexo.technologyadapter.powerpoint.model;

import org.apache.poi.hslf.model.Picture;
import org.openflexo.technologyadapter.powerpoint.PowerpointTechnologyAdapter;

/**
 * 
 * @author vincent, sylvain
 * 
 */
public class PowerpointPicture extends PowerpointSimpleShape {

	public PowerpointPicture(Picture shape, PowerpointSlide powerpointSlide, PowerpointTechnologyAdapter adapter) {
		super(shape, powerpointSlide, adapter);
	}

}
