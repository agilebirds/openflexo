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
package org.openflexo.sg.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.InvalidParametersException;
import org.openflexo.foundation.sg.implmodel.ImplementationModel;
import org.openflexo.foundation.sg.implmodel.exception.TechnologyModuleCompatibilityCheckException;

public class RemoveImplementationModel extends FlexoAction<RemoveImplementationModel, ImplementationModel, ImplementationModel> {

	private static final Logger logger = Logger.getLogger(RemoveImplementationModel.class.getPackage().getName());

	public static FlexoActionType<RemoveImplementationModel, ImplementationModel, ImplementationModel> actionType = new FlexoActionType<RemoveImplementationModel, ImplementationModel, ImplementationModel>(
			"remove_implementation_model", FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public RemoveImplementationModel makeNewAction(ImplementationModel focusedObject, Vector<ImplementationModel> globalSelection,
				FlexoEditor editor) {
			return new RemoveImplementationModel(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(ImplementationModel object, Vector<ImplementationModel> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(ImplementationModel object, Vector<ImplementationModel> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoModelObject.addActionForClass(RemoveImplementationModel.actionType, ImplementationModel.class);
	}

	RemoveImplementationModel(ImplementationModel focusedObject, Vector<ImplementationModel> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws InvalidParametersException, TechnologyModuleCompatibilityCheckException {
		logger.info("Remove implementation model " + getFocusedObject().getName());

		if (getFocusedObject() != null) {
			getFocusedObject().delete();
		}
	}
}