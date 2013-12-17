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

/*
 * FlexoNode.java
 * Project WorkflowEditor
 *
 * Created by benoit on Mar 3, 2004
 */

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.DeletableObject;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.validation.ParameteredFixProposal;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.validation.ValidationWarning;
import org.openflexo.foundation.wkf.FlexoLevel;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.LevelledObject;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.dm.NodeRemoved;
import org.openflexo.foundation.wkf.dm.PostInserted;
import org.openflexo.foundation.wkf.dm.PostRemoved;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.edge.TokenEdge;
import org.openflexo.foundation.wkf.edge.WKFEdge;
import org.openflexo.foundation.wkf.ws.FlexoPortMap;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.toolbox.ToolBox;

/**
 * A AbstractNode is the base element representing a node in a PetriGraph. A AbstractNode is abstract and must be subsequently subclassed
 * with FlexoNode, OperatorNode and FlexoPort
 * 
 * @author sguerin
 */
public abstract class AbstractNode extends WKFNode implements InspectableObject, DeletableObject, LevelledObject {

	private static final Logger logger = Logger.getLogger(AbstractNode.class.getPackage().getName());

	// ==========================================================================
	// ============================= Variables
	// ==================================
	// ==========================================================================

	private String _nodeName = null;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Default constructor
	 */
	public AbstractNode(FlexoProcess process) {
		super(process);
		_outgoingPostConditions = new Vector<FlexoPostCondition<AbstractNode, AbstractNode>>();
		_incomingPostConditions = new Vector<FlexoPostCondition<AbstractNode, AbstractNode>>();
	}

	public AbstractNode getNode() {
		return this;
	}

	public String getNiceName() {
		String niceName = getName();
		if (niceName != null && niceName.trim().length() > 0) {
			return ToolBox.getJavaName(niceName);
		}
		return getNodeTypeName();
	}

	public String getNodeTypeName() {
		String cls = getClass().getSimpleName();
		if (cls.startsWith("Flexo")) {
			cls = cls.substring("Flexo".length());
		}
		if (cls.endsWith("Node")) {
			cls = cls.substring(0, cls.length() - "Node".length());
		}
		return cls;
	}

	// ==========================================================================
	// ============================= InspectableObject
	// ==========================
	// ==========================================================================

	/**
	 * Default inspector name: implemented in sub-classes !
	 */
	@Override
	public String getInspectorName() {
		return Inspectors.WKF.NODE_INSPECTOR;
	}

	/**
	 * Return a Vector of all embedded WKFObjects
	 * 
	 * @return a Vector of WKFObject instances
	 */
	@Override
	public Vector<WKFObject> getAllEmbeddedWKFObjects() {
		Vector<WKFObject> returned = super.getAllEmbeddedWKFObjects();
		returned.addAll(getIncomingPostConditions());
		returned.addAll(getOutgoingPostConditions());
		return returned;
	}

	protected void notifyPostInsertedToProcess(FlexoPostCondition<?, ?> post) {
		if (getProcess() != null) {
			getProcess().notifyPostInserted(post);
		}
	}

	private Vector<FlexoPostCondition<AbstractNode, AbstractNode>> _outgoingPostConditions;

	/**
	 * Stores the incoming post conditions, as a Vector of PreConditionEntry
	 */
	private Vector<FlexoPostCondition<AbstractNode, AbstractNode>> _incomingPostConditions;

	/**
	 * Returns the incoming post conditions, as a Vector of PreConditionEntry
	 */
	public final Vector<FlexoPostCondition<AbstractNode, AbstractNode>> getIncomingPostConditions() {
		return _incomingPostConditions;
	}

	public final void setIncomingPostConditions(Vector<FlexoPostCondition<AbstractNode, AbstractNode>> postConditions) {
		_incomingPostConditions = postConditions;
	}

