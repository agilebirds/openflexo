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
package org.openflexo.foundation.dm.eo;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.eo.model.EOModel;
import org.openflexo.foundation.rm.FlexoEOModelResource;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.RMNotification;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.StorageResourceData;
import org.openflexo.foundation.utils.FlexoProjectFile;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class EOModelResourceData extends FlexoObservable implements StorageResourceData<EOModelResourceData> {

	private static final Logger logger = Logger.getLogger(EOModelResourceData.class.getPackage().getName());

	private EOModel _eoModel;

	private FlexoEOModelResource _resource;

	private boolean isModified;

	private Date lastMemoryUpdate;

	/*
	 * public EOModelResourceData (FlexoProject aProject, DMModel dmModel) {
	 * super(); _project = aProject; _dmModel = dmModel; _eoModel = null;
	 * isModified = false; lastMemoryUpdate = null; }
	 */

	public EOModelResourceData(FlexoEOModelResource resource) {
		super();
		_eoModel = null;
		isModified = false;
		lastMemoryUpdate = null;
		_resource = resource;
	}

	@Override
	public String getDeletedProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	public EOModel getEOModel() {
		return _eoModel;
	}

	public void setEOModel(EOModel model) {
		_eoModel = model;
	}

	public FlexoProjectFile getEOModelFile() {
		return _resource.getResourceFile();
	}

	@Override
	public FlexoEOModelResource getFlexoResource() {
		return _resource;
	}

	@Override
	public void setFlexoResource(FlexoResource resource) {
		_resource = (FlexoEOModelResource) resource;
	}

	@Override
	public org.openflexo.foundation.resource.FlexoResource<EOModelResourceData> getResource() {
		return getFlexoResource();
	}

	@Override
	public void setResource(org.openflexo.foundation.resource.FlexoResource<EOModelResourceData> resource) {
		setFlexoResource((FlexoResource) resource);
	}

	@Override
	public FlexoProject getProject() {
		return getFlexoResource().getProject();
	}

	public DMModel getDMModel() {
		return getProject().getDataModel();
	}

	@Override
	public void save() throws SaveResourceException {
		_resource.saveResourceData();
	}

	@Override
	public synchronized boolean isModified() {
		return isModified;
	}

	@Override
	public synchronized Date lastMemoryUpdate() {
		return lastMemoryUpdate;
	}

	public void _setLastMemoryUpdate(Date date) {
		lastMemoryUpdate = date;
	}

	@Override
	public synchronized void setIsModified() {
		if (!isModified) {
			logger.info(">>>>>>> Resource " + getFlexoResource() + " has been modified");
		}
		isModified = true;
		lastMemoryUpdate = new Date();
	}

	@Override
	public void clearIsModified() {
		clearIsModified(false);
	}

	@Override
	public synchronized void clearIsModified(boolean clearLastMemoryUpdate) {
		isModified = false;
		if (clearLastMemoryUpdate) {
			lastMemoryUpdate = null;
		}
	}

	/**
	 * 
	 * Implements
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResourceData#notifyRM(org.openflexo.foundation.rm.RMNotification) by forwarding
	 * @see org.openflexo.foundation.rm.RMNotification to the related resource, if and only if this object represents the resource data
	 *      itself (@see org.openflexo.foundation.rm.FlexoResourceData). Otherwise, invoking this method is ignored.
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResourceData#notifyRM(org.openflexo.foundation.rm.RMNotification)
	 */
	@Override
	public void notifyRM(RMNotification notification) throws FlexoException {
		getFlexoResource().notifyRM(notification);
	}

	/**
	 * Implements
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResourceData#receiveRMNotification(org.openflexo.foundation.rm.RMNotification) Receive a
	 *      notification that has been propagated by the ResourceManager scheme and coming from a modification on an other resource This
	 *      method is relevant if and only if this object represents the resource data itself (@see
	 *      org.openflexo.foundation.rm.FlexoResourceData). At this level, this method is ignored and just return, so you need to override
	 *      it in subclasses if you want to get the hook to do your stuff !
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResourceData#receiveRMNotification(org.openflexo.foundation.rm.RMNotification)
	 */
	@Override
	public void receiveRMNotification(RMNotification notification) throws FlexoException {
		// Ignore it at this level: please overrides this method in relevant
		// subclasses !
	}

	/**
	 * Overrides
	 * 
	 * @see org.openflexo.foundation.FlexoObservable#notifyObservers(org.openflexo.foundation.DataModification) by propagating
	 *      dataModification if this one implements
	 * @see org.openflexo.foundation.rm.RMNotification to the related resource
	 * 
	 * @see org.openflexo.foundation.FlexoObservable#notifyObservers(org.openflexo.foundation.DataModification)
	 */
	@Override
	public void notifyObservers(DataModification dataModification) {
		super.notifyObservers(dataModification);
		if (dataModification instanceof RMNotification) {
			try {
				notifyRM((RMNotification) dataModification);
			} catch (FlexoException e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("FLEXO Exception raised: " + e.getClass().getName() + ". See console for details.");
				}
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean delete() {
		// TODO Auto-generated method stub
		return true;
	}

}
