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

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.DeletableObject;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.bindings.BindingValue;
import org.openflexo.foundation.bindings.BindingVariable;
import org.openflexo.foundation.wkf.DuplicateWKFObjectException;
import org.openflexo.foundation.wkf.FlexoLevel;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.LevelledObject;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.action.AddServiceOperation;
import org.openflexo.foundation.wkf.dm.ServiceInterfaceRemoved;
import org.openflexo.foundation.wkf.dm.ServiceOperationInserted;
import org.openflexo.foundation.wkf.dm.ServiceOperationRemoved;
import org.openflexo.foundation.ws.DuplicateWSObjectException;
import org.openflexo.foundation.ws.WSPortType;
import org.openflexo.foundation.ws.WSService;
import org.openflexo.foundation.ws.action.WSDelete;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.inspector.InspectableObject;


/**
 * A ServiceInteface is attached to a FlexoProcess and contains all the service operations used in the context of WebServices.
 * 
 * @author Denis VANVYVE
 * 
 */
public class ServiceInterface extends WKFObject implements InspectableObject, LevelledObject, DeletableObject {

	private static final Logger logger = Logger.getLogger(ServiceInterface.class.getPackage().getName());

	// ==========================================================================
	// ============================= Variables
	// ==================================
	// ==========================================================================

	private String _name;

	private Vector<ServiceOperation> _operations;

