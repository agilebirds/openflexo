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
package org.openflexo.foundation.viewpoint.action;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.foundation.viewpoint.ExampleDrawingConnector;
import org.openflexo.foundation.viewpoint.ExampleDrawingObject;
import org.openflexo.foundation.viewpoint.ExampleDrawingShape;

public class DeleteExampleDrawingElements extends
		FlexoUndoableAction<DeleteExampleDrawingElements, ExampleDrawingObject, ExampleDrawingObject> {

	private static final Logger logger = Logger.getLogger(DeleteExampleDrawingElements.class.getPackage().getName());

	public static FlexoActionType<DeleteExampleDrawingElements, ExampleDrawingObject, ExampleDrawingObject> actionType = new FlexoActionType<DeleteExampleDrawingElements, ExampleDrawingObject, ExampleDrawingObject>(
			"delete", FlexoActionType.editGroup, FlexoActionType.DELETE_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DeleteExampleDrawingElements makeNewAction(ExampleDrawingObject focusedObject, Vector<ExampleDrawingObject> globalSelection,
				FlexoEditor editor) {
			return new DeleteExampleDrawingElements(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(ExampleDrawingObject object, Vector<ExampleDrawingObject> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(ExampleDrawingObject focusedObject, Vector<ExampleDrawingObject> globalSelection) {
			Vector<ExampleDrawingObject> objectsToDelete = objectsToDelete(focusedObject, globalSelection);
			return (objectsToDelete.size() > 0);
		}

	};

	static {
		FlexoModelObject.addActionForClass(DeleteExampleDrawingElements.actionType, ExampleDrawingShape.class);
		FlexoModelObject.addActionForClass(DeleteExampleDrawingElements.actionType, ExampleDrawingConnector.class);
	}

	protected static Vector<ExampleDrawingObject> objectsToDelete(ExampleDrawingObject focusedObject,
			Vector<ExampleDrawingObject> globalSelection) {
		Vector<ExampleDrawingObject> allSelection = new Vector<ExampleDrawingObject>();
		if (globalSelection == null || !globalSelection.contains(focusedObject)) {
			allSelection.add(focusedObject);
		}
		if (globalSelection != null) {
			for (ExampleDrawingObject o : globalSelection) {
				allSelection.add(o);
			}
		}

		Vector<ExampleDrawingObject> returned = new Vector<ExampleDrawingObject>();
		for (ExampleDrawingObject o : allSelection) {
			boolean isContainedByAnOtherObject = false;
			for (ExampleDrawingObject potentielContainer : allSelection) {
				if (potentielContainer != o && o.isContainedIn(potentielContainer)) {
					isContainedByAnOtherObject = true;
					break;
				}
			}
			if (!isContainedByAnOtherObject)
				returned.add(o);
		}

		return returned;
	}

	protected DeleteExampleDrawingElements(ExampleDrawingObject focusedObject, Vector<ExampleDrawingObject> globalSelection,
			FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		logger.info("Created DeleteCalcShemaElements action focusedObject=" + focusedObject + "globalSelection=" + globalSelection);
	}

	@Override
	protected void doAction(Object context) {
		if (logger.isLoggable(Level.INFO))
			logger.info("DeleteCalcShemaElements");
		if (logger.isLoggable(Level.INFO))
			logger.info("selection is: " + getGlobalSelection());
		if (logger.isLoggable(Level.INFO))
			logger.info("selection to delete is: " + getObjectsToDelete());
		for (ExampleDrawingObject o : getObjectsToDelete()) {
			o.delete();
		}
	}

	@Override
	protected void undoAction(Object context) {
		logger.warning("UNDO DELETE not implemented yet !");
	}

	@Override
	protected void redoAction(Object context) {
		logger.warning("REDO DELETE not implemented yet !");
	}

	private Vector<ExampleDrawingObject> _objectsToDelete;

	public Vector<ExampleDrawingObject> getObjectsToDelete() {
		if (_objectsToDelete == null) {
			_objectsToDelete = objectsToDelete(getFocusedObject(), getGlobalSelection());
		}
		return _objectsToDelete;
	}

}
