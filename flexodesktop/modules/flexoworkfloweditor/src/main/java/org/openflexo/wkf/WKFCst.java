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
package org.openflexo.wkf;

import java.awt.Color;
import java.awt.Font;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import org.openflexo.foundation.wkf.FlexoLevel;
import org.openflexo.toolbox.FileResource;

/**
 * Constants used by the Workflow Editor module.
 * 
 * @author sguerin
 */
public class WKFCst {

	public static final boolean CUT_COPY_PASTE_ENABLED = true;

	public static final String PALETTE_FOLDER_PATH = "Config/FlexoPalette";

	public static final String DEFAULT_PALETTE_TITLE = "wkf_palette";

	public static final int DEFAULT_PALETTE_WIDTH = 270;

	public static final int DEFAULT_PALETTE_HEIGHT = 235;

	public static final int PALETTE_DOC_SPLIT_LOCATION = 300;

	public static final String DEFAULT_WORKFLOW_BROWSER_WINDOW_TITLE = "workflow_browser";

	public static final int DEFAULT_WORKFLOW_BROWSER_WINDOW_WIDTH = 300;

	public static final int DEFAULT_WORKFLOW_BROWSER_WINDOW_HEIGHT = 250;

	public static final String DEFAULT_PROCESS_BROWSER_WINDOW_TITLE = "process_browser";

	public static final int DEFAULT_PROCESS_BROWSER_WINDOW_WIDTH = 300;

	public static final int DEFAULT_PROCESS_BROWSER_WINDOW_HEIGHT = 250;

	public static final int DEFAULT_MAINFRAME_WIDTH = 1024;

	public static final int DEFAULT_MAINFRAME_HEIGHT = 700;

	public static final int DEFAULT_EXTERNAL_VIEW_WIDTH = 600;

	public static final int DEFAULT_EXTERNAL_VIEW_HEIGHT = 450;

	public static final int DEFAULT_PROCESSVIEW_WIDTH = 2000;

	public static final int DEFAULT_PROCESSVIEW_HEIGHT = 2000;

	public static final Color level0Color = new Color(123, 125, 181);

	public static final Color level1Color = new Color(107, 162, 132);

	public static final Color level2Color = new Color(206, 125, 123);

	public static final Color level0PanelColor = new Color(239, 247, 239);

	public static final Color level1PanelColor = new Color(247, 231, 231);

	public static final Color level2PanelColor = new Color(231, 239, 247);

	public static final Border level0Border = BorderFactory.createLineBorder(level0Color, 1);

	public static final Border level1Border = BorderFactory.createLineBorder(level1Color, 1);

	public static final Border level2Border = BorderFactory.createLineBorder(level2Color, 1);

	public static final Color EDGE_COLOR = Color.BLACK;

	public static final Color NODE_BORDER_COLOR = Color.DARK_GRAY;

	public static Color getOpenNodeColorForNodeLevel(FlexoLevel level) {
		if (level == FlexoLevel.ACTIVITY) {
			return level0Color;
		} else if (level == FlexoLevel.OPERATION) {
			return level1Color;
		} else if (level == FlexoLevel.ACTION) {
			return level2Color;
		} else {
			return null;
		}
	}

	public static Color getOpenNodePanelColorForNodeLevel(FlexoLevel level) {
		if (level == FlexoLevel.ACTIVITY) {
			return level0PanelColor;
		} else if (level == FlexoLevel.OPERATION) {
			return level1PanelColor;
		} else if (level == FlexoLevel.ACTION) {
			return level2PanelColor;
		} else {
			return null;
		}
	}

	public static Border getOpenNodeBorderForNodeLevel(FlexoLevel level) {
		if (level == FlexoLevel.ACTIVITY) {
			return level0Border;
		} else if (level == FlexoLevel.OPERATION) {
			return level1Border;
		} else if (level == FlexoLevel.ACTION) {
			return level2Border;
		} else {
			return null;
		}
	}

	public static final String WKFInvaders = "WKFInvaders";

	public static final File JAVAHELP_EXT_PLUG_IN = new FileResource("lib/javahelp-ext2.jar");

	public static final String JAVAHELP_EXT_PLUG_IN_CLASS_NAME = "org.openflexo.wkf.view.WKFDocumentationView2";

	public static final Font DEFAULT_ACTIVITY_NODE_LABEL_FONT = new Font("SansSerif", Font.PLAIN, 11);
	public static final Font DEFAULT_OPERATION_NODE_LABEL_FONT = new Font("SansSerif", Font.PLAIN, 10);
	public static final Font DEFAULT_ACTION_NODE_LABEL_FONT = new Font("SansSerif", Font.PLAIN, 10);
	public static final Font DEFAULT_EVENT_NODE_LABEL_FONT = new Font("SansSerif", Font.PLAIN, 10);
	public static final Font DEFAULT_ROLE_LABEL_FONT = new Font("SansSerif", Font.ITALIC, 10);
	public static final Font DEFAULT_COMPONENT_LABEL_FONT = new Font("Lucida Sans Typewriter", Font.PLAIN, 10);

	public static final Color OK = new Color(0, 191, 0);

	public static final Font DEFAULT_EDGE_LABEL_FONT = new Font("Lucida Sans Typewriter", Font.PLAIN, 10);

	public static final Font DEFAULT_ARTEFACT_LABEL_FONT = new Font("Lucida Sans Typewriter", Font.PLAIN, 10);;
}
