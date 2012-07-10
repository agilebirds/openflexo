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

import java.awt.event.KeyEvent;
import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import org.openflexo.FlexoCst;
import org.openflexo.dgmodule.DGPreferences;
import org.openflexo.dgmodule.controller.DGController;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.generator.action.DismissUnchangedGeneratedFiles;
import org.openflexo.generator.action.SynchronizeRepositoryCodeGeneration;
import org.openflexo.generator.exception.PermissionDeniedException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class SynchronizeRepositoryCodeGenerationInitializer extends
		ActionInitializer<SynchronizeRepositoryCodeGeneration, GenerationRepository, CGObject> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	SynchronizeRepositoryCodeGenerationInitializer(DGControllerActionInitializer actionInitializer) {
		super(SynchronizeRepositoryCodeGeneration.actionType, actionInitializer);
	}

	@Override
	public DGController getController() {
		return (DGController) super.getController();
	}

	@Override
	protected DGControllerActionInitializer getControllerActionInitializer() {
		return (DGControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<SynchronizeRepositoryCodeGeneration> getDefaultInitializer() {
		return new FlexoActionInitializer<SynchronizeRepositoryCodeGeneration>() {
			@Override
			public boolean run(EventObject e, SynchronizeRepositoryCodeGeneration action) {
				getController().getDgBrowserView().getBrowser().setHoldStructure();
				if (action.getRepository().getDirectory() == null) {
					FlexoController.notify(FlexoLocalization.localizedForKey("please_supply_valid_directory"));
					return false;
				}
				action.setSaveBeforeGenerating(DGPreferences.getSaveBeforeGenerating());
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<SynchronizeRepositoryCodeGeneration> getDefaultFinalizer() {
		return new FlexoActionFinalizer<SynchronizeRepositoryCodeGeneration>() {
			@Override
			public boolean run(EventObject e, SynchronizeRepositoryCodeGeneration action) {
				getController().getDgBrowserView().getBrowser().resetHoldStructure();
				getController().getDgBrowserView().getBrowser().update();
				if (DGPreferences.getAutomaticallyDismissUnchangedFiles()) {
					DismissUnchangedGeneratedFiles.actionType.makeNewAction(action.getFocusedObject(), action.getGlobalSelection(),
							action.getEditor()).doAction();
				}
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<SynchronizeRepositoryCodeGeneration> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<SynchronizeRepositoryCodeGeneration>() {
			@Override
			public boolean handleException(FlexoException exception, SynchronizeRepositoryCodeGeneration action) {
				getController().getDgBrowserView().getBrowser().resetHoldStructure();
				getController().getDgBrowserView().getBrowser().update();
				if (exception instanceof PermissionDeniedException) {
					if (action.getRepository().getDirectory() != null && !action.getRepository().getDirectory().exists()) {
						if (FlexoController.confirm(FlexoLocalization.localizedForKey("directory") + " "
								+ action.getRepository().getDirectory().getAbsolutePath() + " "
								+ FlexoLocalization.localizedForKey("does_no_exists") + ". "
								+ FlexoLocalization.localizedForKey("do_you_want_to_create_it"))) {
							if (action.getRepository().getDirectory().mkdirs()) {
								SynchronizeRepositoryCodeGeneration.actionType.makeNewAction(action.getFocusedObject(),
										action.getGlobalSelection(), action.getEditor()).doAction();
							}
						}
					} else {
						if (action.getRepository().getDirectory() != null) {
							FlexoController.notify(FlexoLocalization.localizedForKey("permission_denied_for") + " "
									+ action.getRepository().getDirectory().getAbsolutePath());
						} else {
							FlexoController.notify(FlexoLocalization.localizedForKey("select_a_directory"));
						}
					}
					return true;
				}
				getControllerActionInitializer().getDGController().disposeProgressWindow();
				exception.printStackTrace();
				FlexoController.showError(FlexoLocalization.localizedForKey("doc_generation_synchronization_for_repository_failed") + ":\n"
						+ exception.getLocalizedMessage());
				return true;
			}
		};
	}

	@Override
	protected KeyStroke getShortcut() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_R, FlexoCst.META_MASK);
	}
}
