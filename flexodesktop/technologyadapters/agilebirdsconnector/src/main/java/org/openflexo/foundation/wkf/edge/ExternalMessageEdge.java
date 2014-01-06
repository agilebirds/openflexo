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
import java.util.logging.Logger;

import org.openflexo.foundation.bindings.AbstractBinding;
import org.openflexo.foundation.bindings.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.bindings.WKFBindingDefinition;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.validation.DeletionFixProposal;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.wkf.Constants;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.foundation.wkf.ws.FlexoPort;
import org.openflexo.foundation.wkf.ws.NewPort;
import org.openflexo.foundation.wkf.ws.PortMapRegistery;
import org.openflexo.foundation.wkf.ws.ServiceOperation;

/**
 * Abstract edge linking a FlexoNode and a FlexoPortMap, and used to make connection between a process and a sub-process
 * 
 * @author sguerin
 * 
 */
public abstract class ExternalMessageEdge<S extends AbstractNode, E extends AbstractNode> extends MessageEdge<S, E> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ExternalMessageEdge.class.getPackage().getName());

	public static final String ACCESSED_PROCESS_INSTANCE = "accessedProcessInstance";

	private AbstractBinding _accessedProcessInstance;

	public static final String PARENT_PROCESS_INSTANCE = "parentProcessInstance";

	private AbstractBinding _parentProcessInstance;

	public static final String RETURNED_PROCESS_INSTANCE = "returnedProcessInstance";

	private AbstractBinding _returnedProcessInstance;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Default constructor
	 */
	public ExternalMessageEdge(FlexoProcess process) {
		super(process);
	}

	public Stroke getStroke() {
		return Constants.DASHED_STROKE;

	}

	public abstract ServiceOperation getServiceOperation();

	public abstract PortMapRegistery getPortMapRegistery();

	@Override
	public FlexoPort getFlexoPort() {
		if (getServiceOperation() != null) {
			return getServiceOperation().getPort();
		}
		return null;
	}

	public FlexoProcess getRelatedSubProcess() {
		if (getFlexoPort() != null) {
			return getFlexoPort().getProcess();
		}
		return null;
	}

	@Override
	public boolean isInputPort() {
		if (getServiceOperation() != null) {
			return getServiceOperation().isInputOperation();
		}
		return false;
	}

	@Override
	public boolean isOutputPort() {
		if (getServiceOperation() != null) {
			return getServiceOperation().isOutputOperation();
		}
		return false;
	}

	// ====================================================
	// ===================== Bindings =====================
	// ====================================================

	public DMEntity getAbstractProcessInstanceEntity() {
		return getProject().getDataModel().getExecutionModelRepository().getProcessInstanceEntity();
	}

	public WKFBindingDefinition getAccessedProcessInstanceBindingDefinition() {
		if (getServiceOperation() != null && getRelatedSubProcess() != null) {
			boolean isNewPort = getServiceOperation().getPort() instanceof NewPort;
			// TODO ? what for operation ??
			return WKFBindingDefinition.get(this, ACCESSED_PROCESS_INSTANCE,
					DMType.makeResolvedDMType(getRelatedSubProcess().getProcessDMEntity()), BindingDefinitionType.GET_SET, !isNewPort);
		}
		return null;
	}

	public AbstractBinding getAccessedProcessInstance() {
		if (_accessedProcessInstance != null && _accessedProcessInstance.getBindingDefinition() == null) {
			_accessedProcessInstance.setBindingDefinition(getAccessedProcessInstanceBindingDefinition());
		}
		return _accessedProcessInstance;
	}

	public String getAccessedProcessInstancePath() {
		return _accessedProcessInstance.getCodeStringRepresentation();
	}

	public void setAccessedProcessInstance(AbstractBinding bindingValue) {
		AbstractBinding oldBindingValue = _accessedProcessInstance;
		_accessedProcessInstance = bindingValue;
		if (_accessedProcessInstance != null) {
			_accessedProcessInstance.setOwner(this);
			_accessedProcessInstance.setBindingDefinition(getAccessedProcessInstanceBindingDefinition());
		}
		setChanged();
		notifyObservers(new WKFAttributeDataModification(ACCESSED_PROCESS_INSTANCE, oldBindingValue, bindingValue));
	}

	public WKFBindingDefinition getParentProcessInstanceBindingDefinition() {
		if (getRelatedSubProcess() != null && getRelatedSubProcess().getParentProcess() != null) {
			boolean isNewPort = getFlexoPort() instanceof NewPort;
			return WKFBindingDefinition.get(this, PARENT_PROCESS_INSTANCE,
					DMType.makeResolvedDMType(getRelatedSubProcess().getParentProcess().getProcessDMEntity()), BindingDefinitionType.GET,
					isNewPort);
		}
		return null;
	}

	public AbstractBinding getParentProcessInstance() {
		if (_parentProcessInstance != null && _parentProcessInstance.getBindingDefinition() == null) {
			_parentProcessInstance.setBindingDefinition(getParentProcessInstanceBindingDefinition());
		}
		return _parentProcessInstance;
	}

	public String getParentProcessInstancePath() {
		return _parentProcessInstance.getCodeStringRepresentation();
	}

	public void setParentProcessInstance(AbstractBinding bindingValue) {
		AbstractBinding oldBindingValue = _parentProcessInstance;
		_parentProcessInstance = bindingValue;
		if (_parentProcessInstance != null) {
			_parentProcessInstance.setBindingDefinition(getParentProcessInstanceBindingDefinition());
			_parentProcessInstance.setOwner(this);
		}
		setChanged();
		notifyObservers(new WKFAttributeDataModification(PARENT_PROCESS_INSTANCE, oldBindingValue, bindingValue));
	}

	public WKFBindingDefinition getReturnedProcessInstanceBindingDefinition() {
		if (getRelatedSubProcess() != null) {
			boolean isNewPort = getFlexoPort() instanceof NewPort;
			// TODO ? what for operation ??
			return WKFBindingDefinition.get(this, RETURNED_PROCESS_INSTANCE,
					DMType.makeResolvedDMType(getRelatedSubProcess().getProcessDMEntity()), BindingDefinitionType.SET, isNewPort);
		}
		return null;
	}

	public AbstractBinding getReturnedProcessInstance() {
		if (_returnedProcessInstance != null && _returnedProcessInstance.getBindingDefinition() == null) {
			_returnedProcessInstance.setBindingDefinition(getReturnedProcessInstanceBindingDefinition());
		}
		return _returnedProcessInstance;
	}

	public String getReturnedProcessInstancePath() {
		return _returnedProcessInstance.getCodeStringRepresentation();
	}

	public void setReturnedProcessInstance(AbstractBinding bindingValue) {
		AbstractBinding oldBindingValue = _returnedProcessInstance;
		_returnedProcessInstance = bindingValue;
		if (_returnedProcessInstance != null) {
			_returnedProcessInstance.setOwner(this);
			_returnedProcessInstance.setBindingDefinition(getReturnedProcessInstanceBindingDefinition());
		}
		setChanged();
		notifyObservers(new WKFAttributeDataModification(RETURNED_PROCESS_INSTANCE, oldBindingValue, bindingValue));
	}

	// ==========================================================================
	// ============================= Validation
	// =================================
	// ==========================================================================

	public static class ExternalMessageEdgeMustReferToAValidPortMapRegistery extends
			ValidationRule<ExternalMessageEdgeMustReferToAValidPortMapRegistery, ExternalMessageEdge<AbstractNode, AbstractNode>> {
		public ExternalMessageEdgeMustReferToAValidPortMapRegistery() {
			super(ExternalMessageEdge.class, "external_edge_must_refer_to_a_valid_port_map_registery");
		}

		@Override
		public ValidationIssue<ExternalMessageEdgeMustReferToAValidPortMapRegistery, ExternalMessageEdge<AbstractNode, AbstractNode>> applyValidation(
				ExternalMessageEdge<AbstractNode, AbstractNode> edge) {
			if (edge.getPortMapRegistery() == null || edge.getPortMapRegistery().getServiceInterface() == null) {
				ValidationError<ExternalMessageEdgeMustReferToAValidPortMapRegistery, ExternalMessageEdge<AbstractNode, AbstractNode>> error = new ValidationError<ExternalMessageEdgeMustReferToAValidPortMapRegistery, ExternalMessageEdge<AbstractNode, AbstractNode>>(
						this, edge, "message_edge_refer_to_an_invalid_port_map_registery");
				error.addToFixProposals(new DeletionFixProposal<ExternalMessageEdgeMustReferToAValidPortMapRegistery, ExternalMessageEdge<AbstractNode, AbstractNode>>(
						"delete_this_message_edge"));
				return error;
			}
			return null;
		}

	}

}
