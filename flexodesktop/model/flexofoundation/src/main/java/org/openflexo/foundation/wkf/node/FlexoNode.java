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
package org.openflexo.foundation.wkf.node;

import java.util.Enumeration;
import java.util.TreeMap;
import java.util.Vector;

import org.openflexo.foundation.validation.DeletionFixProposal;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.validation.ValidationWarning;
import org.openflexo.foundation.wkf.ExecutableWorkflowElement;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.dm.PreInserted;
import org.openflexo.foundation.wkf.dm.PreRemoved;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ProgrammingLanguage;

public abstract class FlexoNode extends PetriGraphNode implements ExecutableWorkflowElement {

	// ==========================================================================
	// ============================= Variables ==================================
	// ==========================================================================

	private NodeType _nodeType;

	private Vector<FlexoPreCondition> _preConditions;

	private boolean isWaiting;

	private boolean isSelfActivated;

	/**
	 * Attached pre-condition: relevant only for begin nodes of Operation and Action level
	 */
	private FlexoPreCondition _attachedPreCondition;

	public FlexoNode(FlexoProcess process) {
		super(process);
		_nodeType = NodeType.NORMAL;
		_preConditions = new Vector<FlexoPreCondition>();

	}

	@Override
	public void delete() {
		if (getAttachedPreCondition() != null) {
			FlexoPreCondition pre = getAttachedPreCondition();
			pre.setAttachedBeginNode(null);
			pre.delete();
		}
		deletePreConditions();
		super.delete();
	}

	/**
	 * Build and return a vector of all the objects that will be deleted during this deletion
	 * 
	 * @param aVector
	 *            of DeletableObject
	 */
	@Override
	public Vector<WKFObject> getAllEmbeddedDeleted() {
		Vector<WKFObject> returned = getAllEmbeddedWKFObjects();
		returned.addAll(getPreConditions());
		if (_attachedPreCondition != null) {
			returned.addAll(_attachedPreCondition.getAllEmbeddedDeleted());
		}
		return returned;
	}

	private void deletePreConditions() {
		Enumeration<FlexoPreCondition> en = new Vector<FlexoPreCondition>(_preConditions).elements();
		while (en.hasMoreElements()) {
			en.nextElement().delete();
		}
	}

	public boolean isSelfActivated() {
		return isSelfActivated;
	}

	public void setIsSelfActivated(boolean is_SelfActivated) {
		this.isSelfActivated = is_SelfActivated;
	}

	public boolean isSelfExecutableNode() {
		return this instanceof SelfExecutableNode;
	}

	public boolean isWaiting() {
		return isWaiting;
	}

	public void setIsWaiting(boolean is_Waiting) {
		this.isWaiting = is_Waiting;
	}

	/**
	 * Sets attached pre-condition: relevant only for begin nodes of Operation and Action level
	 * 
	 * @param condition
	 */
	public void setAttachedPreCondition(FlexoPreCondition preCondition) {
		_attachedPreCondition = preCondition;
	}

	/**
	 * Returns attached pre-condition: relevant only for begin nodes of Operation and Action level
	 */
	public FlexoPreCondition getAttachedPreCondition() {
		return _attachedPreCondition;
	}

	// ==========================================================================
	// ============================= Pre-conditions =============================
	// ==========================================================================

	/**
	 * The insertion or removal of preConditions has to be done with addToPreConditions or removeFromPreConditions methods.
	 * 
	 * @return Vector of FlexoPreCondition
	 */
	public Vector<FlexoPreCondition> getPreConditions() {
		return _preConditions;
	}

	public void setPreConditions(Vector<FlexoPreCondition> aVector) {
		_preConditions = aVector;
	}

	public void addToPreConditions(FlexoPreCondition pre) {
		if (!_preConditions.contains(pre)) {
			_preConditions.add(pre);
			pre.setAttachedNode(this);
			setChanged();
			notifyObservers(new PreInserted(pre));
		}
	}

	public void removeFromPreCondition(FlexoPreCondition pre) {
		if (_preConditions.contains(pre)) {
			_preConditions.remove(pre);
			pre.setAttachedNode(null);
			setChanged();
			notifyObservers(new PreRemoved(pre));
		}
	}

	/**
	 * This attribute isn't supposed to be changed after node creation.
	 * 
	 * @return the NodeType
	 */
	public NodeType getNodeType() {
		return _nodeType;
	}

	/**
	 * This attribute isn't supposed to be changed after node creation.
	 * 
	 * @return the NodeType
	 */
	public void setNodeType(NodeType aType) {
		_nodeType = aType;
	}

	public boolean isBeginOrEndNode() {
		return isBeginNode() || isEndNode();
	}

	public boolean isBeginNode() {
		return (getNodeType() == NodeType.BEGIN);
	}

	public boolean isEndNode() {
		return (getNodeType() == NodeType.END);
	}

	public boolean isNormalNode() {
		return (getNodeType() == NodeType.NORMAL);
	}

