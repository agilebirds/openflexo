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

import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.openflexo.FlexoCst;
import org.openflexo.components.MultipleObjectSelectorPopup;
import org.openflexo.dgmodule.DGPreferences;
import org.openflexo.dgmodule.controller.DGController;
import org.openflexo.dgmodule.view.popups.SelectFilesPopup;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.generator.action.WriteModifiedGeneratedFiles;
import org.openflexo.icon.GeneratorIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class WriteModifiedGeneratedFilesInitializer extends ActionInitializer<WriteModifiedGeneratedFiles, CGObject, CGObject> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	WriteModifiedGeneratedFilesInitializer(DGControllerActionInitializer actionInitializer) {
		super(WriteModifiedGeneratedFiles.actionType, actionInitializer);
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
	protected FlexoActionInitializer<WriteModifiedGeneratedFiles> getDefaultInitializer() {
		return new FlexoActionInitializer<WriteModifiedGeneratedFiles>() {
			@Override
			public boolean run(EventObject e, WriteModifiedGeneratedFiles action) {
				if (action.getFilesToWrite().size() == 0) {
					FlexoController.notify(FlexoLocalization.localizedForKey("no_files_selected"));
					return false;
				} else if ((action.getFilesToWrite().size() > 1 || !(action.getFocusedObject() instanceof CGFile))
						&& !(e instanceof KeyEvent)) {
					SelectFilesPopup popup = new SelectFilesPopup(FlexoLocalization.localizedForKey("write_modified_files_to_disk"),
							FlexoLocalization.localizedForKey("write_modified_files_to_disk_description"), "write_to_disk",
							action.getFilesToWrite(), action.getFocusedObject().getProject(), getControllerActionInitializer()
									.getDGController());
					try {
						popup.setVisible(true);
						if (popup.getStatus() == MultipleObjectSelectorPopup.VALIDATE && popup.getFileSet().getSelectedFiles().size() > 0) {
							action.setFilesToWrite(popup.getFileSet().getSelectedFiles());
							action.setSaveBeforeGenerating(DGPreferences.getSaveBeforeGenerating());
						} else {
							return false;
						}
					} finally {
						popup.delete();
					}
				} else {
					// 1 occurence, continue without confirmation
				}
				action.setSaveBeforeGenerating(DGPreferences.getSaveBeforeGenerating());
				getController().getBrowser().setHoldStructure();
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<WriteModifiedGeneratedFiles> getDefaultFinalizer() {
		return new FlexoActionFinalizer<WriteModifiedGeneratedFiles>() {
			@Override
			public boolean run(EventObject e, WriteModifiedGeneratedFiles action) {
				getController().getBrowser().resetHoldStructure();
				getController().getBrowser().update();
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<WriteModifiedGeneratedFiles> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<WriteModifiedGeneratedFiles>() {
			@Override
			public boolean handleException(FlexoException exception, WriteModifiedGeneratedFiles action) {
				getController().getBrowser().resetHoldStructure();
				getController().getBrowser().update();
				getControllerActionInitializer().getDGController().disposeProgressWindow();
				exception.printStackTrace();
				FlexoController.showError(FlexoLocalization.localizedForKey("file_writing_failed") + ":\n"
						+ exception.getLocalizedMessage());
				getController().getBrowser().resetHoldStructure();
				getController().getBrowser().update();
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return GeneratorIconLibrary.WRITE_TO_DISK_ICON;
	}

	@Override
	protected Icon getDisabledIcon() {
		return GeneratorIconLibrary.WRITE_TO_DISK_DISABLED_ICON;
	}

	@Override
	protected KeyStroke getShortcut() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_D, FlexoCst.META_MASK);
	}
}
