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

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.diagram.action.CreateVirtualModelInstance;
import org.openflexo.icon.VEIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.ve.VECst;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class CreateVirtualModelInstanceInitializer extends ActionInitializer<CreateVirtualModelInstance, View, FlexoModelObject> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	CreateVirtualModelInstanceInitializer(VEControllerActionInitializer actionInitializer) {
		super(CreateVirtualModelInstance.actionType, actionInitializer);
	}

	@Override
	protected VEControllerActionInitializer getControllerActionInitializer() {
		return (VEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<CreateVirtualModelInstance> getDefaultInitializer() {
		return new FlexoActionInitializer<CreateVirtualModelInstance>() {
			@Override
			public boolean run(EventObject e, CreateVirtualModelInstance action) {
				if (action.skipChoosePopup) {
					return true;
				} else {
					return instanciateAndShowDialog(action, VECst.CREATE_DIAGRAM_DIALOG_FIB);
				}

			}
		};
	}

	@Override
	protected FlexoActionFinalizer<CreateVirtualModelInstance> getDefaultFinalizer() {
		return new FlexoActionFinalizer<CreateVirtualModelInstance>() {
			@Override
			public boolean run(EventObject e, CreateVirtualModelInstance action) {
				getController().setCurrentEditedObjectAsModuleView(action.getNewVirtualModelInstance());
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<CreateVirtualModelInstance> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<CreateVirtualModelInstance>() {
			@Override
			public boolean handleException(FlexoException exception, CreateVirtualModelInstance action) {
				if (exception instanceof NotImplementedException) {
					FlexoController.notify(FlexoLocalization.localizedForKey("not_implemented_yet"));
					return true;
				}
				if (exception instanceof DuplicateResourceException) {
					FlexoController.notify(FlexoLocalization.localizedForKey("invalid_name_a_virtual_model_with_this_name_already_exists"));
					return true;
				}
				return false;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return VEIconLibrary.DIAGRAM_ICON;
	}

}
