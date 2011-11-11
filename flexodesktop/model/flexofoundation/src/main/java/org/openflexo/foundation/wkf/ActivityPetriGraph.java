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

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.ActivityNode;
import org.openflexo.foundation.wkf.node.EventNode;
import org.openflexo.foundation.wkf.node.FlexoNode;
import org.openflexo.foundation.wkf.node.LOOPOperator;
import org.openflexo.foundation.wkf.node.NodeType;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.SelfExecutableActivityNode;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.foundation.wkf.node.EventNode.TriggerType;
import org.openflexo.foundation.xml.FlexoProcessBuilder;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public final class ActivityPetriGraph extends FlexoPetriGraph {

	private static final Logger logger = Logger.getLogger(ActivityPetriGraph.class.getPackage().getName());

	// ================================================================
	// ====================== Constructor =============================
	// ================================================================

	/**
	 * Constructor used during deserialization
	 */
	public ActivityPetriGraph(FlexoProcessBuilder builder) {
		this(builder.process);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public ActivityPetriGraph(FlexoProcess process) {
		super(process);
	}

	public static ActivityPetriGraph createNewActivityPetriGraph(FlexoProcess process) {
		ActivityPetriGraph returned = new ActivityPetriGraph(process);
		returned.setIsVisible(true);
		process.setActivityPetriGraph(returned);
		// returned.createNewBeginNode();
		// returned.createNewEndNode();
		returned.createNewStartEventNode("Start");
		returned.createNewEndEventNode("End");

		return returned;
	}

	public static ActivityPetriGraph createNewActivityPetriGraph(SelfExecutableActivityNode selfExecActivityNode) {
		ActivityPetriGraph returned = new ActivityPetriGraph(selfExecActivityNode.getProcess());
		selfExecActivityNode.setExecutionPetriGraph(returned);
		returned.createNewBeginNode();
		returned.createNewEndNode();
		return returned;
	}

	public static ActivityPetriGraph createNewActivityPetriGraph(LOOPOperator loopOperator) {
		ActivityPetriGraph returned = new ActivityPetriGraph(loopOperator.getProcess());
		loopOperator.setExecutionPetriGraph(returned);
		returned.createNewBeginNode();
		returned.createNewEndNode();
		return returned;
	}

	@Override
	public ActivityNode createNewBeginNode() {
		return createNewBeginNode(null);
	}

	public EventNode createNewStartEventNode(String nodeName) {
		EventNode reply = new EventNode(getProcess());
		reply.setEventType(EventNode.EVENT_TYPE.Start);
		reply.setTrigger(TriggerType.NONE);
		reply.setDontGenerate(true);
		reply.setX(15, "bpe");
		reply.setY(200, "bpe");
		reply.setNodeName(getProcess().findNextInitialName(nodeName == null ? reply.getDefaultName() : nodeName));
		addToNodes(reply);
		return reply;
	}

	public EventNode createNewEndEventNode(String nodeName) {
		EventNode reply = new EventNode(getProcess());
		reply.setEventType(EventNode.EVENT_TYPE.End);
		reply.setTrigger(TriggerType.NONE);
		reply.setDontGenerate(true);
		reply.setX(650, "bpe");
		reply.setY(200, "bpe");
		reply.setNodeName(getProcess().findNextInitialName(nodeName == null ? reply.getDefaultName() : nodeName));
		addToNodes(reply);
		return reply;
	}

	@Override
	public ActivityNode createNewBeginNode(String nodeName) {
		if (logger.isLoggable(Level.FINE))
			logger.fine("Create new BEGIN node");
		ActivityNode beginNode = new ActivityNode(getProcess());
		beginNode.updateMetricsValues();
		beginNode.setNodeType(NodeType.BEGIN);
		beginNode.setDontGenerate(true);
		/*beginNode.setPosX(30);
		beginNode.setPosY(200 + 80 * getAllBeginNodes().size());
		beginNode.setSize(new Dimension(60, 65));*/

		// TODO: following must be put in finalizer of CreatePetriGraph action
		/*beginNode.setX(90,WKFRepresentableObject.BASIC_PROCESS_EDITOR);
		beginNode.setY(200+80 * getAllBeginNodes().size(),WKFRepresentableObject.BASIC_PROCESS_EDITOR);
		beginNode.setLabelX(25, WKFRepresentableObject.BASIC_PROCESS_EDITOR);
		beginNode.setLabelY(50, WKFRepresentableObject.BASIC_PROCESS_EDITOR);
		beginNode.setX(90,WKFRepresentableObject.SWIMMING_LANE_EDITOR);
		beginNode.setY(200+80 * getAllBeginNodes().size(),WKFRepresentableObject.SWIMMING_LANE_EDITOR);
		beginNode.setLabelX(25, WKFRepresentableObject.SWIMMING_LANE_EDITOR);
		beginNode.setLabelY(50, WKFRepresentableObject.SWIMMING_LANE_EDITOR);*/

		beginNode.setNodeName(getProcess().findNextInitialName(nodeName == null ? beginNode.getDefaultName() : nodeName));
		addToNodes(beginNode);
		return beginNode;
	}

	@Override
	public ActivityNode createNewEndNode() {
		return createNewEndNode(null);
	}

	@Override
	public ActivityNode createNewEndNode(String nodeName) {
		if (logger.isLoggable(Level.FINE))
			logger.fine("Create new END node");
		ActivityNode endNode = new ActivityNode(getProcess());
		endNode.updateMetricsValues();
		endNode.setNodeType(NodeType.END);
		endNode.setDontGenerate(true);
		/*endNode.setPosX(500);
		endNode.setPosY(200 + 80 * getAllEndNodes().size());
		endNode.setSize(new Dimension(60, 65));*/

		// TODO: following must be put in finalizer of CreatePetriGraph action
		/*endNode.setX(500,WKFRepresentableObject.BASIC_PROCESS_EDITOR);
		endNode.setY(200+80 * getAllEndNodes().size(),WKFRepresentableObject.BASIC_PROCESS_EDITOR);
		endNode.setLabelX(25, WKFRepresentableObject.BASIC_PROCESS_EDITOR);
		endNode.setLabelY(50, WKFRepresentableObject.BASIC_PROCESS_EDITOR);
		endNode.setX(500,WKFRepresentableObject.SWIMMING_LANE_EDITOR);
		endNode.setY(200+80 * getAllBeginNodes().size(),WKFRepresentableObject.SWIMMING_LANE_EDITOR);
		endNode.setLabelX(25, WKFRepresentableObject.SWIMMING_LANE_EDITOR);
		endNode.setLabelY(50, WKFRepresentableObject.SWIMMING_LANE_EDITOR);*/

		endNode.setNodeName(getProcess().findNextInitialName(nodeName == null ? endNode.getDefaultName() : nodeName));
		addToNodes(endNode);
		return endNode;
	}

	public ActivityNode createNewNormalNode() {
		return createNewNormalNode(null);
	}

	public ActivityNode createNewNormalNode(String nodeName) {
		if (logger.isLoggable(Level.FINE))
			logger.fine("Create new NORMAL node");
		ActivityNode newNode = new ActivityNode(getProcess());
		newNode.updateMetricsValues();
		newNode.setNodeType(NodeType.NORMAL);
		newNode.setDontGenerate(true);
		newNode.setNodeName(getProcess().findNextInitialName(nodeName == null ? newNode.getDefaultName() : nodeName));
		addToNodes(newNode);
		return newNode;
	}

	/**
	 * Return all activities for this process
	 * 
	 * @return a Vector of ActivityNode
	 */
	public Vector<ActivityNode> getAllActivityNodes() {
		return getAllNodesOfClass(ActivityNode.class);
	}

	/**
	 * Returns all the abstract activity nodes that are in this petrigraph and the ones embedded by LOOPOperator and SelfExecutableNode.
	 * This is done recursively on all nodes.
	 * 
	 * @return all the abstract activity nodes embedded in this petri graph.
	 */
	public Vector<AbstractActivityNode> getAllEmbeddedAbstractActivityNodes() {
		return getAllEmbeddedNodesOfClass(AbstractActivityNode.class);
	}

	/**
	 * Returns all the activity nodes that are in this petrigraph and the ones embedded by LOOPOperator and SelfExecutableNode. This is done
	 * recursively on all nodes.
	 * 
	 * @return all the activity nodes embedded in this petri graph.
	 */
	public Vector<ActivityNode> getAllEmbeddedActivityNodes() {
		return getAllEmbeddedNodesOfClass(ActivityNode.class);
	}

	/**
	 * Returns all the sub-process nodes that are in this petrigraph and the ones embedded by LOOPOperator and SelfExecutableNode. This is
	 * done recursively on all nodes.
	 * 
	 * @return all the sub-process nodes embedded in this petri graph.
	 */
	public Vector<SubProcessNode> getAllEmbeddedSubProcessNodes() {
		return getAllEmbeddedNodesOfClass(SubProcessNode.class);
	}

	/**
	 * Returns a sorted vector of all the abstract activity nodes that are in this petrigraph and the ones embedded by LOOPOperator and
	 * SelfExecutableNode. This is done recursively on all nodes.
	 * 
	 * @return a sorted vector of all the abstract activity nodes embedded in this petri graph.
	 */
	public Vector<AbstractActivityNode> getAllEmbeddedSortedAbstractActivityNodes() {
		return getAllEmbeddedSortedNodesOfClass(AbstractActivityNode.class);
	}

	/**
	 * Returns a sorted vector of all the activity nodes that are in this petrigraph and the ones embedded by LOOPOperator and
	 * SelfExecutableNode. This is done recursively on all nodes.
	 * 
	 * @return a sorted vector of all the activity nodes embedded in this petri graph.
	 */
	public Vector<ActivityNode> getAllEmbeddedSortedActivityNodes() {
		return getAllEmbeddedSortedNodesOfClass(ActivityNode.class);
	}

	/**
	 * Returns a sorted vector of all the sub-process nodes that are in this petrigraph and the ones embedded by LOOPOperator and
	 * SelfExecutableNode. This is done recursively on all nodes.
	 * 
	 * @return a sorted vector of all the sub-process nodes embedded in this petri graph.
	 */
	public Vector<SubProcessNode> getAllEmbeddedSortedSubProcessNodes() {
		return getAllEmbeddedSortedNodesOfClass(SubProcessNode.class);
	}

	/**
	 * Returns all nodes of this petrigraph of the AbstractActivityNode. This method is not recursive and does not returned nodes that are
	 * defined in petrigraphs of LOOPOperator or SelfExecutableActivity.
	 * 
	 * @return
	 */
	public Vector<AbstractActivityNode> getAllAbstractActivityNodes() {
		return getAllNodesOfClass(AbstractActivityNode.class);
	}

	/**
	 * Returns all nodes of this petrigraph of the SubProcessNode. This method is not recursive and does not returned nodes that are defined
	 * in petrigraphs of LOOPOperator or SelfExecutableActivity.
	 * 
	 * @return
	 */
	public Vector<SubProcessNode> getAllSubProcessNodes() {
		return getAllNodesOfClass(SubProcessNode.class);
	}

	public Vector<OperationNode> getAllEmbeddedOperationNodes() {
		Vector<OperationNode> returned = new Vector<OperationNode>();
		Enumeration en = getAllEmbeddedAbstractActivityNodes().elements();
		while (en.hasMoreElements()) {
			AbstractActivityNode current = (AbstractActivityNode) en.nextElement();
			returned.addAll(current.getAllEmbeddedOperationNodes());
		}
		return returned;
	}

	public SubProcessNode getSubProcessNodeNamed(String name) {
		if (name == null)
			return null;
		for (SubProcessNode node : getAllSubProcessNodes()) {
			if (name.equals(node.getName()))
				return node;
		}
		return null;
	}

	public AbstractActivityNode getAbstractActivityNodeNamed(String name) {
		if (name == null)
			return null;
		for (AbstractActivityNode node : getAllAbstractActivityNodes()) {
			if (name.equals(node.getName()))
				return node;
		}
		return null;
	}

	public ActivityNode getActivityNodeNamed(String name) {
		if (name == null)
			return null;
		for (ActivityNode node : getAllActivityNodes()) {
			if (name.equals(node.getName()))
				return node;
		}
		return null;
	}

	public OperationNode getOperationNodeNamed(String name) {
		if (name == null)
			return null;
		for (OperationNode node : getAllEmbeddedOperationNodes()) {
			if (name.equals(node.getName()))
				return node;
		}
		return null;
	}

	/**
	 * Returns the level of this Petri Graph (which is {@link FlexoLevel.ACTIVITY}).
	 * 
	 * @see org.openflexo.foundation.wkf.LevelledObject#getLevel()
	 */
	@Override
	public FlexoLevel getLevel() {
		return FlexoLevel.ACTIVITY;
	}

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
		return "activity_petri_graph";
	}

}
