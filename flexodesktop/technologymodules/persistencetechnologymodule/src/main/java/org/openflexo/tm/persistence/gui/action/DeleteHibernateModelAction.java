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

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.InvalidParametersException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.tm.persistence.impl.HibernateModel;

@SuppressWarnings("serial")
public class DeleteHibernateModelAction extends FlexoAction<DeleteHibernateModelAction, HibernateModel, HibernateModel> {

	private static final Logger logger = Logger.getLogger(DeleteHibernateModelAction.class.getPackage().getName());

	public static FlexoActionType<DeleteHibernateModelAction, HibernateModel, HibernateModel> actionType = new FlexoActionType<DeleteHibernateModelAction, HibernateModel, HibernateModel>("delete",
			FlexoActionType.defaultGroup, FlexoActionType.DELETE_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DeleteHibernateModelAction makeNewAction(HibernateModel focusedObject, Vector<HibernateModel> globalSelection, FlexoEditor editor) {
			return new DeleteHibernateModelAction(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(HibernateModel object, Vector<HibernateModel> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(HibernateModel object, Vector<HibernateModel> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoModelObject.addActionForClass(DeleteHibernateModelAction.actionType, HibernateModel.class);
	}

	DeleteHibernateModelAction(HibernateModel focusedObject, Vector<HibernateModel> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws InvalidParametersException {
		logger.info("Delete hibernate model");

		getFocusedObject().delete();

		logger.info("Hibernate model " + getFocusedObject().getName() + " deleted");
	}

	public FlexoProject getProject() {
		if (getFocusedObject() != null)
			return getFocusedObject().getProject();
		return null;
	}
}