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
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.AgileBirdsObject;
import org.openflexo.foundation.InvalidArgumentException;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.foundation.wkf.ActionPetriGraph;
import org.openflexo.foundation.wkf.ActivityPetriGraph;
import org.openflexo.foundation.wkf.FlexoPetriGraph;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.OperationPetriGraph;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.FatherNode;
import org.openflexo.foundation.wkf.node.FlexoNode;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.PetriGraphNode;
import org.openflexo.foundation.wkf.node.SelfExecutableNode;

public class CreateNode extends FlexoUndoableAction<CreateNode, WKFObject, WKFObject> {

	private static final Logger logger = Logger.getLogger(CreateNode.class.getPackage().getName());

	private static enum CreatedNodeType {
		BEGIN_ACTIVITY,
		END_ACTIVITY,
		NORMAL_ACTIVITY,
		BEGIN_OPERATION,
		END_OPERATION,
		NORMAL_OPERATION,
		BEGIN_ACTION,
		END_ACTION,
		NORMAL_ACTION;

		boolean concernProcess() {
			return this == BEGIN_ACTIVITY || this == END_ACTIVITY || this == NORMAL_ACTIVITY;
		}

		boolean concernActivity() {
			return this == BEGIN_OPERATION || this == END_OPERATION || this == NORMAL_OPERATION;
		}

		boolean concernOperation() {
			return this == BEGIN_ACTION || this == END_ACTION || this == NORMAL_ACTION;
		}
	}

	public static class CreateNodeActionType extends FlexoActionType<CreateNode, WKFObject, WKFObject> {
		private CreatedNodeType _type;

		protected CreateNodeActionType(String actionName, CreatedNodeType type) {
			super(actionName, FlexoActionType.newMenu, FlexoActionType.newMenuGroup2, FlexoActionType.ADD_ACTION_TYPE);
			_type = type;
		}

