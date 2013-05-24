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
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.openflexo.dm.view.DMModelView;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.action.CreateDMRepository;
import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public abstract class CreateDMRepositoryInitializer<A extends CreateDMRepository<A>> extends ActionInitializer<A, DMObject, DMObject> {

	static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	CreateDMRepositoryInitializer(FlexoActionType<A, DMObject, DMObject> actionType, DMControllerActionInitializer actionInitializer) {
		super(actionType, actionInitializer);
	}

	@Override
	protected DMControllerActionInitializer getControllerActionInitializer() {
		return (DMControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionFinalizer<A> getDefaultFinalizer() {
		return new FlexoActionFinalizer<A>() {
			@Override
			public boolean run(EventObject e, A action) {
				if (action.getNewRepository() != null) {
					logger.info("Finalizer for CreateDMRepository in DMModelView with " + action.getNewRepository());
					if (getControllerActionInitializer().getDMController().getCurrentEditedObject() == action.getNewRepository()
							.getDMModel()) {
						DMModelView dmModelView = (DMModelView) getControllerActionInitializer().getDMController()
								.getCurrentEditedObjectView();
						dmModelView.getRepositoryFolderTable().selectObject(action.getNewRepository().getRepositoryFolder());
						dmModelView.getRepositoriesTable().selectObject(action.getNewRepository());
					}
				}
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return DMEIconLibrary.DM_REPOSITORY_ICON;
	}

	@Override
	protected KeyStroke getShortcut() {
		return null;
	}

}
