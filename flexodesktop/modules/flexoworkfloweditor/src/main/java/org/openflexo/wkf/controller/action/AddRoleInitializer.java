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

import javax.swing.Icon;
import javax.swing.SwingUtilities;

import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.wkf.controller.WKFController;
import org.openflexo.wkf.roleeditor.RoleEditorController;
import org.openflexo.wkf.roleeditor.RoleEditorView;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.components.browser.view.BrowserActionSource;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.view.ShapeView;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.param.ColorParameter;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.TextAreaParameter;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.foundation.utils.FlexoColor;
import org.openflexo.foundation.wkf.DuplicateRoleException;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.action.AddRole;

public class AddRoleInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	AddRoleInitializer(WKFControllerActionInitializer actionInitializer) {
		super(AddRole.actionType, actionInitializer);
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() {
		return (WKFControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<AddRole> getDefaultInitializer() {
		return new FlexoActionInitializer<AddRole>() {
			@Override
			public boolean run(ActionEvent e, AddRole action) {
				if (action.getContext() instanceof DuplicateRoleException)
					return true;
				if (action.getRoleAutomaticallyCreated())
					return true;
				FlexoWorkflow workflow = action.getWorkflow();
				ParameterDefinition[] parameters = new ParameterDefinition[3];
				parameters[0] = new TextFieldParameter("newRoleName", "name", workflow.getRoleList().getNextNewUserRoleName());
				parameters[1] = new ColorParameter("color", "color", workflow.getRoleList().getNewRoleColor());
				parameters[2] = new TextAreaParameter("description", "description", "");
				parameters[2].addParameter("columns", "20");
				AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
						FlexoLocalization.localizedForKey("create_new_role"),
						FlexoLocalization.localizedForKey("enter_parameters_for_the_new_role"), parameters);
				if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
					String newRoleName = (String) dialog.parameterValueWithName("newRoleName");
					if (newRoleName == null)
						return false;
					action.setNewRoleName(newRoleName);
					action.setNewColor((FlexoColor) dialog.parameterValueWithName("color"));
					action.setNewDescription((String) dialog.parameterValueWithName("description"));
					return true;
				} else {
					return false;
				}
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<AddRole> getDefaultFinalizer() {
		return new FlexoActionFinalizer<AddRole>() {
			@Override
			public boolean run(ActionEvent e, AddRole action) {
				Role newRole = action.getNewRole();
				if ((e != null) && (e.getSource() instanceof BrowserActionSource)) {
					ProjectBrowser browser = ((BrowserActionSource) e.getSource()).getBrowser();
					if (!browser.activateBrowsingFor(newRole)) {
						if (FlexoController.confirm(FlexoLocalization.localizedForKey("would_you_like_to_desactivate_role_filtering"))) {
							browser.getFilterForObject(newRole).setStatus(BrowserFilterStatus.SHOW);
							browser.update();
						}
					}
				}
				if (e != null)// If it wasn't created through the process inspector
					getControllerActionInitializer().getWKFSelectionManager().setSelectedObject(newRole);
				// getControllerActionInitializer().getWKFController().getWorkflowBrowser().focusOn(newRole);
				if (getControllerActionInitializer().getWKFController().getCurrentPerspective() == getControllerActionInitializer()
						.getWKFController().ROLE_EDITOR_PERSPECTIVE) {
					RoleEditorController controller = getControllerActionInitializer().getWKFController().ROLE_EDITOR_PERSPECTIVE
							.getRoleEditorController();
					if (controller != null) {
						RoleEditorView drawing = controller.getDrawingView();
						if (drawing != null) {
							ShapeGraphicalRepresentation<?> roleGR = (ShapeGraphicalRepresentation<?>) drawing.getDrawing()
									.getGraphicalRepresentation(newRole);
							if (roleGR != null) {
								final ShapeView<?> view = (ShapeView<?>) drawing.viewForObject(roleGR);
								SwingUtilities.invokeLater(new Runnable() {
									@Override
									public void run() {
										if (view != null && view.getLabelView() != null)
											view.getLabelView().startEdition();
									}
								});
							}
						}
					}
				}
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<AddRole> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<AddRole>() {
			@Override
			public boolean handleException(FlexoException exception, AddRole action) {
				if (exception instanceof DuplicateRoleException) {
					String newRoleName = FlexoController.askForString(FlexoLocalization
							.localizedForKey("sorry_role_already_exists_please_choose_an_other_name"));
					if (newRoleName != null) {
						action.setNewRoleName(newRoleName);
						action.setContext(exception);
						action.doAction();
					}
					return true;
				}
				return false;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return WKFIconLibrary.ROLE_ICON;
	}

}
