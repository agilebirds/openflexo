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
import org.openflexo.foundation.wkf.ws.FlexoPort;
import org.openflexo.foundation.wkf.ws.FlexoPortMap;
import org.openflexo.foundation.wkf.ws.MessageDefinition;
import org.openflexo.foundation.wkf.ws.OutputPort;
import org.openflexo.foundation.wkf.ws.PortMapRegistery;
import org.openflexo.foundation.wkf.ws.PortRegistery;
import org.openflexo.foundation.wkf.ws.ServiceMessageDefinition;
import org.openflexo.foundation.wkf.ws.ServiceOperation;
import org.openflexo.foundation.xml.FlexoProcessBuilder;

/**
 * Edge linking an OUT PortMap a a WS Edge to an OUT port
 * 
 * @author sguerin
 * 
 */
public final class BackwardWSEdge extends ExternalMessageEdge<FlexoPortMap, FlexoPort> {

	private static final Logger logger = Logger.getLogger(BackwardWSEdge.class.getPackage().getName());

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Constructor used during deserialization
	 */
	public BackwardWSEdge(FlexoProcessBuilder builder) {
		this(builder.process);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public BackwardWSEdge(FlexoProcess process) {
		super(process);
	}

	/**
	 * Constructor with start port, next precondition and process
	 */
	public BackwardWSEdge(FlexoPortMap startPortMap, FlexoPort outPort, FlexoProcess process) throws InvalidEdgeException {
		this(process);
		if (outPort.getProcess() == process && startPortMap.getProcess() == process) {
			setStartNode(startPortMap);
			setEndNode(outPort);
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Inconsistent data while building BackwardWSEdge !");
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
	public BackwardWSEdge(FlexoPortMap startPortMap, FlexoPort outPort) throws InvalidEdgeException {
		this(startPortMap, outPort, startPortMap.getProcess());
	}

	@Override
	public String getInspectorName() {
		return Inspectors.WKF.BACKWARD_WS_EDGE_INSPECTOR;
	}

	@Override
	public PortMapRegistery getPortMapRegistery() {
		if (getStartNode() != null) {
			return getStartNode().getPortMapRegistery();
		}
		return null;
	}

	public PortRegistery getEndPortRegistery() {
		if (getEndNode() != null) {
			return getEndNode().getPortRegistery();
		}
		return null;
	}

	// ==========================================================================
	// ============================= Validation
	// =================================
	// ==========================================================================

	@Override
	public boolean isEdgeValid() {
		// Such edges are valid if and only if they link an OUT portmap
		// of a WebService to an OUT port

		if (getStartNode() == null || !getStartNode().isOutputPort() || getEndNode() == null || !getEndNode().isOutPort()
				|| getPortMapRegistery() == null || getPortMapRegistery().getSubProcessNode() == null) {
			return false;
		}

		return getStartNode().getProcess() == getEndNode().getProcess()
				&& getStartNode().getSubProcessNode().getSubProcess().getIsWebService();
	}

	public static class BackwardWSEdgeEdgeMustBeValid extends ValidationRule<BackwardWSEdgeEdgeMustBeValid, BackwardWSEdge> {
		public BackwardWSEdgeEdgeMustBeValid() {
			super(BackwardWSEdge.class, "backward_ws_edge_must_be_valid");
		}

		@Override
		public ValidationIssue<BackwardWSEdgeEdgeMustBeValid, BackwardWSEdge> applyValidation(BackwardWSEdge edge) {
			if (!edge.isEdgeValid()) {
				ValidationError<BackwardWSEdgeEdgeMustBeValid, BackwardWSEdge> error = new ValidationError<BackwardWSEdgeEdgeMustBeValid, BackwardWSEdge>(
						this, edge, "backward_ws_edge_is_not_valid");
				error.addToFixProposals(new DeletionFixProposal<BackwardWSEdgeEdgeMustBeValid, BackwardWSEdge>("delete_this_message_edge"));
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
		return "backward_ws_edge";
	}

	@Override
	public ServiceOperation getServiceOperation() {
		if (getStartNode() != null) {
			return getStartNode().getOperation();
		}
		return null;
	}

	@Override
	public ServiceMessageDefinition getInputMessageDefinition() {
		if (getServiceOperation() != null && getServiceOperation().getOutputMessageDefinition() != null) {
			return getServiceOperation().getOutputMessageDefinition();
		} else {
			if (!isDeserializing()) {
				logger.warning("Inconsistant data found in BackwardWSEdge " + getServiceOperation());
			}
			return null;
		}
	}

	@Override
	public MessageDefinition getOutputMessageDefinition() {
		if (getFlexoPort() != null && getFlexoPort() instanceof OutputPort) {
			return ((OutputPort) getFlexoPort()).getOutputMessageDefinition();
		} else {
			if (!isDeserializing()) {
				logger.warning("Inconsistant data found in BackwardWSEdge");
			}
			return null;
		}
	}

	@Override
	public Class<FlexoPortMap> getStartNodeClass() {
		return FlexoPortMap.class;
	}

	@Override
	public Class<FlexoPort> getEndNodeClass() {
		return FlexoPort.class;
	}

}
