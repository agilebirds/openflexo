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
package org.openflexo.foundation.wkf.ws;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.AttributeDataModification;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.bindings.Bindable;
import org.openflexo.foundation.bindings.BindingModel;
import org.openflexo.foundation.utils.FlexoIndexManager;
import org.openflexo.foundation.utils.Sortable;
import org.openflexo.foundation.validation.DeletionFixProposal;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.wkf.DuplicateWKFObjectException;
import org.openflexo.foundation.wkf.FlexoLevel;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.action.ShowHidePortmap;
import org.openflexo.foundation.wkf.action.WKFDelete;
import org.openflexo.foundation.wkf.dm.ChildrenOrderChanged;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.edge.MessageEdge;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.foundation.xml.FlexoProcessBuilder;


/**
 * Abstract representation of connexion between an operation associated to a ServiceInterface (for PortRegistry, a DefaultServiceInterface)
 * associated to a FlexoProcess and the SubProcessNode and thus, related petri graph where the SubProcessNode is embedded: this make the
 * connexion between the abstract representation of a FlexoProcess, and the instanciation of this process inside a SubProcessNode.
 * 
 * @author sguerin
 * 
 */
public class FlexoPortMap extends AbstractNode implements Bindable, FlexoObserver, Sortable {

	private static final Logger logger = Logger.getLogger(FlexoPortMap.class.getPackage().getName());

	// ==========================================================================
	// ============================= Variables
	// ==================================
	// ==========================================================================

	private ServiceOperation _operation;

	// equal to operation name.
	private String _portName;

	private PortMapRegistery _portMapRegistery;
	
	private int index = -1;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Constructor used during deserialization
	 */
	public FlexoPortMap(FlexoProcessBuilder builder) {
		this(builder.process);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public FlexoPortMap(FlexoProcess process) {
		super(process);
	}

	/**
	 * Constructor with port and process NB: process of the port is NOT the same that aProcess !!!
	 */
	public FlexoPortMap(ServiceOperation op, FlexoProcess aProcess) {
		this(aProcess);
		setOperation(op);
	}

	@Override
	public String getDefaultName() {
		return getOperation().getName();
	}

	public FlexoProcess getRelatedSubProcess() {
		if ((getPortMapRegistery() != null) && (getPortMapRegistery().getSubProcessNode() != null)) {
			return getPortMapRegistery().getSubProcessNode().getSubProcess();
		}
		return null;
	}

	private ServiceInterface getServiceInterface() {
		if (getRelatedSubProcess() != null) {
			return getPortMapRegistery().getServiceInterface();
		}
		return null;
	}

	@Override
	public String getName() {
		if (getOperation() != null) {
			return getOperation().getName();
		}
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Could not find port !");
		}
		return null;
	}

