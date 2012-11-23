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
package org.openflexo.ve.controller.action;

import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.view.action.AddConnector;
import org.openflexo.icon.VEIconLibrary;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class AddConnectorInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	AddConnectorInitializer(VEControllerActionInitializer actionInitializer) {
		super(AddConnector.actionType, actionInitializer);
	}

	@Override
	protected VEControllerActionInitializer getControllerActionInitializer() {
		return (VEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<AddConnector> getDefaultInitializer() {
		return new FlexoActionInitializer<AddConnector>() {
			@Override
			public boolean run(EventObject e, AddConnector action) {
				if (action.getAutomaticallyCreateConnector()) {
					return true;
				}

				/*	DynamicDropDownParameter<Role> roles = new DynamicDropDownParameter<Role>("parentRole", "role_that_will_be_specialized", availableRoles, availableRoles.firstElement());
				roles.setShowReset(false);
				roles.setFormatter("name");
				TextFieldParameter annotation = new TextFieldParameter("Annotation","annotation","");

				AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null, FlexoLocalization.localizedForKey("specialize_a_new_role"), FlexoLocalization.localizedForKey("please_select_a_role"), roles, annotation);
				if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
					Role parentRole =  roles.getValue();
					if (parentRole == null)
						return false;
					action.setNewParentRole(parentRole);
					action.setAnnotation(annotation.getValue());
					return true;
				} else {
					return false;
				}*/

				return false;
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

	@Override
	protected Icon getEnabledIcon() {
		return VEIconLibrary.CONNECTOR_ICON;
	}

}
