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

import org.openflexo.dm.view.DMEOEntityView;
import org.openflexo.dm.view.DMEOModelView;
import org.openflexo.dm.view.DMEntityView;
import org.openflexo.dm.view.DMPackageView;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.dm.action.CreateDMProperty;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class CreateDMPropertyInitializer extends ActionInitializer {

	static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	CreateDMPropertyInitializer(DMControllerActionInitializer actionInitializer) {
		super(CreateDMProperty.actionType, actionInitializer);
	}

	@Override
	protected DMControllerActionInitializer getControllerActionInitializer() {
		return (DMControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<CreateDMProperty> getDefaultInitializer() {
		return new FlexoActionInitializer<CreateDMProperty>() {
			@Override
			public boolean run(EventObject e, CreateDMProperty action) {
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<CreateDMProperty> getDefaultFinalizer() {
		return new FlexoActionFinalizer<CreateDMProperty>() {
			@Override
			public boolean run(EventObject e, CreateDMProperty action) {
				if (action.getEntity() instanceof DMEOEntity) {
					if (getControllerActionInitializer().getDMController().getCurrentEditedObject() == ((DMEOEntity) action.getEntity())
							.getDMEOModel()) {
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("Finalizer for CreateDMProperty in DMEOModelView");
						}
						DMEOModelView dmEOModelView = (DMEOModelView) getControllerActionInitializer().getDMController()
								.getCurrentEditedObjectView();
						dmEOModelView.getEoEntityTable().selectObject(action.getEntity());
						dmEOModelView.getPropertyTable().selectObject(action.getNewProperty());
					} else if (getControllerActionInitializer().getDMController().getCurrentEditedObject() == action.getEntity()) {
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("Finalizer for CreateDMProperty in DMEOEntityView");
						}
						DMEOEntityView eoEntityView = (DMEOEntityView) getControllerActionInitializer().getDMController()
								.getCurrentEditedObjectView();
						eoEntityView.getPropertyTable().selectObject(action.getNewProperty());
					}
				} else {
					if (getControllerActionInitializer().getDMController().getCurrentEditedObject() == action.getEntity().getPackage()) {
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("Finalizer for CreateDMProperty in DMPackageView");
						}
						DMPackageView packageView = (DMPackageView) getControllerActionInitializer().getDMController()
								.getCurrentEditedObjectView();
						packageView.getEntityTable().selectObject(action.getEntity());
						packageView.getPropertyTable().selectObject(action.getNewProperty());
					} else if (getControllerActionInitializer().getDMController().getCurrentEditedObject() == action.getEntity()) {
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("Finalizer for CreateDMProperty in DMEntityView");
						}
						DMEntityView entityView = (DMEntityView) getControllerActionInitializer().getDMController()
								.getCurrentEditedObjectView();
						entityView.getPropertyTable().selectObject(action.getNewProperty());
					}
				}
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return DMEIconLibrary.DM_PROPERTY_ICON;
	}

}