		/**
		 * Factory method
		 */
		@Override
		public CreateNode makeNewAction(WKFObject focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
			return new CreateNode(this, focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(WKFObject focusedObject, Vector<WKFObject> globalSelection) {
			return false;
		}

		@Override
		public boolean isEnabledForSelection(WKFObject focusedObject, Vector<WKFObject> globalSelection) {
			if (focusedObject instanceof SelfExecutableNode) {
				return false;
			}
			if (focusedObject instanceof FlexoProcess) {
				return _type.concernProcess();
			}
			if (focusedObject instanceof AbstractActivityNode) {
				return _type.concernActivity() && ((AbstractActivityNode) focusedObject).mightHaveOperationPetriGraph();
			}
			if (focusedObject instanceof OperationNode) {
				return _type.concernOperation() && ((OperationNode) focusedObject).mightHaveActionPetriGraph();
			}
			return false;
		}

		public CreatedNodeType getCreatedNodeType() {
			return _type;
		}

		private String[] persistentProperties = { "createdNodeType", "newNodeName" };

		@Override
		protected String[] getPersistentProperties() {
			return persistentProperties;
		}

	}

	public static final CreateNodeActionType createActivityBeginNode = new CreateNodeActionType("create_begin_node",
			CreatedNodeType.BEGIN_ACTIVITY);
	public static final CreateNodeActionType createActivityEndNode = new CreateNodeActionType("create_end_node",
			CreatedNodeType.END_ACTIVITY);
	public static final CreateNodeActionType createActivityNormalNode = new CreateNodeActionType("create_normal_activity_node",
			CreatedNodeType.NORMAL_ACTIVITY);
	public static final CreateNodeActionType createOperationBeginNode = new CreateNodeActionType("create_begin_node",
			CreatedNodeType.BEGIN_OPERATION);
	public static final CreateNodeActionType createOperationEndNode = new CreateNodeActionType("create_end_node",
			CreatedNodeType.END_OPERATION);
	public static final CreateNodeActionType createOperationNormalNode = new CreateNodeActionType("create_normal_operation_node",
			CreatedNodeType.NORMAL_OPERATION);
	public static final CreateNodeActionType createActionBeginNode = new CreateNodeActionType("create_begin_node",
			CreatedNodeType.BEGIN_ACTION);
	public static final CreateNodeActionType createActionEndNode = new CreateNodeActionType("create_end_node", CreatedNodeType.END_ACTION);
	public static final CreateNodeActionType createActionNormalNode = new CreateNodeActionType("create_normal_action_node",
			CreatedNodeType.NORMAL_ACTION);

	static {
		AgileBirdsObject.addActionForClass(createActivityBeginNode, FlexoProcess.class);
		AgileBirdsObject.addActionForClass(createActivityEndNode, FlexoProcess.class);
		AgileBirdsObject.addActionForClass(createActivityNormalNode, FlexoProcess.class);
		AgileBirdsObject.addActionForClass(createOperationBeginNode, AbstractActivityNode.class);
		AgileBirdsObject.addActionForClass(createOperationEndNode, AbstractActivityNode.class);
		AgileBirdsObject.addActionForClass(createOperationNormalNode, AbstractActivityNode.class);
		AgileBirdsObject.addActionForClass(createActionBeginNode, OperationNode.class);
		AgileBirdsObject.addActionForClass(createActionEndNode, OperationNode.class);
		AgileBirdsObject.addActionForClass(createActionNormalNode, OperationNode.class);
	}

	CreateNode(CreateNodeActionType actionType, WKFObject focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	private FlexoNode newNode;
	/*private int posX = -1;
	private int posY = -1;*/
	private boolean editNodeLabel = false;
	private String newNodeName = null;

	@Override
	protected void doAction(Object context) throws InvalidArgumentException {
		FlexoPetriGraph pg = null;
		if (getFocusedObject() instanceof FlexoProcess) {
			pg = ((FlexoProcess) getFocusedObject()).getActivityPetriGraph();
			if (pg == null) {
				throw new InvalidArgumentException("process_without_petri_graph");
			}
		} else if (getFocusedObject() instanceof SelfExecutableNode) {
			pg = ((SelfExecutableNode) getFocusedObject()).getExecutionPetriGraph();
			if (pg == null) {
				CreateExecutionPetriGraph createPg = CreateExecutionPetriGraph.actionType.makeNewEmbeddedAction(
						(PetriGraphNode) getFocusedObject(), null, this);
				createPg.doAction();
				pg = createPg.getNewPetriGraph();
			}
		} else if (getFocusedObject() instanceof FatherNode) {
			pg = ((FatherNode) getFocusedObject()).getContainedPetriGraph();
			if (pg == null) {
				CreatePetriGraph createPg = CreatePetriGraph.actionType.makeNewEmbeddedAction((FatherNode) getFocusedObject(), null, this);
				createPg.doAction();
				pg = createPg.getNewPetriGraph();
			}
		}
		if (pg == null) {
			throw new InvalidArgumentException("could_not_obtain_a_petri_graph");
		}

		if (getCreatedNodeType() == CreatedNodeType.BEGIN_ACTIVITY && pg instanceof ActivityPetriGraph) {
			newNode = ((ActivityPetriGraph) pg).createNewBeginNode(getNewNodeName());
			((ActivityPetriGraph) pg).addToNodes(newNode);
		} else if (getCreatedNodeType() == CreatedNodeType.END_ACTIVITY && pg instanceof ActivityPetriGraph) {
			newNode = ((ActivityPetriGraph) pg).createNewEndNode(getNewNodeName());
			((ActivityPetriGraph) pg).addToNodes(newNode);
		} else if (getCreatedNodeType() == CreatedNodeType.NORMAL_ACTIVITY && pg instanceof ActivityPetriGraph) {
			newNode = ((ActivityPetriGraph) pg).createNewNormalNode(getNewNodeName());
			((ActivityPetriGraph) pg).addToNodes(newNode);
		} else if (getCreatedNodeType() == CreatedNodeType.BEGIN_OPERATION && pg instanceof OperationPetriGraph) {
			newNode = ((OperationPetriGraph) pg).createNewBeginNode(getNewNodeName());
			((OperationPetriGraph) pg).addToNodes(newNode);
		} else if (getCreatedNodeType() == CreatedNodeType.END_OPERATION && pg instanceof OperationPetriGraph) {
			newNode = ((OperationPetriGraph) pg).createNewEndNode(getNewNodeName());
			((OperationPetriGraph) pg).addToNodes(newNode);
		} else if (getCreatedNodeType() == CreatedNodeType.NORMAL_OPERATION && pg instanceof OperationPetriGraph) {
			newNode = ((OperationPetriGraph) pg).createNewNormalNode(getNewNodeName());
			((OperationPetriGraph) pg).addToNodes(newNode);
		} else if (getCreatedNodeType() == CreatedNodeType.BEGIN_ACTION && pg instanceof ActionPetriGraph) {
			newNode = ((ActionPetriGraph) pg).createNewBeginNode(getNewNodeName());
			((ActionPetriGraph) pg).addToNodes(newNode);
		} else if (getCreatedNodeType() == CreatedNodeType.END_ACTION && pg instanceof ActionPetriGraph) {
			newNode = ((ActionPetriGraph) pg).createNewEndNode(getNewNodeName());
			((ActionPetriGraph) pg).addToNodes(newNode);
		} else if (getCreatedNodeType() == CreatedNodeType.NORMAL_ACTION && pg instanceof ActionPetriGraph) {
			newNode = ((ActionPetriGraph) pg).createNewNormalNode(getNewNodeName());
			((ActionPetriGraph) pg).addToNodes(newNode);
		}

		objectCreated("NEW_NODE", newNode);
	}

	@Override
	protected void redoAction(Object context) throws FlexoException {
		logger.info("CreateNode: REDO");
		doAction(context);
	}

	@Override
	protected void undoAction(Object context) throws FlexoException {
		logger.info("CreateNode: UNDO");
		getNewNode().delete();
	}

	@Override
	public CreateNodeActionType getActionType() {
		return (CreateNodeActionType) super.getActionType();
	}

	public CreatedNodeType getCreatedNodeType() {
		return getActionType().getCreatedNodeType();
	}

	public AbstractNode getNewNode() {
		return newNode;
	}

	/*public int getPosX()
	{
		return posX;
	}

	public void setPosX(int posX)
	{
		this.posX = posX;
	}

	public int getPosY()
	{
		return posY;
	}

	public void setPosY(int posY)
	{
		this.posY = posY;
	}

	public void setLocation (int posX, int posY)
	{
		setPosX(posX);
		setPosY(posY);
	}*/

	public boolean getEditNodeLabel() {
		return editNodeLabel;
	}

	public void setEditNodeLabel(boolean editNodeLabel) {
		this.editNodeLabel = editNodeLabel;
	}

	public String getNewNodeName() {
		if (newNodeName == null && getFocusedObject() != null) {
			if (getCreatedNodeType() == CreatedNodeType.BEGIN_ACTIVITY) {
				newNodeName = /*getFocusedObject().getProcess().findNextInitialName(*/FlexoNode.DEFAULT_BEGIN_FLEXO_NODE_NAME()/*)*/;
			} else if (getCreatedNodeType() == CreatedNodeType.END_ACTIVITY) {
				newNodeName = /*getFocusedObject().getProcess().findNextInitialName(*/FlexoNode.DEFAULT_END_FLEXO_NODE_NAME()/*)*/;
			} else if (getCreatedNodeType() == CreatedNodeType.NORMAL_ACTIVITY) {
				newNodeName = /*getFocusedObject().getProcess().findNextInitialName(*/AbstractActivityNode.DEFAULT_ACTIVITY_NODE_NAME()/*)*/;
			} else if (getCreatedNodeType() == CreatedNodeType.BEGIN_OPERATION) {
				newNodeName = /*getFocusedObject().getProcess().findNextInitialName(*/FlexoNode.DEFAULT_BEGIN_FLEXO_NODE_NAME()/*,(FatherNode)getFocusedObject())*/;
			} else if (getCreatedNodeType() == CreatedNodeType.END_OPERATION) {
				newNodeName = /*getFocusedObject().getProcess().findNextInitialName(*/FlexoNode.DEFAULT_END_FLEXO_NODE_NAME()/*,(FatherNode)getFocusedObject())*/;
			} else if (getCreatedNodeType() == CreatedNodeType.NORMAL_OPERATION) {
				newNodeName = /*getFocusedObject().getProcess().findNextInitialName(*/OperationNode.DEFAULT_OPERATION_NODE_NAME()/*,(FatherNode)getFocusedObject())*/;
			} else if (getCreatedNodeType() == CreatedNodeType.BEGIN_ACTION) {
				newNodeName = /*getFocusedObject().getProcess().findNextInitialName(*/FlexoNode.DEFAULT_BEGIN_FLEXO_NODE_NAME()/*,(FatherNode)getFocusedObject())*/;
			} else if (getCreatedNodeType() == CreatedNodeType.END_ACTION) {
				newNodeName = /*getFocusedObject().getProcess().findNextInitialName(*/FlexoNode.DEFAULT_END_FLEXO_NODE_NAME()/*,(FatherNode)getFocusedObject())*/;
			} else if (getCreatedNodeType() == CreatedNodeType.NORMAL_ACTION) {
				newNodeName = /*getFocusedObject().getProcess().findNextInitialName(*/ActionNode.DEFAULT_ACTION_NODE_NAME()/*,(FatherNode)getFocusedObject())*/;
			}
		}
		return newNodeName;
	}

	public void setNewNodeName(String newNodeName) {
		this.newNodeName = newNodeName;
		newNodeNameInitialized = true;
	}

	private boolean newNodeNameInitialized = false;

	public boolean isNewNodeNameInitialized() {
		return newNodeNameInitialized;
	}

}
