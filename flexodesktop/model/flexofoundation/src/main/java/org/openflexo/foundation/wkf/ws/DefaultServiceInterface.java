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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.DeletableObject;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.wkf.DuplicateWKFObjectException;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.LevelledObject;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.dm.ServiceOperationInserted;
import org.openflexo.foundation.wkf.dm.ServiceOperationRemoved;
import org.openflexo.inspector.InspectableObject;

/**
 * A ServiceInteface is attached to a FlexoProcess and contains all the service operations used in the context of WebServices.
 * 
 * The Default Service Interface is the ServiceInterface of the port registery.
 * 
 * 
 * @author Denis VANVYVE
 * 
 */
public final class DefaultServiceInterface extends ServiceInterface implements InspectableObject, LevelledObject, DeletableObject,
		FlexoObserver {

	private static final Logger logger = Logger.getLogger(DefaultServiceInterface.class.getPackage().getName());

	private Hashtable operationTable;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Default constructor
	 */
	public DefaultServiceInterface(FlexoProcess process) throws FlexoException {
		super(process, process.getPortRegistery().getName());
		operationTable = new Hashtable();
		// add observer
		getPortRegistery();
		updateFromPortRegistery();
	}

	// ==========================================================================
	// ============================= Methods
	// ================================
	// ==========================================================================
	public PortRegistery getPortRegistery() {
		if (getProcess() != null) {
			if (getProcess().getPortRegistery() != null) {
				getProcess().getPortRegistery().addObserver(this);
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("No related PortRegistery !");
				}
			}
			return getProcess().getPortRegistery();
		}
		return null;
	}

	@Override
	public String getName() {
		return getPortRegistery().getName();
	}

	@Override
	public void setName(String aName) {
		// not possible on port registry
	}

	@Override
	public String getFullyQualifiedName() {
		return getProcess().getFullyQualifiedName() + "." + getName() + ".DEFAULT_SERVICE_INTERFACE";
	}

	public ServiceOperation addServiceOperation(FlexoPort relatedPort) throws DuplicateWKFObjectException {
		return addServiceOperation(relatedPort.getName(), relatedPort);
	}

	@Override
	public ServiceOperation addServiceOperation(String name, FlexoPort relatedPort) throws DuplicateWKFObjectException {
		// will throw an exception
		try {
			ServiceOperation.checkOperationName(this, name, null);
		} catch (DuplicateWKFObjectException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("An operation with the same name (" + name + ") already exist");
			}
			throw e;
		}
		ServiceOperation newOp = new DefaultServiceOperation(this, relatedPort);
		addToOperations(newOp);
		return newOp;
	}

	@Override
	public Vector getOperations() {
		// updateFromPortRegistery();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("getOperations in DefaultServiceInterface:" + operationTable);
		}
		return new Vector(operationTable.values());
	}

	@Override
	public void setOperations(Vector operations) {
		// not applicable.
	}

	public void updateFromPortRegistery() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("updateFromPortRegistery() in DefaultServiceInterface of process:" + getProcess().getName());
		}
		// Check if some ports have been deleted.
		Vector operationsToDelete = new Vector(operationTable.values());
		if (getPortRegistery() != null) {
			Vector ports = getPortRegistery().getAllPorts();
			// logger.info("updateFromPortRegistery()");
			// for (FlexoPort p : getPortRegistery().getAllPorts()) logger.info("port: "+p);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Ports of portRegistry for " + getProcess().getName() + ":" + ports);
			}
			for (Enumeration e = ports.elements(); e.hasMoreElements();) {
				FlexoPort port = (FlexoPort) e.nextElement();
				if (operationForPort(port) == null) {
					// port not already in operationTable
					// add port as a DefaultServiceOperation
					try {
						DefaultServiceOperation newOperation = new DefaultServiceOperation(this, port);
						addToOperations(newOperation);
						// logger.info("Add operation for "+port);
					} catch (FlexoException f) {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Exception should not arise here");
						}
						f.printStackTrace();
					}

				} else {
					// port already registered in the hashtable
					// do not delete it.
					operationsToDelete.remove(operationForPort(port));
				}
			}
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("No related PortRegistery !");
			}
		}
		for (Enumeration e = operationsToDelete.elements(); e.hasMoreElements();) {
			ServiceOperation op = (ServiceOperation) e.nextElement();
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Remove ServiceOperation " + op.getName());
			}
			op.delete();// will thereby be removed from operations.

		}
	}

	private ServiceOperation operationForPort(FlexoPort port) {
		return (ServiceOperation) operationTable.get(port);
	}

	@Override
	public void addToOperations(ServiceOperation anOp) {
		// logger.info("addToOperations "+anOp);
		if (!operationTable.contains(anOp)) {
			operationTable.put(anOp.getPort(), anOp);
			anOp.setServiceInterface(this);
			if (!isDeserializing()) {
				setChanged();
				notifyObservers(new ServiceOperationInserted(anOp));
			}
		}
	}

	@Override
	public void removeFromOperations(ServiceOperation anOp) {
		// logger.info("removeFromOperations "+anOp);
		if (operationTable.contains(anOp)) {
			operationTable.remove(anOp.getPort());
			setChanged();
			notifyObservers(new ServiceOperationRemoved(anOp));
			anOp.setServiceInterface(null);
		}
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public void setDescription(String a) {
		// not applicable
	}

	@Override
	public ServiceOperation operationWithName(String name) {

		for (Enumeration e = operationTable.elements(); e.hasMoreElements();) {
			ServiceOperation op = (ServiceOperation) e.nextElement();
			if (op.getName().equals(name)) {
				return op;
			}
		}

		return null;
	}

	@Override
	public Vector<WKFObject> getAllEmbeddedDeleted() {
		return getAllEmbeddedWKFObjects();
	}

	/*
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("update in DefaultServiceInterface of " + getProcess().getName() + ": " + observable + " - " + dataModification);
		}
		if (!isSerializing()) {
			if (observable == getPortRegistery()) {
				updateFromPortRegistery();
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Received a notification from " + observable);
				}
			}
		}
	}

	@Override
	public boolean delete() {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Delete in DefaultServiceInterface... ????");
		}
		return super.delete();
	}

	public static ServiceInterface copyPortsFromRegistry(ServiceInterface toInterface, PortRegistery fromReg) {
		// NOT APPLICABLE
		return null;
	}

	@Override
	public String getClassNameKey() {
		return "default_service_interface";
	}
}
