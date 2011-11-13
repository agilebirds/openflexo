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
package org.openflexo.foundation.wkf;

import java.awt.BasicStroke;
import java.awt.Stroke;

/**
 * @author sguerin
 * 
 *         TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class Constants {

	/**
	 * Default size in pixel for a FlexoNode It should be an odd number so that we get a proper centre
	 */
	public static final int FLEXONODE_SIZE = 49;

	/**
	 * A gap between the drawing area (including precondition) and the edge of the flexo node
	 */
	public static final int FLEXONODE_BORDER = 1;

	public static final int FLEXO_CONTROL_POINT_SIZE = 10;

	/**
	 * The distance from the edge of the node's circle to the centre of a precondition
	 */
	public static final int FLEXOPRECONDITION_OFFSET = 1;

	/**
	 * The radius of a precondition It should be an odd number so that we get a proper centre
	 */
	public static final int FLEXOPRECONDITION_RADIUS = 5;

	/**
	 * The radius of a precondition It should be an odd number so that we get a proper centre
	 */
	public static final int FLEXO_PORTMAP_RADIUS = 5;

	/**
	 * The distance from the flexnode drawing area (excluding precondition) and the edge of the flexo node
	 */
	public static final int FLEXNODE_INNER_BORDER = FLEXONODE_BORDER + FLEXOPRECONDITION_OFFSET + FLEXOPRECONDITION_RADIUS;

	/**
	 * The total size of a flexo precondition
	 */
	public static final int FLEXO_PRECONDITION_SIZE = FLEXOPRECONDITION_RADIUS * 2 + 1;// +
																						// FLEXONODE_BORDER*2
																						// + 1;

	/**
	 * The centre of a flexo precondition
	 */
	public static final int FLEXO_PRECONDITION_CENTRE = FLEXO_PRECONDITION_SIZE / 2;

	/**
	 * the x position of the bounding box of a circle in a precondition
	 */
	public static final int FLEXO_PRECONDITION_CIRCLE_X = 0;

	/**
	 * the y position of the bounding box of a circle in a precondition
	 */
	public static final int FLEXO_PRECONDITION_CIRCLE_Y = 0;

	/**
	 * The centre of a FLEXO node
	 */
	public static final int FLEXO_NODE_CENTRE = (FLEXONODE_SIZE / 2);

	/**
	 * the x position of the bounding box of a circle in a flexo node
	 */
	public static final int FLEXO_NODE_CIRCLE_X = FLEXNODE_INNER_BORDER + 1;

	/**
	 * the y position of the bounding box of a circle in a flexo node
	 */
	public static final int FLEXO_NODE_CIRCLE_Y = FLEXNODE_INNER_BORDER + 1;

	/**
	 * The radius of the outer flexonode circle
	 */
	public static final int FLEXONODE_OUTER_RADIUS = (FLEXONODE_SIZE / 2)
			- (FLEXOPRECONDITION_RADIUS + FLEXOPRECONDITION_OFFSET + FLEXONODE_BORDER);

	/**
	 * The radius of the inner flexonode circle
	 */
	public static final int FLEXONODE_INNER_RADIUS = FLEXONODE_OUTER_RADIUS / 2;

	public static final int ACCEPTABLE_POS_X_MIN_VALUE = -2000;

	public static final int ACCEPTABLE_POS_Y_MIN_VALUE = -2000;

	public static final int ACCEPTABLE_POS_X_MAX_VALUE = 2000;

	public static final int ACCEPTABLE_POS_Y_MAX_VALUE = 2000;

	public static final int ACCEPTABLE_WIDTH_MIN_VALUE = 0;

	public static final int ACCEPTABLE_HEIGHT_MIN_VALUE = 0;

	public static final int ACCEPTABLE_WIDTH_MAX_VALUE = 2000;

	public static final int ACCEPTABLE_HEIGHT_MAX_VALUE = 2000;

	public static final int ACTIVITY_ARC_WIDTH = 20;

	public static final int ACTIVITY_ARC_HEIGHT = 20;

	public static final int ACTIVITY_MARGIN = 9;

	public static final int ACTIVITY_PRECONDITIONS_MARGIN = 6;

	public static final int OPERATION_MARGIN = 9;

	public static final int OPERATION_PRECONDITIONS_MARGIN = 6;

	public static final int ACTION_FIXED_WIDTH = 30;

	public static final int ACTION_FIXED_HEIGHT = 30;

	public static final int BEGIN_NODE_FIXED_WIDTH = 30;

	public static final int BEGIN_NODE_FIXED_HEIGHT = 30;

	public static final int END_NODE_FIXED_WIDTH = 30;

	public static final int END_NODE_FIXED_HEIGHT = 30;

	public static final int COMPOUND_VIEW_BORDER_SIZE = 10;
	public static final int COMPOUND_VIEW_MINIMUM_WIDTH = 200;
	public static final int COMPOUND_VIEW_MINIMUM_HEIGHT = 100;

	public static final Stroke PLAIN_STROKE = new BasicStroke(1.0f);

	private static float[] dash = { 3.0f, 3.0f };

	public static final Stroke DASHED_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
}