	public final void removeFromIncomingPostConditions(FlexoPostCondition post) {
		if (_incomingPostConditions.contains(post)) {
			_incomingPostConditions.remove(post);
			post.setEndNode(null);
			setChanged();
			notifyObservers(new PostRemoved(post));
		}
	}

	public final void addToIncomingPostConditions(FlexoPostCondition post) {
		if (!_incomingPostConditions.contains(post) && post.getEndNodeClass().isAssignableFrom(getClass())
				&& mayHaveIncomingPostConditions()) {
			_incomingPostConditions.add(post);
			post.setEndNode(this);
			if (!isDeserializing()) {
				setChanged();
				notifyObservers(new PostInserted(post));
				notifyPostInsertedToProcess(post);
			}
		}
	}

	public abstract boolean mayHaveIncomingPostConditions();

	public final boolean hasIncomingPostConditions() {
		return getIncomingPostConditions().size() > 0;
	}

	/**
	 * Returns all the start nodes of the incoming post-conditions of this node
	 * 
	 * @return all the start nodes of the incoming post-conditions of this node.
	 */
	public Vector<AbstractNode> getFromPostconditionnedNodes() {
		Vector<AbstractNode> v = new Vector<AbstractNode>();
		for (FlexoPostCondition<AbstractNode, AbstractNode> a : getIncomingPostConditions()) {
			if (!v.contains(a)) {
				v.add(a.getStartNode());
			}
		}
		return v;
	}

	@Override
	public Vector<WKFEdge<?, ?>> getAllIncomingEdges() {
		Vector<WKFEdge<?, ?>> v = super.getAllIncomingEdges();
		v.addAll(getIncomingPostConditions());
		return v;
	}

	// ==========================================================================
	// ======================== Outgoing Post-conditions ======================
	// ==========================================================================

	/**
	 * @return Vector of FlexoPostCondition
	 */
	public Vector<FlexoPostCondition<AbstractNode, AbstractNode>> getOutgoingPostConditions() {
		return _outgoingPostConditions;
	}

	public void setOutgoingPostConditions(Vector<FlexoPostCondition<AbstractNode, AbstractNode>> aVector) {
		_outgoingPostConditions = aVector;
	}

	public void addToOutgoingPostConditions(FlexoPostCondition post) {
		if (!_outgoingPostConditions.contains(post) && post.getStartNodeClass().isAssignableFrom(getClass())
				&& mayHaveOutgoingPostConditions()) {
			_outgoingPostConditions.add(post);
			post.setStartNode(this);
			if (!isDeserializing()) {
				setChanged();
				notifyObservers(new PostInserted(post));
				notifyPostInsertedToProcess(post);
			}
		}
	}

	public void removeFromOutgoingPostConditions(FlexoPostCondition post) {
		if (_outgoingPostConditions.contains(post)) {
			_outgoingPostConditions.remove(post);
			post.setStartNode(null);
			setChanged();
			notifyObservers(new PostRemoved(post));
		}
	}

	public abstract boolean mayHaveOutgoingPostConditions();

	public final boolean hasOutgoingPostConditions() {
		return getOutgoingPostConditions().size() > 0;
	}

	/**
	 * Returns all the end nodes of the outgoing post-conditions of this node
	 * 
	 * @return all the end nodes of the outgoing post-conditions of this node.
	 */
	public Vector<AbstractNode> getToPostconditionnedNodes() {
		Vector<AbstractNode> v = new Vector<AbstractNode>();
		for (FlexoPostCondition<AbstractNode, AbstractNode> a : getOutgoingPostConditions()) {
			if (!v.contains(a)) {
				v.add(a.getEndNode());
			}
		}
		return v;
	}

	@Override
	public Vector<WKFEdge<?, ?>> getAllOutgoingEdges() {
		Vector<WKFEdge<?, ?>> v = super.getAllOutgoingEdges();
		v.addAll(getOutgoingPostConditions());
		return v;
	}

	// ==========================================================================
	// ================================= Delete ===============================
	// ==========================================================================

