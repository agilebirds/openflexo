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

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.bindings.Bindable;
import org.openflexo.foundation.bindings.BindingModel;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.utils.FlexoModelObjectReference.ReferenceOwner;
import org.openflexo.foundation.validation.DeletionFixProposal;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.validation.ValidationWarning;
import org.openflexo.foundation.wkf.ExecutableWorkflowElement;
import org.openflexo.foundation.wkf.FlexoLevel;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.dm.RoleChanged;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ProgrammingLanguage;

/**
 * Represents an Operator Node (AND or OR)
 * 
 * @author sguerin
 */
public abstract class OperatorNode extends PetriGraphNode implements Bindable, ExecutableWorkflowElement, ReferenceOwner {

	private static final String RESIZABLE = "resizable";

	static final Logger logger = Logger.getLogger(OperatorNode.class.getPackage().getName());

	private FlexoModelObjectReference<Role> roleReference;

	// ================================================================
	// ====================== Constructor =============================
	// ================================================================

	/**
	 * Constructor used during deserialization
	 */
	public OperatorNode(FlexoProcessBuilder builder) {
		this(builder.process);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public OperatorNode(FlexoProcess process) {
		super(process);
	}

	@Override
	public abstract String getInspectorName();

	@Override
	public OperatorNode getNode() {
		return this;
	}

	@Override
	public FlexoLevel getLevel() {
		if (getParentPetriGraph() != null) {
			return getParentPetriGraph().getLevel();
		}
		return null;
	}

	public boolean isSendingTokens() {
		return hasOutgoingPostConditions();
	}

	@Override
	public boolean isOperatorNode() {
		return true;
	}

	public boolean isTestNode() {
		return this instanceof IFOperator;
	}

	public boolean isAndNode() {
		return this instanceof ANDOperator;
	}

	public boolean isOrNode() {
		return this instanceof OROperator;
	}

	public boolean isSwitchNode() {
		return this instanceof SWITCHOperator;
	}

	public boolean isLoopNode() {
		return this instanceof LOOPOperator;
	}

	public final boolean isExclusiveGateway() {
		return this instanceof ExclusiveEventBasedOperator;
	}

	public final boolean isInclusiveGateway() {
		return this instanceof InclusiveOperator;
	}

	@Override
	public boolean mayHaveIncomingPostConditions() {
		return true;
	}

	@Override
	public boolean mayHaveOutgoingPostConditions() {
		return true;
	}

	public static String getResizableKeyForContext(String context) {
		return RESIZABLE + '_' + context;
	}

	public boolean isResizable(String context) {
		return _booleanGraphicalPropertyForKey(getResizableKeyForContext(context), false);
	}

	public void setIsResizable(boolean resizable, String context) {
		if (requireChange(isResizable(context), resizable)) {
			_setGraphicalPropertyForKey(resizable, getResizableKeyForContext(context));
			setChanged();
			notifyObservers(new WKFAttributeDataModification(getResizableKeyForContext(context), !resizable, resizable));
		}
	}

	@Override
	public final boolean delete() {
		super.delete();
		deleteObservers();
		return true;
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

	// Used when serializing
	public FlexoModelObjectReference<Role> getRoleReference() {
		return roleReference;
	}

	// Used when deserializing
	public void setRoleReference(FlexoModelObjectReference<Role> aRoleReference) {
		this.roleReference = aRoleReference;
	}

	public Role getRole() {
		if (roleReference != null) {
			Role object = roleReference.getObject();
			if (object != null) {
				return object;
			} else {
				return getWorkflow().getCachedRole(roleReference);
			}
		} else {
			return null;
		}
	}

	public void setRole(Role aRole) {
		if (aRole != null && aRole.isCache()) {
			aRole = aRole.getUncachedObject();
			if (aRole == null) {
				return;
			}
		}
		Role oldRole = getRole();
		if (oldRole != aRole) {
			if (roleReference != null) {
				roleReference.delete(false);
				roleReference = null;
			}
			if (aRole != null) {
				roleReference = new FlexoModelObjectReference<Role>(aRole);
				roleReference.setOwner(this);
			}
			setChanged();
			notifyObservers(new RoleChanged(oldRole, aRole));
		}
	}

	@Override
	public void notifyObjectLoaded(FlexoModelObjectReference<?> reference) {
		// TODO Auto-generated method stub

	}

	@Override
	public void objectCantBeFound(FlexoModelObjectReference<?> reference) {
		setRole(null);
	}

	@Override
	public void objectDeleted(FlexoModelObjectReference<?> reference) {
		setRole(null);
	}

	@Override
	public void objectSerializationIdChanged(FlexoModelObjectReference<?> reference) {
		setChanged();
	}

	public static class OperatorNodeShouldSendTokens extends ValidationRule<OperatorNodeShouldSendTokens, OperatorNode> {
		public OperatorNodeShouldSendTokens() {
			super(OperatorNode.class, "operator_node_should_send_tokens");
		}

		@Override
		public ValidationIssue<OperatorNodeShouldSendTokens, OperatorNode> applyValidation(OperatorNode node) {
			if (!node.isSendingTokens()) {
				ValidationWarning<OperatorNodeShouldSendTokens, OperatorNode> warning = new ValidationWarning<OperatorNodeShouldSendTokens, OperatorNode>(
						this, node, "operator_node_($object.name)_never_send_any_tokens");
				warning.addToFixProposals(new DeletionFixProposal<OperatorNodeShouldSendTokens, OperatorNode>("delete_this_node"));
				return warning;
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
		return "operator_node";
	}

	@Override
	public BindingModel getBindingModel() {
		if (getProcess() != null) {
			return getProcess().getBindingModel();
		}
		return null;
	}

	// =========================================================
	// ============= Control graph management ==================
	// =========================================================

	private static ControlGraphFactory<OperatorNode> _executionComputingFactory;

	public static void setExecutionComputingFactory(ControlGraphFactory<OperatorNode> factory) {
		_executionComputingFactory = factory;
	}

	public WorkflowControlGraph<OperatorNode> getExecution() {
		if (_executionComputingFactory != null) {
			return _executionComputingFactory.getControlGraph(this);
		}
		return null;
	}

	@Override
	public void setProgrammingLanguageForControlGraphComputation(ProgrammingLanguage language) {
		if (getExecution() != null) {
			getExecution().setProgrammingLanguage(language);
		}
	}

	@Override
	public void setInterproceduralForControlGraphComputation(boolean interprocedural) {
		if (getExecution() != null) {
			getExecution().setInterprocedural(interprocedural);
		}
	}

	@Override
	public String getExecutableElementName() {
		return FlexoLocalization.localizedForKeyWithParams("operator_($0)", getName());
	}

}