	@Override
	public void setName(String aName) {
		try {
			getOperation().setName(aName);
		} catch (DuplicateWKFObjectException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception in setName FlexoPortMap");
				e.printStackTrace();
			}
		}
	}

	public ServiceOperation getOperation() {
		if (logger.isLoggable(Level.FINEST)) {
			logger.finest("getOperation(): op=" + _operation + " name:" + _portName);
		}
		if ((_operation == null) && (_portName != null)) {
			lookupOperation();
		}
		return _operation;
	}

	public void setOperation(ServiceOperation anOp) {
		if (_operation != null) {
			_operation.deleteObserver(this);
		}
		_operation = anOp;
		if (_operation != null) {
			_operation.addObserver(this);
		}
	}

	private void lookupOperation() {
		// the portName can store the operationName...
		if (logger.isLoggable(Level.FINEST)) {
			logger.finest("lookupOperation:" + getServiceInterface());
		}
		if ((getServiceInterface() != null) && (_portName != null)) {
			_operation = getServiceInterface().operationWithName(_portName);

			if (_operation == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not find operation named: " + _portName);
				}
			} else {
				_operation.addObserver(this);

				for (FlexoPostCondition<AbstractNode,AbstractNode> entry : getIncomingPostConditions()) {
					if (entry instanceof MessageEdge) {
						((MessageEdge<AbstractNode, AbstractNode>) entry).lookupMessageDefinition();
					}
				}
				for (FlexoPostCondition<AbstractNode,AbstractNode> exit : getOutgoingPostConditions()) {
					if (exit instanceof MessageEdge) {
						((MessageEdge<AbstractNode, AbstractNode>) exit).lookupMessageDefinition();
					}
				}

			}
		}
	}

	/**
	 * 
	 * @return the operation name is the portMap is mapping a ServiceOperation !
	 */
	public String getPortName() {
		if (getOperation() != null) {
			return getOperation().getName();
		}
		return _portName;
	}

	public void setPortName(String name) {
		_portName = name;
		if ((_operation != null) && (!_portName.equals(_operation.getName()))) {
			lookupOperation();
		}
	}

	@Override
	public String getInspectorName() {
		return Inspectors.WKF.PORTMAP_INSPECTOR;

		/*
		 * if (getOperation().getPort() instanceof InPort) { return Inspectors.WKF.IN_PORTMAP_INSPECTOR; } else if (getOperation().getPort()
		 * instanceof OutPort) { return Inspectors.WKF.OUT_PORTMAP_INSPECTOR; } else if (getOperation().getPort() instanceof InOutPort) {
		 * return Inspectors.WKF.IN_OUT_PORTMAP_INSPECTOR; } else if (getOperation().getPort() instanceof NewPort) { return
		 * Inspectors.WKF.NEW_PORTMAP_INSPECTOR; } else if (getOperation().getPort() instanceof DeletePort) { return
		 * Inspectors.WKF.DELETE_PORTMAP_INSPECTOR; } return null;
		 */
	}

	@Override
	public AbstractNode getNode() {
		return this;
	}

	/**
	 * Return a Vector of all embedded WKFObjects
	 * 
	 * @return a Vector of WKFObject instances
	 */
	@Override
	public Vector<WKFObject> getAllEmbeddedWKFObjects() {
		Vector<WKFObject> returned = super.getAllEmbeddedWKFObjects();
		returned.addAll(getOutgoingPostConditions());
		returned.addAll(getIncomingPostConditions());
		return returned;
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

	@Override
	public FlexoLevel getLevel() {
		return FlexoLevel.ACTIVITY;
	}

	public SubProcessNode getSubProcessNode() {
		if (getPortMapRegistery() != null) {
			return getPortMapRegistery().getSubProcessNode();
		} else {
			return null;
		}
	}

	private boolean isRegisteredUnderSubProcessNode = false;

	public void registerUnderSubProcessNode() {
		if ((getSubProcessNode() != null) && (!isRegisteredUnderSubProcessNode)) {
			getSubProcessNode().addObserver(this);
			isRegisteredUnderSubProcessNode = true;
		}
	}

	public PortMapRegistery getPortMapRegistery() {
		return _portMapRegistery;
	}

	public void setPortMapRegistery(PortMapRegistery portMapRegistery) {
		_portMapRegistery = portMapRegistery;
	}

	public boolean isInputPort() {
		if (getOperation() != null) {
			return getOperation().isInputOperation();
		}
		return false;
	}

	public boolean isOutputPort() {
		if (getOperation() != null) {
			return getOperation().isOutputOperation();
		}
		return false;
	}

	public boolean isNewPort() {
		return getOperation().getPort() instanceof NewPort;
	}

	/**
	 * Return the father which is the FlexoProcess
	 * 
	 * @return
	 */
	public FlexoProcess getFather() {
		return getProcess();
	}

	@Override
	public String getFullyQualifiedName() {
		if (getSubProcessNode() != null) {
			return getSubProcessNode().getFullyQualifiedName() + ".PORTMAP." + formattedString(getName());
		} else {
			return "NULL." + formattedString(getNodeName());
		}
	}

	@Override
	public BindingModel getBindingModel() {
		if (getProcess() != null) {
			return getProcess().getBindingModel();
		} else {
			return null;
		}
	}

	@Override
	public boolean mayHaveIncomingPostConditions() {
		if (getOperation()!=null) {
			return isInputPort();
		}
		return true;
	}
	
	@Override
	public boolean mayHaveOutgoingPostConditions() {
		if (getOperation()!=null) {
			return isOutputPort();
		}
		return true;
	}

	// ==========================================================================
	// ================================= Delete ===============================
	// ==========================================================================

	@Override
	public final void delete() {
		if (_operation != null) {
			_operation.deleteObserver(this);
		}
		getPortMapRegistery().removeFromPortMaps(this);
		super.delete();
		deleteObservers();
	}

	@Override
	public boolean isContainedIn(WKFObject obj) {
		if (obj instanceof PortMapRegistery) {
			return ((PortMapRegistery) obj).getPortMaps().contains(this);
		}
		return super.isContainedIn(obj);
	}

	/**
	 * Overrides getSpecificActionListForThatClass
	 * 
	 * @see org.openflexo.foundation.wkf.WKFObject#getSpecificActionListForThatClass()
	 */
	@Override
	protected Vector<FlexoActionType> getSpecificActionListForThatClass() {
		Vector<FlexoActionType> returned = super.getSpecificActionListForThatClass();
		returned.remove(WKFDelete.actionType);
		returned.add(ShowHidePortmap.actionType);
		return returned;
	}

	@Override
	public boolean getIsVisible() {
		return getIsVisible(true);
	}

	public boolean getIsHidden() {
		return !getIsVisible();
	}

	public void setIsHidden(boolean hidePortMap) {
		setIsVisible(!hidePortMap);
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (dataModification instanceof NameChanged) {
			setChanged();
			notifyObservers(dataModification);
		}
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "flexo_port_map";
	}

	public boolean getHasInputMessage() {
		return getInputMessageDefinition() != null;
	}

	public boolean getHasOutputMessage() {
		return getOutputMessageDefinition() != null;
	}

	public ServiceMessageDefinition getInputMessageDefinition() {
		if (isInputPort()) {
			return getOperation().getInputMessageDefinition();
		}
		return null;
	}

	public ServiceMessageDefinition getOutputMessageDefinition() {
		if (isOutputPort()) {
			return getOperation().getOutputMessageDefinition();
		}
		return null;
	}

	@Override
	public int getIndex()
	{
		if (isBeingCloned()) {
			return -1;
		}
		if ((index==-1) && (getCollection()!=null)) {
			index = getCollection().length;
			FlexoIndexManager.reIndexObjectOfArray(getCollection());
		}
		return index;
	}

	@Override
	public final void setIndex(int index)
	{
		if (isDeserializing() || isCreatedByCloning()) {
			setIndexValue(index);
			return;
		}
		FlexoIndexManager.switchIndexForKey(this.index,index,this);
		if (getIndex()!=index) {
			setChanged();
			AttributeDataModification dm = new AttributeDataModification("index",null,getIndex());
			dm.setReentrant(true);
			notifyObservers(dm);
		}
	}

	@Override
	public int getIndexValue()
	{
		return getIndex();
	}

	@Override
	public final void setIndexValue(int index) {
		if(index==this.index) {
			return;
		}
		int old = this.index;
		this.index = index;
		setChanged();
		notifyAttributeModification("index", old, index);
		if (!isDeserializing() && (getPortMapRegistery()!=null)) { 
			getPortMapRegistery().setChanged();
			getPortMapRegistery().notifyObservers(new ChildrenOrderChanged());
		}
	}

	/**
	 * Overrides getCollection
	 * 
	 * @see org.openflexo.foundation.utils.Sortable#getCollection()
	 */
	@Override
	public FlexoPortMap[] getCollection() {
		if (getPortMapRegistery() == null) {
			return null;
		}
		return getPortMapRegistery().getPortMaps().toArray(new FlexoPortMap[0]);
	}

	// ==========================================================================
	// ============================= Validation
	// =================================
	// ==========================================================================

	/**
	 * must refer to a port OR an operation
	 */
	public static class PortMapMustReferToAServiceOperation extends ValidationRule<PortMapMustReferToAServiceOperation, FlexoPortMap> {
		public PortMapMustReferToAServiceOperation() {
			super(FlexoPortMap.class, "portmap_must_be_linked_to_a_service_operation");
		}

		@Override
		public ValidationIssue<PortMapMustReferToAServiceOperation, FlexoPortMap> applyValidation(FlexoPortMap portMap) {
			if (portMap.getOperation() == null) {
				ValidationError<PortMapMustReferToAServiceOperation, FlexoPortMap> error = new ValidationError<PortMapMustReferToAServiceOperation, FlexoPortMap>(this, portMap, "portmap_is_not_linked_to_a_service_operation");
				error.addToFixProposals(new DeletionFixProposal<PortMapMustReferToAServiceOperation, FlexoPortMap>("delete_this_portmap"));
				return error;
			}
			return null;
		}

	}

}
