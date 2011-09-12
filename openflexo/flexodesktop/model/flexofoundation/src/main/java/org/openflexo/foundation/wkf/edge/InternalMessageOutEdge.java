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
import org.openflexo.foundation.wkf.node.PetriGraphNode;
import org.openflexo.foundation.wkf.ws.FlexoPort;
import org.openflexo.foundation.wkf.ws.MessageDefinition;
import org.openflexo.foundation.wkf.ws.OutputPort;
import org.openflexo.foundation.wkf.ws.PortRegistery;
import org.openflexo.foundation.xml.FlexoProcessBuilder;


/**
 * Edge linking a FlexoNode to a OutPort
 * 
 * @author sguerin
 * 
 */
public final class InternalMessageOutEdge extends InternalMessageEdge<PetriGraphNode, FlexoPort> implements PortEntry
{

    private static final Logger logger = Logger.getLogger(InternalMessageOutEdge.class.getPackage().getName());

    // ==========================================================================
    // ============================= Constructor
    // ================================
    // ==========================================================================

    /**
     * Constructor used during deserialization
     */
    public InternalMessageOutEdge(FlexoProcessBuilder builder)
    {
        this(builder.process);
        initializeDeserialization(builder);
    }

    /**
     * Default constructor
     */
    public InternalMessageOutEdge(FlexoProcess process)
    {
        super(process);
    }

    /**
     * Constructor with start node, next port and process
     */
    public InternalMessageOutEdge(PetriGraphNode startNode, FlexoPort nextPort, FlexoProcess process) throws InvalidEdgeException
    {
        this(process);
        if (nextPort.getProcess() == process && startNode.getProcess() == process && nextPort.isOutPort()) {
        	setStartNode(startNode);
            setEndNode(nextPort);
        } else {
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Inconsistent data while building InternalMessageOutEdge !");
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
    public InternalMessageOutEdge(PetriGraphNode startNode, FlexoPort nextPort) throws InvalidEdgeException
    {
        this(startNode, nextPort, startNode.getProcess());
    }

    @Override
	public String getInspectorName()
    {
        return Inspectors.WKF.INTERNAL_MESSAGE_OUT_EDGE_INSPECTOR;
    }

    @Override
	public PortRegistery getPortRegistery()
    {
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
	public boolean isEdgeValid()
    {
        // Such edges are valid if and only if they link to a OUT port a
        // FlexoNode of any level
        // located in the petri graph of the sub-process where the port
        // registery
        // is registered

        if (getStartNode() == null || getEndNode() == null || !getEndNode().isOutPort())
            return false;

        return (getStartNode().getProcess() == getPortRegistery().getProcess());
    }

    public static class InternalMessageOutEdgeMustBeValid extends ValidationRule<InternalMessageOutEdgeMustBeValid,InternalMessageOutEdge>
    {
        public InternalMessageOutEdgeMustBeValid()
        {
            super(InternalMessageOutEdge.class, "internal_message_out_edge_must_be_valid");
        }

        @Override
		public ValidationIssue<InternalMessageOutEdgeMustBeValid,InternalMessageOutEdge> applyValidation(InternalMessageOutEdge edge)
        {
            if (!edge.isEdgeValid()) {
                ValidationError<InternalMessageOutEdgeMustBeValid,InternalMessageOutEdge> error = new ValidationError<InternalMessageOutEdgeMustBeValid,InternalMessageOutEdge>(this, edge, "internal_message_out_edge_is_not_valid");
                error.addToFixProposals(new DeletionFixProposal<InternalMessageOutEdgeMustBeValid,InternalMessageOutEdge>("delete_this_message_edge"));
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
        return "internal_message_out_edge";
    }
    
    @Override
	public FlexoPort getFlexoPort()
    {
    	return getEndNode();
    }

    @Override
	public MessageDefinition getInputMessageDefinition()
    {
    	return null;
    }

    @Override
	public MessageDefinition getOutputMessageDefinition()
    {
    	if (getFlexoPort()!=null && isOutputPort()) {
    		return ((OutputPort)getFlexoPort()).getOutputMessageDefinition();
    	}
    	return null;
    }
    
    @Override
    public Class<PetriGraphNode> getStartNodeClass() {
    	return PetriGraphNode.class;
    }

    @Override
    public Class<FlexoPort> getEndNodeClass() {
    	return FlexoPort.class;
    }
}