	@Override
	public boolean delete() {
		Enumeration<FlexoPostCondition> en = new Vector<FlexoPostCondition>(_incomingPostConditions).elements();
		while (en.hasMoreElements()) {
			en.nextElement().delete();
		}
		en = new Vector<FlexoPostCondition>(_outgoingPostConditions).elements();
		while (en.hasMoreElements()) {
			en.nextElement().delete();
		}
		setChanged();
		notifyObservers(new NodeRemoved(this));
		return super.delete();
	}

	/**
	 * Build and return a vector of all the objects that will be deleted during this deletion
	 * 
	 * @param aVector
	 *            of DeletableObject
	 */
	@Override
	public Vector<WKFObject> getAllEmbeddedDeleted() {
		return getAllEmbeddedWKFObjects();
	}

	@Override
	public void setName(String aName) {
		String oldValue = getName();
		if (oldValue == null || !oldValue.equals(aName)) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Name was: " + oldValue + " values now: " + aName);
			}
			setNodeName(aName);
			setChanged();
			notifyObservers(new NameChanged(oldValue, aName));
		}
	}

	@Override
	public String getName() {
		return getNodeName();
	}

	public Vector<AbstractNode> nodesWithSameName() {
		Vector<AbstractNode> returned = new Vector<AbstractNode>();
		Vector<AbstractNode> allNodes = getProcess().getAllAbstractNodes();
		for (Enumeration<AbstractNode> e = allNodes.elements(); e.hasMoreElements();) {
			AbstractNode node = e.nextElement();
			if (node != this && node.getFullyQualifiedName().equalsIgnoreCase(getFullyQualifiedName())) {
				returned.add(node);
			}
		}
		return returned;
	}

	public boolean isNameAmbigous() {
		return nodesWithSameName().size() > 0;
	}

	public String findNextNonAmbigousName() {
		return getProcess().findNextNonAmbigousNameForNode(getName(), this);
	}

	public abstract String getDefaultName();

	public String getNodeName() {
		return _nodeName;
	}

	public void setNodeName(String aName) {
		_nodeName = aName;
	}

	@Override
	public boolean isNodeValid() {
		return getProcess() != null;
	}

	/**
	 * default implementation is false. overriden for OperatorNode
	 * 
	 * @return false
	 */
	public boolean isOperatorNode() {
		return false;
	}

	/**
	 * This attribute isn't supposed to be changed after node creation. Activity nodes are level ACTIVITY. Operations are level OPERATION.
	 * Actions are level ACTION. Other specific node can exist at level 3 or more.
	 * 
	 * @return the node level.
	 */
	@Override
	public abstract FlexoLevel getLevel();

	@Override
	public String toString() {
		return getFullyQualifiedName();
	}

	@Override
	public abstract String getFullyQualifiedName();

	public static String formattedString(String s) {
		if (s != null) {
			s = s.replaceAll("\\s*", "");
		}
		return s;
	}

	// ==========================================================================
	// ============================= Validation
	// =================================
	// ==========================================================================

	public static class NodeShouldHaveNonAmbigousName extends ValidationRule<NodeShouldHaveNonAmbigousName, AbstractNode> {
		public NodeShouldHaveNonAmbigousName() {
			super(AbstractNode.class, "node_should_have_non_ambigous_name");
		}

		@Override
		public ValidationIssue<NodeShouldHaveNonAmbigousName, AbstractNode> applyValidation(final AbstractNode node) {
			Vector<? extends Validable> nodesWithSameName = node.nodesWithSameName();
			if (nodesWithSameName.size() > 0) {
				ValidationWarning<NodeShouldHaveNonAmbigousName, AbstractNode> warning = new ValidationWarning<NodeShouldHaveNonAmbigousName, AbstractNode>(
						this, node, "node_($object.name)_has_ambigous_name");
				warning.addToRelatedValidableObjects(nodesWithSameName);
				warning.addToFixProposals(new RenameThisNode(node));
				return warning;
			}
			if (node.getProcess() != null && node.getProcess().getName().equalsIgnoreCase(node.getName())) {
				ValidationWarning<NodeShouldHaveNonAmbigousName, AbstractNode> warning = new ValidationWarning<NodeShouldHaveNonAmbigousName, AbstractNode>(
						this, node, "node_($object.name)_has_ambigous_name");
				warning.addToRelatedValidableObjects(node.getProcess());
				warning.addToFixProposals(new RenameThisNode(node));
				return warning;
			}
			return null;
		}

		public class RenameThisNode extends ParameteredFixProposal<NodeShouldHaveNonAmbigousName, AbstractNode> {
			public RenameThisNode(AbstractNode node) {
				super("rename_this_node", "newName", "enter_a_non_ambigous_name", node.findNextNonAmbigousName());
			}

			@Override
			protected void fixAction() {
				String newName = (String) getValueForParameter("newName");
				getObject().setName(newName);
			}
		}
	}

	public static class NodeCannotHaveMoreThanOneDefaultOutgoingTokenEdge extends
			ValidationRule<NodeCannotHaveMoreThanOneDefaultOutgoingTokenEdge, AbstractNode> {
		public NodeCannotHaveMoreThanOneDefaultOutgoingTokenEdge() {
			super(AbstractNode.class, "node_cannot_have_more_than_one_default_outgoing_token_edge");
		}

		@Override
		public ValidationIssue<NodeCannotHaveMoreThanOneDefaultOutgoingTokenEdge, AbstractNode> applyValidation(final AbstractNode node) {
			boolean hasAlreadyOne = false;
			for (FlexoPostCondition p : node.getOutgoingPostConditions()) {
				if (p instanceof TokenEdge && p.getIsDefaultFlow()) {
					if (hasAlreadyOne) {
						ValidationWarning<NodeCannotHaveMoreThanOneDefaultOutgoingTokenEdge, AbstractNode> warning = new ValidationWarning<NodeCannotHaveMoreThanOneDefaultOutgoingTokenEdge, AbstractNode>(
								this, node, "node_($object.name)_has_more_than_one_default_outgoing_edge");
						return warning;
					} else {
						hasAlreadyOne = true;
					}
				}
			}
			return null;
		}
	}

	public static class NodeWithConditionalEdgeOrDefaultEdgeMustHaveMoreThanOneEdge extends
			ValidationRule<NodeWithConditionalEdgeOrDefaultEdgeMustHaveMoreThanOneEdge, AbstractNode> {
		public NodeWithConditionalEdgeOrDefaultEdgeMustHaveMoreThanOneEdge() {
			super(AbstractNode.class, "node_with_conditional_edge_or_default_edge_must_have_more_than_one_edge");
		}

		@Override
		public ValidationIssue<NodeWithConditionalEdgeOrDefaultEdgeMustHaveMoreThanOneEdge, AbstractNode> applyValidation(
				final AbstractNode node) {
			int defaultCount = 0;
			int conditionalCount = 0;
			int regular = 0;
			Vector<FlexoPostCondition<?, ?>> listOfAllPostToConsider = new Vector<FlexoPostCondition<?, ?>>();
			listOfAllPostToConsider.addAll(node.getOutgoingPostConditions());

			if (node instanceof ActivityNode || node instanceof OperationNode) {
				FlexoNode startNode = (FlexoNode) node;
				if (startNode.isEndNode()) {
					// try to find other brother end node with outgoing post
					for (PetriGraphNode brotherNode : startNode.getParentPetriGraph().getNodes()) {
						if (brotherNode instanceof FlexoNode) {
							if (((FlexoNode) brotherNode).isEndNode() && brotherNode != startNode) {
								return null;
							}
						}
					}
					if (startNode.getParentPetriGraph().getContainer() instanceof SubProcessNode) {
						SubProcessNode sub = (SubProcessNode) startNode.getParentPetriGraph().getContainer();
						if (sub.getPortMapRegistery() != null && sub.getPortMapRegistery().getAllOutPortmaps().size() > 0) {
							return null;
						}
					}
				}
			} else if (node instanceof FlexoPortMap && ((FlexoPortMap) node).isOutputPort()) {
				SubProcessNode sub = ((FlexoPortMap) node).getSubProcessNode();
				if (sub.getPortMapRegistery().getAllOutPortmaps().size() > 0) {
					return null;
				}
				if (sub.getOperationPetriGraph() != null && sub.getOperationPetriGraph().getAllEndNodes().size() > 0) {
					return null;
				}
			}

			for (FlexoPostCondition p : listOfAllPostToConsider) {
				if (p.getIsDefaultFlow()) {
					defaultCount++;
				} else if (p.getIsConditional()) {
					conditionalCount++;
				} else {
					regular++;
				}
			}
			if (defaultCount > 0 && regular + conditionalCount == 0) {
				if (node instanceof OperatorNode && ((OperatorNode) node).isExclusiveGateway()) {
					return null; // There is a rule on the post conditions to prevent this already!
				} else {
					ValidationWarning<NodeWithConditionalEdgeOrDefaultEdgeMustHaveMoreThanOneEdge, AbstractNode> warning = new ValidationWarning<NodeWithConditionalEdgeOrDefaultEdgeMustHaveMoreThanOneEdge, AbstractNode>(
							this, node, "node_($object.name)_has_one_default_outgoing_edge_should_have_at_least_another_outgoing_edge");
					return warning;
				}
			}
			if (conditionalCount == 1 && regular == 0 && defaultCount == 0 && !listOfAllPostToConsider.firstElement().mustBeConditional()) {
				if (node instanceof OperatorNode && ((OperatorNode) node).isExclusiveGateway()) {
					return null; // There is a rule on the post conditions to prevent this already!
				} else {
					ValidationWarning<NodeWithConditionalEdgeOrDefaultEdgeMustHaveMoreThanOneEdge, AbstractNode> warning = new ValidationWarning<NodeWithConditionalEdgeOrDefaultEdgeMustHaveMoreThanOneEdge, AbstractNode>(
							this, node, "node_($object.name)_has_one_conditional_outgoing_edge_should_have_at_least_another_outgoing_edge");
					return warning;
				}
			}
			return null;
		}
	}

	public static class NodeWithDefaultFlowMustHaveConditionOneOtherEdge extends
			ValidationRule<NodeWithDefaultFlowMustHaveConditionOneOtherEdge, AbstractNode> {
		public NodeWithDefaultFlowMustHaveConditionOneOtherEdge() {
			super(AbstractNode.class, "node_cannot_have_more_than_one_default_outgoing_token_edge");
		}

		@Override
		public ValidationIssue<NodeWithDefaultFlowMustHaveConditionOneOtherEdge, AbstractNode> applyValidation(final AbstractNode node) {
			int defaultCount = 0;
			int conditionalCount = 0;
			int totalCount = 0;
			for (FlexoPostCondition p : node.getOutgoingPostConditions()) {
				if (p instanceof TokenEdge) {
					totalCount++;
					if (p.getIsDefaultFlow()) {
						defaultCount++;
					} else if (p.getIsConditional()) {
						conditionalCount++;
					}
				}
			}
			if (defaultCount > 0 && totalCount != defaultCount + conditionalCount) {
				if (node instanceof OperatorNode && ((OperatorNode) node).isExclusiveGateway()) {
					return null; // There is a rule on the post conditions to prevent this already!
				} else {
					ValidationWarning<NodeWithDefaultFlowMustHaveConditionOneOtherEdge, AbstractNode> warning = new ValidationWarning<NodeWithDefaultFlowMustHaveConditionOneOtherEdge, AbstractNode>(
							this, node, "node_($object.name)_has_one_default_outgoing_flow_and_so_other_edges_must_be_conditionnal");
					return warning;
				}
			}
			return null;
		}
	}

}
