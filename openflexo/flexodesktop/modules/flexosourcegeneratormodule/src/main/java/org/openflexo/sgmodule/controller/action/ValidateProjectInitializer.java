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


import org.openflexo.components.ProgressWindow;
import org.openflexo.components.validation.ConsistencyCheckDialog;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.generator.action.SynchronizeRepositoryCodeGeneration;
import org.openflexo.generator.action.ValidateProject;
import org.openflexo.generator.exception.ModelValidationException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.sgmodule.SGPreferences;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;


public class ValidateProjectInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	ValidateProjectInitializer(SGControllerActionInitializer actionInitializer)
	{
		super(ValidateProject.actionType,actionInitializer);
	}

	@Override
	protected SGControllerActionInitializer getControllerActionInitializer() 
	{
		return (SGControllerActionInitializer)super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<ValidateProject> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<ValidateProject>() {
			@Override
			public boolean run(ActionEvent e, ValidateProject action)
			{
				// If disabled, don't do it
				if (action.getContext() instanceof SynchronizeRepositoryCodeGeneration) {
					return (SGPreferences.getValidateBeforeGenerating());
				}
				if (action.getProjectGenerator() != null)
					action.getProjectGenerator().startHandleLogs();
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<ValidateProject> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<ValidateProject>() {
			@Override
			public boolean run(ActionEvent e, ValidateProject action)
			{
				if (action.getProjectGenerator() != null) {
					action.getProjectGenerator().stopHandleLogs();
					action.getProjectGenerator().flushLogs();
				}
				getControllerActionInitializer().getSGController().disposeProgressWindow();
				if (action.isProjectValid()) {
					FlexoController.notify(FlexoLocalization.localizedForKey("validation_successfull"));
				}
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<ValidateProject> getDefaultExceptionHandler() 
	{
		return new FlexoExceptionHandler<ValidateProject>() {
			@Override
			public boolean handleException(FlexoException exception, ValidateProject action) {
				getControllerActionInitializer().getSGController().disposeProgressWindow();
				if (exception instanceof ModelValidationException) {
					ProgressWindow.hideProgressWindow();
					if (action.getContext() instanceof SynchronizeRepositoryCodeGeneration) {
						SynchronizeRepositoryCodeGeneration myAction = (SynchronizeRepositoryCodeGeneration) action.getContext();
						int whatToDo = FlexoController.selectOption(FlexoLocalization.localizedForKey("check_consistency_failed") + "\n"
								+ FlexoLocalization.localizedForKey("what_would_you_like_to_do") + "\n"
								+ FlexoLocalization.localizedForKey("you_can_desactivate_automatic_validation_in_preferences"),
								FlexoLocalization.localizedForKey("abort"), FlexoLocalization.localizedForKey("abort"), FlexoLocalization
								.localizedForKey("generate_anyway"), FlexoLocalization.localizedForKey("review_errors"));
						if (whatToDo == 0) {
							// Abort
							myAction.setContinueAfterValidation(false);
						} else if (whatToDo == 1) {
							// Generate anyway
							// continue
							myAction.setContinueAfterValidation(true);
						} else if (whatToDo == 2) {
							ConsistencyCheckDialog reviewDialog = new ConsistencyCheckDialog(null, ((ModelValidationException) exception)
									.getValidationReport());
							reviewDialog.setVisible(true);
							// Abort
							myAction.setContinueAfterValidation(false);
						}
					} else {
						int whatToDo = FlexoController.selectOption(FlexoLocalization.localizedForKey("check_consistency_failed"),
								FlexoLocalization.localizedForKey("review_errors"), FlexoLocalization.localizedForKey("done"),
								FlexoLocalization.localizedForKey("review_errors"));
						if (whatToDo == 0) {
							// Done
						} else if (whatToDo == 1) {
							// Review errors
							ConsistencyCheckDialog reviewDialog = new ConsistencyCheckDialog(null,((ModelValidationException) exception)
									.getValidationReport());
							reviewDialog.setVisible(true);
						}
					}
					return true;
				}
				exception.printStackTrace();
				return false;
			}
		};
	}

}
