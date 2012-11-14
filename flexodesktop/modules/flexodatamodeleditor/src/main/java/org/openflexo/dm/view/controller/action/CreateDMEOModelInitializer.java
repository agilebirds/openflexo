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
package org.openflexo.dm.view.controller.action;

import java.util.EventObject;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.dm.view.DMEORepositoryView;
import org.openflexo.dm.view.popups.CreatesNewEOModelDialog;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.dm.action.CreateDMEOModel;
import org.openflexo.foundation.dm.eo.InvalidEOModelFileException;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class CreateDMEOModelInitializer extends ActionInitializer {

	static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	CreateDMEOModelInitializer(DMControllerActionInitializer actionInitializer) {
		super(CreateDMEOModel.actionType, actionInitializer);
	}

	@Override
	protected DMControllerActionInitializer getControllerActionInitializer() {
		return (DMControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<CreateDMEOModel> getDefaultInitializer() {
		return new FlexoActionInitializer<CreateDMEOModel>() {
			@Override
			public boolean run(EventObject e, CreateDMEOModel action) {
				return CreatesNewEOModelDialog.displayDialog(action, getControllerActionInitializer().getDMController().getProject(),
						getControllerActionInitializer().getDMController().getFlexoFrame()) == CreatesNewEOModelDialog.VALIDATE;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<CreateDMEOModel> getDefaultFinalizer() {
		return new FlexoActionFinalizer<CreateDMEOModel>() {
			@Override
			public boolean run(EventObject e, CreateDMEOModel action) {
				if (getControllerActionInitializer().getDMController().getCurrentEditedObject() == action.getRepository()) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Finalizer for CreateDMEOModel in DMEORepository");
					}
					DMEORepositoryView repView = (DMEORepositoryView) getControllerActionInitializer().getDMController()
							.getCurrentEditedObjectView();
					repView.getEoModelTable().selectObject(action.getNewDMEOModel());
				}
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<? super CreateDMEOModel> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<CreateDMEOModel>() {

			@Override
			public boolean handleException(FlexoException exception, CreateDMEOModel action) {
				if (exception instanceof InvalidFileNameException || exception instanceof InvalidEOModelFileException) {
					if (action.getEOModelFile() != null) {
						FlexoController.notify(FlexoLocalization.localizedForKey("invalid_file_name") + ": "
								+ action.getEOModelFile().getName());
					} else {
						FlexoController.notify(FlexoLocalization.localizedForKey("invalid_file_name"));
					}
					return true;
				}
				return false;
			}

		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return DMEIconLibrary.DM_EOMODEL_ICON;
	}

}
