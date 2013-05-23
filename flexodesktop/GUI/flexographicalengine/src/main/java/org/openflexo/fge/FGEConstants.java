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
package org.openflexo.fge;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.Toolkit;

public interface FGEConstants {

	// GPO: this flag cannot be turned to false so far because there are many issues with the invalidation of the Graphical hierarchy
	// Mainly: when initiating a view or when invalidating the gr hierarchy, all gr's are rebuilt which forces them to apply their
	// constraints
	// to avoid this, we should rework code to cache GR's (avoir rebuilding them) and don't apply constraints on creation of GR's, only on
	// creation of model objects
	public static final boolean APPLY_CONSTRAINTS_IMMEDIATELY = true;
	public static final boolean DEBUG = false;
	public static final int CONTROL_POINT_SIZE = 2;
	public static final int POINT_SIZE = 4;

	public static final Cursor MOVE_CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(FGEIconLibrary.CURSOR_MOVE_ICON.getImage(),
			new Point(16, 16), "CustomMove");
	public static final Integer INITIAL_LAYER = 32;
	public static final Integer DEFAULT_CONNECTOR_LAYER = 64;
	public static final Integer DEFAULT_SHAPE_LAYER = 1;
	public static final Integer DEFAULT_OBJECT_LAYER = 10;

	public static final int DEFAULT_ROUNDED_RECTANGLE_ARC_SIZE = 30; // pixels

	public static final int DEFAULT_RECT_POLYLIN_PIXEL_OVERLAP = 20; // overlap expressed in pixels relative to 1.0 scale
	public static final int DEFAULT_ROUNDED_RECT_POLYLIN_ARC_SIZE = 5; // pixels

	public static final int DEFAULT_BORDER_SIZE = 20; // pixels

	public static final double SELECTION_DISTANCE = 5.0; // < 5 pixels
	public static final double DND_DISTANCE = 20.0; // < 20 pixels

	public static final int DEFAULT_SHADOW_DARKNESS = 150;
	public static final int DEFAULT_SHADOW_DEEP = 2;
	public static final int DEFAULT_SHADOW_BLUR = 4;

	public static final Color DEFAULT_TEXT_COLOR = Color.BLACK;
	public static final Font DEFAULT_TEXT_FONT = new Font("Lucida Sans", Font.PLAIN, 11);

	public static final Font DEFAULT_SMALL_TEXT_FONT = new Font("Lucida Sans", Font.PLAIN, 9);

	public static final Color DEFAULT_BACKGROUND_COLOR = new Color(254, 247, 217);

	public static Stroke DASHED = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.0f, new float[] { 3.0f, 3.0f }, 1);

}
