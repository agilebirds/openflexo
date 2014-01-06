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
import org.openflexo.foundation.validation.DeletionFixProposal;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.node.Node;
import org.openflexo.foundation.wkf.ws.AbstractInPort;
import org.openflexo.foundation.wkf.ws.FlexoPort;
import org.openflexo.foundation.wkf.ws.MessageDefinition;
import org.openflexo.foundation.wkf.ws.PortRegistery;
import org.openflexo.foundation.xml.FlexoProcessBuilder;

/**
 * Edge linking a AbstractInPort to a FlexoNode
 * 
 * @author sguerin
 * 
 */
public final class InternalMessageInEdge extends InternalMessageEdge<AbstractInPort, Node> implements PortExit {

	private static final Logger logger = Logger.getLogger(InternalMessageInEdge.class.getPackage().getName());

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Constructor used during deserialization
	 */
	public InternalMessageInEdge(FlexoProcessBuilder builder) {
		this(builder.process);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public InternalMessageInEdge(FlexoProcess process) {
		super(process);
	}

	/**
	 * Constructor with start port, next precondition and process
	 */
	public InternalMessageInEdge(AbstractInPort startPort, Node endPre, FlexoProcess process) throws InvalidEdgeException {
		this(process);
		if (endPre.getProcess() == process && startPort.getProcess() == process) {
			setStartNode(startPort);
			setEndNode(endPre);
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Inconsistent data while building InternalMessageInEdge !");
			}
			throw new InvalidEdgeException(this);
		}
		if (!isEdgeValid()) {
			resetStartAndEndNode();
			throw new InvalidEdgeException(this);
		}

	}

	/**
	 * Constructor with start port, next precondition
	 */
	public InternalMessageInEdge(AbstractInPort startPort, Node endPre) throws InvalidEdgeException {
		this(startPort, endPre, startPort.getProcess());
	}

	@Override
	public String getInspectorName() {
		return Inspectors.WKF.INTERNAL_MESSAGE_IN_EDGE_INSPECTOR;
	}

	@Override
	public PortRegistery getPortRegistery() {
		if (getStartNode() != null) {
			return getStartNode().getPortRegistery();
		}
		return null;
	}

	// ==========================================================================
	// ============================= Validation
	// =================================
	// ==========================================================================

	@Override
	public boolean isEdgeValid() {
		// Such edges are valid if and only if they link a IN port to a
		// FlexoNode of any level located in the petri graph of the sub-process where
		// the port registery is registered

		if (getStartNode() == null || getEndNode() == null || getEndNode().getNode() == null) {
			return false;
		}

		return getEndNode().getNode().getProcess() == getPortRegistery().getProcess();
	}

	public static class InternalMessageInEdgeMustBeValid extends ValidationRule<InternalMessageInEdgeMustBeValid, InternalMessageInEdge> {
		public InternalMessageInEdgeMustBeValid() {
			super(InternalMessageInEdge.class, "internal_message_in_edge_must_be_valid");
		}

		@Override
		public ValidationIssue<InternalMessageInEdgeMustBeValid, InternalMessageInEdge> applyValidation(InternalMessageInEdge edge) {
			if (!edge.isEdgeValid()) {
				ValidationError<InternalMessageInEdgeMustBeValid, InternalMessageInEdge> error = new ValidationError<InternalMessageInEdgeMustBeValid, InternalMessageInEdge>(
						this, edge, "internal_message_in_edge_is_not_valid");
				error.addToFixProposals(new DeletionFixProposal<InternalMessageInEdgeMustBeValid, InternalMessageInEdge>(
						"delete_this_message_edge"));
				return error;
			}
			return null;
		}

	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.AgileBirdsObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "internal_message_in_edge";
	}

	@Override
	public MessageDefinition getInputMessageDefinition() {
		if (getStartNode() != null) {
			return getStartNode().getInputMessageDefinition();
		}
		return null;
	}

	@Override
	public MessageDefinition getOutputMessageDefinition() {
		return null;
	}

	@Override
	public FlexoPort getFlexoPort() {
		return getStartNode();
	}

	@Override
	public Class<Node> getEndNodeClass() {
		return Node.class;
	}

	@Override
	public Class<AbstractInPort> getStartNodeClass() {
		return AbstractInPort.class;
	}

}
