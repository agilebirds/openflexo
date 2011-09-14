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
import org.openflexo.foundation.wkf.ws.FlexoPortMap;
import org.openflexo.foundation.wkf.ws.NewPort;
import org.openflexo.foundation.wkf.ws.PortMapRegistery;
import org.openflexo.foundation.wkf.ws.ServiceMessageDefinition;
import org.openflexo.foundation.wkf.ws.ServiceOperation;
import org.openflexo.foundation.xml.FlexoProcessBuilder;


public final class ExternalMessageInEdge extends ExternalMessageEdge<PetriGraphNode, FlexoPortMap> implements PortMapEntry {

	  private static final Logger logger = Logger.getLogger(ExternalMessageInEdge.class.getPackage().getName());

	    // ==========================================================================
	    // ============================= Constructor
	    // ================================
	    // ==========================================================================

	    /**
	     * Constructor used during deserialization
	     */
	    public ExternalMessageInEdge(FlexoProcessBuilder builder)
	    {
	        this(builder.process);
	        initializeDeserialization(builder);
	    }

	    /**
	     * Default constructor
	     */
	    public ExternalMessageInEdge(FlexoProcess process)
	    {
	        super(process);
	    }

	    /**
	     * Constructor with start node, next port and process
	     */
	    public ExternalMessageInEdge(PetriGraphNode startNode, FlexoPortMap nextPortMap, FlexoProcess process) throws InvalidEdgeException
	    {
	        this(process);
	        if (nextPortMap.getProcess() == process && startNode.getNode().getProcess() == process) {
	        	setStartNode(startNode);
	            setEndNode(nextPortMap);
	        } else {
	            if (logger.isLoggable(Level.WARNING))
	                logger.warning("Inconsistent data while building ExternalMessageInEdge !");
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
	    public ExternalMessageInEdge(PetriGraphNode startNode, FlexoPortMap nextPortMap) throws InvalidEdgeException
	    {
	        this(startNode, nextPortMap, startNode.getNode().getProcess());
	    }

	    @Override
		public String getInspectorName()
	    {
	    	if (getFlexoPort() != null && getFlexoPort() instanceof NewPort) {
	    	       return Inspectors.WKF.EXTERNAL_MESSAGE_IN_EDGE_FOR_NEW_PORT_INSPECTOR;
	    	}
	        return Inspectors.WKF.EXTERNAL_MESSAGE_IN_EDGE_INSPECTOR;
	    }

	    @Override
		public PortMapRegistery getPortMapRegistery()
	    {
	        if (getEndNode() != null) {
	            return getEndNode().getPortMapRegistery();
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
	        // (of any level) but located in the same ACTIVITY petri graph where the
	        // SubProcessNode
	        // is located

	        if ((getStartNode() == null) || (getEndNode() == null) || !(getEndNode().isInputPort()) || (getPortMapRegistery() == null) || (getPortMapRegistery().getSubProcessNode() == null))
	            return false;

	        return getPortMapRegistery().getProcess()==getStartNode().getProcess();
	    }

	    public static class ExternalMessageInEdgeMustBeValid extends ValidationRule<ExternalMessageInEdgeMustBeValid, ExternalMessageInEdge>
	    {
	        public ExternalMessageInEdgeMustBeValid()
	        {
	            super(ExternalMessageInEdge.class, "external_message_in_edge_must_be_valid");
	        }

	        @Override
			public ValidationIssue<ExternalMessageInEdgeMustBeValid, ExternalMessageInEdge> applyValidation(ExternalMessageInEdge edge)
	        {
	            if (!edge.isEdgeValid()) {
	                ValidationError<ExternalMessageInEdgeMustBeValid, ExternalMessageInEdge> error = new ValidationError<ExternalMessageInEdgeMustBeValid, ExternalMessageInEdge>(this, edge, "external_message_in_edge_is_not_valid");
	                error.addToFixProposals(new DeletionFixProposal<ExternalMessageInEdgeMustBeValid, ExternalMessageInEdge>("delete_this_message_edge"));
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
	        return "external_message_in_edge";
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
		public ServiceMessageDefinition getInputMessageDefinition()
	    {
	    	if (getServiceOperation()!=null && isInputPort()) {
	    		return getServiceOperation().getInputMessageDefinition();
	    	}
	    	return null;
	    }

	    @Override
		public ServiceMessageDefinition getOutputMessageDefinition()
	    {
	     	return null;
	    }

	    @Override
	    public Class<PetriGraphNode> getStartNodeClass() {
	    	return PetriGraphNode.class;
	    }
	    
	    @Override
	    public final Class<FlexoPortMap> getEndNodeClass() {
	    	return FlexoPortMap.class;
	    }
}
