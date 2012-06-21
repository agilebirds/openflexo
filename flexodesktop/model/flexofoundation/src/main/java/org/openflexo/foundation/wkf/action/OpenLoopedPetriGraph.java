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
package org.openflexo.foundation.wkf.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.foundation.wkf.FlexoPetriGraph;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.dm.PetriGraphHasBeenClosed;
import org.openflexo.foundation.wkf.dm.PetriGraphHasBeenOpened;
import org.openflexo.foundation.wkf.node.LOOPOperator;
import org.openflexo.localization.FlexoLocalization;

public class OpenLoopedPetriGraph extends FlexoUndoableAction<OpenLoopedPetriGraph, LOOPOperator, WKFObject> {

	private static final Logger logger = Logger.getLogger(OpenLoopedPetriGraph.class.getPackage().getName());

	public static FlexoActionType<OpenLoopedPetriGraph, LOOPOperator, WKFObject> actionType = new FlexoActionType<OpenLoopedPetriGraph, LOOPOperator, WKFObject>(
			"open_execution_level", FlexoActionType.defaultGroup) {

		/**
		 * Factory method
		 */
		@Override
		public OpenLoopedPetriGraph makeNewAction(LOOPOperator focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
			return new OpenLoopedPetriGraph(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(LOOPOperator object, Vector<WKFObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(LOOPOperator object, Vector<WKFObject> globalSelection) {
			return isVisibleForSelection(object, globalSelection);
		}

	};

	static {
		FlexoModelObject.addActionForClass(OpenLoopedPetriGraph.actionType, LOOPOperator.class);
	}

	OpenLoopedPetriGraph(LOOPOperator focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		if (getExecutionPetriGraph() == null) {
			// We use here a null editor because this action is embedded
			_createPetriGraph = CreateLoopedPetriGraph.actionType.makeNewEmbeddedAction(getFocusedObject(), null, this).doAction();
			getExecutionPetriGraph().setIsVisible(false);
		}
		logger.info("OpenLoopedPetriGraph");
		if (getExecutionPetriGraph() != null) {
			if (getExecutionPetriGraph().getIsVisible()) {
				getExecutionPetriGraph().setIsVisible(false);
				getFocusedObject().setChanged();
				getFocusedObject().notifyObservers(new PetriGraphHasBeenClosed(getExecutionPetriGraph()));
			} else {
				getExecutionPetriGraph().setIsVisible(true);
				getFocusedObject().setChanged();
				getFocusedObject().notifyObservers(new PetriGraphHasBeenOpened(getExecutionPetriGraph()));
				getExecutionPetriGraph().setChanged();
				getExecutionPetriGraph().notifyObservers(new PetriGraphHasBeenOpened(getExecutionPetriGraph()));
			}
		}
	}

	/**
	 * @return
	 */
	public FlexoPetriGraph getExecutionPetriGraph() {
		return getLOOPOperator().getExecutionPetriGraph();
	}

	public LOOPOperator getLOOPOperator() {
		return getFocusedObject();
	}

	@Override
	public String getLocalizedName() {
		if ((getLOOPOperator()).getExecutionPetriGraph() != null) {
			if ((getLOOPOperator()).getExecutionPetriGraph().getIsVisible()) {
				return FlexoLocalization.localizedForKey("close_loop_execution_level");
			} else {
				return FlexoLocalization.localizedForKey("open_loop_execution_level");
			}
		}
		return super.getLocalizedName();

	}

	@Override
	protected void undoAction(Object context) {
		doAction(context);
	}

	@Override
	protected void redoAction(Object context) {
		doAction(context);
	}

	private CreateLoopedPetriGraph _createPetriGraph = null;
}
