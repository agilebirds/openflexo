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
import org.openflexo.foundation.AgileBirdsObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.foundation.wkf.ActionPetriGraph;
import org.openflexo.foundation.wkf.ActivityPetriGraph;
import org.openflexo.foundation.wkf.FlexoPetriGraph;
import org.openflexo.foundation.wkf.OperationPetriGraph;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.node.PetriGraphNode;
import org.openflexo.foundation.wkf.node.SelfExecutableActionNode;
import org.openflexo.foundation.wkf.node.SelfExecutableActivityNode;
import org.openflexo.foundation.wkf.node.SelfExecutableNode;
import org.openflexo.foundation.wkf.node.SelfExecutableOperationNode;

public class CreateExecutionPetriGraph extends FlexoUndoableAction<CreateExecutionPetriGraph, PetriGraphNode, WKFObject> {

	private static final Logger logger = Logger.getLogger(CreateExecutionPetriGraph.class.getPackage().getName());

	public static FlexoActionType<CreateExecutionPetriGraph, PetriGraphNode, WKFObject> actionType = new FlexoActionType<CreateExecutionPetriGraph, PetriGraphNode, WKFObject>(
			"create_petri_graph", FlexoActionType.defaultGroup) {

		/**
		 * Factory method
		 */
		@Override
		public CreateExecutionPetriGraph makeNewAction(PetriGraphNode focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
			return new CreateExecutionPetriGraph(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(PetriGraphNode object, Vector<WKFObject> globalSelection) {
			return false; // Action is never visible but always active.
		}

		@Override
		public boolean isEnabledForSelection(PetriGraphNode object, Vector<WKFObject> globalSelection) {
			return object instanceof SelfExecutableNode && ((SelfExecutableNode) object).getExecutionPetriGraph() == null;
		}

	};

	static {
		AgileBirdsObject.addActionForClass(CreateExecutionPetriGraph.actionType, SelfExecutableActivityNode.class);
		AgileBirdsObject.addActionForClass(CreateExecutionPetriGraph.actionType, SelfExecutableOperationNode.class);
		AgileBirdsObject.addActionForClass(CreateExecutionPetriGraph.actionType, SelfExecutableActionNode.class);
	}

	CreateExecutionPetriGraph(PetriGraphNode focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	public SelfExecutableNode getSelfExecutableNode() {
		return (SelfExecutableNode) getFocusedObject();
	}

	@Override
	protected void doAction(Object context) {
		SelfExecutableNode node = getSelfExecutableNode();
		if (node.getExecutionPetriGraph() == null) {
			logger.info("CreatePetriGraph");
			if (node instanceof SelfExecutableActivityNode) {
				newPetriGraph = ActivityPetriGraph.createNewActivityPetriGraph((SelfExecutableActivityNode) node);
			} else if (node instanceof SelfExecutableOperationNode) {
				newPetriGraph = OperationPetriGraph.createNewOperationPetriGraph((SelfExecutableOperationNode) node);
			} else if (node instanceof SelfExecutableActionNode) {
				newPetriGraph = ActionPetriGraph.createNewActionPetriGraph((SelfExecutableActionNode) node);
			}
			CreateEdge createEdge = CreateEdge.actionType
					.makeNewEmbeddedAction(newPetriGraph.getAllBeginNodes().firstElement(), null, this);
			createEdge.setStartingNode(newPetriGraph.getAllBeginNodes().firstElement());
			createEdge.setEndNode(newPetriGraph.getAllEndNodes().firstElement());
			createEdge.doAction();
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
