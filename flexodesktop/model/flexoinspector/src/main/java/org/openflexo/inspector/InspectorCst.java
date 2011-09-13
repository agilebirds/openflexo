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
package org.openflexo.inspector;

import java.awt.Color;

import org.openflexo.toolbox.ToolBox;


/**
 * Constants used by FlexoInspector project
 *
 * @author sguerin
 */
public class InspectorCst
{

    public static final int INSPECTOR_WINDOW_WIDTH = 375;

    public static final int INSPECTOR_WINDOW_HEIGHT = 400;

    public static final Color SELECTED_TEXT_COLOR = new Color(145, 170, 208);

    public static final Color SELECTED_LINES_PROPERTY_LIST_TABLE_VIEW_COLOR = new Color(181, 213, 255);

    public static final Color ODD_LINES_PROPERTY_LIST_TABLE_VIEW_COLOR = new Color(237, 243, 254);

    public static final Color NON_ODD_LINES_PROPERTY_LIST_TABLE_VIEW_COLOR = Color.WHITE;

    public static Color BACK_COLOR = ToolBox.getPLATFORM()==ToolBox.MACOS ? null : Color.WHITE;
}