	@Override
	public void getBestRole(Vector<Node> visited, TreeMap<Integer, Vector<Role>> roles, int depth) {
		if (visited.contains(this)) {
			return;
		}
		visited.add(this);
		for (FlexoPreCondition pre : getPreConditions()) {
			getBestRole(pre, visited, roles, depth);
		}
		super.getBestRole(visited, roles, depth);
	}

	@Override
	public boolean isAccessible() {
		if (isBeginNode()) {
			return true;
		}
		int accessNumber = getIncomingPostConditions().size();
		for (Enumeration e = getPreConditions().elements(); e.hasMoreElements();) {
			FlexoPreCondition pre = (FlexoPreCondition) e.nextElement();
			accessNumber += pre.getIncomingPostConditions().size();
		}
		return (accessNumber > 0) || super.isAccessible();
	}

	public abstract boolean isInteractive();

	public static String DEFAULT_NORMAL_FLEXO_NODE_NAME() {
		return FlexoLocalization.localizedForKey("noname");
	}

	public static String DEFAULT_BEGIN_FLEXO_NODE_NAME() {
		return FlexoLocalization.localizedForKey("begin_node");
	}

	public static String DEFAULT_END_FLEXO_NODE_NAME() {
		return FlexoLocalization.localizedForKey("end_node");
	}

	@Override
	public String getDefaultName() {
		if (isBeginNode()) {
			return DEFAULT_BEGIN_FLEXO_NODE_NAME();
		} else if (isEndNode()) {
			return DEFAULT_END_FLEXO_NODE_NAME();
		} else {
			return DEFAULT_NORMAL_FLEXO_NODE_NAME();
		}
	}

	// =========================================================
	// ============= Control graph management ==================
	// =========================================================

	private static ControlGraphFactory<FlexoNode> _activationComputingFactory;
	private static ControlGraphFactory<FlexoNode> _desactivationComputingFactory;

	public static void setActivationComputingFactory(ControlGraphFactory<FlexoNode> factory) {
		_activationComputingFactory = factory;
	}

	public static void setDesactivationComputingFactory(ControlGraphFactory<FlexoNode> factory) {
		_desactivationComputingFactory = factory;
	}

	public WorkflowControlGraph<FlexoNode> getActivation() {
		if (_activationComputingFactory != null) {
			return _activationComputingFactory.getControlGraph(this);
		}
		return null;
	}

	public WorkflowControlGraph<FlexoNode> getDesactivation() {
		if (_desactivationComputingFactory != null) {
			return _desactivationComputingFactory.getControlGraph(this);
		}
		return null;
	}

	@Override
	public void setProgrammingLanguageForControlGraphComputation(ProgrammingLanguage language) {
		if (getActivation() != null) {
			getActivation().setProgrammingLanguage(language);
		}
		if (getDesactivation() != null) {
			getDesactivation().setProgrammingLanguage(language);
		}
	}

	@Override
	public void setInterproceduralForControlGraphComputation(boolean interprocedural) {
		if (getActivation() != null) {
			getActivation().setInterprocedural(interprocedural);
		}
		if (getDesactivation() != null) {
			getDesactivation().setInterprocedural(interprocedural);
		}
	}

	@Override
	public String getExecutableElementName() {
		return FlexoLocalization.localizedForKeyWithParams("node_($0)", getName());
	}

	public static class InteractiveNodeCannotBePutInsideExecutionPetriGraph extends
			ValidationRule<InteractiveNodeCannotBePutInsideExecutionPetriGraph, FlexoNode> {
		public InteractiveNodeCannotBePutInsideExecutionPetriGraph() {
			super(FlexoNode.class, "interactive_nodes_cannot_be_found_inside_an_execution_petri_graph");
		}

		@Override
		public ValidationIssue<InteractiveNodeCannotBePutInsideExecutionPetriGraph, FlexoNode> applyValidation(FlexoNode node) {
			if (node.isInteractive() && node.isEmbeddedInSelfExecutableNode()) {
				return new ValidationError<InteractiveNodeCannotBePutInsideExecutionPetriGraph, FlexoNode>(this, node,
						"interactive_nodes_cannot_be_found_inside_an_execution_petri_graph",
						new DeletionFixProposal<InteractiveNodeCannotBePutInsideExecutionPetriGraph, FlexoNode>("delete_this_node"));
			}
			return null;
		}
	}

	public static class EndNodeCannotHaveMultipleEdges extends ValidationRule<EndNodeCannotHaveMultipleEdges, FlexoNode> {

		public EndNodeCannotHaveMultipleEdges() {
			super(FlexoNode.class, "end_node_at_this_level_cannot_have_multiple_outgoing_edges");
		}

		@Override
		public ValidationIssue<EndNodeCannotHaveMultipleEdges, FlexoNode> applyValidation(FlexoNode node) {
			if (node instanceof ActionNode && node.isEndNode() && node.getOutgoingPostConditions().size() > 1) {
				return new ValidationWarning<EndNodeCannotHaveMultipleEdges, FlexoNode>(this, node,
						"end_node_at_this_level_cannot_have_multiple_outgoing_edges");
			}
			return null;
		}

	}
}
