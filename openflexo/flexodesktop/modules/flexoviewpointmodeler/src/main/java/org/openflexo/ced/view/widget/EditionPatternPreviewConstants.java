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
package org.openflexo.ced.view.widget;

import java.awt.Color;
import java.awt.Font;

public interface EditionPatternPreviewConstants {

    public static final Font DEFAULT_FONT = new Font("SansSerif", Font.BOLD, 11);
	public static final double WIDTH = 400;
	public static final double HEIGHT = 130;
	public static final double DEFAULT_SHAPE_WIDTH = 80;
	public static final double DEFAULT_SHAPE_HEIGHT = 50;
	
    public static final Color BACKGROUND_COLOR = new Color(255,255,225);
    public static final Color DEFAULT_SHAPE_TEXT_COLOR = Color.BLACK;
    public static final Color DEFAULT_SHAPE_BACKGROUND_COLOR = Color.LIGHT_GRAY;
    public static final Color SELECTED_COLOR = new Color(181,213,255);
    public static final Color FOCUSED_COLOR = new Color(237,243,254);
    
}
