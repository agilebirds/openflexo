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

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.fib.controller.FIBController.Status;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.action.InvalidParametersException;
import org.openflexo.foundation.sg.implmodel.CreateTechnologyModuleImplementation;
import org.openflexo.foundation.sg.implmodel.exception.TechnologyModuleCompatibilityCheckException;
import org.openflexo.icon.SGIconLibrary;
import org.openflexo.sgmodule.SGCst;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class CreateTechnologyModuleImplementationInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	CreateTechnologyModuleImplementationInitializer(SGControllerActionInitializer actionInitializer) {
		super(CreateTechnologyModuleImplementation.actionType, actionInitializer);
	}

	@Override
	protected SGControllerActionInitializer getControllerActionInitializer() {
		return (SGControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<CreateTechnologyModuleImplementation> getDefaultInitializer() {
		return new FlexoActionInitializer<CreateTechnologyModuleImplementation>() {
			@Override
			public boolean run(ActionEvent e, CreateTechnologyModuleImplementation action) {
				FIBDialog dialog = FIBDialog.instanciateComponent(SGCst.CREATE_TECHNOLOGY_MODULE_IMPLEMENTATION_DIALOG_FIB, action, null,
						true);
				return (dialog.getStatus() == Status.VALIDATED);
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<CreateTechnologyModuleImplementation> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<CreateTechnologyModuleImplementation>() {
			@Override
			public boolean handleException(FlexoException exception, CreateTechnologyModuleImplementation action) {
				if ((exception instanceof InvalidParametersException) || (exception instanceof TechnologyModuleCompatibilityCheckException)) {
					FlexoController.notify(exception.getLocalizedMessage());
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
