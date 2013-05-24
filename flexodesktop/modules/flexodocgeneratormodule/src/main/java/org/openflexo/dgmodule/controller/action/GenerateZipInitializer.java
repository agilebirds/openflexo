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

import java.io.IOException;
import java.util.EventObject;
import java.util.logging.Logger;

import org.openflexo.dgmodule.DGPreferences;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.generator.action.GenerateZip;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class GenerateZipInitializer extends ActionInitializer<GenerateZip, GenerationRepository, CGObject> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	GenerateZipInitializer(DGControllerActionInitializer actionInitializer) {
		super(GenerateZip.actionType, actionInitializer);
	}

	@Override
	protected DGControllerActionInitializer getControllerActionInitializer() {
		return (DGControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<GenerateZip> getDefaultInitializer() {
		return new FlexoActionInitializer<GenerateZip>() {
			@Override
			public boolean run(EventObject e, GenerateZip action) {
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
	protected FlexoActionFinalizer<GenerateZip> getDefaultFinalizer() {
		return new FlexoActionFinalizer<GenerateZip>() {
			@Override
			public boolean run(EventObject e, GenerateZip action) {
				if (action.getGeneratedZipFile() != null && DGPreferences.getShowZIP()) {
					if (action.getGeneratedZipFile() != null && action.getGeneratedZipFile().exists()) {
						try {
							ToolBox.showFileInExplorer(action.getGeneratedZipFile());
						} catch (IOException e1) {
							FlexoController.notify(FlexoLocalization.localizedForKey("could_not_open_file") + " "
									+ action.getGeneratedZipFile().getAbsolutePath());
							return false;
						}
					}
				}
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<GenerateZip> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<GenerateZip>() {
			@Override
			public boolean handleException(FlexoException exception, GenerateZip action) {
				getControllerActionInitializer().getDGController().disposeProgressWindow();
				if (exception instanceof GenerationException) {
					FlexoController.notify(FlexoLocalization.localizedForKey("generation_failed") + ":\n"
							+ ((GenerationException) exception).getLocalizedMessage());
					return true;
				}
				exception.printStackTrace();
				return false;
			}
		};
	}

}
