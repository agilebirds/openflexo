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

import java.awt.Stroke;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.validation.DeletionFixProposal;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.wkf.Constants;
import org.openflexo.foundation.wkf.FlexoPetriGraph;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.ws.AbstractMessageDefinition;
import org.openflexo.foundation.wkf.ws.FlexoPort;
import org.openflexo.foundation.wkf.ws.FlexoPortMap;
import org.openflexo.foundation.wkf.ws.PortMapRegistery;
import org.openflexo.foundation.wkf.ws.ServiceOperation;
import org.openflexo.foundation.xml.FlexoProcessBuilder;

/**
 * Edge linking a WS OUT portmap to a WS IN portmap
 * 
 * @author sguerin
 * 
 */
public final class TransferWSEdge extends MessageEdge<FlexoPortMap, FlexoPortMap> implements PortMapEntry, PortMapExit {

	private static final Logger logger = Logger.getLogger(TransferWSEdge.class.getPackage().getName());

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Constructor used during deserialization
	 */
	public TransferWSEdge(FlexoProcessBuilder builder) {
		this(builder.process);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public TransferWSEdge(FlexoProcess process) {
		super(process);
	}

	/**
	 * Constructor with start node, next port and process
	 */
	public TransferWSEdge(FlexoPortMap startPortMap, FlexoPortMap nextPortMap, FlexoProcess process) throws InvalidEdgeException {
		this(process);
		if (nextPortMap.getProcess() == process && startPortMap.getProcess() == process) {
			setStartNode(startPortMap);
			setEndNode(nextPortMap);
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Inconsistent data while building TransferWSEdge !");
			}
			throw new InvalidEdgeException(this);
		}
		if (!isEdgeValid()) {
			resetStartAndEndNode();
			throw new InvalidEdgeException(this);
		}
	}

	/**
	 * Constructor with start node, next port
	 */
	public TransferWSEdge(FlexoPortMap startPortMap, FlexoPortMap nextPortMap) throws InvalidEdgeException {
		this(startPortMap, nextPortMap, startPortMap.getProcess());
	}

	@Override
	public String getInspectorName() {
		return Inspectors.WKF.TRANSFER_WS_EDGE_INSPECTOR;
	}

	public PortMapRegistery getStartPortMapRegistery() {
		if (getStartNode() != null) {
			return getStartNode().getPortMapRegistery();
		}
		return null;
	}

	public PortMapRegistery getEndPortMapRegistery() {
		if (getEndNode() != null) {
			return getEndNode().getPortMapRegistery();
		}
		return null;
	}

	public Stroke getStroke() {
		return Constants.DASHED_STROKE;

	}

	// ==========================================================================
	// ============================= Validation
	// =================================
	// ==========================================================================

	@Override
	public boolean isEdgeValid() {
		// Such edges are valid if and only if they link a OUT portmap
		// to a IN portmap

		if (getStartNode() == null || !getStartNode().isOutputPort() || getEndNode() == null || !getEndNode().isInputPort()
				|| getStartPortMapRegistery() == null || getEndPortMapRegistery() == null
				|| getStartPortMapRegistery().getSubProcessNode() == null || getEndPortMapRegistery().getSubProcessNode() == null) {
			return false;
		}
		FlexoPetriGraph startPG = getStartPortMapRegistery().getSubProcessNode().getParentPetriGraph();
		FlexoPetriGraph endPG = getEndPortMapRegistery().getSubProcessNode().getParentPetriGraph();
		return startPG == endPG;
	}

	public static class TransferWSEdgeMustBeValid extends ValidationRule<TransferWSEdgeMustBeValid, TransferWSEdge> {
		public TransferWSEdgeMustBeValid() {
			super(TransferWSEdge.class, "transfer_ws_edge_must_be_valid");
		}

		@Override
		public ValidationIssue<TransferWSEdgeMustBeValid, TransferWSEdge> applyValidation(TransferWSEdge edge) {
			if (!edge.isEdgeValid()) {
				ValidationError<TransferWSEdgeMustBeValid, TransferWSEdge> error = new ValidationError<TransferWSEdgeMustBeValid, TransferWSEdge>(
						this, edge, "transfer_ws_edge_is_not_valid");
				error.addToFixProposals(new DeletionFixProposal<TransferWSEdgeMustBeValid, TransferWSEdge>("delete_this_message_edge"));
				return error;
			}
			return null;
		}

	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "transfer_ws_edge";
	}

	@Override
	public FlexoPort getFlexoPort() {
		if (getInputServiceOperation() != null) {
			return getInputServiceOperation().getPort();
		}
		return null;
	}

	public ServiceOperation getInputServiceOperation() {
		if (getStartNode() != null) {
			return getStartNode().getOperation();
		}
		return null;
	}

	public ServiceOperation getOutputServiceOperation() {
		if (getEndNode() != null) {
			return getEndNode().getOperation();
		}
		return null;
	}

	@Override
	public AbstractMessageDefinition getInputMessageDefinition() {
		return null;
	}

	@Override
	public AbstractMessageDefinition getOutputMessageDefinition() {
		return null;
	}

	@Override
	public boolean isInputPort() {
		return false;
	}

	@Override
	public boolean isOutputPort() {
		return false;
	}

	@Override
	public Class<FlexoPortMap> getStartNodeClass() {
		return FlexoPortMap.class;
	}

	@Override
	public Class<FlexoPortMap> getEndNodeClass() {
		return FlexoPortMap.class;
	}
}
