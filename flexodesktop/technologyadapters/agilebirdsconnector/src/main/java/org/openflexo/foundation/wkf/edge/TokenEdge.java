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
package org.openflexo.foundation.wkf.edge;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.bindings.Bindable;
import org.openflexo.foundation.validation.DeletionFixProposal;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.validation.ValidationWarning;
import org.openflexo.foundation.wkf.FlexoLevel;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.FlexoNode;
import org.openflexo.foundation.wkf.node.Node;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.PetriGraphNode;
import org.openflexo.foundation.xml.FlexoProcessBuilder;

/**
 * Edge linking 2 FlexoNode in the context of petri graphs (carry tokens) <U>Main attributes</U><BR>
 * <B>Token increment</B>: the number of tokens that are sent to the next precondition when the node is executed.<BR>
 * <B>Delay</B>: the time taken by the tokens to go to the next precondition (usually: no delay).<BR>
 * 
 * @author sguerin
 * 
 */
public final class TokenEdge extends FlexoPostCondition<PetriGraphNode, Node> implements Bindable {

	static final Logger logger = Logger.getLogger(TokenEdge.class.getPackage().getName());

	private int _tokenIncrem;

	/**
	 * Constructor used during deserialization
	 */
	public TokenEdge(FlexoProcessBuilder builder) {
		this(builder.process);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public TokenEdge(FlexoProcess process) {
		super(process);
		_tokenIncrem = 1;
	}

	/**
	 * Constructor with start node, next precondition and process
	 */
	public TokenEdge(PetriGraphNode startNode, Node endNode, FlexoProcess process) throws InvalidEdgeException {
		this(process);
		if (endNode.getProcess() == process) {
			setEndNode(endNode);
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Inconsistent data while building TokenEdge !");
			}
			throw new InvalidEdgeException(this);
		}
		if (startNode.getProcess() == process) {
			setStartNode(startNode);
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Inconsistent data while building TokenEdge !");
			}
			throw new InvalidEdgeException(this);
		}
		if (!isEdgeValid()) {
			throw new InvalidEdgeException(this);
		}
		// Special stuff to make operation node synchronized if required
		if (startNode instanceof ActionNode && endNode.getNode() instanceof ActionNode && ((ActionNode) endNode.getNode()).isEndNode()) {
			ActionNode s = (ActionNode) startNode;
			ActionNode e = (ActionNode) endNode.getNode();
			if (s.getOperationNode() == e.getOperationNode() && s.getOperationNode() != null) {
				s.getOperationNode().setIsSynchronized(true);
			}
		}
	}

	/**
	 * Constructor with start node, next precondition
	 */
	public TokenEdge(PetriGraphNode startNode, Node nextPre) throws InvalidEdgeException {
		this(startNode, nextPre, startNode.getProcess());
	}

	@Override
	public int getTokenIncrem() {
		return _tokenIncrem;
	}

	public void setTokenIncrem(int increm) {
		_tokenIncrem = increm;
	}

	// ==========================================================================
	// ============================= InspectableObject
	// ==========================
	// ==========================================================================

	@Override
	public String getInspectorName() {
		return Inspectors.WKF.TOKEN_EDGE_INSPECTOR;
	}

	// ==========================================================================
	// ============================= Accessors
	// ==================================
	// ==========================================================================

	@Override
	public FlexoLevel getLevel() {
		if (getStartNode() != null) {
			if (getStartNode() instanceof FlexoNode && ((FlexoNode) getStartNode()).isEndNode()) {
				if (getStartNode() instanceof OperationNode) {
					return FlexoLevel.ACTIVITY;
				}
				if (getStartNode() instanceof ActionNode) {
					return FlexoLevel.OPERATION;
				}
			}
			return getStartNode().getLevel();
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("No attached starting node for this post-condition !");
			}
			return null;
		}
	}

	// ==========================================================================
	// ============================= Validation
	// =================================
	// ==========================================================================

	@Override
	public boolean isEdgeValid() {
		// Such edges are valid if and only if:
		// - both nodes are in the same process
		// While execution engine is not well defined, we dont't perform more
		// validation on it

		if (getStartNode() == null || getNextNode() == null || getStartNode() == getNextNode()) {
			return false;
		}

		PetriGraphNode startNode = getStartNode();
		PetriGraphNode nextNode = getEndNode().getNode();
		if (startNodeIsOperatorNode()) {
			// OperatorOutEdge rule
			if (getStartNode().getParentPetriGraph() != nextNode.getParentPetriGraph()) {
				return false;
			}
		} else if (endNodeIsOperatorNode()) {
			// OperatorInEdge rule
			if (!getStartNode().isEmbeddedInPetriGraph(nextNode.getParentPetriGraph())) {
				return false;
			}
		}
		return startNode.getProcess() == nextNode.getProcess();

	}

	public static class TokenEdgeMustBeValid extends ValidationRule<TokenEdgeMustBeValid, TokenEdge> {
		public TokenEdgeMustBeValid() {
			super(TokenEdge.class, "token_edge_must_be_valid");
		}

		@Override
		public ValidationIssue<TokenEdgeMustBeValid, TokenEdge> applyValidation(TokenEdge edge) {
			if (!edge.isEdgeValid()) {
				ValidationError<TokenEdgeMustBeValid, TokenEdge> error = new ValidationError<TokenEdgeMustBeValid, TokenEdge>(this, edge,
						"token_edge_is_not_valid");
				error.addToFixProposals(new DeletionFixProposal<TokenEdgeMustBeValid, TokenEdge>("delete_this_edge"));
				return error;
			}
			return null;
		}

	}

	public static class TokenEdgeShouldHaveNonNullTokenIncrement extends
			ValidationRule<TokenEdgeShouldHaveNonNullTokenIncrement, TokenEdge> {
		public TokenEdgeShouldHaveNonNullTokenIncrement() {
			super(TokenEdge.class, "token_edge_should_have_non_null_token_increment");
		}

		@Override
		public ValidationIssue<TokenEdgeShouldHaveNonNullTokenIncrement, TokenEdge> applyValidation(TokenEdge edge) {
			if (edge.getTokenIncrem() == 0) {
				ValidationWarning<TokenEdgeShouldHaveNonNullTokenIncrement, TokenEdge> warning = new ValidationWarning<TokenEdgeShouldHaveNonNullTokenIncrement, TokenEdge>(
						this, edge, "token_edge_has_a_null_token_increment");
				warning.addToFixProposals(new SetsTokenIncrementToDefaultValue("sets_token_increment_to_default_value"));
				warning.addToFixProposals(new DeletionFixProposal<TokenEdgeShouldHaveNonNullTokenIncrement, TokenEdge>("delete_this_edge"));
				return warning;
			}
			return null;
		}

	}

	public static class SetsTokenIncrementToDefaultValue extends FixProposal<TokenEdgeShouldHaveNonNullTokenIncrement, TokenEdge> {
		public SetsTokenIncrementToDefaultValue(String aMessage) {
			super(aMessage);
		}

		@Override
		protected void fixAction() {
			getObject().setTokenIncrem(1);
		}
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.AgileBirdsObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "token_edge";
	}

	@Override
	public String toString() {
		return super.toString() + " id=" + getSerializationIdentifier();
	}

	@Override
	public Class<PetriGraphNode> getStartNodeClass() {
		return PetriGraphNode.class;
	}

	@Override
	public Class<Node> getEndNodeClass() {
		return Node.class;
	}
}
