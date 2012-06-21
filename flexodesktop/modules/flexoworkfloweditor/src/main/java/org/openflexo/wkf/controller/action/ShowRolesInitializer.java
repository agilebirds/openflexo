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

import java.util.EventObject;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoActionVisibleCondition;
import org.openflexo.foundation.param.CheckboxListParameter;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.action.ShowRoles;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.wkf.controller.WKFController;
import org.openflexo.wkf.swleditor.SwimmingLaneRepresentation;

public class ShowRolesInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	ShowRolesInitializer(WKFControllerActionInitializer actionInitializer) {
		super(ShowRoles.actionType, actionInitializer);
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() {
		return (WKFControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	public WKFController getController() {
		return (WKFController) super.getController();
	}

	@Override
	protected FlexoActionInitializer getDefaultInitializer() {
		return new FlexoActionInitializer<ShowRoles>() {

			@Override
			public boolean run(EventObject event, ShowRoles action) {
				if (action.getProcess() != null) {
					String context = SwimmingLaneRepresentation.getRoleVisibilityContextForProcess(action.getProcess());
					Vector<Role> roles = new Vector<Role>();
					for (Role role : action.getProcess().getWorkflow().getRoleList().getRoles()) {
						if (!role.getIsVisible(context)) {
							roles.add(role);
						}
					}
					if (action.getProcess().getWorkflow().getImportedRoleList() != null) {
						for (Role role : action.getProcess().getWorkflow().getImportedRoleList().getRoles()) {
							if (!role.getIsVisible(context)) {
								roles.add(role);
							}
						}
					}
					if (roles.size() == 0) {
						FlexoController.notify(FlexoLocalization.localizedForKey("there_are_no_roles_to_add"));
						return false;
					}
					ParameterDefinition<?>[] params = new ParameterDefinition<?>[1];
					CheckboxListParameter<Role> rolesToAdd = new CheckboxListParameter<Role>("selectedRoles", "select_roles_to_show",
							roles, new Vector<Role>());
					rolesToAdd.setFormatter("name");
					params[0] = rolesToAdd;
					AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(action.getProcess().getProject(),
							FlexoLocalization.localizedForKey("select_roles_to_show"),
							FlexoLocalization.localizedForKey("select_roles_to_show"), params);
					if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
						for (Role role : rolesToAdd.getValue()) {
							role.setIntegerParameter(Integer.MAX_VALUE,
									SwimmingLaneRepresentation.getRoleIndexContextedParameterForProcess(action.getProcess()));
							role.setIsVisible(true, context);

						}
						return true;
					} else {
						return false;
					}
				}
				return false;
			}

		};
	}

	@Override
	protected FlexoActionVisibleCondition<ShowRoles, FlexoProcess, FlexoProcess> getVisibleCondition() {
		return new FlexoActionVisibleCondition<ShowRoles, FlexoProcess, FlexoProcess>() {

			@Override
			public boolean isVisible(FlexoActionType<ShowRoles, FlexoProcess, FlexoProcess> actionType, FlexoProcess object,
					Vector<FlexoProcess> globalSelection, FlexoEditor editor) {
				return getController().getCurrentPerspective() == getController().SWIMMING_LANE_PERSPECTIVE
						&& getController().getCurrentModuleView().getRepresentedObject() == object;
			}

		};
	}

	@Override
	protected FlexoActionFinalizer<ShowRoles> getDefaultFinalizer() {
		return new FlexoActionFinalizer<ShowRoles>() {
			@Override
			public boolean run(EventObject e, ShowRoles action) {
				return true;
			}
		};
	}

}
