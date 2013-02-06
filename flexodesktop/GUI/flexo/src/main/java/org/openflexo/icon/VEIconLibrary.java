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
package org.openflexo.icon;

import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.foundation.rm.FlexoViewResource;
import org.openflexo.foundation.rm.VirtualModelInstanceResource;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.ModelSlotInstance;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.ViewObject;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.view.diagram.model.Diagram;
import org.openflexo.foundation.view.diagram.model.DiagramConnector;
import org.openflexo.foundation.view.diagram.model.DiagramElement;
import org.openflexo.foundation.view.diagram.model.DiagramRootPane;
import org.openflexo.foundation.view.diagram.model.DiagramShape;
import org.openflexo.foundation.view.diagram.rm.DiagramResource;
import org.openflexo.toolbox.ImageIconResource;

/**
 * Utility class containing all icons used in context of VEModule
 * 
 * @author sylvain
 * 
 */
public class VEIconLibrary extends IconLibrary {

	private static final Logger logger = Logger.getLogger(VEIconLibrary.class.getPackage().getName());

	// Module icons
	public static final ImageIcon VE_SMALL_ICON = new ImageIconResource("Icons/VE/module-ve-16.png");
	public static final ImageIcon VE_MEDIUM_ICON = new ImageIconResource("Icons/VE/module-ve-32.png");
	public static final ImageIcon VE_MEDIUM_ICON_WITH_HOVER = new ImageIconResource("Icons/VE/module-ve-hover-32.png");
	public static final ImageIcon VE_BIG_ICON = new ImageIconResource("Icons/VE/module-ve-hover-64.png");

	// Perspective icons
	public static final ImageIcon VE_OP_ACTIVE_ICON = new ImageIconResource("Icons/VE/ontology-perspective.png");
	public static final ImageIcon VE_SP_ACTIVE_ICON = new ImageIconResource("Icons/VE/diagram-perspective.png");

	// Model icons
	public static final ImageIconResource VIEW_LIBRARY_ICON = new ImageIconResource("Icons/Model/VE/ViewLibrary.png");
	public static final ImageIconResource VIEW_ICON = new ImageIconResource("Icons/Model/VE/View.png");
	public static final ImageIconResource VIRTUAL_MODEL_INSTANCE_ICON = new ImageIconResource("Icons/Model/VE/VirtualModelInstance.png");
	public static final ImageIconResource EDITION_PATTERN_INSTANCE_ICON = new ImageIconResource("Icons/Model/VE/EditionPatternInstance.png");
	public static final ImageIconResource MODEL_SLOT_INSTANCE_ICON = new ImageIconResource("Icons/Model/VE/ModelSlotInstance.png");

	public static final ImageIconResource DIAGRAM_ICON = new ImageIconResource("Icons/Model/VE/Diagram.png");
	public static final ImageIconResource SHAPE_ICON = new ImageIconResource("Icons/Model/VE/DiagramShape.png");
	public static final ImageIconResource CONNECTOR_ICON = new ImageIconResource("Icons/Model/VE/DiagramConnector.png");

	public static final ImageIconResource UNKNOWN_ICON = new ImageIconResource("Icons/Model/VPM/UnknownIcon.gif");

	public static ImageIcon iconForObject(DiagramElement object) {
		if (object instanceof DiagramRootPane) {
			return VIEW_ICON;
		} else if (object instanceof DiagramConnector) {
			return CONNECTOR_ICON;
		} else if (object instanceof DiagramShape) {
			return SHAPE_ICON;
		}
		logger.warning("No icon for " + object.getClass());
		return UNKNOWN_ICON;
	}

	public static ImageIcon iconForObject(ViewObject object) {
		if (object instanceof DiagramElement) {
			return iconForObject((DiagramElement) object);
		} else if (object instanceof View) {
			return VIEW_ICON;
		} else if (object instanceof ModelSlotInstance) {
			return MODEL_SLOT_INSTANCE_ICON;
		} else if (object instanceof Diagram) {
			return DIAGRAM_ICON;
		} else if (object instanceof VirtualModelInstance) {
			return VIRTUAL_MODEL_INSTANCE_ICON;
		} else if (object instanceof EditionPatternInstance) {
			return EDITION_PATTERN_INSTANCE_ICON;
		}
		logger.warning("No icon for " + object.getClass());
		return UNKNOWN_ICON;
	}

	public static ImageIcon iconForObject(FlexoViewResource object) {
		return VIEW_ICON;
	}

	public static ImageIcon iconForObject(VirtualModelInstanceResource object) {
		if (object instanceof DiagramResource) {
			return DIAGRAM_ICON;
		}
		return VIRTUAL_MODEL_INSTANCE_ICON;
	}

}
