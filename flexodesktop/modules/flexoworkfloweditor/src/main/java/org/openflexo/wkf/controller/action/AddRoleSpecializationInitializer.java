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
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.param.DynamicDropDownParameter;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.action.AddRoleSpecialization;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class AddRoleSpecializationInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	AddRoleSpecializationInitializer(WKFControllerActionInitializer actionInitializer) {
		super(AddRoleSpecialization.actionType, actionInitializer);
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() {
		return (WKFControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<AddRoleSpecialization> getDefaultInitializer() {
		return new FlexoActionInitializer<AddRoleSpecialization>() {
			@Override
			public boolean run(ActionEvent e, AddRoleSpecialization action) {
				Vector<Role> availableRoles = action.getFocusedObject().getAvailableRolesForSpecialization();

				if (action.getRoleSpecializationAutomaticallyCreated()) {
					if (availableRoles.contains(action.getNewParentRole()))
						return true;
					else {
						// This parent is not conform
						if (action.getFocusedObject().isSpecializingRole(action.getNewParentRole())) {
							FlexoController.notify(FlexoLocalization.localizedForKeyWithParams(
									"role_($0)_is_already_specialized_by_role_($1)", action.getNewParentRole().getName(), action
											.getFocusedObject().getName()));
						} else if (action.getFocusedObject().isTransitivelySpecializingRole(action.getNewParentRole())) {
							FlexoController.notify(FlexoLocalization.localizedForKeyWithParams(
									"role_($0)_is_already_transitively_specialized_by_role_($1)", action.getNewParentRole().getName(),
									action.getFocusedObject().getName()));
						} else if (action.getNewParentRole().isTransitivelySpecializingRole(action.getFocusedObject())) {
							FlexoController.notify(FlexoLocalization.localizedForKeyWithParams(
									"role_($0)_cannot_specialize_role_($1)_which_already_specialize_this_role_(forbidden_cycles)", action
											.getFocusedObject().getName(), action.getNewParentRole().getName()));
						} else {
							FlexoController.notify(FlexoLocalization.localizedForKey("invalid_role"));
						}
						return false;
					}
				}

				if (availableRoles.size() == 0) {
					FlexoController.notify(FlexoLocalization.localizedForKeyWithParams(
							"found_no_role_that_could_be_specialized_by_role_($0)", action.getFocusedObject().getName()));
					return false;
				}
				DynamicDropDownParameter<Role> roles = new DynamicDropDownParameter<Role>("parentRole", "role_that_will_be_specialized",
						availableRoles, availableRoles.firstElement());
				roles.setShowReset(false);
				roles.setFormatter("name");
				TextFieldParameter annotation = new TextFieldParameter("Annotation", "annotation", "");

				AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
						FlexoLocalization.localizedForKey("specialize_a_new_role"),
						FlexoLocalization.localizedForKey("please_select_a_role"), roles, annotation);
				if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
					Role parentRole = roles.getValue();
					if (parentRole == null)
						return false;
					action.setNewParentRole(parentRole);
					action.setAnnotation(annotation.getValue());
					return true;
				} else {
					return false;
				}
			}
		};
	}

	/*   @Override
	protected FlexoActionFinalizer<AddParentRole> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<AddParentRole>() {
	        public boolean run(ActionEvent e, AddParentRole action)
	        {
	            return true;
	      }
	    };
	}*/

}
