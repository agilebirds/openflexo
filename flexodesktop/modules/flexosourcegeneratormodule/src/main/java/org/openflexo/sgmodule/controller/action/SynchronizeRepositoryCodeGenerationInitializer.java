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
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.openflexo.FlexoCst;
import org.openflexo.icon.GeneratorIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.sgmodule.SGPreferences;
import org.openflexo.sgmodule.controller.SGController;
import org.openflexo.sgmodule.view.SourceRepositoryModuleView;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.generator.action.DismissUnchangedGeneratedFiles;
import org.openflexo.generator.action.SynchronizeRepositoryCodeGeneration;

public class SynchronizeRepositoryCodeGenerationInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	SynchronizeRepositoryCodeGenerationInitializer(SGControllerActionInitializer actionInitializer) {
		super(SynchronizeRepositoryCodeGeneration.actionType, actionInitializer);
	}

	@Override
	protected SGControllerActionInitializer getControllerActionInitializer() {
		return (SGControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<SynchronizeRepositoryCodeGeneration> getDefaultInitializer() {
		return new FlexoActionInitializer<SynchronizeRepositoryCodeGeneration>() {
			@Override
			public boolean run(ActionEvent e, SynchronizeRepositoryCodeGeneration action) {
				if (action.getRepository().getDirectory() == null) {
					FlexoController.notify(FlexoLocalization.localizedForKey("please_supply_valid_directory"));
					return false;
				}
				if (!action.getRepository().getDirectory().exists()) {
					if (FlexoController.confirm(FlexoLocalization.localizedForKey("directory") + " "
							+ action.getRepository().getDirectory().getAbsolutePath() + " "
							+ FlexoLocalization.localizedForKey("does_not_exist") + "\n"
							+ FlexoLocalization.localizedForKey("would_you_like_to_create_it_and_continue?"))) {
						action.getRepository().getDirectory().mkdirs();
					} else {
						return false;
					}
				}
				action.setSaveBeforeGenerating(SGPreferences.getSaveBeforeGenerating());

				ModuleView cgRepositoryView = getController().moduleViewForObject(action.getRepository());
				if (cgRepositoryView != null && cgRepositoryView instanceof SourceRepositoryModuleView) {
					((SourceRepositoryModuleView) cgRepositoryView).getConsole().clear();
				}

				action.getProjectGenerator().startHandleLogs();
				((SGController) getController()).getBrowser().setHoldStructure();
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<SynchronizeRepositoryCodeGeneration> getDefaultFinalizer() {
		return new FlexoActionFinalizer<SynchronizeRepositoryCodeGeneration>() {
			@Override
			public boolean run(ActionEvent e, SynchronizeRepositoryCodeGeneration action) {
				action.getProjectGenerator().stopHandleLogs();
				action.getProjectGenerator().flushLogs();

				if (SGPreferences.getAutomaticallyDismissUnchangedFiles())
					DismissUnchangedGeneratedFiles.actionType.makeNewAction(action.getFocusedObject(), action.getGlobalSelection(),
							action.getEditor()).doAction();

				((SGController) getController()).getBrowser().resetHoldStructure();
				((SGController) getController()).getBrowser().update();
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<SynchronizeRepositoryCodeGeneration> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<SynchronizeRepositoryCodeGeneration>() {
			@Override
			public boolean handleException(FlexoException exception, SynchronizeRepositoryCodeGeneration action) {
				((SGController) getController()).getBrowser().resetHoldStructure();
				((SGController) getController()).getBrowser().update();
				action.getProjectGenerator().stopHandleLogs();
				getControllerActionInitializer().getSGController().disposeProgressWindow();
				exception.printStackTrace();
				FlexoController.showError(FlexoLocalization.localizedForKey("code_generation_synchronization_for_repository_failed")
						+ ":\n" + exception.getLocalizedMessage());
				return true;
			}
		};
	}

	@Override
	protected KeyStroke getShortcut() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_R, FlexoCst.META_MASK);
	}

	@Override
	protected Icon getEnabledIcon() {
		return GeneratorIconLibrary.SYNCHRONIZE_CODE_GENERATION_ICON;
	}

}
