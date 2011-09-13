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
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.DMPropertyImplementationType;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.edge.BackwardWSEdge;
import org.openflexo.foundation.wkf.edge.ContextualEdgeStarting;
import org.openflexo.foundation.wkf.edge.ExternalMessageInEdge;
import org.openflexo.foundation.wkf.edge.ExternalMessageOutEdge;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.edge.ForwardWSEdge;
import org.openflexo.foundation.wkf.edge.InternalMessageInEdge;
import org.openflexo.foundation.wkf.edge.InternalMessageOutEdge;
import org.openflexo.foundation.wkf.edge.InvalidEdgeException;
import org.openflexo.foundation.wkf.edge.MessageEdge;
import org.openflexo.foundation.wkf.edge.TokenEdge;
import org.openflexo.foundation.wkf.edge.TransferWSEdge;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.ActionType;
import org.openflexo.foundation.wkf.node.ChildNode;
import org.openflexo.foundation.wkf.node.FlexoNode;
import org.openflexo.foundation.wkf.node.FlexoPreCondition;
import org.openflexo.foundation.wkf.node.Node;
import org.openflexo.foundation.wkf.node.PetriGraphNode;
import org.openflexo.foundation.wkf.ws.AbstractInPort;
import org.openflexo.foundation.wkf.ws.FlexoPort;
import org.openflexo.foundation.wkf.ws.FlexoPortMap;
import org.openflexo.foundation.wkf.ws.InOutPort;
import org.openflexo.foundation.wkf.ws.MessageEntry;
import org.openflexo.foundation.wkf.ws.MessageEntryBinding;


public class CreateEdge extends FlexoUndoableAction<CreateEdge,AbstractNode,AbstractNode>
{

    private static final Logger logger = Logger.getLogger(CreateEdge.class.getPackage().getName());

    public static FlexoActionType<CreateEdge,AbstractNode,AbstractNode> actionType
    = new FlexoActionType<CreateEdge,AbstractNode,AbstractNode> ("create_edge",FlexoActionType.defaultGroup) {

        /**
         * Factory method
         */
        @Override
		public CreateEdge makeNewAction(AbstractNode focusedObject, Vector<AbstractNode> globalSelection, FlexoEditor editor)
        {
            return new CreateEdge(focusedObject, globalSelection,editor);
        }

        @Override
		protected boolean isVisibleForSelection(AbstractNode object, Vector<AbstractNode> globalSelection)
        {
            return false;
        }

        @Override
		protected boolean isEnabledForSelection(AbstractNode object, Vector<AbstractNode> globalSelection)
        {
            return true;
        }

        private String[] persistentProperties = { "startingNode", "endNode", "endNodePreCondition" };

        @Override
		protected String[] getPersistentProperties()
        {
        	return persistentProperties;
        }

    };

