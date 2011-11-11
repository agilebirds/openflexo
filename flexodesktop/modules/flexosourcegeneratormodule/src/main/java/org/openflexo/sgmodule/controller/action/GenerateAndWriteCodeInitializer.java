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

import org.openflexo.icon.GeneratorIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.sgmodule.SGPreferences;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.generator.action.DismissUnchangedGeneratedFiles;
import org.openflexo.generator.action.GenerateAndWrite;
import org.openflexo.generator.exception.GenerationException;

public class GenerateAndWriteCodeInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	GenerateAndWriteCodeInitializer(SGControllerActionInitializer actionInitializer) {
		super(GenerateAndWrite.actionType, actionInitializer);
	}

	@Override
	protected SGControllerActionInitializer getControllerActionInitializer() {
		return (SGControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<GenerateAndWrite> getDefaultInitializer() {
		return new FlexoActionInitializer<GenerateAndWrite>() {
			@Override
			public boolean run(ActionEvent e, GenerateAndWrite action) {
				if (action.getRepository().getDirectory() == null) {
					FlexoController.notify(FlexoLocalization.localizedForKey("please_supply_valid_directory"));
					return false;
				}
				action.setSaveBeforeGenerating(SGPreferences.getSaveBeforeGenerating());
				action.setWriteUnchangedFiles(!SGPreferences.getAutomaticallyDismissUnchangedFiles());
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<GenerateAndWrite> getDefaultFinalizer() {
		return new FlexoActionFinalizer<GenerateAndWrite>() {
			@Override
			public boolean run(ActionEvent e, GenerateAndWrite action) {
				getControllerActionInitializer().getSGController().disposeProgressWindow();
				if (SGPreferences.getAutomaticallyDismissUnchangedFiles())
					DismissUnchangedGeneratedFiles.actionType.makeNewAction(action.getFocusedObject(), action.getGlobalSelection(),
							action.getEditor()).doAction();
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<GenerateAndWrite> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<GenerateAndWrite>() {
			@Override
			public boolean handleException(FlexoException exception, GenerateAndWrite action) {
				getControllerActionInitializer().getSGController().disposeProgressWindow();
				if (exception instanceof GenerationException) {
					FlexoController.showError(FlexoLocalization.localizedForKey("generation_failed") + ":\n"
							+ ((GenerationException) exception).getLocalizedMessage());
					return true;
				}
				exception.printStackTrace();
				return false;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return GeneratorIconLibrary.GENERATE_WRITE_CODE_ICON;
	}

}
