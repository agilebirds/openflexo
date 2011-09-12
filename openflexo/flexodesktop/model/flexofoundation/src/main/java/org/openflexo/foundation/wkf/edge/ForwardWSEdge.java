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
import org.openflexo.foundation.wkf.ws.AbstractInPort;
import org.openflexo.foundation.wkf.ws.FlexoPortMap;
import org.openflexo.foundation.wkf.ws.MessageDefinition;
import org.openflexo.foundation.wkf.ws.PortMapRegistery;
import org.openflexo.foundation.wkf.ws.PortRegistery;
import org.openflexo.foundation.wkf.ws.ServiceMessageDefinition;
import org.openflexo.foundation.wkf.ws.ServiceOperation;
import org.openflexo.foundation.xml.FlexoProcessBuilder;


/**
 * Edge linking an IN port to a WS IN portmap
 * 
 * @author sguerin
 * 
 */
public final class ForwardWSEdge extends ExternalMessageEdge<AbstractInPort,FlexoPortMap> implements PortExit, PortMapEntry
{

    private static final Logger logger = Logger.getLogger(ForwardWSEdge.class.getPackage().getName());

    // ==========================================================================
    // ============================= Constructor
    // ================================
    // ==========================================================================

    /**
     * Constructor used during deserialization
     */
    public ForwardWSEdge(FlexoProcessBuilder builder)
    {
        this(builder.process);
        initializeDeserialization(builder);
    }

    /**
     * Default constructor
     */
    public ForwardWSEdge(FlexoProcess process)
    {
        super(process);
    }

    /**
	 * Constructor with start node, next port and process
	 */
	public ForwardWSEdge(AbstractInPort startPort, FlexoPortMap nextPortMap, FlexoProcess process) throws InvalidEdgeException {
		this(process);
		if (nextPortMap.getProcess() == process && startPort.getProcess() == process) {
			setStartNode(startPort);
			setEndNode(nextPortMap);
		} else {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("Inconsistent data while building ForwardWSEdge !");
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
    public ForwardWSEdge(AbstractInPort startPort, FlexoPortMap nextPortMap) throws InvalidEdgeException
    {
        this(startPort, nextPortMap, startPort.getProcess());
    }

    @Override
	public String getInspectorName()
    {
        return Inspectors.WKF.FORWARD_WS_EDGE_INSPECTOR;
    }

    @Override
	public PortMapRegistery getPortMapRegistery()
    {
        if (getEndNode() != null) {
            return getEndNode().getPortMapRegistery();
        }
        return null;
    }

    public PortRegistery getPortRegistery()
    {
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
	public boolean isEdgeValid()
    {
        // Such edges are valid if and only if they link to a FlexoPortMap a IN
        // port
        // of a WebService

        if (getStartNode() == null || getEndNode() == null || !getEndNode().isInputPort() || getPortMapRegistery() == null
                || getPortMapRegistery().getSubProcessNode() == null)
            return false;

        return (getStartNode().getProcess() == getEndNode().getProcess() && (getEndNode().getSubProcessNode().getSubProcess().getIsWebService()));
    }

    public static class ForwardWSEdgeMustBeValid extends ValidationRule<ForwardWSEdgeMustBeValid,ForwardWSEdge>
    {
        public ForwardWSEdgeMustBeValid()
        {
            super(ForwardWSEdge.class, "forward_ws_edge_must_be_valid");
        }

        @Override
		public ValidationIssue<ForwardWSEdgeMustBeValid,ForwardWSEdge> applyValidation(ForwardWSEdge edge)
        {
            if (!edge.isEdgeValid()) {
                ValidationError<ForwardWSEdgeMustBeValid,ForwardWSEdge> error = new ValidationError<ForwardWSEdgeMustBeValid,ForwardWSEdge>(this, edge, "forward_ws_edge_is_not_valid");
                error.addToFixProposals(new DeletionFixProposal<ForwardWSEdgeMustBeValid,ForwardWSEdge>("delete_this_message_edge"));
                return error;
            }
            return null;
        }

    }

    /**
     * Overrides getClassNameKey
     * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
     */
    @Override
	public String getClassNameKey()
    {
        return "forward_ws_edge";
    }
    
    @Override
	public ServiceOperation getServiceOperation()
    {
    	if (getEndNode() != null) {
    		return getEndNode().getOperation();
    	}
    	return null;
    }

    @Override
	public MessageDefinition getInputMessageDefinition()
    {
    	if (getStartNode() != null) {
    		return getStartNode().getInputMessageDefinition();
    	} else {
    		logger.warning("Inconsistant data found in ForwardWSEdge");
    		return null;
    	}
     }

    @Override
	public ServiceMessageDefinition getOutputMessageDefinition()
    {
       	if (getServiceOperation() != null && getServiceOperation().getInputMessageDefinition() != null) {
       		return getServiceOperation().getInputMessageDefinition();
    	}
    	else {
    		if (!isDeserializing())
    			logger.warning("Inconsistant data found in ForwardWSEdge "+getServiceOperation());
    		return null;
    	}
    }

	@Override
	public Class<FlexoPortMap> getEndNodeClass() {
		return FlexoPortMap.class;
	}

	@Override
	public Class<AbstractInPort> getStartNodeClass() {
		return AbstractInPort.class;
	}

}
