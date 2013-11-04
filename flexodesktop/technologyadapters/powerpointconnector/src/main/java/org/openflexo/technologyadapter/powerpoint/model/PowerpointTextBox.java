package org.openflexo.technologyadapter.powerpoint.model;

import org.apache.poi.hslf.model.TextBox;
import org.openflexo.technologyadapter.powerpoint.PowerpointTechnologyAdapter;

/**
 * 
 * @author vincent, sylvain
 * 
 */
public class PowerpointTextBox extends PowerpointTextShape {

	public PowerpointTextBox(TextBox shape, PowerpointSlide powerpointSlide, PowerpointTechnologyAdapter adapter) {
		super(shape, powerpointSlide, adapter);
	}

}
