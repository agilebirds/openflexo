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
package org.openflexo.technologyadapter.diagram.fml.action;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.technologyadapter.diagram.fml.ExampleDiagramConnector;
import org.openflexo.technologyadapter.diagram.fml.ExampleDiagramObject;
import org.openflexo.technologyadapter.diagram.fml.ExampleDiagramShape;

public class DeleteExampleDiagramElements extends
		FlexoUndoableAction<DeleteExampleDiagramElements, ExampleDiagramObject, ExampleDiagramObject> {

	private static final Logger logger = Logger.getLogger(DeleteExampleDiagramElements.class.getPackage().getName());

	public static FlexoActionType<DeleteExampleDiagramElements, ExampleDiagramObject, ExampleDiagramObject> actionType = new FlexoActionType<DeleteExampleDiagramElements, ExampleDiagramObject, ExampleDiagramObject>(
			"delete", FlexoActionType.editGroup, FlexoActionType.DELETE_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DeleteExampleDiagramElements makeNewAction(ExampleDiagramObject focusedObject, Vector<ExampleDiagramObject> globalSelection,
				FlexoEditor editor) {
			return new DeleteExampleDiagramElements(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(ExampleDiagramObject object, Vector<ExampleDiagramObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(ExampleDiagramObject focusedObject, Vector<ExampleDiagramObject> globalSelection) {
			Vector<ExampleDiagramObject> objectsToDelete = objectsToDelete(focusedObject, globalSelection);
			return objectsToDelete.size() > 0;
		}

	};

	static {
		FlexoModelObject.addActionForClass(DeleteExampleDiagramElements.actionType, ExampleDiagramShape.class);
		FlexoModelObject.addActionForClass(DeleteExampleDiagramElements.actionType, ExampleDiagramConnector.class);
	}

	protected static Vector<ExampleDiagramObject> objectsToDelete(ExampleDiagramObject focusedObject,
			Vector<ExampleDiagramObject> globalSelection) {
		Vector<ExampleDiagramObject> allSelection = new Vector<ExampleDiagramObject>();
		if (globalSelection == null || !globalSelection.contains(focusedObject)) {
			allSelection.add(focusedObject);
		}
		if (globalSelection != null) {
			for (ExampleDiagramObject o : globalSelection) {
				allSelection.add(o);
			}
		}

		Vector<ExampleDiagramObject> returned = new Vector<ExampleDiagramObject>();
		for (ExampleDiagramObject o : allSelection) {
			boolean isContainedByAnOtherObject = false;
			for (ExampleDiagramObject potentielContainer : allSelection) {
				if (potentielContainer != o && o.isContainedIn(potentielContainer)) {
					isContainedByAnOtherObject = true;
					break;
				}
			}
			if (!isContainedByAnOtherObject) {
				returned.add(o);
			}
		}

		return returned;
	}

	protected DeleteExampleDiagramElements(ExampleDiagramObject focusedObject, Vector<ExampleDiagramObject> globalSelection,
			FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		logger.info("Created DeleteCalcShemaElements action focusedObject=" + focusedObject + "globalSelection=" + globalSelection);
	}

	@Override
	protected void doAction(Object context) {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("DeleteCalcShemaElements");
		}
		if (logger.isLoggable(Level.INFO)) {
			logger.info("selection is: " + getGlobalSelection());
		}
		if (logger.isLoggable(Level.INFO)) {
			logger.info("selection to delete is: " + getObjectsToDelete());
		}
		for (ExampleDiagramObject o : getObjectsToDelete()) {
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

	private Vector<ExampleDiagramObject> _objectsToDelete;

	public Vector<ExampleDiagramObject> getObjectsToDelete() {
		if (_objectsToDelete == null) {
			_objectsToDelete = objectsToDelete(getFocusedObject(), getGlobalSelection());
		}
		return _objectsToDelete;
	}

}
