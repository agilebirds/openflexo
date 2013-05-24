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
package org.openflexo.sgmodule.controller.action;

import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.fib.controller.FIBController.Status;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.sg.implmodel.CreateImplementationModel;
import org.openflexo.foundation.sg.implmodel.ImplementationModelDefinition.DuplicateImplementationModelNameException;
import org.openflexo.icon.SGIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.sgmodule.SGCst;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class CreateImplementationModelInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	CreateImplementationModelInitializer(SGControllerActionInitializer actionInitializer) {
		super(CreateImplementationModel.actionType, actionInitializer);
	}

	@Override
	protected SGControllerActionInitializer getControllerActionInitializer() {
		return (SGControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<CreateImplementationModel> getDefaultInitializer() {
		return new FlexoActionInitializer<CreateImplementationModel>() {
			@Override
			public boolean run(EventObject e, CreateImplementationModel action) {
				if (action.skipDialog) {
					return true;
				}
				FIBDialog dialog = FIBDialog.instanciateAndShowDialog(SGCst.CREATE_IMPLEMENTATION_MODEL_DIALOG_FIB, action,
						FlexoFrame.getActiveFrame(), true, FlexoLocalization.getMainLocalizer());
				return dialog.getStatus() == Status.VALIDATED;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<CreateImplementationModel> getDefaultFinalizer() {
		return new FlexoActionFinalizer<CreateImplementationModel>() {
			@Override
			public boolean run(EventObject e, CreateImplementationModel action) {
				getController().setCurrentEditedObjectAsModuleView(action.getNewImplementationModelDefinition());
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<CreateImplementationModel> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<CreateImplementationModel>() {
			@Override
			public boolean handleException(FlexoException exception, CreateImplementationModel action) {
				if (exception instanceof DuplicateResourceException) {
					FlexoController.notify(FlexoLocalization.localizedForKey("invalid_name_a_model_with_this_name_already_exists"));
					return true;
				}
				if (exception instanceof DuplicateImplementationModelNameException) {
					FlexoController.notify(FlexoLocalization.localizedForKey("invalid_name_a_model_with_this_name_already_exists"));
					return true;
				}
				return false;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return SGIconLibrary.GENERATED_CODE_ICON;
	}

}
