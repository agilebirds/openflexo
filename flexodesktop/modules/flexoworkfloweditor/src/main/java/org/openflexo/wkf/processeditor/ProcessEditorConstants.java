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
package org.openflexo.wkf.processeditor;

import java.awt.Color;

public interface ProcessEditorConstants {

	public static final double PROCESS_DEFAULT_WIDTH = 1000;
	public static final double PROCESS_DEFAULT_HEIGHT = 1000;

	public static final String BASIC_PROCESS_EDITOR = "bpe";

	public static final int DEFAULT_BEGIN_Y_OFFSET = 10;

	public static final String GRID_SIZE_IS_SET = "gridSizeHasBeenSet";

	static final int DEFAULT_ACTIVITY_WIDTH = 150;
	static final int DEFAULT_ACTIVITY_HEIGHT = 90;

	public static final Color PORT_REGISTRY_PG_COLOR = new Color(123, 125, 181);

	public static final Color ACTIVITY_PG_COLOR = Color.ORANGE;
	public static final Color ACTIVITY_PG_BACK_COLOR = new Color(255, 255, 204);

	public static final Color OPERATION_PG_COLOR = new Color(107, 162, 132);
	public static final Color OPERATION_PG_BACK_COLOR = new Color(218, 232, 225);

	public static final Color ACTION_PG_COLOR = new Color(206, 125, 123);
	public static final Color ACTION_PG_BACK_COLOR = new Color(242, 219, 219);

	// public static final Color ACTIVITY_GROUP_COLOR = new Color(157, 162, 132);
	// public static final Color ACTIVITY_GROUP_BACK_COLOR = new Color(238,242,225);

	// Note: you need at least 3 layers between the layer of a node and a petri graph layer that is on top of it.
	// Let's say your node is on layer 1, then a pre-condition will be on layer 2 and an incoming edge will be on layer 3, therefore you
	// need at least 3 layers between a node and a petri graph that is shown on top of it
	public static final int ACTIVITY_LAYER = 6;
	public static final int ACTIVITY_PG_LAYER = 9;
	public static final int EMBEDDED_ACTIVITY_LAYER = 12;
	public static final int OPERATION_PG_LAYER = 15;
	public static final int OPERATION_LAYER = 18;
	public static final int ACTION_PG_LAYER = 21;
	public static final int ACTION_LAYER = 24;

	/*public static final int ANNOTATION_LAYER = 5;
	public static final int BOUNDING_BOX_LAYER=4;*/

	public static final int SELECTION_LAYER = 30;

	public static final double NODE_MINIMAL_WIDTH = 60;
	public static final double NODE_MINIMAL_HEIGHT = 35;

	public static final int CONTAINER_LABEL_HEIGHT = 12;

	public static final int REQUIRED_SPACE_ON_RIGHT_FOR_PALETTE = 25; // Think of palette
	public static final int REQUIRED_SPACE_ON_TOP_FOR_CLOSING_BOX = 30; // Think of closing box

	public static final int REQUIRED_SPACE_ON_RIGHT = 10;
	public static final int REQUIRED_SPACE_ON_TOP = 10;
	public static final int REQUIRED_SPACE_ON_LEFT = 10;
	public static final int REQUIRED_SPACE_ON_BOTTOM = 20;

	public static final int PORTMAP_REGISTERY_WIDTH = 30;
	public static final int PORTMAP_MARGIN = 3;

}
