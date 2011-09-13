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
package org.openflexo.foundation.wkf;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.ActionType;
import org.openflexo.foundation.wkf.node.FlexoNode;
import org.openflexo.foundation.wkf.node.LOOPOperator;
import org.openflexo.foundation.wkf.node.NodeType;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.PetriGraphNode;
import org.openflexo.foundation.wkf.node.SelfExecutableActionNode;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.logging.FlexoLogger;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public final class ActionPetriGraph extends FlexoPetriGraph
{

    @SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger.getLogger(ActionPetriGraph.class.getPackage()
            .getName());

    //private OperationNode _operationNode;

    // ==========================================================================
    // ============================= Constructor
    // ================================
    // ==========================================================================

    /**
     * Constructor used during deserialization
     */
    public ActionPetriGraph(FlexoProcessBuilder builder)
    {
        this(builder.process);
        initializeDeserialization(builder);
    }

    /**
     * Default constructor
     */
    public ActionPetriGraph(FlexoProcess process)
    {
        super(process);
    }

    /**
     * Contructor to use in case of a GUI action petri graph creation
     */
    public ActionPetriGraph(OperationNode node)
    {
        super(node.getProcess());
        node.setContainedPetriGraph(this);
 
        ActionNode begin = createNewBeginNode();
        ActionNode end = createNewEndNode();
        if (node.isBeginNode() || node.isEndNode()||node.isSelfExecutableNode()) {
            createTokenEdge(begin, end);
        }
    }

    public static ActionPetriGraph createNewActionPetriGraph(SelfExecutableActionNode selfExecActionNode)
    {
    	ActionPetriGraph returned = new ActionPetriGraph(selfExecActionNode.getProcess());
    	selfExecActionNode.setExecutionPetriGraph(returned);
        returned.createNewBeginNode();
        returned.createNewEndNode();
        return returned;
    }

    public static ActionPetriGraph createNewActionPetriGraph(LOOPOperator loopOperator)
    {
    	ActionPetriGraph returned = new ActionPetriGraph(loopOperator.getProcess());
        loopOperator.setExecutionPetriGraph(returned);
        returned.createNewBeginNode();
        returned.createNewEndNode();
        return returned;
    }


    @Override
	public ActionNode createNewBeginNode()
    {
        return createNewBeginNode(null);
    }

    @Override
	public ActionNode createNewBeginNode(String nodeName)
    {
        ActionNode beginNode = new ActionNode(getProcess());
        beginNode.setNodeType(NodeType.BEGIN);
        beginNode.setDontGenerate(true);
        beginNode.setNodeName(getProcess().findNextInitialName(
                nodeName == null ? beginNode.getDefaultName() : nodeName, getOperationNode()));
        //beginNode.setNodeLabelPosX(25);
        //beginNode.setNodeLabelPosY(50);

        // TODO: following must be put in finalizer of CreatePetriGraph action
        /*beginNode.setX(30,WKFRepresentableObject.BASIC_PROCESS_EDITOR);
        beginNode.setY(10+80 * getAllBeginNodes().size(),WKFRepresentableObject.BASIC_PROCESS_EDITOR);
        beginNode.setLabelX(25, WKFRepresentableObject.BASIC_PROCESS_EDITOR);
        beginNode.setLabelY(50, WKFRepresentableObject.BASIC_PROCESS_EDITOR);
        beginNode.setX(30,WKFRepresentableObject.SWIMMING_LANE_EDITOR);
        beginNode.setY(10+80 * getAllBeginNodes().size(),WKFRepresentableObject.SWIMMING_LANE_EDITOR);
        beginNode.setLabelX(25, WKFRepresentableObject.SWIMMING_LANE_EDITOR);
        beginNode.setLabelY(50, WKFRepresentableObject.SWIMMING_LANE_EDITOR);*/

        addToNodes(beginNode);
        return beginNode;
    }

    @Override
	public ActionNode createNewEndNode()
    {
        return createNewEndNode(null);
    }

    @Override
	public ActionNode createNewEndNode(String nodeName)
    {
        ActionNode endNode = new ActionNode(getProcess());
        endNode.setNodeType(NodeType.END);
        endNode.setDontGenerate(true);
        endNode.setNodeName(getProcess().findNextInitialName(
                nodeName == null ? endNode.getDefaultName() : nodeName, getOperationNode()));
        //endNode.setNodeLabelPosX(25);
        //endNode.setNodeLabelPosY(50);

        // TODO: following must be put in finalizer of CreatePetriGraph action
        /*endNode.setX(200,WKFRepresentableObject.BASIC_PROCESS_EDITOR);
        endNode.setY(10+80 * getAllEndNodes().size(),WKFRepresentableObject.BASIC_PROCESS_EDITOR);
        endNode.setLabelX(25, WKFRepresentableObject.BASIC_PROCESS_EDITOR);
        endNode.setLabelY(50, WKFRepresentableObject.BASIC_PROCESS_EDITOR);
        endNode.setX(200,WKFRepresentableObject.SWIMMING_LANE_EDITOR);
        endNode.setY(10+80 * getAllBeginNodes().size(),WKFRepresentableObject.SWIMMING_LANE_EDITOR);
        endNode.setLabelX(25, WKFRepresentableObject.SWIMMING_LANE_EDITOR);
        endNode.setLabelY(50, WKFRepresentableObject.SWIMMING_LANE_EDITOR);*/

        addToNodes(endNode);
         return endNode;
    }
    
    public ActionNode createNewNormalNode()
    {
        return createNewNormalNode(null);
    }

    public ActionNode createNewNormalNode(String nodeName)
    {
    	ActionNode newNode = new ActionNode(getProcess());
    	newNode.setNodeType(NodeType.NORMAL);
    	newNode.setActionType(ActionType.FLEXO_ACTION);
    	newNode.setDontGenerate(true);
    	newNode.setNodeName(getProcess().findNextInitialName(nodeName == null ? newNode.getDefaultName() : nodeName));
    	addToNodes(newNode);
    	return newNode;
    }

    /**
     * Returns the level of this Petri Graph (which is {@link FlexoLevel.ACTION}).
     * 
     * @see org.openflexo.foundation.wkf.LevelledObject#getLevel()
     */
    @Override
	public FlexoLevel getLevel()
    {
        return FlexoLevel.ACTION;
    }

    /**
     * Returns all the action nodes that are contained 
     * @return
     */
    public Vector<ActionNode> getAllActionNodes() {
    	return getAllNodesOfClass(ActionNode.class);
    }
    
    /**
	 * Returns all the Action nodes that are in this petrigraph and the ones embedded by LOOPOperator and SelfExecutableNode.
	 * This is done recursively on all nodes.
	 * @return all the action nodes embedded in this petri graph.
	 */
    public Vector<ActionNode> getAllEmbeddedActionNodes() {
    	return getAllEmbeddedNodesOfClass(ActionNode.class);
    }

    /**
     * Returns a sorted vector of all the Action nodes that are in this petrigraph and the ones embedded by LOOPOperator and SelfExecutableNode.
     * This is done recursively on all nodes.
     * @return a sorted vector of all the action nodes embedded in this petri graph.
     */
    public Vector<ActionNode> getAllEmbeddedSortedActionNodes() {
    	return getAllEmbeddedSortedNodesOfClass(ActionNode.class);
    }
    
    /**
     * Overrides createBeginNode
     * 
     * @see org.openflexo.foundation.wkf.FlexoPetriGraph#createBeginNode(String)
     */
    @Override
	public FlexoNode createBeginNode(String context)
    {
        return createNewBeginNode(context);
    }

    /**
     * Overrides createEndNode
     * 
     * @see org.openflexo.foundation.wkf.FlexoPetriGraph#createEndNode(String)
     */
    @Override
	public FlexoNode createEndNode(String context)
    {
        return createNewEndNode(context);
    }

    /**
     * Overrides getClassNameKey
     * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
     */
    @Override
	public String getClassNameKey()
    {
        return "action_petri_graph";
    }
    
    /**
     * Recursively looks for the root operation node of this petri graph
     * @return
     */
    public OperationNode getOperationNode() {
    	if (getContainer() instanceof OperationNode) {
    		return (OperationNode) getContainer();
    	} else if (getContainer() instanceof PetriGraphNode) {
    		return ((PetriGraphNode)getContainer()).getOperationNode();
    	} else
    		return null;
    }

}
