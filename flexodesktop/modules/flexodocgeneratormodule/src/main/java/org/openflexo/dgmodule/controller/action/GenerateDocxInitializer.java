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
package org.openflexo.dgmodule.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.dg.action.GenerateDocx;
import org.openflexo.dgmodule.DGPreferences;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.IOFlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class GenerateDocxInitializer extends ActionInitializer<GenerateDocx, GenerationRepository, CGObject> {

	@SuppressWarnings("unused")
	protected static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	GenerateDocxInitializer(DGControllerActionInitializer actionInitializer) {
		super(GenerateDocx.actionType, actionInitializer);
	}

	@Override
	protected DGControllerActionInitializer getControllerActionInitializer() {
		return (DGControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<GenerateDocx> getDefaultInitializer() {
		return new FlexoActionInitializer<GenerateDocx>() {
			@Override
			public boolean run(ActionEvent e, GenerateDocx action) {
				DGRepository repository = (DGRepository) action.getFocusedObject();
				if (action.getRepository().getDirectory() == null) {
					FlexoController.notify(FlexoLocalization.localizedForKey("please_supply_valid_directory"));
					return false;
				}
				if (repository.getPostBuildDirectory() == null) {
					FlexoController.notify(FlexoLocalization.localizedForKey("please_supply_valid_directory"));
					return false;
				}
				if (!repository.getPostBuildRepository().getDirectory().exists()) {
					if (FlexoController.confirm(FlexoLocalization.localizedForKey("directory") + " "
							+ repository.getPostBuildRepository().getDirectory().getAbsolutePath() + " "
							+ FlexoLocalization.localizedForKey("does_not_exist") + "\n"
							+ FlexoLocalization.localizedForKey("would_you_like_to_create_it_and_continue?"))) {
						repository.getPostBuildRepository().getDirectory().mkdirs();
					} else {
						return false;
					}
				}
				action.setSaveBeforeGenerating(DGPreferences.getSaveBeforeGenerating());
				if (repository.getPostBuildDirectory().exists() && !repository.getPostBuildDirectory().canWrite()) {
					FlexoController.notify(FlexoLocalization.localizedForKey("permission_denied_for ")
							+ repository.getPostBuildDirectory().getAbsolutePath());
					return false;
				}
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<GenerateDocx> getDefaultFinalizer() {
		return new FlexoActionFinalizer<GenerateDocx>() {
			@Override
			public boolean run(ActionEvent e, GenerateDocx action) {
				if (action.getGeneratedDocxFile() != null && action.getGeneratedDocxFile().exists() && DGPreferences.getOpenDocx()) {
					ToolBox.openFile(action.getGeneratedDocxFile());
				}
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<GenerateDocx> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<GenerateDocx>() {
			@Override
			public boolean handleException(FlexoException exception, GenerateDocx action) {
				getControllerActionInitializer().getDGController().disposeProgressWindow();
				if (exception instanceof GenerationException) {
					FlexoController.notify(FlexoLocalization.localizedForKey("generation_failed") + ":\n"
							+ ((GenerationException) exception).getLocalizedMessage());
					return true;
				}
				if (exception instanceof IOFlexoException) {
					FlexoController.notify(FlexoLocalization.localizedForKey("generation_failed") + ":\n"
							+ ((IOFlexoException) exception).getCause().getLocalizedMessage());
					return false;// We don't want the finalizer to be executed.
				}
				logger.log(Level.SEVERE, exception.getMessage(), exception);
				return false;
			}
		};
	}

}
