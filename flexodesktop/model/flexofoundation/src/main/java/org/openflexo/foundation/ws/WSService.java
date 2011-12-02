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
package org.openflexo.foundation.ws;

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.InvalidArgumentException;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.dm.WSDLRepository;
import org.openflexo.foundation.rm.FlexoXMLStorageResource;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.wkf.ws.ServiceInterface;
import org.openflexo.foundation.ws.action.WSDelete;
import org.openflexo.foundation.ws.dm.WSPortTypeAdded;
import org.openflexo.foundation.ws.dm.WSPortTypeRemoved;
import org.openflexo.foundation.ws.dm.WSRepositoryAdded;
import org.openflexo.foundation.ws.dm.WSRepositoryRemoved;
import org.openflexo.foundation.xml.FlexoWSLibraryBuilder;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileUtils;

public abstract class WSService extends WSObject implements FlexoObserver {

	private static final Logger logger = FlexoLogger.getLogger(WSService.class.getPackage().getName());

	protected Vector<WSPortType> wsPortTypes;
	protected Vector<WSRepository> wsRepositories;

	/*    protected Vector flexoProcesses;
	    protected Vector wsdlRepositories;
	  */
	private FlexoProjectFile wsdlFile;
	private WSPortTypeFolder wsPortTypeList;
	private WSRepositoryFolder wsRepositoryList;

	private String targetNamespace;

	public WSService(FlexoWSLibraryBuilder builder) {
		this(builder.wsLibrary);
		initializeDeserialization(builder);
	}

	/**
	     * 
	     */
	public WSService(FlexoWSLibrary wsLib) {
		super(wsLib);
		try {
			setName("new Service");
		} catch (DuplicateWSObjectException e) {
			e.printStackTrace();
		}
		wsPortTypes = new Vector<WSPortType>();
		wsRepositories = new Vector<WSRepository>();
		setWSLibrary(wsLib);
		setProject(wsLib.getProject());
		wsPortTypeList = new WSPortTypeFolder(this);
		wsRepositoryList = new WSRepositoryFolder(this);

	}

	@Override
	protected Vector<FlexoActionType> getSpecificActionListForThatClass() {
		Vector<FlexoActionType> returned = super.getSpecificActionListForThatClass();
		returned.add(WSDelete.actionType);
		return returned;
	}

	/**
	 * Overrides getFullyQualifiedName
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getFullyQualifiedName()
	 */
	@Override
	public String getFullyQualifiedName() {
		return "WSSERVICE_" + getName() + "_" + getProject().getProjectName();
	}

	@Override
	public void setName(String aName) throws DuplicateWSObjectException {
		if (!isDeserializing()) {
			checkName(getWSLibrary(), aName, this);
		}
		// if no exception, set name
		super.setName(aName);
	}

	public static void checkName(FlexoWSLibrary lib, String aName, WSService currentService) throws DuplicateWSObjectException {
		if (lib != null) {
			WSService service = lib.getWSServiceNamed(aName);
			if (service != null && service != currentService) {
				throw new DuplicateWSObjectException(service, "a_service_with_the_same_name_already_exist");
			}
		}
	}

	/**
	 * Overrides getFlexoXMLFileResource
	 * 
	 * @see org.openflexo.foundation.rm.XMLStorageResourceData#getFlexoXMLFileResource()
	 */
	public FlexoXMLStorageResource getFlexoXMLFileResource() {
		return (FlexoXMLStorageResource) getFlexoResource();
	}

	public FlexoProjectFile getWSDLFile() {
		if (wsdlFile.getProject() == null) {
			wsdlFile.setProject(this.getProject());
		}
		return wsdlFile;
	}

	public void setWSDLFile(FlexoProjectFile aFile) {
		wsdlFile = aFile;
		if (aFile.getProject() == null) {
			wsdlFile.setProject(this.getProject());
		}
	}

	public String getTargetNamespace() {
		return targetNamespace;
	}

	public void setTargetNamespace(String tns) {
		targetNamespace = tns;
	}

	public Vector<WSPortType> getWSPortTypes() {
		return wsPortTypes;
	}

	public void setWSPortTypes(Vector<WSPortType> ws) {
		this.wsPortTypes = ws;
	}

	public Vector<WSRepository> getWSRepositories() {
		return wsRepositories;
	}

	public void setWSRepositories(Vector<WSRepository> wsr) {
		this.wsRepositories = wsr;
	}

	public WSPortType addServiceInterfaceAsPortType(ServiceInterface serviceInterface) throws FlexoException {
		if (serviceInterface == null) {
			throw new InvalidArgumentException("Input service interface is null", "ws_attempt_to_add_a_null_service_interface");
		}
		if (getWSLibrary().getParentOfServiceInterface(serviceInterface) != null) {
			throw new InvalidArgumentException("This serviceInterface " + serviceInterface.getName() + " is already used in a WSService",
					"ws_service_interface_already_used_in_a_ws_service");
		}

		// proc.setIsWebService(true);
		// TODO ADDITIONAL STUFF like calculate WSRepositories to include
		WSPortType wsp = new WSPortType(getWSLibrary(), serviceInterface, this);

		serviceInterface.getProcess().setIsWebService(true);
		addToWSPortTypes(wsp);
		return wsp;
	}

