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

import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.dm.view.DMEOModelView;
import org.openflexo.dm.view.DMEORepositoryView;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.dm.action.CreateDMEOEntity;
import org.openflexo.foundation.dm.eo.EOAccessException;
import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class CreateDMEOEntityInitializer extends ActionInitializer {

	static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	CreateDMEOEntityInitializer(DMControllerActionInitializer actionInitializer) {
		super(CreateDMEOEntity.actionType, actionInitializer);
	}

	@Override
	protected DMControllerActionInitializer getControllerActionInitializer() {
		return (DMControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<CreateDMEOEntity> getDefaultInitializer() {
		return new FlexoActionInitializer<CreateDMEOEntity>() {
			@Override
			public boolean run(ActionEvent e, CreateDMEOEntity action) {
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<CreateDMEOEntity> getDefaultFinalizer() {
		return new FlexoActionFinalizer<CreateDMEOEntity>() {
			@Override
			public boolean run(ActionEvent e, CreateDMEOEntity action) {
				if (getControllerActionInitializer().getDMController().getCurrentEditedObject() == action.getRepository()) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Finalizer for CreateDMEOEntity in DMEORepositoryView");
					}
					DMEORepositoryView repView = (DMEORepositoryView) getControllerActionInitializer().getDMController()
							.getCurrentEditedObjectView();
					repView.getEoModelTable().selectObject(action.getDMEOModel());
					repView.getEoEntityTable().selectObject(action.getNewEntity());
				} else if (getControllerActionInitializer().getDMController().getCurrentEditedObject() == action.getDMEOModel()) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Finalizer for CreateDMEOEntity in DMPackageView");
					}
					DMEOModelView eomodelView = (DMEOModelView) getControllerActionInitializer().getDMController()
							.getCurrentEditedObjectView();
					eomodelView.getEoEntityTable().selectObject(action.getNewEntity());
				}
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<CreateDMEOEntity> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<CreateDMEOEntity>() {
			@Override
			public boolean handleException(FlexoException exception, CreateDMEOEntity action) {
				if (exception instanceof EOAccessException) {
					FlexoController.showError(exception.getMessage());
					return true;
				}
				return false;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return DMEIconLibrary.DM_EOENTITY_ICON;
	}

}
