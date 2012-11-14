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
package org.openflexo.cgmodule.controller.action;

import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.cgmodule.view.popups.SelectFilesPopup;
import org.openflexo.components.MultipleObjectSelectorPopup;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.generator.action.AcceptDiskUpdateAndReinjectInModel;
import org.openflexo.icon.GeneratorIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class AcceptDiskUpdateAndReinjectInModelnitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	AcceptDiskUpdateAndReinjectInModelnitializer(GeneratorControllerActionInitializer actionInitializer) {
		super(AcceptDiskUpdateAndReinjectInModel.actionType, actionInitializer);
	}

	@Override
	protected GeneratorControllerActionInitializer getControllerActionInitializer() {
		return (GeneratorControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<AcceptDiskUpdateAndReinjectInModel> getDefaultInitializer() {
		return new FlexoActionInitializer<AcceptDiskUpdateAndReinjectInModel>() {
			@Override
			public boolean run(EventObject e, AcceptDiskUpdateAndReinjectInModel action) {
				if (action.getFilesToAccept().size() == 0) {
					FlexoController.notify(FlexoLocalization.localizedForKey("no_files_selected"));
					return false;
				} else if (action.getFilesToAccept().size() > 1 || !(action.getFocusedObject() instanceof CGFile)) {
					SelectFilesPopup popup = new SelectFilesPopup(
							FlexoLocalization.localizedForKey("accept_disk_version_and_reinject_in_model"),
							FlexoLocalization.localizedForKey("accept_disk_version_and_reinject_in_model_description"),
							"accept_disk_version_and_reinject_in_model", action.getFilesToAccept(), action.getFocusedObject().getProject(),
							getControllerActionInitializer().getGeneratorController());
					popup.setVisible(true);
					if (popup.getStatus() == MultipleObjectSelectorPopup.VALIDATE && popup.getFileSet().getSelectedFiles().size() > 0) {
						action.setFilesToAccept(popup.getFileSet().getSelectedFiles());
					} else {
						return false;
					}
				} else {
					// 1 occurence, continue without confirmation
				}

				action.getProjectGenerator().startHandleLogs();
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<AcceptDiskUpdateAndReinjectInModel> getDefaultFinalizer() {
		return new FlexoActionFinalizer<AcceptDiskUpdateAndReinjectInModel>() {
			@Override
			public boolean run(EventObject e, AcceptDiskUpdateAndReinjectInModel action) {
				action.getProjectGenerator().stopHandleLogs();
				action.getProjectGenerator().flushLogs();
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<AcceptDiskUpdateAndReinjectInModel> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<AcceptDiskUpdateAndReinjectInModel>() {
			@Override
			public boolean handleException(FlexoException exception, AcceptDiskUpdateAndReinjectInModel action) {
				getControllerActionInitializer().getGeneratorController().disposeProgressWindow();
				exception.printStackTrace();
				FlexoController.showError(FlexoLocalization.localizedForKey("file_accepting_and_reinjecting_failed") + ":\n"
						+ exception.getLocalizedMessage());
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return GeneratorIconLibrary.ACCEPT_AND_REINJECT_ICON;
	}

	@Override
	protected Icon getDisabledIcon() {
		return GeneratorIconLibrary.ACCEPT_AND_REINJECT_DISABLED_ICON;
	}

}
