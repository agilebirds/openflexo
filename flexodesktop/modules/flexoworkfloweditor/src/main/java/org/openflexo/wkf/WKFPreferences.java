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

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fge.connectors.RectPolylinConnectorSpecification.RectPolylinAdjustability;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.Module;
import org.openflexo.prefs.ModulePreferences;
import org.openflexo.swing.FlexoFont;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.wkf.processeditor.gr.EdgeGR.EdgeRepresentation;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public final class WKFPreferences extends ModulePreferences {

	private static final Logger logger = Logger.getLogger(WKFPreferences.class.getPackage().getName());

	private static final Class<WKFPreferences> WKF_PREFERENCES = WKFPreferences.class;

	public static final String ALIGN_ON_GRID_KEY = "newAlignOnGrid";

	public static final String SHOW_GRID = "showGrid";

	public static final String SHOW_WO_NAME_KEY = "showWONameInWKF";

	public static final String SHOW_MESSAGES_NAME_KEY = "showMessagesInWKF";

	public static final String GRID_SIZE_KEY = "newGridSize";

	public static final String SHOW_SHADOWS = "showShadows";

	public static final String USE_TRANSPARENCY = "useTransparency";

	public static final String SHOW_LEAN_TAB = "showLeanTab";

	public static final String USE_SIMPLE_EVENT_PALETTE = "useSimpleEventPalette";

	public static final String SHOW_ALERT_WHEN_DROPPING_INCORRECT = "showAlertWhenDroppingIsIncorrect";

	public static final String SCREENSHOT_QUALITY = "screenshotQuality";

	/*
	 * public static final String ACTIVITY_CONNECTOR = "activityConnector"; public static final String OPERATION_CONNECTOR =
	 * "operationConnector"; public static final String ACTION_CONNECTOR = "actionConnector";
	 */

	public static final String CONNECTOR_REPRESENTATION = "connectorRepresentation";
	public static final String CONNECTOR_ADJUSTABILITY = "connectorAdjustability";

	public static final String ACTIVITY_NODE_FONT_KEY = "activityNodeFont";

	public static final String OPERATION_NODE_FONT_KEY = "operationNodeFont";

	public static final String ACTION_NODE_FONT_KEY = "actionNodeFont";

	public static final String COMPONENT_FONT_KEY = "componentFont";

	public static final String EVENT_NODE_FONT_KEY = "eventNodeFont";

	public static final String ARTEFACT_FONT_KEY = "artefactFont";

	public static final String EDGE_FONT_KEY = "edgeFont";

	public static final String ROLE_FONT_KEY = "roleFont";

	public static void init() {
		getPreferences();
	}

	public static WKFPreferences getPreferences() {
		return preferences(WKF_PREFERENCES);
	}

	public static void restoreDefault() {
		setShowWONameInWKF(Boolean.TRUE);
		setShowMessagesInWKF(Boolean.TRUE);
		setAlignOnGrid(Boolean.FALSE);
		setGridSize(Integer.valueOf(10));
		setShowShadows(true);
		setUseTransparency(true);
		setActivityNodeFont(new FlexoFont(WKFCst.DEFAULT_ACTIVITY_NODE_LABEL_FONT));
		setOperationNodeFont(new FlexoFont(WKFCst.DEFAULT_OPERATION_NODE_LABEL_FONT));
		setActionNodeFont(new FlexoFont(WKFCst.DEFAULT_ACTION_NODE_LABEL_FONT));
		setEventNodeFont(new FlexoFont(WKFCst.DEFAULT_EVENT_NODE_LABEL_FONT));
		setRoleFont(new FlexoFont(WKFCst.DEFAULT_ROLE_LABEL_FONT));
		setComponentFont(new FlexoFont(WKFCst.DEFAULT_COMPONENT_LABEL_FONT));
		setShowLeanTabs(false);
		setShowAlertWhenDroppingIsIncorrect(true);
		setConnectorRepresentation(EdgeRepresentation.RECT_POLYLIN);
	}

	public WKFPreferences() {
		super(Module.WKF_MODULE);
	}

	@Override
	public File getInspectorFile() {
		return new FileResource("Config/Preferences/WKFPrefs.inspector");
	}

	public static Boolean getShowWONameInWKF() {
		Boolean value = getPreferences().getBooleanProperty(SHOW_WO_NAME_KEY);
		if (value == null) {
			setShowWONameInWKF(Boolean.FALSE);
			return getShowWONameInWKF();
		}
		return value;
	}

	public static void setShowWONameInWKF(Boolean showWOName) {
		getPreferences().setBooleanProperty(SHOW_WO_NAME_KEY, showWOName);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setShowWONameInWKF() with " + showWOName);
		}
	}

	public static Boolean getShowMessagesInWKF() {
		Boolean value = getPreferences().getBooleanProperty(SHOW_MESSAGES_NAME_KEY);
		if (value == null) {
			setShowMessagesInWKF(Boolean.TRUE);
			return getShowMessagesInWKF();
		}
		return value;
	}

	public static void setShowMessagesInWKF(Boolean showMessages) {
		getPreferences().setBooleanProperty(SHOW_MESSAGES_NAME_KEY, showMessages);
	}

	public static Boolean getAlignOnGrid() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("getAlignOnGrid");
		}
		Boolean value = getPreferences().getBooleanProperty(ALIGN_ON_GRID_KEY);
		if (value == null) {
			setAlignOnGrid(Boolean.FALSE);
			return getAlignOnGrid();
		}
		return value;
	}

	public static void setAlignOnGrid(Boolean alignOnGrid) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("AlignOnGrid");
		}
		getPreferences().setBooleanProperty(ALIGN_ON_GRID_KEY, alignOnGrid);
	}

	public static boolean getShowGrid() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("getShowGrid");
		}
		Boolean value = getPreferences().getBooleanProperty(SHOW_GRID);
		if (value == null) {
			setShowGrid(Boolean.FALSE);
			return getShowGrid();
		}
		return value;
	}

	public static void setShowGrid(boolean showGrid) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setShowGrid");
		}
		getPreferences().setBooleanProperty(SHOW_GRID, showGrid);
	}

	public static Integer getGridSize() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("getGridSize");
		}
		Integer value = getPreferences().getIntegerProperty(GRID_SIZE_KEY);
		if (value == null) {
			setGridSize(15);
			return getGridSize();
		}
		return value;
	}

	public static void setGridSize(Integer gridSize) {
		if (gridSize == null) {
			return;
		}
		if (gridSize < 1) {
			gridSize = 1;
		}
		if (gridSize > 200) {
			gridSize = 200;
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setGridSize");
		}
		getPreferences().setIntegerProperty(GRID_SIZE_KEY, gridSize);
	}

	public static Boolean getShowShadows() {
		Boolean value = getPreferences().getBooleanProperty(SHOW_SHADOWS);
		if (value == null) {
			return Boolean.TRUE;
		}
		return value;
	}

	public static void setShowShadows(Boolean showShadows) {
		getPreferences().setBooleanProperty(SHOW_SHADOWS, showShadows);
	}

	public static Boolean getShowLeanTabs() {
		Boolean value = getPreferences().getBooleanProperty(SHOW_LEAN_TAB);
		if (value == null) {
			return Boolean.FALSE;
		}
		return value;
	}

	public static void setShowLeanTabs(Boolean showLeanTabs) {
		getPreferences().setBooleanProperty(SHOW_LEAN_TAB, showLeanTabs);
	}

	public static Boolean getUseSimpleEventPalette() {
		Boolean value = getPreferences().getBooleanProperty(USE_SIMPLE_EVENT_PALETTE);
		if (value == null) {
			return Boolean.TRUE;
		}
		return value;
	}

	public static void setUseSimpleEventPalette(Boolean showLeanTabs) {
		getPreferences().setBooleanProperty(USE_SIMPLE_EVENT_PALETTE, showLeanTabs);
	}

	public static Boolean getShowAlertWhenDroppingIsIncorrect() {
		Boolean value = getPreferences().getBooleanProperty(SHOW_ALERT_WHEN_DROPPING_INCORRECT);
		if (value == null) {
			return Boolean.TRUE;
		}
		return value;
	}

	public static void setShowAlertWhenDroppingIsIncorrect(Boolean showLeanTabs) {
		getPreferences().setBooleanProperty(SHOW_ALERT_WHEN_DROPPING_INCORRECT, showLeanTabs);
	}

	public static Boolean getUseTransparency() {
		Boolean value = getPreferences().getBooleanProperty(USE_TRANSPARENCY);
		if (value == null) {
			return Boolean.TRUE;
		}
		return value;
	}

	public static void setUseTransparency(Boolean useTransparency) {
		getPreferences().setBooleanProperty(USE_TRANSPARENCY, useTransparency);
	}

	public static FlexoFont getActivityNodeFont() {
		FlexoFont returned = FlexoFont.get(getPreferences().getProperty(ACTIVITY_NODE_FONT_KEY));
		if (returned == null) {
			setActivityNodeFont(new FlexoFont(WKFCst.DEFAULT_ACTIVITY_NODE_LABEL_FONT));
			return FlexoFont.get(getPreferences().getProperty(ACTIVITY_NODE_FONT_KEY));
		}
		return returned;
	}

	public static void setActivityNodeFont(FlexoFont font) {
		getPreferences().setProperty(ACTIVITY_NODE_FONT_KEY, font.toString());
	}

	public static FlexoFont getOperationNodeFont() {
		FlexoFont returned = FlexoFont.get(getPreferences().getProperty(OPERATION_NODE_FONT_KEY));
		if (returned == null) {
			setOperationNodeFont(new FlexoFont(WKFCst.DEFAULT_OPERATION_NODE_LABEL_FONT));
			return FlexoFont.get(getPreferences().getProperty(OPERATION_NODE_FONT_KEY));
		}
		return returned;
	}

	public static void setOperationNodeFont(FlexoFont font) {
		getPreferences().setProperty(OPERATION_NODE_FONT_KEY, font.toString());
	}

	public static FlexoFont getActionNodeFont() {
		FlexoFont returned = FlexoFont.get(getPreferences().getProperty(ACTION_NODE_FONT_KEY));
		if (returned == null) {
			setActionNodeFont(new FlexoFont(WKFCst.DEFAULT_ACTION_NODE_LABEL_FONT));
			return FlexoFont.get(getPreferences().getProperty(ACTION_NODE_FONT_KEY));
		}
		return returned;
	}

	public static void setActionNodeFont(FlexoFont font) {
		getPreferences().setProperty(ACTION_NODE_FONT_KEY, font.toString());
	}

	public static FlexoFont getEventNodeFont() {
		FlexoFont returned = FlexoFont.get(getPreferences().getProperty(EVENT_NODE_FONT_KEY));
		if (returned == null) {
			setEventNodeFont(new FlexoFont(WKFCst.DEFAULT_EVENT_NODE_LABEL_FONT));
			return FlexoFont.get(getPreferences().getProperty(EVENT_NODE_FONT_KEY));
		}
		return returned;
	}

	public static void setEventNodeFont(FlexoFont font) {
		getPreferences().setProperty(EVENT_NODE_FONT_KEY, font.toString());
	}

	public static FlexoFont getRoleFont() {
		FlexoFont returned = FlexoFont.get(getPreferences().getProperty(ROLE_FONT_KEY));
		if (returned == null) {
			setRoleFont(new FlexoFont(WKFCst.DEFAULT_ROLE_LABEL_FONT));
			return FlexoFont.get(getPreferences().getProperty(ROLE_FONT_KEY));
		}
		return returned;
	}

	public static void setRoleFont(FlexoFont font) {
		getPreferences().setProperty(ROLE_FONT_KEY, font.toString());
	}

	public static FlexoFont getEdgeFont() {
		FlexoFont returned = FlexoFont.get(getPreferences().getProperty(EDGE_FONT_KEY));
		if (returned == null) {
			setEdgeFont(new FlexoFont(WKFCst.DEFAULT_EDGE_LABEL_FONT));
			return FlexoFont.get(getPreferences().getProperty(EDGE_FONT_KEY));
		}
		return returned;
	}

	public static void setEdgeFont(FlexoFont font) {
		getPreferences().setProperty(EDGE_FONT_KEY, font.toString());
	}

	public static FlexoFont getArtefactFont() {
		FlexoFont returned = FlexoFont.get(getPreferences().getProperty(ARTEFACT_FONT_KEY));
		if (returned == null) {
			setArtefactFont(new FlexoFont(WKFCst.DEFAULT_ARTEFACT_LABEL_FONT));
			return FlexoFont.get(getPreferences().getProperty(ARTEFACT_FONT_KEY));
		}
		return returned;
	}

	public static void setArtefactFont(FlexoFont font) {
		getPreferences().setProperty(ARTEFACT_FONT_KEY, font.toString());
	}

	public static FlexoFont getComponentFont() {
		FlexoFont returned = FlexoFont.get(getPreferences().getProperty(COMPONENT_FONT_KEY));
		if (returned == null) {
			setComponentFont(new FlexoFont(WKFCst.DEFAULT_COMPONENT_LABEL_FONT));
			return FlexoFont.get(getPreferences().getProperty(COMPONENT_FONT_KEY));
		}
		return returned;
	}

	public static void setComponentFont(FlexoFont font) {
		getPreferences().setProperty(COMPONENT_FONT_KEY, font.toString());
	}

	/*
	 * public static EdgeRepresentation getActivityConnector() { String s = preferences(WKF_PREFERENCES).getProperty(ACTIVITY_CONNECTOR);
	 * EdgeRepresentation returned = null; if (s!=null) try { returned = EdgeRepresentation.valueOf(s); } catch (RuntimeException e) { if
	 * (logger.isLoggable(Level.WARNING)) logger.warning("Could not decode connector type named: "+s); } if (returned == null) {
	 * setActivityConnector(returned = EdgeRepresentation.RECT_POLYLIN); } return returned; }
	 * 
	 * public static void setActivityConnector(EdgeRepresentation type) { if (type!=null)
	 * preferences(WKF_PREFERENCES).setProperty(ACTIVITY_CONNECTOR, type.name()); }
	 * 
	 * public static EdgeRepresentation getOperationConnector() { String s = preferences(WKF_PREFERENCES).getProperty(OPERATION_CONNECTOR);
	 * EdgeRepresentation returned = null; if (s != null) try { returned = EdgeRepresentation.valueOf(s); } catch (RuntimeException e) { if
	 * (logger.isLoggable(Level.WARNING)) logger.warning("Could not decode connector type named: " + s); } if (returned == null) {
	 * setOperationConnector(returned = EdgeRepresentation.RECT_POLYLIN); } return returned; }
	 * 
	 * public static void setOperationConnector(EdgeRepresentation type) { if (type!=null)
	 * preferences(WKF_PREFERENCES).setProperty(OPERATION_CONNECTOR, type.name()); }
	 * 
	 * public static EdgeRepresentation getActionConnector() { String s = preferences(WKF_PREFERENCES).getProperty(ACTION_CONNECTOR);
	 * EdgeRepresentation returned = null; if (s != null) try { returned = EdgeRepresentation.valueOf(s); } catch (RuntimeException e) { if
	 * (logger.isLoggable(Level.WARNING)) logger.warning("Could not decode connector type named: " + s); } if (returned == null) {
	 * setActionConnector(returned = EdgeRepresentation.CURVE); } return returned; }
	 * 
	 * public static void setActionConnector(EdgeRepresentation type) { if (type!=null)
	 * preferences(WKF_PREFERENCES).setProperty(ACTION_CONNECTOR, type.name()); }
	 */

	public static EdgeRepresentation getConnectorRepresentation() {
		String s = getPreferences().getProperty(CONNECTOR_REPRESENTATION);
		EdgeRepresentation returned = null;
		if (s != null) {
			try {
				returned = EdgeRepresentation.valueOf(s);
			} catch (RuntimeException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not decode connector type named: " + s);
				}
			}
		}
		if (returned == null) {
			setConnectorRepresentation(returned = EdgeRepresentation.RECT_POLYLIN);
		}
		return returned;
	}

	public static String getActionConnectorRepresentationInfo() {
		return FlexoLocalization.localizedForKey("note_that_action_level_edges_are_always_curved");
	}

	public static String getPreferenceMessage() {
		return FlexoLocalization.localizedForKey("wkf_preferences_message");
	}

	public static void setConnectorRepresentation(EdgeRepresentation type) {
		if (type != null) {
			getPreferences().setProperty(CONNECTOR_REPRESENTATION, type.name());
		}
	}

	public static RectPolylinAdjustability getConnectorAdjustability() {
		String s = getPreferences().getProperty(CONNECTOR_ADJUSTABILITY);
		RectPolylinAdjustability returned = null;
		if (s != null) {
			try {
				returned = RectPolylinAdjustability.valueOf(s);
			} catch (RuntimeException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not decode connector adjustability named: " + s);
				}
			}
		}
		if (returned == null) {
			setConnectorAdjustability(returned = RectPolylinAdjustability.BASICALLY_ADJUSTABLE, false);
		}
		return returned;
	}

	public static void setConnectorAdjustability(RectPolylinAdjustability adjustability) {
		setConnectorAdjustability(adjustability, true);
	}

	public static void setConnectorAdjustability(RectPolylinAdjustability adjustability, boolean notify) {
		if (adjustability != null) {
			getPreferences().setProperty(CONNECTOR_ADJUSTABILITY, adjustability.name());
			if (notify) {
				FlexoController.notify(FlexoLocalization.localizedForKey("connector_adjustability_is_a_local_preference") + "\n"
						+ FlexoLocalization.localizedForKey("in_order_for_this_change_to_take_effect_you_must_restart_flexo"));
			}
		}
	}

	public static int getScreenshotQuality() {
		Integer limit = getPreferences().getIntegerProperty(SCREENSHOT_QUALITY);
		if (limit == null) {
			limit = 100;
		}
		return limit;
	}

	public static void setScreenshotQuality(int limit) {
		getPreferences().setIntegerProperty(SCREENSHOT_QUALITY, limit);
	}

}