	public void addToWSPortTypes(WSPortType pt) {
		if (wsPortTypes.contains(pt)) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Attempt to insert twice the same PortType.");
			}
			return;
		}

		wsPortTypes.add(pt);
		pt.setWSService(this);

		// rebuild the direct link between a wsGroup and flexoProcess.
		// flexoProcesses.add(proc.getFlexoProcess());

		pt.addObserver(this);
		setChanged();
		WSPortTypeAdded pa = new WSPortTypeAdded(pt);
		notifyObservers(pa);
		getWSPortTypeFolder().setChanged();
		getWSPortTypeFolder().notifyObservers(pa);
	}

	public void removeFromWSPortTypes(WSPortType proc) {
		if (!wsPortTypes.contains(proc)) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Attempt to remove a portType that can not be found: " + proc.getName());
			}
			return;
		}
		wsPortTypes.remove(proc);
		proc.setWSService(null);
		// flexoProcesses.remove(proc.getFlexoProcess());

		proc.deleteObserver(this);
		setChanged();
		WSPortTypeRemoved pr = new WSPortTypeRemoved(proc);
		notifyObservers(pr);
		getWSPortTypeFolder().setChanged();
		getWSPortTypeFolder().notifyObservers(pr);

	}

	public WSRepository addRepository(WSDLRepository rep) throws DuplicateWSObjectException, InvalidArgumentException {
		if (rep == null) {
			throw new InvalidArgumentException("null repository", "ws_attempt_to_add_a_null_wsdlrepository_to_a_ws_service");
		}

		// proc.setIsWebService(true);
		// TODO ADDITIONAL STUFF like calculate WSRepositories to include
		WSRepository wsr = new WSRepository(getWSLibrary(), rep, this);
		addToWSRepositories(wsr);
		return wsr;
	}

	public void addToWSRepositories(WSRepository rep) {
		if (wsRepositories.contains(rep)) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Attempt to insert twice the same Repository.");
			}
			return;
		}

		wsRepositories.add(rep);
		rep.setWSService(this);
		// wsdlRepositories.add(rep.getWSDLRepository());

		rep.addObserver(this);
		setChanged();
		WSRepositoryAdded pa = new WSRepositoryAdded(rep);
		notifyObservers(pa);
		getWSRepositoryFolder().setChanged();
		getWSRepositoryFolder().notifyObservers(pa);
	}

	public void removeFromWSRepositories(WSRepository rep) {
		if (!wsRepositories.contains(rep)) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Attempt to remove a repository that can not be found: " + rep.getName());
			}
			return;
		}
		wsRepositories.remove(rep);
		rep.setWSService(null);
		// wsdlRepositories.remove(rep.getWSDLRepository());
		rep.deleteObserver(this);
		setChanged();
		WSRepositoryRemoved pr = new WSRepositoryRemoved(rep);
		notifyObservers(pr);
		getWSRepositoryFolder().setChanged();
		getWSRepositoryFolder().notifyObservers(pr);

	}

	public static Logger getLogger() {
		return logger;
	}

	public WSPortType getWSPortTypeNamed(String name) {
		if (name == null) {
			return null;
		}
		Enumeration en = wsPortTypes.elements();
		while (en.hasMoreElements()) {
			WSPortType wsp = (WSPortType) en.nextElement();
			if (wsp.getName() != null && wsp.getName().equals(name)) {
				return wsp;
			}
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("WS PortType " + name + " not be found in service:" + getName());
		}
		return null;
	}

	public WSRepository getWSRepositoryNamed(String name) {
		if (name == null) {
			return null;
		}
		Enumeration en = wsRepositories.elements();
		while (en.hasMoreElements()) {
			WSRepository wsr = (WSRepository) en.nextElement();
			if (wsr.getName().equals(name)) {
				return wsr;
			}
		}
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("WS Repository " + name + " could not be found.");
		}
		return null;
	}

	/**
	 * Overrides update
	 * 
	 * @see org.openflexo.foundation.FlexoObserver#update(org.openflexo.foundation.FlexoObservable,
	 *      org.openflexo.foundation.DataModification)
	 */
	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		// fucking things to do.

	}

	/**
	 * Overrides getInspectorName
	 * 
	 * @see org.openflexo.inspector.InspectableObject#getInspectorName()
	 */
	public String getInspectorName() {
		return Inspectors.WSE.WSSERVICE_INSPECTOR;
	}

	public WSPortTypeFolder getWSPortTypeFolder() {
		return wsPortTypeList;
	}

	public WSRepositoryFolder getWSRepositoryFolder() {
		return wsRepositoryList;
	}

	@Override
	public void delete() {

		// remove wsdl file ?
		if (wsdlFile != null && wsdlFile.getFile() != null) {
			FileUtils.recursiveDeleteFile(getWSDLFile().getFile());
		}
		wsdlFile = null;

		wsPortTypes.clear();
		wsRepositories.clear();
		wsPortTypes = null;
		wsRepositories = null;

		wsPortTypeList.delete();
		wsPortTypeList = null;
		wsRepositoryList.delete();
		wsRepositoryList = null;
		super.delete();
	}

	// ==========================================================================
	// ======================== Search WSPortType
	// =========================
	// ==========================================================================

}
