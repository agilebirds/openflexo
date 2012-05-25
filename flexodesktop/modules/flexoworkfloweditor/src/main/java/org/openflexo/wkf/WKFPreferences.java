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

import org.openflexo.fge.connectors.rpc.RectPolylinConnector.RectPolylinAdjustability;
import org.openflexo.foundation.utils.FlexoFont;
import org.openflexo.foundation.wkf.FlexoWorkflow.GraphicalProperties;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.Module;
import org.openflexo.prefs.ModulePreferences;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.wkf.controller.WKFController;
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

	protected static final String ALIGN_ON_GRID_KEY = "newAlignOnGrid";

	protected static final String SHOW_GRID = "showGrid";

	protected static final String SHOW_WO_NAME_KEY = "showWONameInWKF";

	protected static final String SHOW_MESSAGES_NAME_KEY = "showMessagesInWKF";

	protected static final String GRID_SIZE_KEY = "newGridSize";

	protected static final String SHOW_SHADOWS = "showShadows";

	protected static final String USE_TRANSPARENCY = "useTransparency";

	protected static final String SHOW_LEAN_TAB = "showLeanTab";

	protected static final String USE_SIMPLE_EVENT_PALETTE = "useSimpleEventPalette";

	protected static final String SHOW_ALERT_WHEN_DROPPING_INCORRECT = "showAlertWhenDroppingIsIncorrect";

	protected static final String SCREENSHOT_QUALITY = "screenshotQuality";

	/*
	 * protected static final String ACTIVITY_CONNECTOR = "activityConnector"; protected static final String OPERATION_CONNECTOR =
	 * "operationConnector"; protected static final String ACTION_CONNECTOR = "actionConnector";
	 */

	protected static final String CONNECTOR_REPRESENTATION = "connectorRepresentation";
	protected static final String CONNECTOR_ADJUSTABILITY = "connectorAdjustability";

	protected static final String ACTIVITY_NODE_FONT_KEY = "activityNodeFont";

	protected static final String OPERATION_NODE_FONT_KEY = "operationNodeFont";

	protected static final String ACTION_NODE_FONT_KEY = "actionNodeFont";

	protected static final String COMPONENT_FONT_KEY = "componentFont";

	protected static final String EVENT_NODE_FONT_KEY = "eventNodeFont";

	protected static final String ARTEFACT_FONT_KEY = "artefactFont";

	protected static final String EDGE_FONT_KEY = "edgeFont";

	protected static final String ROLE_FONT_KEY = "roleFont";

	private static WKFController _controller;

	public static void init(WKFController controller) {
		_controller = controller;
		preferences(WKF_PREFERENCES);
		for (GraphicalProperties prop : GraphicalProperties.values()) {
			if (_controller.getFlexoWorkflow().hasGraphicalPropertyForKey(prop.getSerializationName())) {
				switch (prop) {
				case ACTION_FONT:
					setActionNodeFont(_controller.getProject().getFlexoWorkflow().getActionFont());
					break;
				case ACTIVITY_FONT:
					setActivityNodeFont(_controller.getProject().getFlexoWorkflow().getActivityFont());
					break;
				case COMPONENT_FONT:
					setComponentFont(_controller.getProject().getFlexoWorkflow().getComponentFont());
					break;
				case CONNECTOR_REPRESENTATION:
					try {
						setConnectorRepresentation((EdgeRepresentation) _controller.getProject().getFlexoWorkflow()
								.getConnectorRepresentation());
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case EVENT_FONT:
					setEventNodeFont(_controller.getProject().getFlexoWorkflow().getEventFont());
					break;
				case ARTEFACT_FONT:
					setArtefactFont(_controller.getProject().getFlexoWorkflow().getArtefactFont());
					break;
				case EDGE_FONT:
					setEdgeFont(_controller.getProject().getFlexoWorkflow().getEdgeFont());
					break;
				case OPERATION_FONT:
					setOperationNodeFont(_controller.getProject().getFlexoWorkflow().getOperationFont());
					break;
				case ROLE_FONT:
					setRoleFont(_controller.getProject().getFlexoWorkflow().getRoleFont());
					break;
				case SHOW_MESSAGES:
					setShowMessagesInWKF(_controller.getProject().getFlexoWorkflow().getShowMessages());
					break;
				case SHOW_SHADOWS:
					setShowShadows(_controller.getProject().getFlexoWorkflow().getShowShadows());
					break;
				case SHOW_WO_NAME:
					setShowWONameInWKF(_controller.getProject().getFlexoWorkflow().getShowWOName());
					break;
				case USE_TRANSPARENCY:
					setUseTransparency(_controller.getProject().getFlexoWorkflow().getUseTransparency());
					break;
				default:
					break;
				}
			}
		}
		_controller.notifyShowLeanTabHasChanged();
		_controller.notifyShowMessages(getShowMessagesInWKF());
	}

	public static void restoreDefault() {
		setShowWONameInWKF(Boolean.TRUE);
		setShowMessagesInWKF(Boolean.TRUE);
		setAlignOnGrid(Boolean.FALSE);
		setGridSize(new Integer(10));
		// setShowBrowserInWKF(false);
		// setShowPaletteInWKF(true);

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
		setConnectorRepresentation(EdgeRepresentation.RECT_POLYLIN, false);
		/*
		 * setActivityConnector(EdgeRepresentation.RECT_POLYLIN); setOperationConnector(EdgeRepresentation.RECT_POLYLIN);
		 * setActionConnector(EdgeRepresentation.CURVE);
		 */
	}

	public static void reset(WKFController controller) {
		if (_controller == controller) {
			_controller = null;
		}
	}

	public WKFPreferences() {
		super(Module.WKF_MODULE);
	}

	@Override
	public File getInspectorFile() {
		return new FileResource("Config/Preferences/WKFPrefs.inspector");
	}

	public static Boolean getShowWONameInWKF() {
		Boolean value = preferences(WKF_PREFERENCES).getBooleanProperty(SHOW_WO_NAME_KEY);
		if (value == null) {
			setShowWONameInWKF(Boolean.FALSE);
			return getShowWONameInWKF();
		}
		return value;
	}

	public static void setShowWONameInWKF(Boolean showWOName) {
		preferences(WKF_PREFERENCES).setBooleanProperty(SHOW_WO_NAME_KEY, showWOName);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setShowWONameInWKF() with " + showWOName);
		}
		if (_controller != null) {
			_controller.getFlexoWorkflow().setShowWOName(showWOName);
			_controller.notifyShowWOName(showWOName.booleanValue());
		}
	}

	public static Boolean getShowMessagesInWKF() {
		Boolean value = preferences(WKF_PREFERENCES).getBooleanProperty(SHOW_MESSAGES_NAME_KEY);
		if (value == null) {
			setShowMessagesInWKF(Boolean.TRUE);
			return getShowMessagesInWKF();
		}
		return value;
	}

	public static void setShowMessagesInWKF(Boolean showMessages) {
		preferences(WKF_PREFERENCES).setBooleanProperty(SHOW_MESSAGES_NAME_KEY, showMessages);
		if (_controller != null) {
			_controller.getFlexoWorkflow().setShowMessages(showMessages);
			_controller.notifyShowMessages(showMessages.booleanValue());
		}
	}

	public static Boolean getAlignOnGrid() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("getAlignOnGrid");
		}
		Boolean value = preferences(WKF_PREFERENCES).getBooleanProperty(ALIGN_ON_GRID_KEY);
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
		preferences(WKF_PREFERENCES).setBooleanProperty(ALIGN_ON_GRID_KEY, alignOnGrid);
	}

	public static boolean getShowGrid() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("getShowGrid");
		}
		Boolean value = preferences(WKF_PREFERENCES).getBooleanProperty(SHOW_GRID);
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
		preferences(WKF_PREFERENCES).setBooleanProperty(SHOW_GRID, showGrid);
		if (_controller != null) {
			_controller.notifyShowGrid(showGrid);
		}
	}

	public static Integer getGridSize() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("getGridSize");
		}
		Integer value = preferences(WKF_PREFERENCES).getIntegerProperty(GRID_SIZE_KEY);
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
		preferences(WKF_PREFERENCES).setIntegerProperty(GRID_SIZE_KEY, gridSize);
	}

	public static Boolean getShowShadows() {
		Boolean value = preferences(WKF_PREFERENCES).getBooleanProperty(SHOW_SHADOWS);
		if (value == null) {
			return Boolean.TRUE;
		}
		return value;
	}

	public static void setShowShadows(Boolean showShadows) {
		preferences(WKF_PREFERENCES).setBooleanProperty(SHOW_SHADOWS, showShadows);
		if (_controller != null) {
			_controller.getFlexoWorkflow().setShowShadows(showShadows);
			_controller.notifyShowShadowChanged();
		}
	}

	public static Boolean getShowLeanTabs() {
		Boolean value = preferences(WKF_PREFERENCES).getBooleanProperty(SHOW_LEAN_TAB);
		if (value == null) {
			return Boolean.FALSE;
		}
		return value;
	}

	public static void setShowLeanTabs(Boolean showLeanTabs) {
		preferences(WKF_PREFERENCES).setBooleanProperty(SHOW_LEAN_TAB, showLeanTabs);
		if (_controller != null) {
			_controller.notifyShowLeanTabHasChanged();
		}
	}

	public static Boolean getUseSimpleEventPalette() {
		Boolean value = preferences(WKF_PREFERENCES).getBooleanProperty(USE_SIMPLE_EVENT_PALETTE);
		if (value == null) {
			return Boolean.TRUE;
		}
		return value;
	}

	public static void setUseSimpleEventPalette(Boolean showLeanTabs) {
		preferences(WKF_PREFERENCES).setBooleanProperty(USE_SIMPLE_EVENT_PALETTE, showLeanTabs);
		if (_controller != null) {
			_controller.notifyUseSimpleEventPaletteHasChanged();
		}
	}

	public static Boolean getShowAlertWhenDroppingIsIncorrect() {
		Boolean value = preferences(WKF_PREFERENCES).getBooleanProperty(SHOW_ALERT_WHEN_DROPPING_INCORRECT);
		if (value == null) {
			return Boolean.TRUE;
		}
		return value;
	}

	public static void setShowAlertWhenDroppingIsIncorrect(Boolean showLeanTabs) {
		preferences(WKF_PREFERENCES).setBooleanProperty(SHOW_ALERT_WHEN_DROPPING_INCORRECT, showLeanTabs);
	}

	public static Boolean getUseTransparency() {
		Boolean value = preferences(WKF_PREFERENCES).getBooleanProperty(USE_TRANSPARENCY);
		if (value == null) {
			return Boolean.TRUE;
		}
		return value;
	}

	public static void setUseTransparency(Boolean useTransparency) {
		preferences(WKF_PREFERENCES).setBooleanProperty(USE_TRANSPARENCY, useTransparency);
		if (_controller != null) {
			_controller.getFlexoWorkflow().setUseTransparency(useTransparency);
			_controller.notifyUseTransparencyChanged();
		}
	}

	public static FlexoFont getActivityNodeFont() {
		FlexoFont returned = FlexoFont.get(preferences(WKF_PREFERENCES).getProperty(ACTIVITY_NODE_FONT_KEY));
		if (returned == null) {
			setActivityNodeFont(new FlexoFont(WKFCst.DEFAULT_ACTIVITY_NODE_LABEL_FONT));
			return returned = FlexoFont.get(preferences(WKF_PREFERENCES).getProperty(ACTIVITY_NODE_FONT_KEY));
		}
		return returned;
	}

	public static void setActivityNodeFont(FlexoFont font) {
		preferences(WKF_PREFERENCES).setProperty(ACTIVITY_NODE_FONT_KEY, font.toString());
		if (_controller != null) {
			_controller.getFlexoWorkflow().setActivityFont(font);
			_controller.notifyActivityFontChanged();
		}
	}

	public static FlexoFont getOperationNodeFont() {
		FlexoFont returned = FlexoFont.get(preferences(WKF_PREFERENCES).getProperty(OPERATION_NODE_FONT_KEY));
		if (returned == null) {
			setOperationNodeFont(new FlexoFont(WKFCst.DEFAULT_OPERATION_NODE_LABEL_FONT));
			return returned = FlexoFont.get(preferences(WKF_PREFERENCES).getProperty(OPERATION_NODE_FONT_KEY));
		}
		return returned;
	}

	public static void setOperationNodeFont(FlexoFont font) {
		preferences(WKF_PREFERENCES).setProperty(OPERATION_NODE_FONT_KEY, font.toString());
		if (_controller != null) {
			_controller.getFlexoWorkflow().setOperationFont(font);
			_controller.notifyOperationFontChanged();
		}
	}

	public static FlexoFont getActionNodeFont() {
		FlexoFont returned = FlexoFont.get(preferences(WKF_PREFERENCES).getProperty(ACTION_NODE_FONT_KEY));
		if (returned == null) {
			setActionNodeFont(new FlexoFont(WKFCst.DEFAULT_ACTION_NODE_LABEL_FONT));
			return returned = FlexoFont.get(preferences(WKF_PREFERENCES).getProperty(ACTION_NODE_FONT_KEY));
		}
		return returned;
	}

	public static void setActionNodeFont(FlexoFont font) {
		preferences(WKF_PREFERENCES).setProperty(ACTION_NODE_FONT_KEY, font.toString());
		if (_controller != null) {
			_controller.getFlexoWorkflow().setActionFont(font);
			_controller.notifyActionFontChanged();
		}
	}

	public static FlexoFont getEventNodeFont() {
		FlexoFont returned = FlexoFont.get(preferences(WKF_PREFERENCES).getProperty(EVENT_NODE_FONT_KEY));
		if (returned == null) {
			setEventNodeFont(new FlexoFont(WKFCst.DEFAULT_EVENT_NODE_LABEL_FONT));
			return returned = FlexoFont.get(preferences(WKF_PREFERENCES).getProperty(EVENT_NODE_FONT_KEY));
		}
		return returned;
	}

	public static void setEventNodeFont(FlexoFont font) {
		preferences(WKF_PREFERENCES).setProperty(EVENT_NODE_FONT_KEY, font.toString());
		if (_controller != null) {
			_controller.getProject().getFlexoWorkflow().setEventFont(font);
			_controller.notifyEventFontChanged();
		}
	}

	public static FlexoFont getRoleFont() {
		FlexoFont returned = FlexoFont.get(preferences(WKF_PREFERENCES).getProperty(ROLE_FONT_KEY));
		if (returned == null) {
			setRoleFont(new FlexoFont(WKFCst.DEFAULT_ROLE_LABEL_FONT));
			return returned = FlexoFont.get(preferences(WKF_PREFERENCES).getProperty(ROLE_FONT_KEY));
		}
		return returned;
	}

	public static void setRoleFont(FlexoFont font) {
		preferences(WKF_PREFERENCES).setProperty(ROLE_FONT_KEY, font.toString());
		if (_controller != null) {
			_controller.getProject().getFlexoWorkflow().setRoleFont(font);
			_controller.notifyRoleFontChanged();
		}
	}

	public static FlexoFont getEdgeFont() {
		FlexoFont returned = FlexoFont.get(preferences(WKF_PREFERENCES).getProperty(EDGE_FONT_KEY));
		if (returned == null) {
			setEdgeFont(new FlexoFont(WKFCst.DEFAULT_EDGE_LABEL_FONT));
			return returned = FlexoFont.get(preferences(WKF_PREFERENCES).getProperty(EDGE_FONT_KEY));
		}
		return returned;
	}

	public static void setEdgeFont(FlexoFont font) {
		preferences(WKF_PREFERENCES).setProperty(EDGE_FONT_KEY, font.toString());
		if (_controller != null) {
			_controller.getProject().getFlexoWorkflow().setEdgeFont(font);
			_controller.notifyEdgeFontChanged();
		}
	}

	public static FlexoFont getArtefactFont() {
		FlexoFont returned = FlexoFont.get(preferences(WKF_PREFERENCES).getProperty(ARTEFACT_FONT_KEY));
		if (returned == null) {
			setArtefactFont(new FlexoFont(WKFCst.DEFAULT_ARTEFACT_LABEL_FONT));
			return returned = FlexoFont.get(preferences(WKF_PREFERENCES).getProperty(ARTEFACT_FONT_KEY));
		}
		return returned;
	}

	public static void setArtefactFont(FlexoFont font) {
		preferences(WKF_PREFERENCES).setProperty(ARTEFACT_FONT_KEY, font.toString());
		if (_controller != null) {
			_controller.getProject().getFlexoWorkflow().setArtefactFont(font);
			_controller.notifyArtefactFontChanged();
		}
	}

	public static FlexoFont getComponentFont() {
		FlexoFont returned = FlexoFont.get(preferences(WKF_PREFERENCES).getProperty(COMPONENT_FONT_KEY));
		if (returned == null) {
			setComponentFont(new FlexoFont(WKFCst.DEFAULT_COMPONENT_LABEL_FONT));
			return returned = FlexoFont.get(preferences(WKF_PREFERENCES).getProperty(COMPONENT_FONT_KEY));
		}
		return returned;
	}

	public static void setComponentFont(FlexoFont font) {
		preferences(WKF_PREFERENCES).setProperty(COMPONENT_FONT_KEY, font.toString());
		if (_controller != null) {
			_controller.getProject().getFlexoWorkflow().setComponentFont(font);
			_controller.notifyComponentFontChanged();
		}
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
		String s = preferences(WKF_PREFERENCES).getProperty(CONNECTOR_REPRESENTATION);
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
			setConnectorRepresentation(returned = EdgeRepresentation.RECT_POLYLIN, false);
		}
		return returned;
	}

	public static void setConnectorRepresentation(EdgeRepresentation type) {
		setConnectorRepresentation(type, true);
	}

	public static String getActionConnectorRepresentationInfo() {
		return FlexoLocalization.localizedForKey("note_that_action_level_edges_are_always_curved");
	}

	public static String getPreferenceMessage() {
		return FlexoLocalization.localizedForKey("wkf_preferences_message");
	}

	public static void setConnectorRepresentation(EdgeRepresentation type, boolean notify) {
		if (type != null) {
			preferences(WKF_PREFERENCES).setProperty(CONNECTOR_REPRESENTATION, type.name());
			if (_controller != null) {
				_controller.getFlexoWorkflow().setConnectorRepresentation(type);
				_controller.notifyEdgeRepresentationChanged();
			}
			/*
			 * if (notify) FlexoController.notify(FlexoLocalization.localizedForKey("connector_representation_is_a_local_preference"));
			 */
		}
	}

	public static RectPolylinAdjustability getConnectorAdjustability() {
		String s = preferences(WKF_PREFERENCES).getProperty(CONNECTOR_ADJUSTABILITY);
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
			preferences(WKF_PREFERENCES).setProperty(CONNECTOR_ADJUSTABILITY, adjustability.name());
			if (_controller != null) {
				_controller.notifyEdgeRepresentationChanged();
			}
			if (notify) {
				FlexoController.notify(FlexoLocalization.localizedForKey("connector_adjustability_is_a_local_preference") + "\n"
						+ FlexoLocalization.localizedForKey("in_order_for_this_change_to_take_effect_you_must_restart_flexo"));
			}
		}
	}

	public static int getScreenshotQuality() {
		Integer limit = preferences(WKF_PREFERENCES).getIntegerProperty(SCREENSHOT_QUALITY);
		if (limit == null) {
			limit = 100;
		}
		return limit;
	}

	public static void setScreenshotQuality(int limit) {
		preferences(WKF_PREFERENCES).setIntegerProperty(SCREENSHOT_QUALITY, limit);
	}

}
