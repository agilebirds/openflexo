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

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.tree.TreeNode;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.dm.WSDLRepository;
import org.openflexo.foundation.rm.FlexoXMLStorageResource;
import org.openflexo.foundation.ws.dm.WSRepositoryRemoved;
import org.openflexo.foundation.xml.FlexoWSLibraryBuilder;
import org.openflexo.logging.FlexoLogger;

public class WSRepository extends WSObject implements FlexoObserver {

	private static final Logger logger = FlexoLogger.getLogger(WSRepository.class.getPackage().getName());

	private WSService parentService;

	public WSRepository(FlexoWSLibraryBuilder builder) {
		this(builder.wsLibrary);
		initializeDeserialization(builder);
		setProject(builder.getProject());
	}

	/**
	     * 
	     */
	public WSRepository(FlexoWSLibrary wsLib) {
		super(wsLib);
		this.setProject(wsLib.getProject());
	}

	/**
	 * dynamic constructor
	 * 
	 * @param wsLib
	 * @param rep
	 */
	public WSRepository(FlexoWSLibrary wsLib, WSDLRepository rep, WSService group) throws DuplicateWSObjectException {
		this(wsLib);
		setName(rep.getName());
		setWSService(group);
	}

	/**
	 * Overrides getFullyQualifiedName
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getFullyQualifiedName()
	 */
	@Override
	public String getFullyQualifiedName() {
		return "WSRepository_" + getName() + "_" + getProject().getProjectName();
	}

	/**
	 * Overrides getFlexoXMLFileResource
	 * 
	 * @see org.openflexo.foundation.rm.XMLStorageResourceData#getFlexoXMLFileResource()
	 */
	public FlexoXMLStorageResource getFlexoXMLFileResource() {
		return (FlexoXMLStorageResource) getFlexoResource();
	}

	public WSService getWSService() {
		return parentService;
	}

	public void setWSService(WSService a) {
		parentService = a;
	}

	public static Logger getLogger() {
		return logger;
	}

	public WSDLRepository getWSDLRepository() {

		return (WSDLRepository) getProject().getDataModel().getRepositoryNamed(getName());
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
		/*
		if (dataModification instanceof LanguageRemoved) {
		    Language lg = (Language) ((LanguageRemoved) dataModification).oldValue();
		    Enumeration en = getKeys().elements();
		    while (en.hasMoreElements()) {
		        Key key = (Key) en.nextElement();
		        values.remove(lg.getName() + "." + key.getName());
		    }
		    getValueList().setChanged();
		    getValueList().notifyObservers(dataModification);
		} else if (dataModification instanceof LanguageAdded) {
		    Language lg = (Language) ((LanguageAdded) dataModification).newValue();
		    Enumeration en = keys.elements();
		    while (en.hasMoreElements()) {
		        Key key = (Key) en.nextElement();
		        Value v = new Value(getDkvModel(), key, lg);
		        values.put(v.getFullyQualifiedName(), v);
		    }
		    getValueList().setChanged();
		    getValueList().notifyObservers(dataModification);
		} else if (dataModification instanceof DKVDataModification && ((DKVDataModification)dataModification).propertyName().equals("value")) {
		    setChanged();
		    notifyObservers(dataModification);
		}
		*/
	}

	/**
	 * deletes the WSRepository BUT do not delete the underlying WSDLRepository. it is the wsgroup's responsibility to decide wheither the
	 * WSDLRepository should be deleted.
	 */
	@Override
	public void delete() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("delete: WSRepository " + getName());
		}
		if (parentService != null) {
			parentService.removeFromWSRepositories(this);
			parentService = null;
		}

		// Delete only deletes WSObjects by default.
		// It is the responsibility of the WSService to decide if it should delete also the
		// real DataRepository.
		// getWSDLRepository().delete();

		super.delete();
		setChanged();
		notifyObservers(new WSRepositoryRemoved(this));
		deleteObservers();
	}

	// ==========================================================================
	// ======================== TreeNode implementation
	// =========================
	// ==========================================================================

	@Override
	public TreeNode getParent() {
		return getWSService().getWSRepositoryFolder();
	}

	@Override
	public boolean getAllowsChildren() {
		return false;
	}

	@Override
	public Vector getOrderedChildren() {
		return null;
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "ws_repository";
	}

}
