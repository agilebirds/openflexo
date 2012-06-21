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

import org.openflexo.dm.model.DMMethodTableModel;
import org.openflexo.dm.view.DMEOEntityView;
import org.openflexo.dm.view.DMEOModelView;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.dm.action.DuplicateDMMethod;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class DuplicateDMMethodInitializer extends ActionInitializer {

	static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	DuplicateDMMethodInitializer(DMControllerActionInitializer actionInitializer) {
		super(DuplicateDMMethod.actionType, actionInitializer);
	}

	@Override
	protected DMControllerActionInitializer getControllerActionInitializer() {
		return (DMControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<DuplicateDMMethod> getDefaultInitializer() {
		return new FlexoActionInitializer<DuplicateDMMethod>() {
			@Override
			public boolean run(EventObject e, DuplicateDMMethod action) {
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<DuplicateDMMethod> getDefaultFinalizer() {
		return new FlexoActionFinalizer<DuplicateDMMethod>() {
			@Override
			public boolean run(EventObject e, DuplicateDMMethod action) {
				if (action.getMethodToDuplicate().getEntity() instanceof DMEOEntity
						&& getControllerActionInitializer().getDMController().getCurrentEditedObject() == ((DMEOEntity) action
								.getMethodToDuplicate().getEntity()).getDMEOModel()) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Finalizer for DuplicateDMMethod in DMEOModelView");
					}
					DMEOModelView dmEOModelView = (DMEOModelView) getControllerActionInitializer().getDMController()
							.getCurrentEditedObjectView();
					dmEOModelView.getEoEntityTable().selectObject(action.getMethodToDuplicate().getEntity());
					dmEOModelView.getMethodTable().selectObject(action.getNewMethod());
				} else if (getControllerActionInitializer().getDMController().getCurrentEditedObject() == action.getMethodToDuplicate()
						.getEntity()) {
					if (logger.isLoggable(Level.INFO)) {
						logger.info("Finalizer for DuplicateDMMethod in DMEOEntityView");
					}
					DMEOEntityView eoEntityView = (DMEOEntityView) getControllerActionInitializer().getDMController()
							.getCurrentEditedObjectView();
					eoEntityView.getMethodTable().selectObject(action.getNewMethod());
					eoEntityView.getMethodTable()
							.editCellAt(
									eoEntityView.getMethodTable().getModel()
											.getIndexForColumnWithName(DMMethodTableModel.METHOD_NAME_COLUMN_TITLE),
									eoEntityView.getMethodTable().getModel().indexOf(action.getNewMethod()));
				}
				return true;
			}
		};
	}

}
