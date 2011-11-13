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
import org.openflexo.sg.action.ImportInModel;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;

public class ImportInModelInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	ImportInModelInitializer(SGControllerActionInitializer actionInitializer) {
		super(ImportInModel.actionType, actionInitializer);
	}

	@Override
	protected SGControllerActionInitializer getControllerActionInitializer() {
		return (SGControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<ImportInModel> getDefaultInitializer() {
		return new FlexoActionInitializer<ImportInModel>() {
			@Override
			public boolean run(ActionEvent e, ImportInModel action) {
				FlexoController.showError(FlexoLocalization.localizedForKey("sorry_not_implemented_yet"));
				action.getProjectGenerator().startHandleLogs();
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<ImportInModel> getDefaultFinalizer() {
		return new FlexoActionFinalizer<ImportInModel>() {
			@Override
			public boolean run(ActionEvent e, ImportInModel action) {
				action.getProjectGenerator().stopHandleLogs();
				action.getProjectGenerator().flushLogs();
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<ImportInModel> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<ImportInModel>() {
			@Override
			public boolean handleException(FlexoException exception, ImportInModel action) {
				getControllerActionInitializer().getSGController().disposeProgressWindow();
				exception.printStackTrace();
				FlexoController.showError(FlexoLocalization.localizedForKey("file_importing_failed") + ":\n"
						+ exception.getLocalizedMessage());
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return GeneratorIconLibrary.IMPORT_IN_MODEL_ICON;
	}

	@Override
	protected Icon getDisabledIcon() {
		return GeneratorIconLibrary.IMPORT_IN_MODEL_DISABLED_ICON;
	}

}
