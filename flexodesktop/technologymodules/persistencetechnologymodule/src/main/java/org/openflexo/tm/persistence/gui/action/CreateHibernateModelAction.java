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
package org.openflexo.tm.persistence.gui.action;

import java.util.Vector;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;

import org.apache.commons.lang.StringUtils;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.InvalidParametersException;
import org.openflexo.foundation.dm.DMRepository;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.tm.persistence.impl.HibernateModel;
import org.openflexo.tm.persistence.impl.PersistenceImplementation;

@SuppressWarnings("serial")
public class CreateHibernateModelAction extends FlexoAction<CreateHibernateModelAction, PersistenceImplementation, PersistenceImplementation> {

	private static final Logger logger = Logger.getLogger(CreateHibernateModelAction.class.getPackage().getName());

	public static FlexoActionType<CreateHibernateModelAction, PersistenceImplementation, PersistenceImplementation> actionType = new FlexoActionType<CreateHibernateModelAction, PersistenceImplementation, PersistenceImplementation>(
			"create_new_persistence_model", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateHibernateModelAction makeNewAction(PersistenceImplementation focusedObject,
				Vector<PersistenceImplementation> globalSelection, FlexoEditor editor) {
			return new CreateHibernateModelAction(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(PersistenceImplementation object, Vector<PersistenceImplementation> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(PersistenceImplementation object, Vector<PersistenceImplementation> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoModelObject.addActionForClass(CreateHibernateModelAction.actionType, PersistenceImplementation.class);
	}

	public String newModelName;
	public DMRepository watchedRepository;

	CreateHibernateModelAction(PersistenceImplementation focusedObject, Vector<PersistenceImplementation> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws InvalidParametersException {
		logger.info("Add hibernate model");

		if (StringUtils.isEmpty(newModelName)) {
			throw new InvalidParametersException("no_hibernate_model_name");
		}

		try {
			HibernateModel.createNewHibernateModel(newModelName, getFocusedObject(), watchedRepository);

			logger.info("Created hibernate model " + newModelName + " for implementation model "
					+ getFocusedObject().getImplementationModel());
		} catch (DuplicateResourceException e) {
			throw new InvalidParametersException("duplicate_hibernate_model_name");
		} catch (InvalidNameException e) {
			throw new InvalidParametersException("invalid_hibernate_model_name");
		}
	}

	public FlexoProject getProject() {
		if (getFocusedObject() != null) {
			return getFocusedObject().getProject();
		}
		return null;
	}
}