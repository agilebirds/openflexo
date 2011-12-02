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
import org.openflexo.foundation.wkf.ActionPetriGraph;
import org.openflexo.foundation.wkf.ActivityPetriGraph;
import org.openflexo.foundation.wkf.FlexoLevel;
import org.openflexo.foundation.wkf.FlexoPetriGraph;
import org.openflexo.foundation.wkf.OperationPetriGraph;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.node.LOOPOperator;
import org.openflexo.foundation.wkf.node.SelfExecutableNode;

public class CreateLoopedPetriGraph extends FlexoUndoableAction<CreateLoopedPetriGraph, LOOPOperator, WKFObject> {

	private static final Logger logger = Logger.getLogger(CreateLoopedPetriGraph.class.getPackage().getName());

	public static FlexoActionType<CreateLoopedPetriGraph, LOOPOperator, WKFObject> actionType = new FlexoActionType<CreateLoopedPetriGraph, LOOPOperator, WKFObject>(
			"create_petri_graph", FlexoActionType.defaultGroup) {

		/**
		 * Factory method
		 */
		@Override
		public CreateLoopedPetriGraph makeNewAction(LOOPOperator focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
			return new CreateLoopedPetriGraph(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(LOOPOperator object, Vector<WKFObject> globalSelection) {
			return false; // Action is never visible but always active.
		}

		@Override
		protected boolean isEnabledForSelection(LOOPOperator object, Vector<WKFObject> globalSelection) {
			return object instanceof SelfExecutableNode && ((SelfExecutableNode) object).getExecutionPetriGraph() == null;
		}

	};

	static {
		FlexoModelObject.addActionForClass(CreateLoopedPetriGraph.actionType, LOOPOperator.class);
	}

	CreateLoopedPetriGraph(LOOPOperator focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	public LOOPOperator getLOOPOperator() {
		return getFocusedObject();
	}

	@Override
	protected void doAction(Object context) {
		LOOPOperator loop = getLOOPOperator();
		if (loop.getExecutionPetriGraph() == null) {
			logger.info("CreatePetriGraph");
			if (loop.getLevel() == FlexoLevel.ACTIVITY) {
				newPetriGraph = ActivityPetriGraph.createNewActivityPetriGraph(loop);
			} else if (loop.getLevel() == FlexoLevel.OPERATION) {
				newPetriGraph = OperationPetriGraph.createNewOperationPetriGraph(loop);
			} else if (loop.getLevel() == FlexoLevel.ACTION) {
				newPetriGraph = ActionPetriGraph.createNewActionPetriGraph(loop);
			}
			System.out.println("newPetriGraph=" + newPetriGraph + " of " + newPetriGraph.getClass());
			System.out.println("getAllBeginNodes()=" + newPetriGraph.getAllBeginNodes());
			System.out.println("getAllEndNodes()=" + newPetriGraph.getAllEndNodes());
			objectCreated("NEW_PETRI_GRAPH", newPetriGraph);
			objectCreated("NEW_BEGIN_NODE", newPetriGraph.getAllBeginNodes().firstElement());
			objectCreated("NEW_END_NODE", newPetriGraph.getAllEndNodes().firstElement());
		}
	}

	@Override
	protected void undoAction(Object context) {
		logger.info("CreatePetriGraph: UNDO");
		newPetriGraph.delete();
	}

	@Override
	protected void redoAction(Object context) {
		logger.info("CreatePetriGraph: REDO");
		doAction(context);
	}

	private FlexoPetriGraph newPetriGraph = null;

	public FlexoPetriGraph getNewPetriGraph() {
		return newPetriGraph;
	}

}
