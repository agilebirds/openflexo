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

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.dkv.EmptyStringException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoWSLibraryResource;
import org.openflexo.foundation.rm.FlexoXMLStorageResource;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.ProjectRestructuration;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.ws.ServiceInterface;
import org.openflexo.foundation.ws.action.CreateNewWebService;
import org.openflexo.foundation.ws.dm.ExternalWSServiceAdded;
import org.openflexo.foundation.ws.dm.ExternalWSServiceRemoved;
import org.openflexo.foundation.ws.dm.InternalWSServiceAdded;
import org.openflexo.foundation.ws.dm.InternalWSServiceRemoved;
import org.openflexo.foundation.xml.FlexoWSLibraryBuilder;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.xmlcode.XMLMapping;

/**
 * @author gpolet
 * 
 */
public class FlexoWSLibrary extends WSObject implements XMLStorageResourceData {

	private FlexoWSLibraryResource _resource;

	private static final Logger logger = FlexoLogger.getLogger(FlexoWSLibrary.class.getPackage().getName());

	public static FlexoWSLibrary createNewWSLibrary(FlexoProject project) {
		FlexoWSLibrary wsl = new FlexoWSLibrary(project);
		File wsFile = ProjectRestructuration.getExpectedWSLibraryFile(project, project.getProjectName());
		FlexoProjectFile wsLibraryFile = new FlexoProjectFile(wsFile, project);
		FlexoWSLibraryResource wslRes;
		try {
			wslRes = new FlexoWSLibraryResource(project, wsl, wsLibraryFile);
		} catch (InvalidFileNameException e) {
			wsLibraryFile = new FlexoProjectFile(FileUtils.getValidFileName(wsLibraryFile.getRelativePath()));
			wsLibraryFile.setProject(project);
			try {
				wslRes = new FlexoWSLibraryResource(project, wsl, wsLibraryFile);
			} catch (InvalidFileNameException e1) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("Could not create WS Library with name: " + wsLibraryFile.getRelativePath()
							+ ". This should never happen");
				}
				return null;
			}
		}

		try {
			wslRes.saveResourceData();
			project.registerResource(wslRes);
		} catch (Exception e1) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e1.getClass().getName() + ". See console for details.");
			}
			e1.printStackTrace();
		}
		return wsl;
	}

	/**
	 * Overrides save
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResourceData#save()
	 */
	@Override
	public void save() throws SaveResourceException {
		_resource.saveResourceData();
	}

	protected Vector<ExternalWSService> externalWSServices;
	protected Vector<InternalWSService> internalWSServices;

	private ExternalWSFolder externalWSServiceList = new ExternalWSFolder(this);
	private InternalWSFolder internalWSServiceList = new InternalWSFolder(this);

	public FlexoWSLibrary(FlexoWSLibraryBuilder builder) {
		this(builder.getProject());
		builder.wsLibrary = this;
		_resource = builder.resource;
		initializeDeserialization(builder);
	}

	/**
     *
     */
	public FlexoWSLibrary(FlexoProject project) {
		super(project);
		setWSLibrary(this);
		externalWSServices = new Vector<ExternalWSService>();
		internalWSServices = new Vector<InternalWSService>();
		setProject(project);
	}

	@Override
	protected Vector getSpecificActionListForThatClass() {
		Vector returned = super.getSpecificActionListForThatClass();
		returned.add(CreateNewWebService.actionType);
		return returned;
	}

	/**
	 * Overrides getFullyQualifiedName
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getFullyQualifiedName()
	 */
	@Override
	public String getFullyQualifiedName() {
		return "WSLIBRARY_" + getProject().getProjectName();
	}

	/**
	 * Overrides getFlexoXMLFileResource
	 * 
	 * @see org.openflexo.foundation.rm.XMLStorageResourceData#getFlexoXMLFileResource()
	 */
	@Override
	public FlexoXMLStorageResource getFlexoXMLFileResource() {
		return getFlexoResource();
	}

	/**
	 * utility method to retrieve a service from a serviceInterface.
	 * 
	 * @param si
	 * @return
	 */
	public WSService getParentOfServiceInterface(ServiceInterface si) {
		if (si != null) {
			WSPortType pt = getWSPortTypeNamed(si.getName());
			if (pt != null) {
				return pt.getWSService();
			}
		}
		return null;
	}

	/**
	 * creates an externalWSService, without linking it to the library. call wslibrary.addExternalWSService(wsgroup, wsdlfile) to link a
	 * wsgroup.
	 * 
	 * @param name
	 * @return
	 */
	public ExternalWSService createExternalWSService(String wsServiceName) throws FlexoException {
		if (wsServiceName == null) {
			throw new FlexoException("Input name for the WSService is null", "ws_service_with_no_name");
		}
		if (wsServiceName.trim().length() == 0) {
			// TODO this exception comes from DKV package. not really nice.
			throw new FlexoException("Empty String for WSService name", "ws_service_with_no_name");
		}

		ExternalWSService group = new ExternalWSService(this);

		group.setName(wsServiceName);
		return group;
	}

	/**
	 * 
	 * @param group
	 *            a WSService to link to the library.
	 * @param wsdlFileToCopy
	 * @return
	 * @throws DuplicateWSObjectException
	 * @throws EmptyStringException
	 */
	public ExternalWSService addExternalWSService(ExternalWSService group, File wsdlFileToCopy) throws DuplicateWSObjectException {
		Enumeration<ExternalWSService> en = externalWSServices.elements();
		while (en.hasMoreElements()) {
			ExternalWSService element = en.nextElement();
			if (element.getName().equals(group.getName())) {
				throw new DuplicateWSObjectException(element, "ws_service_already_exists");
			}
		}
		// wsdl file
		File copiedFile = new File(ProjectRestructuration.getExpectedWSLibraryDirectory(getWSLibrary().getProject().getProjectDirectory()),
				wsdlFileToCopy.getName());
		/* if (progress != null) {
		     progress.setProgress(FlexoLocalization.localizedForKey("copying") + " " + wsdlFileToCopy.getName());
		 }*/
		try {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Copying file " + wsdlFileToCopy.getAbsolutePath() + " to " + copiedFile.getAbsolutePath());
			}
			FileUtils.copyFileToFile(wsdlFileToCopy, copiedFile);
		} catch (IOException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not copy file " + wsdlFileToCopy.getAbsolutePath() + " to " + copiedFile.getAbsolutePath());
			}
		}

		// Perform some settings
		FlexoProjectFile wsdlFile = new FlexoProjectFile(copiedFile, getWSLibrary().getProject());
		group.setWSDLFile(wsdlFile);

		//
		addToExternalWSServices(group);

		addObserver(group);
		return group;
	}

	/**
	 * creates an externalWSService, without linking it to the library. call wslibrary.addExternalWSService(wsgroup, wsdlfile) to link a
	 * wsgroup.
	 * 
	 * @param name
	 * @return
	 */
	public InternalWSService createInternalWSService(String wsServiceName) throws FlexoException {
		if (wsServiceName == null) {
			throw new FlexoException("Input name for the WSService is null", "ws_service_with_no_name");
		}
		if (wsServiceName.trim().length() == 0) {
			// TODO this exception comes from DKV package. not really nice.
			throw new FlexoException("Empty String for wsgroup name", "ws_service_with_no_name");
		}

		InternalWSService group = new InternalWSService(this);

		group.setName(wsServiceName);
		return group;
	}

	public InternalWSService addInternalWSServiceNamed(InternalWSService group) throws DuplicateWSObjectException {

		Enumeration<InternalWSService> en = internalWSServices.elements();
		while (en.hasMoreElements()) {
			InternalWSService elem = en.nextElement();
			if (elem.getName().equals(group.getName())) {
				throw new DuplicateWSObjectException(group, "ws_service_already_exists");
			}
		}
		addToInternalWSServices(group);

		addObserver(group);
		return group;
	}

	public void addToExternalWSServices(ExternalWSService group) {
		if (externalWSServices.contains(group)) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Attempt to insert twice the same wsService.");
			}
			return;
		}
		externalWSServices.add(group);
		setChanged();

		ExternalWSServiceAdded ga = new ExternalWSServiceAdded(group);
		notifyObservers(ga);
		getExternalWSFolder().setChanged();
		getExternalWSFolder().notifyObservers(ga);
	}

	public void addToInternalWSServices(InternalWSService group) {
		if (internalWSServices.contains(group)) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Attempt to insert twice the same wsService.");
			}
			return;
		}
		internalWSServices.add(group);
		setChanged();
		InternalWSServiceAdded ga = new InternalWSServiceAdded(group);
		notifyObservers(ga);
		getInternalWSFolder().setChanged();
		getInternalWSFolder().notifyObservers(ga);
	}

	public void removeFromExternalWSServices(ExternalWSService group) {
		externalWSServices.remove(group);
		setChanged();
		ExternalWSServiceRemoved gr = new ExternalWSServiceRemoved(group);
		notifyObservers(gr);
		getExternalWSFolder().setChanged();
		getExternalWSFolder().notifyObservers(gr);
	}

	public void removeFromInternalWSServices(InternalWSService group) {
		internalWSServices.remove(group);
		setChanged();
		InternalWSServiceRemoved gr = new InternalWSServiceRemoved(group);
		notifyObservers(gr);
		getInternalWSFolder().setChanged();
		getInternalWSFolder().notifyObservers(gr);
	}

	public Vector<ExternalWSService> getExternalWSServices() {
		return externalWSServices;
	}

	public void setExternalWSServices(Vector<ExternalWSService> wsServices) {
		this.externalWSServices = wsServices;
	}

	public Vector<InternalWSService> getInternalWSServices() {
		return internalWSServices;
	}

	public void setInternalWSServices(Vector<InternalWSService> wsServices) {
		this.internalWSServices = wsServices;
	}

	public static Logger getLogger() {
		return logger;
	}

	public ExternalWSService getExternalWSServiceNamed(String name) {
		Enumeration<ExternalWSService> en = externalWSServices.elements();
		while (en.hasMoreElements()) {
			ExternalWSService group = en.nextElement();
			if (logger.isLoggable(Level.INFO)) {
				logger.info("looking in :" + group.getName());
			}
			if (group.getName().equals(name)) {
				return group;
			}
		}
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Service " + name + " could not be found.");
		}
		return null;
	}

	public InternalWSService getInternalWSServiceNamed(String name) {
		Enumeration<InternalWSService> en = internalWSServices.elements();
		while (en.hasMoreElements()) {
			InternalWSService group = en.nextElement();
			if (logger.isLoggable(Level.INFO)) {
				logger.info("looking in :" + group.getName());
			}
			if (group.getName().equals(name)) {
				return group;
			}
		}
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Service " + name + " could not be found.");
		}
		return null;
	}

	public WSService getWSServiceNamed(String s) {

		WSService group = getExternalWSServiceNamed(s);
		if (group == null) {
			group = getInternalWSServiceNamed(s);
		}
		return group;
	}

	public WSPortType getWSPortTypeNamed(String s) {

		// Look in external wsgroups.
		Enumeration<ExternalWSService> en = getExternalWSServices().elements();
		WSPortType toReturn = null;
		while (en.hasMoreElements()) {
			ExternalWSService group = en.nextElement();
			toReturn = group.getWSPortTypeNamed(s);
			if (toReturn != null) {
				return toReturn;
			}
		}

		// Look in internal wsgroups
		Enumeration<InternalWSService> en1 = getInternalWSServices().elements();
		while (en1.hasMoreElements()) {
			InternalWSService group = en1.nextElement();
			toReturn = group.getWSPortTypeNamed(s);
			if (toReturn != null) {
				return toReturn;
			}
		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Could not find a WSPortType named: " + s);
		}
		return null;

	}

	public WSRepository getWSRepositoryNamed(String s) {

		// Look in external wsgroups.
		Enumeration<ExternalWSService> en = getExternalWSServices().elements();
		WSRepository toReturn = null;
		while (en.hasMoreElements()) {
			ExternalWSService group = en.nextElement();
			toReturn = group.getWSRepositoryNamed(s);
			if (toReturn != null) {
				return toReturn;
			}
		}

		// Look in internal wsgroups
		Enumeration<InternalWSService> en1 = getInternalWSServices().elements();
		while (en1.hasMoreElements()) {
			InternalWSService group = en1.nextElement();
			toReturn = group.getWSRepositoryNamed(s);
			if (toReturn != null) {
				return toReturn;
			}
		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Could not find a WSRepository named: " + s);
		}
		return null;

	}

	@Override
	public FlexoWSLibraryResource getFlexoResource() {
		return _resource;
	}

	/**
	 * Overrides getXMLMapping
	 * 
	 * @see org.openflexo.foundation.ws.WSObject#getXMLMapping()
	 */
	@Override
	public XMLMapping getXMLMapping() {
		return getProject().getXmlMappings().getWSMapping();
	}

	@Override
	public void setFlexoResource(FlexoResource resource) {
		_resource = (FlexoWSLibraryResource) resource;
	}

	public ExternalWSFolder getExternalWSFolder() {
		return externalWSServiceList;
	}

	public InternalWSFolder getInternalWSFolder() {
		return internalWSServiceList;
	}

	@Override
	public void delete() {
		// euh, delete the entire library??
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("delete : FlexoWSLibrary");
		}
		getInternalWSFolder().delete();
		getExternalWSFolder().delete();
	}

	public boolean isDeclaredAsWS(FlexoProcess aProcess) {
		return portTypeForProcess(aProcess) != null;
	}

	public WSPortType portTypeForProcess(FlexoProcess aProcess) {
		WSPortType portType = getWSPortTypeNamed(aProcess.getName());
		if (portType != null && portType.getFlexoProcess() == aProcess) {
			return portType;
		}
		return null;
	}

	@Override
	public WSObject getParent() {
		return null;
	}

	@Override
	public Vector<WSObject> getOrderedChildren() {
		Vector<WSObject> a = new Vector<WSObject>();
		a.add(externalWSServiceList);
		a.add(internalWSServiceList);
		return a;
	}

	@Override
	public String getName() {
		return "ws_library";
	}

	@Override
	public String getLocalizedName() {
		return FlexoLocalization.localizedForKey(getName());
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return getName();
	}

}
