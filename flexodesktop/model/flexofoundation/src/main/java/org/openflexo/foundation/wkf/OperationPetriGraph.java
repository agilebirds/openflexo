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

import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.FlexoNode;
import org.openflexo.foundation.wkf.node.LOOPOperator;
import org.openflexo.foundation.wkf.node.NodeType;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.PetriGraphNode;
import org.openflexo.foundation.wkf.node.SelfExecutableOperationNode;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.logging.FlexoLogger;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public final class OperationPetriGraph extends FlexoPetriGraph {

	private static final Logger logger = FlexoLogger.getLogger(OperationPetriGraph.class.getPackage().getName());

	// private AbstractActivityNode _abstractActivityNode;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Constructor used during deserialization
	 */
	public OperationPetriGraph(FlexoProcessBuilder builder) {
		this(builder.process);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public OperationPetriGraph(FlexoProcess process) {
		super(process);
	}

	/**
	 * Contructor to use in case of a GUI operation petri graph creation
	 */
	public OperationPetriGraph(AbstractActivityNode node) {
		super(node.getProcess());
		node.setContainedPetriGraph(this);

		OperationNode begin = createNewBeginNode();
		OperationNode end = createNewEndNode();
		if (node.isBeginNode() || node.isEndNode() || node.isSelfExecutableNode()) {
			createTokenEdge(begin, end);
		}
	}

	public static OperationPetriGraph createNewOperationPetriGraph(SelfExecutableOperationNode selfExecOperationNode) {
		OperationPetriGraph returned = new OperationPetriGraph(selfExecOperationNode.getProcess());
		selfExecOperationNode.setExecutionPetriGraph(returned);
		returned.createNewBeginNode();
		returned.createNewEndNode();
		return returned;
	}

	public static OperationPetriGraph createNewOperationPetriGraph(LOOPOperator loopOperator) {
		OperationPetriGraph returned = new OperationPetriGraph(loopOperator.getProcess());
		loopOperator.setExecutionPetriGraph(returned);
		returned.createNewBeginNode();
		returned.createNewEndNode();
		return returned;
	}

	@Override
	public OperationNode createNewBeginNode() {
		return createNewBeginNode(null);
	}

	@Override
	public OperationNode createNewBeginNode(String nodeName) {
		OperationNode beginNode = new OperationNode(getProcess());
		beginNode.updateMetricsValues();
		beginNode.setNodeType(NodeType.BEGIN);
		beginNode.setDontGenerate(true);
		/*beginNode.setPosX(30);
		 beginNode.setPosY(Constants.COMPOUND_VIEW_BORDER_SIZE + 80 * getAllBeginNodes().size());
		beginNode.setSize(new Dimension(60, 65));*/
		beginNode.setNodeName(getProcess().findNextInitialName(nodeName == null ? beginNode.getDefaultName() : nodeName,
				getAbstractActivityNode()));
		// beginNode.setNodeLabelPosX(25);
		// beginNode.setNodeLabelPosY(50);

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
	public OperationNode createNewEndNode() {
		return createNewEndNode(null);
	}

	@Override
	public OperationNode createNewEndNode(String nodeName) {
		OperationNode endNode = new OperationNode(getProcess());
		endNode.updateMetricsValues();
		endNode.setNodeType(NodeType.END);
		endNode.setDontGenerate(true);
		/* endNode.setPosX(200);
		 endNode.setPosY(Constants.COMPOUND_VIEW_BORDER_SIZE + 80 * getAllEndNodes().size());
		 endNode.setSize(new Dimension(60, 65));*/
		endNode.setNodeName(getProcess().findNextInitialName(nodeName == null ? endNode.getDefaultName() : nodeName,
				getAbstractActivityNode()));
		/*endNode.setNodeLabelPosX(25);
		endNode.setNodeLabelPosY(50);*/

		addToNodes(endNode);
		return endNode;
	}

	public OperationNode createNewNormalNode() {
		return createNewNormalNode(null);
	}

	public OperationNode createNewNormalNode(String nodeName) {
		OperationNode newNode = new OperationNode(getProcess());
		newNode.updateMetricsValues();
		newNode.setNodeType(NodeType.NORMAL);
		newNode.setDontGenerate(true);
		newNode.setNodeName(getProcess().findNextInitialName(nodeName == null ? newNode.getDefaultName() : nodeName));
		addToNodes(newNode);
		return newNode;
	}

	/**
	 * Returns all nodes of this petrigraph of the AbstractActivityNode. This method is not recursive and does not returned nodes that are
	 * defined in petrigraphs of LOOPOperator or SelfExecutableActivity.
	 * 
	 * @return
	 */
	public Vector<OperationNode> getAllOperationNodes() {
		return getAllNodesOfClass(OperationNode.class);
	}

	/**
	 * Returns all the Operation nodes that are in this petrigraph and the ones embedded by LOOPOperator and SelfExecutableNode. This is
	 * done recursively on all nodes.
	 * 
	 * @return all the operation nodes embedded in this petri graph.
	 */
	public Vector<OperationNode> getAllEmbeddedOperationNodes() {
		return getAllEmbeddedNodesOfClass(OperationNode.class);
	}

	/**
	 * Returns all the Operation nodes that are in this petrigraph and the ones embedded by LOOPOperator and SelfExecutableNode. This is
	 * done recursively on all nodes.
	 * 
	 * @return all the operation nodes embedded in this petri graph.
	 */
	public Vector<OperationNode> getAllEmbeddedSortedOperationNodes() {
		return getAllEmbeddedSortedNodesOfClass(OperationNode.class);
	}

	/**
	 * Returns the level of this Petri Graph (which is {@link FlexoLevel.OPERATION}).
	 * 
	 * @see org.openflexo.foundation.wkf.LevelledObject#getLevel()
	 */
	@Override
	public FlexoLevel getLevel() {
		return FlexoLevel.OPERATION;
	}

	/*public AbstractActivityNode getAbstractActivityNode()
	{
	    return _abstractActivityNode;
	}

	public void setAbstractActivityNode(AbstractActivityNode aNode)
	{
	    _abstractActivityNode = aNode;
	}*/

	/**
	 * Overrides createBeginNode
	 * 
	 * @see org.openflexo.foundation.wkf.FlexoPetriGraph#createBeginNode(String)
	 */
	@Override
	public FlexoNode createBeginNode(String context) {
		return createNewBeginNode(context);
	}

	/**
	 * Overrides createEndNode
	 * 
	 * @see org.openflexo.foundation.wkf.FlexoPetriGraph#createEndNode(String)
	 */
	@Override
	public FlexoNode createEndNode(String context) {
		return createNewEndNode(context);
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "operation_petri_graph";
	}

	/**
	 * Recursively looks for the root activity node of this petri graph
	 * 
	 * @return
	 */
	public AbstractActivityNode getAbstractActivityNode() {
		if (getContainer() instanceof AbstractActivityNode) {
			return (AbstractActivityNode) getContainer();
		} else if (getContainer() instanceof PetriGraphNode) {
			return ((PetriGraphNode) getContainer()).getAbstractActivityNode();
		} else
			return null;
	}
}
