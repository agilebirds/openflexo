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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.openflexo.FlexoCst;
import org.openflexo.cgmodule.view.popups.SelectFilesPopup;
import org.openflexo.components.MultipleObjectSelectorPopup;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.generator.action.AcceptDiskUpdate;
import org.openflexo.icon.GeneratorIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class AcceptDiskUpdateInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	AcceptDiskUpdateInitializer(GeneratorControllerActionInitializer actionInitializer) {
		super(AcceptDiskUpdate.actionType, actionInitializer);
	}

	@Override
	protected GeneratorControllerActionInitializer getControllerActionInitializer() {
		return (GeneratorControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<AcceptDiskUpdate> getDefaultInitializer() {
		return new FlexoActionInitializer<AcceptDiskUpdate>() {
			@Override
			public boolean run(ActionEvent e, AcceptDiskUpdate action) {
				if (action.getFilesToAccept().size() == 0) {
					FlexoController.notify(FlexoLocalization.localizedForKey("no_files_selected"));
					return false;
				} else if (action.getFilesToAccept().size() > 1 || (!(action.getFocusedObject() instanceof CGFile))) {
					SelectFilesPopup popup = new SelectFilesPopup(FlexoLocalization.localizedForKey("accept_disk_version"),
							FlexoLocalization.localizedForKey("accept_disk_version_description"), "accept_disk_version",
							action.getFilesToAccept(), action.getFocusedObject().getProject(), getControllerActionInitializer()
									.getGeneratorController());
					popup.setVisible(true);
					if ((popup.getStatus() == MultipleObjectSelectorPopup.VALIDATE) && (popup.getFileSet().getSelectedFiles().size() > 0)) {
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
	protected FlexoActionFinalizer<AcceptDiskUpdate> getDefaultFinalizer() {
		return new FlexoActionFinalizer<AcceptDiskUpdate>() {
			@Override
			public boolean run(ActionEvent e, AcceptDiskUpdate action) {
				action.getProjectGenerator().stopHandleLogs();
				action.getProjectGenerator().flushLogs();
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<AcceptDiskUpdate> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<AcceptDiskUpdate>() {
			@Override
			public boolean handleException(FlexoException exception, AcceptDiskUpdate action) {
				getControllerActionInitializer().getGeneratorController().disposeProgressWindow();
				exception.printStackTrace();
				if (exception.getCause() != null) {
					FlexoController.showError(FlexoLocalization.localizedForKey("file_accepting_failed") + ":\n"
							+ exception.getCause().getLocalizedMessage());
				} else {
					FlexoController.showError(FlexoLocalization.localizedForKey("file_accepting_failed") + ":\n" + exception.getMessage());
				}
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return GeneratorIconLibrary.ACCEPT_FROM_DISK_ICON;
	}

	@Override
	protected Icon getDisabledIcon() {
		return GeneratorIconLibrary.ACCEPT_FROM_DISK_DISABLED_ICON;
	}

	@Override
	protected KeyStroke getShortcut() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_U, FlexoCst.META_MASK);
	}
}