	/**
	 * Constructor used during deserialization
	 */
	public ServiceInterface(FlexoProcessBuilder builder) {
		this(builder.process);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public ServiceInterface(FlexoProcess process) {
		super(process);
		_operations = new Vector();
	}

	public ServiceInterface(FlexoProcess process, String name) throws DuplicateWKFObjectException, DuplicateWSObjectException {
		this(process);
		setName(name);
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public void setName(String aName) throws DuplicateWSObjectException, DuplicateWKFObjectException {
		if (!isDeserializing())
			checkInterfaceName(getProcess(), aName, this);
		// TODO: check that the serviceInterface is not used !
		if (!isDeserializing() && isUsedInWebService() && _name != null && !_name.equals(aName)) {
			WSPortType pt = getProject().getFlexoWSLibrary().getWSPortTypeNamed(getName());
			while (pt != null) {
				pt.setName(aName);
				pt = null;
				pt = getProject().getFlexoWSLibrary().getWSPortTypeNamed(getName());
			}
		}
		_name = aName;
		setChanged();
		notifyObservers(new NameChanged(null,_name));
	}

	public static void checkInterfaceName(FlexoProcess process, String aName, ServiceInterface currentInt)
			throws DuplicateWKFObjectException {
		Vector allInterfaces = process.getServiceInterfaces();
		for (Enumeration e = allInterfaces.elements(); e.hasMoreElements();) {
			ServiceInterface p = (ServiceInterface) e.nextElement();
			if (p != currentInt && (p.getName().equals(aName))) {
				throw new DuplicateWKFObjectException(p, "a_service_interface_with_the_same_name_already_exist");
			}
		}
	}

	@Override
	public String getFullyQualifiedName() {
		return getProcess().getFullyQualifiedName() + "." + getName() + ".SERVICE_INTERFACE";
	}

	/**
	 * Return a Vector of all embedded WKFObjects
	 * 
	 * @return a Vector of WKFObject instances
	 */
	@Override
	public Vector<WKFObject> getAllEmbeddedWKFObjects() {
		Vector<WKFObject> returned = new Vector<WKFObject>();
		returned.add(this);
		returned.addAll(getOperations());
		return returned;
	}

	public ServiceOperation addServiceOperation(String name, FlexoPort relatedPort) throws DuplicateWKFObjectException {
		// will throw an exception
		try {
			ServiceOperation.checkOperationName(this, name, null);
		} catch (DuplicateWKFObjectException e) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("An operation with the same name (" + name + ") already exist");
			throw e;
		}
		ServiceOperation newOp = new ServiceOperation(this, name, relatedPort);
		addToOperations(newOp);
		return newOp;
	}

	public Vector<ServiceOperation> getOperations() {
		return _operations;
	}

	public void setOperations(Vector<ServiceOperation> operations) {
		_operations = operations;
	}

	public Enumeration<ServiceOperation> getSortedOperations() {
		Vector<ServiceOperation> returned = new Vector<ServiceOperation>(getOperations());
		Collections.sort(returned, new Comparator<ServiceOperation>() {
			@Override
			public int compare(ServiceOperation o1, ServiceOperation o2) {
				return o1.getPort().getIndex() - o2.getPort().getIndex();
			}
		});
		return returned.elements();
	}

	public void addToOperations(ServiceOperation anOp) {
		if (!_operations.contains(anOp)) {
			_operations.add(anOp);
			anOp.setServiceInterface(this);
			setChanged();
			notifyObservers(new ServiceOperationInserted(anOp));
		}
	}

	public void removeFromOperations(ServiceOperation anOp) {
		if (_operations.contains(anOp)) {
			_operations.remove(anOp);
			setChanged();
			notifyObservers(new ServiceOperationRemoved(anOp));
			anOp.setServiceInterface(null);
		}
	}

	public ServiceOperation operationWithName(String name) {

		for (Enumeration e = getOperations().elements(); e.hasMoreElements();) {
			ServiceOperation op = (ServiceOperation) e.nextElement();
			if (op.getName().equals(name)) {
				return op;
			}
		}

		return null;
	}

	@Override
	protected Vector<FlexoActionType> getSpecificActionListForThatClass() {
		Vector<FlexoActionType> returned = super.getSpecificActionListForThatClass();
		returned.add(AddServiceOperation.actionType);
		returned.add(WSDelete.actionType);
		return returned;
	}

	@Override
	public String getInspectorName() {
		return Inspectors.WKF.SERVICE_INTERFACE_INSPECTOR;
	}

	@Override
	public FlexoLevel getLevel() {
		return FlexoLevel.PROCESS;
	}

	@Override
	public Vector<WKFObject> getAllEmbeddedDeleted() {
		return getAllEmbeddedWKFObjects();
	}

	public boolean isUsedInWebService() {
		WSPortType pt = getProject().getFlexoWSLibrary().getWSPortTypeNamed(getName());
		if (pt != null && pt.getServiceInterface() != null && pt.getServiceInterface().equals(this))
			return true;
		return false;
	}

	/**
	 * uni-directional relationship between WSService and ServiceInterface. This method tries to implement the "implicit" inverse
	 * relationship. CAUTION: based on the assumption of a one-to-one relationship.
	 * 
	 * @return
	 */
	public WSService getParentService() {
		return getProject().getFlexoWSLibrary().getParentOfServiceInterface(this);
	}

	@Override
	public void delete() {

		// 1. remove from WS (while if there are many portTypes...)
		while (isUsedInWebService()) {
			WSPortType pt = getProject().getFlexoWSLibrary().getWSPortTypeNamed(getName());
			if (pt != null) {
				pt.delete();
			}
		}
		// 2. delete
		getProcess().removeFromServiceInterfaces(this);
		Enumeration<ServiceOperation> en = new Vector<ServiceOperation>(getOperations()).elements();
		while (en.hasMoreElements()) {
			ServiceOperation op = en.nextElement();
			removeFromOperations(op);
			op.delete();
		}
		super.delete();
		_name = null;
		_operations.clear();
		_operations = null;
		notifyObservers(new ServiceInterfaceRemoved(this));
		deleteObservers();
	}

	public static ServiceInterface copyPortsFromRegistry(ServiceInterface toInterface, PortRegistery fromReg) {

		Enumeration<FlexoPort> en = fromReg.getAllPorts().elements();
		while (en.hasMoreElements()) {
			FlexoPort port = en.nextElement();
			try {
				ServiceOperation serviceOp = toInterface.addServiceOperation(port.getName(), port);
				if (port instanceof AbstractInPort) {
					MessageDefinition def = ((AbstractInPort) port).getInputMessageDefinition();
					ServiceMessageDefinition serviceDef = serviceOp.getInputMessageDefinition();
					Iterator it3 = def.getEntries().iterator();
					serviceDef.setName(def.getName());
					serviceDef.setDescription(def.getDescription());

					while (it3.hasNext()) {
						MessageEntry fromEntry = (MessageEntry) it3.next();
						MessageEntry toEntry = new MessageEntry(toInterface.getProcess(), serviceDef);
						toEntry.setVariableName(fromEntry.getVariableName());
						toEntry.setType(fromEntry.getType());
						serviceDef.addToEntries(toEntry);
						ServiceMessageEntryBinding binding = serviceOp.getInputMessageBindings().getBinding(fromEntry);
						BindingValue bvalue = new BindingValue(fromEntry, serviceOp.getInputMessageBindings());
						// binding model based on variables of toEntry (operation's message)
						// message def is from Port.
						// a_pt=b_op
						BindingVariable bvar = serviceOp.getInputMessageBindings().getBindingModel().bindingVariableNamed(
								toEntry.getVariableName());
						bvalue.setBindingVariable(bvar);
						binding.setBindingValue(bvalue);
					}
				}
				if (port instanceof OutputPort) {
					MessageDefinition def = ((OutputPort) port).getOutputMessageDefinition();
					ServiceMessageDefinition serviceDef = serviceOp.getOutputMessageDefinition();
					Iterator it3 = def.getEntries().iterator();
					serviceDef.setName(def.getName());
					serviceDef.setDescription(def.getDescription());

					while (it3.hasNext()) {
						MessageEntry fromEntry = (MessageEntry) it3.next();
						MessageEntry toEntry = new MessageEntry(toInterface.getProcess(), serviceDef);
						toEntry.setVariableName(fromEntry.getVariableName());
						toEntry.setType(fromEntry.getType());
						serviceDef.addToEntries(toEntry);
						// binding model based on variables of fromEntry (port's message)
						// message def is from operation.
						// a_op=b_pt
						ServiceMessageEntryBinding binding = serviceOp.getOutputMessageBindings().getBinding(toEntry);
						BindingValue bvalue = new BindingValue(toEntry, serviceOp.getOutputMessageBindings());
						BindingVariable bvar = serviceOp.getOutputMessageBindings().getBindingModel().bindingVariableNamed(
								fromEntry.getVariableName());
						bvalue.setBindingVariable(bvar);
						binding.setBindingValue(bvalue);
					}
				}
			} catch (DuplicateWKFObjectException e) {
				if (logger.isLoggable(Level.INFO))
					logger.info("ServiceOperation already exist.");
			}
		}
		return toInterface;
	}

	@Override
	public String getClassNameKey() {
		return "service_interface";
	}
}
