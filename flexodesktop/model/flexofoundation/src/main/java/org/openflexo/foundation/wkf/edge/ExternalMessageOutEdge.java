
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
import org.openflexo.foundation.wkf.FlexoPetriGraph;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.node.FlexoPreCondition;
import org.openflexo.foundation.wkf.node.Node;
import org.openflexo.foundation.wkf.ws.FlexoPortMap;
import org.openflexo.foundation.wkf.ws.PortMapRegistery;
import org.openflexo.foundation.wkf.ws.ServiceMessageDefinition;
import org.openflexo.foundation.wkf.ws.ServiceOperation;
import org.openflexo.foundation.xml.FlexoProcessBuilder;


/**
 * Edge linking a PortMap to a Node
 * 
 * @author sguerin
 * 
 */
public final class ExternalMessageOutEdge extends ExternalMessageEdge<FlexoPortMap, Node> implements PortMapExit
{

    private static final Logger logger = Logger.getLogger(ExternalMessageOutEdge.class.getPackage().getName());

    // ==========================================================================
    // ============================= Constructor
    // ================================
    // ==========================================================================

    /**
     * Constructor used during deserialization
     */
    public ExternalMessageOutEdge(FlexoProcessBuilder builder)
    {
        this(builder.process);
        initializeDeserialization(builder);
    }

    /**
     * Default constructor
     */
    public ExternalMessageOutEdge(FlexoProcess process)
    {
        super(process);
    }

    /**
     * Constructor with start port, next precondition and process
     */
    public ExternalMessageOutEdge(FlexoPortMap startPortMap, Node endPre, FlexoProcess process) throws InvalidEdgeException
    {
        this(process);
        if (endPre.getNode().getProcess() == process && startPortMap.getProcess() == process) {
        	setStartNode(startPortMap);
            setEndNode(endPre);
        } else {
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Inconsistent data while building ExternalMessageOutEdge !");
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
    public ExternalMessageOutEdge(FlexoPortMap startPortMap, Node endPre) throws InvalidEdgeException
    {
        this(startPortMap, endPre, startPortMap.getProcess());
    }

    @Override
	public String getInspectorName()
    {
        return Inspectors.WKF.EXTERNAL_MESSAGE_OUT_EDGE_INSPECTOR;
    }

    @Override
	public PortMapRegistery getPortMapRegistery()
    {
        if (getStartNode() != null) {
            return getStartNode().getPortMapRegistery();
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
        // Such edges are valid if and only if they link to a FlexoPortMap a
        // FlexoNode
        // which is located in the same ACTIVITY petri graph where the
        // SubProcessNode
        // is located

        if (getStartNode() == null || getEndNode() == null || !getStartNode().isOutputPort())
            return false;

        if (getEndNode() instanceof FlexoPreCondition) {
        	FlexoPetriGraph pg = getPortMapRegistery().getSubProcessNode().getParentPetriGraph();
        	if (getEndNode().getNode() != null) {
        		return (((FlexoPreCondition)getEndNode()).getAttachedNode().getActivityPetriGraph() == pg);
        	}
        	return false;
        }
        return true;
    }

    public static class ExternalMessageOutEdgeMustBeValid extends ValidationRule<ExternalMessageOutEdgeMustBeValid,ExternalMessageOutEdge>
    {
        public ExternalMessageOutEdgeMustBeValid()
        {
            super(ExternalMessageOutEdge.class, "external_message_out_edge_must_be_valid");
        }

        @Override
		public ValidationIssue<ExternalMessageOutEdgeMustBeValid,ExternalMessageOutEdge> applyValidation(ExternalMessageOutEdge edge)
        {
            if (!edge.isEdgeValid()) {
                ValidationError<ExternalMessageOutEdgeMustBeValid,ExternalMessageOutEdge> error = new ValidationError<ExternalMessageOutEdgeMustBeValid,ExternalMessageOutEdge>(this, edge, "external_message_out_edge_is_not_valid");
                error.addToFixProposals(new DeletionFixProposal<ExternalMessageOutEdgeMustBeValid,ExternalMessageOutEdge>("delete_this_message_edge"));
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
        return "external_message_out_edge";
    }
    
    @Override
	public ServiceOperation getServiceOperation()
    {
    	if (getStartNode() != null) {
    		return getStartNode().getOperation();
    	}
    	return null;
    }

    @Override
	public ServiceMessageDefinition getInputMessageDefinition()
    {
     	return null;
    }

    @Override
	public ServiceMessageDefinition getOutputMessageDefinition()
    {
    	if (getServiceOperation()!=null && isOutputPort()) {
    		return getServiceOperation().getOutputMessageDefinition();
    	}
    	return null;
    }

    @Override
    public Class<FlexoPortMap> getStartNodeClass() {
    	return FlexoPortMap.class;
    }
    
    @Override
    public Class<Node> getEndNodeClass() {
    	return Node.class;
    }
}