    CreateEdge (AbstractNode focusedObject, Vector<AbstractNode> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection,editor);
    }

    static {
    	FlexoModelObject.addActionForClass(actionType, AbstractNode.class);
    }

    AbstractNode startingNode;
    AbstractNode endNode;
    private FlexoPreCondition endNodePreCondition;
    private FlexoPostCondition newPostCondition = null;

    private boolean isGenericOutput = false;

    private Object outputContext;
    private String newEdgeName;

    private CreatePreCondition createPreConditionForInducedEdgeConstruction = null;
    private CreatePreCondition createPreCondition = null;

    @Override
	protected void doAction(Object context) throws InvalidEdgeDefinition, DisplayActionCannotBeBound
    {
    	// 1. Check validity
    	// Cannot have null start node or end node
    	if (startingNode == null || endNode == null)
    		throw new InvalidEdgeDefinition();

    	// Display action cannot have outgoing edges
    	if (startingNode instanceof ActionNode
    			&&  ((ActionNode)startingNode).getActionType()==ActionType.DISPLAY_ACTION)
    		throw new DisplayActionCannotBeBound();

    	// End activity node of the top level cannot have outgoing edges
    	if(startingNode instanceof AbstractActivityNode &&
    			((AbstractActivityNode)startingNode).isEndNode() && ((AbstractActivityNode)startingNode).getActivityPetriGraph().getContainer() instanceof FlexoProcess){
    		throw new InvalidEdgeDefinition();
    	}


    	// 2. Let's find the end node (mainly ensure that edges that go to a FlexoNode go onto a precondition
    	AbstractNode realEndNode = findEndNode();
    	// Start node check
    	if (!startingNode.mayHaveOutgoingPostConditions())
    		throw new InvalidEdgeDefinition();

    	// End node check
    	if (!realEndNode.mayHaveIncomingPostConditions())
    		throw new InvalidEdgeDefinition();

    	// 3. Create the edge

    	try {
			if (startingNode instanceof AbstractInPort) {
				AbstractInPort start = (AbstractInPort)startingNode;
				if (realEndNode instanceof FlexoPortMap && ((FlexoPortMap)realEndNode).isInputPort()) {
					newPostCondition = new ForwardWSEdge(start,(FlexoPortMap) realEndNode);
				} else if (realEndNode instanceof Node) {
					newPostCondition = new InternalMessageInEdge(start,(Node) realEndNode);
				}
			} else if (startingNode instanceof FlexoPortMap && ((FlexoPortMap)startingNode).isOutputPort()) {
				FlexoPortMap start = (FlexoPortMap)startingNode;
				if (realEndNode instanceof FlexoPort && ((FlexoPort)realEndNode).isOutPort()) {
					newPostCondition = new BackwardWSEdge(start,(FlexoPort) realEndNode);
				} else if (realEndNode instanceof FlexoPortMap && ((FlexoPortMap)realEndNode).isInputPort()) {
					newPostCondition = new TransferWSEdge(start,(FlexoPortMap) realEndNode);
				} else if (realEndNode instanceof Node) {
					newPostCondition = new ExternalMessageOutEdge(start,(Node) realEndNode);
				}
			} else if (realEndNode instanceof FlexoPort && ((FlexoPort)realEndNode).isOutPort()) {
				FlexoPort end = (FlexoPort) realEndNode;
				if (startingNode instanceof FlexoPortMap && ((FlexoPortMap)startingNode).isOutputPort()) {
					newPostCondition = new BackwardWSEdge((FlexoPortMap) startingNode,end);
				} else if (startingNode instanceof PetriGraphNode) {
					newPostCondition = new InternalMessageOutEdge((PetriGraphNode) startingNode,end);
				}
			} else if (realEndNode instanceof FlexoPortMap && ((FlexoPortMap)realEndNode).isInputPort()) {
				FlexoPortMap end = (FlexoPortMap) realEndNode;
				if (startingNode instanceof AbstractInPort) {
					newPostCondition = new ForwardWSEdge((AbstractInPort) startingNode,end);
				} else if (startingNode instanceof PetriGraphNode) {
					newPostCondition = new ExternalMessageInEdge((PetriGraphNode) startingNode,end);
				}
			} else if (startingNode instanceof PetriGraphNode && realEndNode instanceof Node) {
				newPostCondition = new TokenEdge((PetriGraphNode)startingNode, (Node) realEndNode);
			}
		} catch (InvalidEdgeException e) {
			e.printStackTrace();
			throw new InvalidEdgeDefinition();
		}

		if (newPostCondition instanceof MessageEdge) {
			addMessageVariableToProcessInstance((MessageEdge) newPostCondition);
		}

        if (newPostCondition != null) {
        	// Set name when defined
        	if (getNewEdgeName() != null){
        		newPostCondition.setName(getNewEdgeName());
        	}
        	newPostCondition.setIsGenericOutput(getIsGenericOutput());
        	newPostCondition.updateMetricsValues();
        	if (getStartingNode() instanceof ContextualEdgeStarting && getOutputContext() != null) {
        		((ContextualEdgeStarting)getStartingNode()).addToOutgoingPostConditions(newPostCondition,getOutputContext());
        	} else {
        		getStartingNode().addToOutgoingPostConditions(newPostCondition);
        	}
        } else {
           	throw new InvalidEdgeDefinition();
        }

        if (createPreCondition != null && createPreCondition.getNewPreCondition() != null) {
         	createPreCondition.getNewPreCondition().resetLocation();
        }
        if (createPreConditionForInducedEdgeConstruction != null
        		&& createPreConditionForInducedEdgeConstruction.getNewPreCondition() != null)
        	createPreConditionForInducedEdgeConstruction.getNewPreCondition().resetLocation();

        objectCreated("NEW_POST_CONDITION",newPostCondition);
    }

	@Override
	protected void undoAction(Object context) throws FlexoException
	{
		logger.info("CreateEdge: UNDO");
		getNewPostCondition().delete();
		if (createPreConditionForInducedEdgeConstruction != null) {
			createPreConditionForInducedEdgeConstruction.undoAction();
		}
		if (createPreCondition != null) {
			createPreCondition.undoAction();
		}
	}

	@Override
	protected void redoAction(Object context) throws FlexoException
	{
		logger.info("CreateEdge: REDO");
		if (createPreConditionForInducedEdgeConstruction != null) {
			createPreConditionForInducedEdgeConstruction.redoAction();
            endNodePreCondition = createPreConditionForInducedEdgeConstruction.getNewPreCondition();
		}
		if (createPreCondition != null) {
			createPreCondition.redoAction();
            endNodePreCondition = createPreCondition.getNewPreCondition();
		}
		doAction(context);
	}

	private AbstractNode findEndNode() throws InvalidEdgeDefinition {
		if (endNode instanceof ActionNode)
			return endNode;
		if (endNode instanceof FlexoNode) {
    		FlexoNode endFlexoNode = (FlexoNode)endNode;
            if ((endFlexoNode.isBeginNode()) && (!(startingNode instanceof AbstractInPort))) {
                // This is a induced edge construction
                if (endFlexoNode.getAttachedPreCondition() != null) {
                    endNodePreCondition = endFlexoNode.getAttachedPreCondition();
                } else if (endFlexoNode instanceof ChildNode) {
                	createPreConditionForInducedEdgeConstruction
            		= CreatePreCondition.actionType.makeNewEmbeddedAction(
            				((ChildNode)endFlexoNode).getFather(), null, this);
            		createPreConditionForInducedEdgeConstruction.setAttachedBeginNode(endFlexoNode);
            		createPreConditionForInducedEdgeConstruction.doAction();
                    endNodePreCondition = createPreConditionForInducedEdgeConstruction.getNewPreCondition();
                } else {
                	throw new InvalidEdgeDefinition();
                }
            }

        	// We have to check that precondition is defined
        	// Otherwise create it (but this is normally handled by initializer)
        	if (endNodePreCondition == null) {
        		createPreCondition
        		= CreatePreCondition.actionType.makeNewEmbeddedAction(
        				(FlexoNode)endNode, null, this);
        		createPreCondition.setAllowsToSelectPreconditionOnly(true);
        		createPreCondition.doAction();
                endNodePreCondition = createPreCondition.getNewPreCondition();
        	}
        	return endNodePreCondition;
		}
		return endNode;
	}

	public AbstractNode getStartingNode()
	{
		if (startingNode == null) {
			return getFocusedObject();
		}
		return startingNode;
	}

	public void setStartingNode(AbstractNode startingNode)
	{
		this.startingNode = startingNode;
	}

	public AbstractNode getEndNode()
	{
		return endNode;
	}

	public void setEndNode(AbstractNode endNode)
	{
		this.endNode = endNode;
	}

	public class InvalidEdgeDefinition extends FlexoException
	{
		protected InvalidEdgeDefinition()
		{
			super("InvalidEdgeDefinition startingNode="+startingNode+" endNode="+endNode,"invalid_edge_definition");
		}
	}

	public class DisplayActionCannotBeBound extends FlexoException
	{
		protected DisplayActionCannotBeBound()
		{
			super("DisplayActionCannotBeBound","display_action_can_not_be_bound");
		}
	}

	public FlexoPreCondition getEndNodePreCondition()
	{
		return endNodePreCondition;
	}

	public void setEndNodePreCondition(FlexoPreCondition endNodePreCondition)
	{
		this.endNodePreCondition = endNodePreCondition;
	}

	public FlexoPostCondition getNewPostCondition()
	{
		return newPostCondition;
	}

	public Object getOutputContext() {
		return outputContext;
	}

	public void setOutputContext(Object outputContext) {
		this.outputContext = outputContext;
	}

	public String getNewEdgeName() {
		return newEdgeName;
	}

	public void setNewEdgeName(String newEdgeName) {
		this.newEdgeName = newEdgeName;
	}

	private void addMessageVariableToProcessInstance(MessageEdge pme) {

    	// here, should insert the code to create automatically the bindings with the messages.
    	DMEntity processInstance=pme.getProcess().getProcessDMEntity();


		// TODO : wrong convention used with ports.
		//edge leaving a API Port
		if (pme.getStartNode() instanceof  InOutPort) {
			Vector<MessageEntryBinding> inputBindings=pme.getInputMessage().getBindings();

    		for (MessageEntryBinding b:inputBindings) {
    			// for every entry in the message def

    			// add an property to the process instance
    			MessageEntry m=b.getBindingDefinition();

    			String propertyName="Service_"+m.getVariableName()+"_IN";

    			//String propertyName=pme.getOutputMessage().getName()+"_"+m.getVariableName()+"_IN";
    			DMProperty p=processInstance.createDMProperty(propertyName, m.getType(),DMPropertyImplementationType.PUBLIC_ACCESSORS_PRIVATE_FIELD);

    			// bind the created property to the message.
    			pme.getProject().getBindingValueConverter().setBindable(pme);
    			b.setBindingValue(pme.getProject().getBindingValueConverter().convertFromString("processInstance."+propertyName));
    		}
		}

		if (pme.getEndNode() instanceof  InOutPort) {
			Vector<MessageEntryBinding> inputBindings=pme.getOutputMessage().getBindings();

    		for (MessageEntryBinding b:inputBindings) {
    			// for every entry in the message def

    			// add an property to the process instance
    			MessageEntry m=b.getBindingDefinition();

    			String propertyName="Service_"+m.getVariableName()+"_OUT";

    			//String propertyName=pme.getOutputMessage().getName()+"_"+m.getVariableName()+"_IN";
    			DMProperty p=processInstance.createDMProperty(propertyName, m.getType(),DMPropertyImplementationType.PUBLIC_ACCESSORS_PRIVATE_FIELD);

    			// bind the created property to the message.
    			pme.getProject().getBindingValueConverter().setBindable(pme);
    			b.setBindingValue(pme.getProject().getBindingValueConverter().convertFromString("processInstance."+propertyName));
    		}
		}


    	// edge ending in a WS port -> create a message for input
    	if (pme.getEndNode() instanceof FlexoPortMap && pme.getInputMessage()!=null) {
    		Vector<MessageEntryBinding> inputBindings=pme.getInputMessage().getBindings();

    		for (MessageEntryBinding b:inputBindings) {
    			// for every entry in the message def

    			// add an property to the process instance
    			MessageEntry m=b.getBindingDefinition();

    			String propertyName=((FlexoPortMap)pme.getEndNode()).getSubProcessNode().getName()+"_"+m.getVariableName()+"_IN";

    			//String propertyName=pme.getOutputMessage().getName()+"_"+m.getVariableName()+"_IN";
    			DMProperty p=processInstance.createDMProperty(propertyName, m.getType(),DMPropertyImplementationType.PUBLIC_ACCESSORS_PRIVATE_FIELD);

    			// bind the created property to the message.
    			pme.getProject().getBindingValueConverter().setBindable(pme);
    			b.setBindingValue(pme.getProject().getBindingValueConverter().convertFromString("processInstance."+propertyName));
    		}
    	}


    	// edge starting is a WSPort -> create a message for output
    	if (pme.getStartNode() instanceof FlexoPortMap && pme.getOutputMessage() != null) {
    		Vector<MessageEntryBinding> outputBindings=pme.getOutputMessage().getBindings();

        	for (MessageEntryBinding b:outputBindings) {
        		// for every entry in the message def

        		// add an property to the process instance
        		MessageEntry m=b.getBindingDefinition();

        		String propertyName=((FlexoPortMap)pme.getStartNode()).getSubProcessNode().getName()+"_"+m.getVariableName()+"_OUT";

        		DMProperty p=processInstance.createDMProperty(propertyName, m.getType(),DMPropertyImplementationType.PUBLIC_ACCESSORS_PRIVATE_FIELD);

        		// bind the created property to the message.
        		pme.getProject().getBindingValueConverter().setBindable(pme);
        		b.setBindingValue(pme.getProject().getBindingValueConverter().convertFromString("processInstance."+propertyName));
        	}
    	}
	}

	public boolean getIsGenericOutput()
	{
		return isGenericOutput;
	}

	public void setIsGenericOutput(boolean isGenericOutput)
	{
		this.isGenericOutput = isGenericOutput;
	}


}
