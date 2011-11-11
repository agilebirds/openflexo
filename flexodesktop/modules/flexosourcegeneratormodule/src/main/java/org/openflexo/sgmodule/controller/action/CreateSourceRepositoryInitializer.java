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

import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.fib.controller.FIBController.Status;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.cg.DuplicateCodeRepositoryNameException;
import org.openflexo.foundation.sg.CreateSourceRepository;
import org.openflexo.icon.SGIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.sgmodule.SGCst;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class CreateSourceRepositoryInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	CreateSourceRepositoryInitializer(SGControllerActionInitializer actionInitializer) {
		super(CreateSourceRepository.actionType, actionInitializer);
	}

	@Override
	protected SGControllerActionInitializer getControllerActionInitializer() {
		return (SGControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<CreateSourceRepository> getDefaultInitializer() {
		return new FlexoActionInitializer<CreateSourceRepository>() {
			@Override
			public boolean run(ActionEvent e, CreateSourceRepository action) {

				FIBDialog dialog = FIBDialog.instanciateComponent(SGCst.CREATE_SOURCE_REPOSITORY_DIALOG_FIB, action, null, true);
				return (dialog.getStatus() == Status.VALIDATED);
			}

		};
	}

	@Override
	protected FlexoActionFinalizer<CreateSourceRepository> getDefaultFinalizer() {
		return new FlexoActionFinalizer<CreateSourceRepository>() {
			@Override
			public boolean run(ActionEvent e, CreateSourceRepository action) {
				if (action.getNewSourceRepository() != null) {
					getController().setCurrentEditedObjectAsModuleView(action.getNewSourceRepository());
				}
				return true;
			}
		};
	}

	/**
	 * Overrides getDefaultExceptionHandler
	 * 
	 * @see org.openflexo.view.controller.ActionInitializer#getDefaultExceptionHandler()
	 */
	@Override
	protected FlexoExceptionHandler<CreateSourceRepository> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<CreateSourceRepository>() {

			@Override
			public boolean handleException(FlexoException exception, CreateSourceRepository action) {
				if (exception instanceof DuplicateCodeRepositoryNameException) {
					FlexoController.notify(FlexoLocalization.localizedForKey("name_is_already_used"));
					return true;
				}
				return false;
			}

		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return SGIconLibrary.GENERATED_CODE_REPOSITORY_ICON;
	}

}
