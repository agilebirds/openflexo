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
package org.openflexo.ie;

import java.awt.Color;
import java.awt.Font;

import org.openflexo.foundation.ie.util.FlexoConceptualColor;

/**
 * Constants used by the Interface Editor module.
 * 
 * @author sguerin
 */
public class IECst {

	public static final Color CONDITIONAL_COLOR = new Color(197, 217, 255);

	public static final String IEPALETTE_FOLDER_PATH = "Config/IEPalette";

	public static final String SMALL_BUTTONS_DIR = "Config/SmallButtons";

	public static final String BIG_BUTTONS_DIR = "Config/BigButtons";

	public static final String DEFAULT_PALETTE_TITLE = "gui_palette";

	public static final int DEFAULT_PALETTE_WIDTH = 290;
	public static final int DEFAULT_PALETTE_HEIGHT = 320;
	public static final int PALETTE_DOC_SPLIT_LOCATION = 400;

	public static final String DEFAULT_COMPONENT_LIBRARY_BROWSER_WINDOW_TITLE = "components_browser";

	public static final int DEFAULT_COMPONENT_LIBRARY_BROWSER_WINDOW_WIDTH = 300;

	public static final int DEFAULT_COMPONENT_LIBRARY_BROWSER_WINDOW_HEIGHT = 250;

	public static final int IE_WINDOW_WIDTH = 1024;

	public static final int IE_WINDOW_HEIGHT = 700;

	public static final int IE_TREE_VIEW_WIDTH = 200;

	protected static final Font LABEL_FONT = new Font("Verdana", Font.BOLD, 10);

	public static final Font WOSTRING_FONT = new Font("Verdana", Font.PLAIN, 10);

	public static final Font LABEL_BOLD_FONT = new Font("Verdana", Font.BOLD, 11);

	public static FlexoConceptualColor colorForRow(int n) {
		if (n % 2 == 0) {
			return FlexoConceptualColor.ODD_LINE_COLOR;
		} else {
			return FlexoConceptualColor.OTHER_LINE_COLOR;
		}

	}
}
