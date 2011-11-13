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
package org.openflexo.wkf.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.components.browser.view.BrowserActionSource;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.param.EnumDropDownParameter;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.foundation.wkf.action.AddPort;
import org.openflexo.foundation.wkf.ws.FlexoPort;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.wkf.controller.ProcessBrowser;
import org.openflexo.wkf.controller.WKFController;
import org.openflexo.wkf.controller.WorkflowBrowser;
import org.openflexo.wkf.processeditor.ProcessEditorConstants;
import org.openflexo.wkf.swleditor.SWLEditorConstants;

public class AddPortInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	AddPortInitializer(WKFControllerActionInitializer actionInitializer) {
		super(AddPort.createPort, actionInitializer);
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() {
		return (WKFControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<AddPort> getDefaultInitializer() {
		return new FlexoActionInitializer<AddPort>() {
			@Override
			public boolean run(ActionEvent e, final AddPort action) {
				if (!action.hasBeenLocated()) {
					WKFController controller = (WKFController) getController();
					FGEPoint lastClickedPoint = controller.getLastClickedPoint();
					if (lastClickedPoint != null) {
						if (controller.getCurrentPerspective() == controller.PROCESS_EDITOR_PERSPECTIVE)
							action.setGraphicalContext(ProcessEditorConstants.BASIC_PROCESS_EDITOR);
						else if (controller.getCurrentPerspective() == controller.SWIMMING_LANE_PERSPECTIVE)
							action.setGraphicalContext(SWLEditorConstants.SWIMMING_LANE_EDITOR);
						action.setLocation(lastClickedPoint.x, lastClickedPoint.y);
					}
				}

				if (!action.isNewPortNameInitialized() || action.getNewPortType() == null) {
					final TextFieldParameter newNodeNameParam = new TextFieldParameter("newPortName", "new_port_name",
							action.getNewPortName()) {
						@Override
						public void setValue(String aValue) {
							super.setValue(aValue);
							action.setNewPortName(aValue);
						}
					};
					EnumDropDownParameter<AddPort.CreatedPortType> portTypeParam = new EnumDropDownParameter<AddPort.CreatedPortType>(
							"portType", "port_type", action.getNewPortType(), AddPort.CreatedPortType.values()) {
						@Override
						public void setValue(AddPort.CreatedPortType aValue) {
							super.setValue(aValue);
							action.setNewPortType(aValue);
							newNodeNameParam.setValue(action.getNewPortName());
						}
					};
					portTypeParam.addParameter("showReset", "false");
					newNodeNameParam.setDepends("portType");
					AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
							action.getLocalizedName(), FlexoLocalization.localizedForKey("please_enter_name_for_newly_created_port"),
							portTypeParam, newNodeNameParam);
					if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
						return true;
					} else {
						return false;
					}
				}
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<AddPort> getDefaultFinalizer() {
		return new FlexoActionFinalizer<AddPort>() {
			@Override
			public boolean run(ActionEvent e, AddPort action) {
				FlexoPort newFlexoPort = action.getNewPort();
				if (newFlexoPort == null)
					return false;
				if (e != null && e.getSource() instanceof BrowserActionSource) {
					ProjectBrowser browser = ((BrowserActionSource) e.getSource()).getBrowser();
					if (browser instanceof WorkflowBrowser) {
						if (!browser.activateBrowsingFor(newFlexoPort)) {
							if (FlexoController.confirm(FlexoLocalization.localizedForKey("would_you_like_to_desactivate_ports_filtering"))) {
								browser.getFilterForObject(newFlexoPort).setStatus(BrowserFilterStatus.SHOW);
								browser.update();
							}
						}
						// browser.focusOn(newFlexoPort);
					} else if (browser instanceof ProcessBrowser) {
						// browser.focusOn(newFlexoPort);
					}
				}
				if (e != null)
					getControllerActionInitializer().getWKFSelectionManager().setSelectedObject(newFlexoPort);
				return true;
			}
		};
	}

	@Override
	public void init() {
		initActionType(AddPort.createPort, getDefaultInitializer(), getDefaultFinalizer(), getDefaultExceptionHandler(),
				getEnableCondition(), getVisibleCondition(), null, null, null);
		initActionType(AddPort.createNewPort, getDefaultInitializer(), getDefaultFinalizer(), getDefaultExceptionHandler(),
				getEnableCondition(), getVisibleCondition(), null, WKFIconLibrary.SMALL_NEW_PORT_ICON, null);
		initActionType(AddPort.createDeletePort, getDefaultInitializer(), getDefaultFinalizer(), getDefaultExceptionHandler(),
				getEnableCondition(), getVisibleCondition(), null, WKFIconLibrary.SMALL_DELETE_PORT_ICON, null);
		initActionType(AddPort.createInPort, getDefaultInitializer(), getDefaultFinalizer(), getDefaultExceptionHandler(),
				getEnableCondition(), getVisibleCondition(), null, WKFIconLibrary.SMALL_IN_PORT_LEFT_ICON, null);
		initActionType(AddPort.createOutPort, getDefaultInitializer(), getDefaultFinalizer(), getDefaultExceptionHandler(),
				getEnableCondition(), getVisibleCondition(), null, WKFIconLibrary.SMALL_OUT_PORT_LEFT_ICON, null);
		initActionType(AddPort.createInOutPort, getDefaultInitializer(), getDefaultFinalizer(), getDefaultExceptionHandler(),
				getEnableCondition(), getVisibleCondition(), null, WKFIconLibrary.SMALL_IN_OUT_PORT_LEFT_ICON, null);
	}

}
