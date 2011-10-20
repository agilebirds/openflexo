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
package org.openflexo;

/*
 * FlexoCst.java
 * Project WorkflowEditor
 *
 * Created by benoit on Mar 1, 2004
 */

/**
 * Constants used by the FLEXO application.
 *
 * @author benoit
 */

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.FlexoVersion;
import org.openflexo.toolbox.ToolBox;


public class FlexoCst extends ColorCst
{
	public static final String DLPM_WORKPACKAGE_ID = "1000023";

	public static final String BUILD_ID = new String(ApplicationVersion.BUILD_ID);

	public static final FlexoVersion BUSINESS_APPLICATION_VERSION = new FlexoVersion(ApplicationVersion.BUSINESS_APPLICATION_VERSION);

	public static final String BUSINESS_APPLICATION_VERSION_NAME = "OpenFlexo "
			+ BUSINESS_APPLICATION_VERSION;

	public static final String OUTPUT_FILES_ENCODING = "UTF-8";

	public static final int META_MASK = ToolBox.getPLATFORM()==ToolBox.MACOS ? InputEvent.META_MASK : InputEvent.CTRL_MASK;

	public static final int MULTI_SELECTION_MASK = ToolBox.getPLATFORM()==ToolBox.MACOS ? InputEvent.META_DOWN_MASK : InputEvent.CTRL_DOWN_MASK;

	public static final int DELETE_KEY_CODE = ToolBox.getPLATFORM()==ToolBox.MACOS? KeyEvent.VK_BACK_SPACE:KeyEvent.VK_DELETE;
	public static final int BACKSPACE_DELETE_KEY_CODE = ToolBox.getPLATFORM()==ToolBox.MACOS? KeyEvent.VK_DELETE:KeyEvent.VK_BACK_SPACE;

	public static final Font BIG_FONT = new Font("SansSerif", Font.PLAIN, 13);

	public static final Font TITLE_FONT = new Font("SansSerif", Font.PLAIN, 18);

	public static final Font NORMAL_FONT = new Font("SansSerif", Font.PLAIN, 11);

	public static final Font BOLD_FONT = new Font("SansSerif", Font.BOLD, 11);

	public static final Font MEDIUM_FONT = new Font("SansSerif", Font.PLAIN, 10);

	public static final Font SMALL_FONT = new Font("SansSerif", Font.PLAIN, 9);

	public static final Font CODE_FONT = new Font("Monospaced", Font.PLAIN, 10);

	public static final String DENALI_SUPPORT_EMAIL = "benoit.mangez@denali.be";

	public static URL cssUrl()
	{
		if (_cssURL == null) {
			try {
				_cssURL = new FileResource("Config/FlexoMasterStyle.css")
				.toURL();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return _cssURL;
	}

	private static URL _cssURL;

	public static final BasicStroke STROKE_BOLD = new BasicStroke(2.0f);

	public static final BasicStroke STROKE = new BasicStroke(1.0f);

	public static final BasicStroke DASHED_STROKE_BOLD = new BasicStroke(1.0f);

	public static final Border FOCUSED_BORDER = BorderFactory.createLineBorder(Color.RED, 1);

	public static final Border HIDDEN_BORDER = BorderFactory.createLineBorder(new Color(0f, 0f, 0f, 0f), 1);

	public static boolean MODEL_EVENT_LISTENER_DEBUG = false;

	public static Color DEFAULT_PROCESS_VIEW_BG_COLOR = Color.white;

	public static Color SELECTION_COLOR = Color.GREEN;

	public static Color WELCOME_BG_COLOR = Color.WHITE;

	public static final Color DARK_BLUE_FLEXO_COLOR = new Color(2,67,123);

	public static final Color OPEN_BLUE_COLOR = new Color(65, 91, 116);

	public static final Color WELCOME_FLEXO_COLOR = OPEN_BLUE_COLOR;

	public static final Color WELCOME_FLEXO_BG_LIST_COLOR = ToolBox.getPLATFORM()==ToolBox.WINDOWS?Color.WHITE:new Color(254,255,216);


	public static final int MINIMUM_BROWSER_VIEW_WIDTH = 200;
	public static final int MINIMUM_BROWSER_VIEW_HEIGHT = 0;
	public static final int PREFERRED_BROWSER_VIEW_WIDTH = 200;
	public static final int PREFERRED_BROWSER_VIEW_HEIGHT = 200;
	public static final int MINIMUM_BROWSER_CONTROL_PANEL_HEIGHT = 50;

	public static void switchColors(String colorSet)
	{
		if (colorSet.equals("Contento")) {
			oddLineColor = new Color(249, 246, 249);
			otherLineColor = new Color(231, 232, 234);
			flexoTextColor = new Color(29, 67, 130);
			flexoMainColor = new Color(145, 170, 208);
		} else if (colorSet.equals("Omniscio")) {
			oddLineColor = new Color(255, 255, 255);
			otherLineColor = new Color(253, 229, 200);
			flexoTextColor = new Color(0, 0, 0);
			flexoMainColor = new Color(249, 186, 109);
		} else {
			// flexoTextColor=new Color(74,119,50);
			// flexoMainColor=new Color(152,185,94);
			flexoTextColor = new Color(53, 85, 36);
			flexoMainColor = new Color(162, 185, 94);
			oddLineColor = new Color(244, 246, 235);
			otherLineColor = new Color(232, 237, 215);
		}
	}

	public static Color flexoTextColor = new Color(53, 85, 36);

	public static Color flexoMainColor = new Color(162, 185, 94);

	public static Color oddLineColor = new Color(244, 246, 235);

	public static Color otherLineColor = new Color(232, 237, 215);

	public static final int LOADING_PROGRESS_STEPS = 26;
}
