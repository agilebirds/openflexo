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
package org.openflexo.dm.view.erdiagram;

import java.awt.Color;
import java.awt.Font;

public interface ERDiagramConstants {

	public static final int ENTITY_BORDER = 10;
	public static final int PROPERTY_BORDER = 5;
	
	public static final double WIDTH = 200;
	public static final double PROPERTY_HEIGHT = 17;
	public static final double HEADER_HEIGHT = 30;
	
    public static final Font ENTITY_FONT = new Font("SansSerif", Font.BOLD, 11);
    public static final Font ATTRIBUTE_FONT = new Font("SansSerif", Font.PLAIN, 10);
    public static final Font RELATIONSHIP_FONT = new Font("SansSerif", Font.ITALIC, 10);

    public static final Color SELECTED_COLOR = new Color(181,213,255);
    public static final Color FOCUSED_COLOR = new Color(237,243,254);
}
