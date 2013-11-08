/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.technologyadapter.powerpoint.gui;

import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointShape;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointSlide;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointSlideshow;
import org.openflexo.toolbox.ImageIconResource;

public class PowerpointIconLibrary {

	private static final Logger logger = Logger.getLogger(PowerpointIconLibrary.class.getPackage().getName());

	public static final ImageIconResource POWERPOINT_TECHNOLOGY_BIG_ICON = new ImageIconResource("Icons/powerpoint_big.png");
	public static final ImageIconResource POWERPOINT_TECHNOLOGY_ICON = new ImageIconResource("Icons/powerpoint_small.png");

	public static ImageIcon iconForObject(Class<? extends TechnologyObject> objectClass) {
		if (PowerpointSlideshow.class.isAssignableFrom(objectClass)) {
			return POWERPOINT_TECHNOLOGY_ICON;
		} 
		else if (PowerpointShape.class.isAssignableFrom(objectClass)) {
			return POWERPOINT_TECHNOLOGY_ICON;
		} else if (PowerpointSlide.class.isAssignableFrom(objectClass)) {
			return POWERPOINT_TECHNOLOGY_ICON;
		} 
		logger.warning("No icon for " + objectClass);
		return null;
	}

}
