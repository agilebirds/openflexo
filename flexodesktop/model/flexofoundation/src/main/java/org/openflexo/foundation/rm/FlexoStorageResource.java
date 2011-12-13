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
package org.openflexo.foundation.rm;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.dm.InvalidFileException;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.utils.ProjectLoadingHandler;

public abstract class FlexoStorageResource<SRD extends StorageResourceData> extends FlexoFileResource<SRD> {

	private static final Logger logger = Logger.getLogger(FlexoStorageResource.class.getPackage().getName());

	public static interface ResourceLoadingListener {
		public void notifyResourceWillBeLoaded(FlexoStorageResource resource);

		public void notifyResourceHasBeenLoaded(FlexoStorageResource resource);
	}

	private Vector<ResourceLoadingListener> resourceLoadingListeners = new Vector<ResourceLoadingListener>();

	private Date lastKnownMemoryUpdate;

	/**
	 * Constructor used for XML Serialization: never try to instanciate resource from this constructor
	 * 
	 * @param builder
	 */
	public FlexoStorageResource(FlexoProjectBuilder builder) {
		this(builder.project);
		builder.notifyResourceLoading(this);
	}

	@Override
	public synchronized void delete() {
		resourceLoadingListeners.clear();
		super.delete();
	}

	@Override
	public synchronized void delete(boolean deleteFile) {
		resourceLoadingListeners.clear();
		super.delete(deleteFile);
	}

	public FlexoStorageResource(FlexoProject aProject) {
		super(aProject);
	}

	public boolean needsSaving() {
		return isModified();
	}

	/**
	 * This date is VERY IMPORTANT and CRITICAL since this is the date used by ResourceManager to compute dependancies between resources.
	 * This method returns the date that must be considered as last known update for this resource
	 * 
	 * @return a Date object
	 */
	@Override
	public final Date getLastUpdate() {
		if (getLastKnownMemoryUpdate() != null) {
			return getLastKnownMemoryUpdate();
		}
		return getDiskLastModifiedDate();
	}

	/**
	 * Returns the last known memory update date known by Flexo
	 * 
	 * @return
	 */
	public Date getLastKnownMemoryUpdate() {
		if (lastMemoryUpdate() != null) {
			return lastMemoryUpdate();// We are loaded so we return the lastMemortUpdate()
		}
		if (isLoaded() && lastKnownMemoryUpdate == null) {
			// last modified which is never null
			lastKnownMemoryUpdate = getDiskLastModifiedDate();
		}
		return lastKnownMemoryUpdate;
	}

	public void setLastKnownMemoryUpdate(Date lastKnownMemoryUpdate) {
		this.lastKnownMemoryUpdate = lastKnownMemoryUpdate;
	}

	/**
	 * Returns date when data related to this resource was last modified
	 * 
	 * @return
	 */
	public Date lastMemoryUpdate() {
		if (!isLoaded()) {
			return null;
		} else {
			try {
				return getResourceData().lastMemoryUpdate();
			} catch (Exception e) {
				e.printStackTrace();
				return new Date();
			}
		}
	}

	/**
	 * Returns a flag indicating if data related to this resource has been modified. Returns false if data is not loaded
	 * 
	 * @return
	 */
	public boolean isModified() {
		if (!isLoaded()) {
			return false;
		} else {
			try {
				return getResourceData().isModified();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	/**
	 * Return a flag indicating if resource is loaded in memory
	 * 
	 * @return
	 */
	public synchronized boolean isLoaded() {
		return (_resourceData != null);
	}

	public final void saveResourceData() throws SaveResourceException {
		getProject().saveResourceData(this, true);
	}

	protected abstract void saveResourceData(boolean clearIsModified) throws SaveResourceException;

	/**
	 * Return data related to this resource, as an instance of an object implementing
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResourceData
	 * @return
	 */
	public SRD getResourceData() {
		if (_resourceData == null) {
			try {
				_resourceData = loadResourceData();
			} catch (FlexoException e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not load resource data for resource " + getResourceIdentifier());
				}
				e.printStackTrace();
			}
		}
		return _resourceData;
	}

	/**
	 * Return data related to this resource, as an instance of an object implementing
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResourceData
	 * @return
	 */
	public SRD reloadResourceData() {
		try {
			_resourceData = loadResourceData();
		} catch (FlexoException e) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not reload resource data for resource " + getResourceIdentifier());
			}
			e.printStackTrace();
		}
		return _resourceData;
	}

	/**
	 * 
	 * Receive a notification that has been propagated by the ResourceManager scheme and coming from a modification on an other resource
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResource#notifyRM(org.openflexo.foundation.rm.RMNotification)
	 */
	@Override
	public void receiveRMNotification(RMNotification notification) throws FlexoException {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Resource " + getResourceIdentifier() + " receive " + notification);
		}

		super.receiveRMNotification(notification);

		if (!isLoaded()) {
			if (notification.forceUpdateWhenUnload()) {
				// Resource is not loaded but requires immediate update
				// Load the resource and invoke synchronizing for this
				// notification
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Resource " + getResourceIdentifier() + " receive " + notification);
					logger.fine("Resource " + getResourceIdentifier() + " !isLoaded() &&  notification.forceUpdateWhenUnload()");
					try {
						logger.fine("getResourceData() = " + getResourceData());
					} catch (Exception e) {
						e.printStackTrace();
						throw new FlexoException();
					}

				}

				try {
					getResourceData().receiveRMNotification(notification);
				} catch (Exception e) {
					e.printStackTrace();
					throw new FlexoException();
				}
			} else {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Resource " + getResourceIdentifier() + " receive " + notification);
					logger.fine("Resource " + getResourceIdentifier() + " !isLoaded() &&  !notification.forceUpdateWhenUnload()");
					logger.fine("DO NOTHING MORE");

				}

			}
		} else {
			// Resource is loaded.
			// Just invoke synchronizing for this notification
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Resource " + getResourceIdentifier() + " receive " + notification);
				logger.fine("Resource " + getResourceIdentifier() + " isLoaded()");
				try {
					logger.fine("getResourceData() = " + getResourceData());
				} catch (Exception e) {
					e.printStackTrace();
					throw new FlexoException();
				}

			}
			try {
				getResourceData().receiveRMNotification(notification);
			} catch (Exception e) {
				e.printStackTrace();
				throw new FlexoException();
			}
		}
	}

	public void addResourceLoadingListener(ResourceLoadingListener listener) {
		if (!resourceLoadingListeners.contains(listener)) {
			resourceLoadingListeners.add(listener);
		}
	}

	public void removeResourceLoadingListener(ResourceLoadingListener listener) {
		resourceLoadingListeners.remove(listener);
	}

	protected void notifyListenersResourceWillBeLoaded() {
		for (ResourceLoadingListener listener : resourceLoadingListeners) {
			listener.notifyResourceWillBeLoaded(this);
		}
	}

	protected void notifyListenersResourceHasBeenLoaded() {
		for (ResourceLoadingListener listener : resourceLoadingListeners) {
			listener.notifyResourceHasBeenLoaded(this);
		}
	}

	protected boolean _resolveDependanciesSchemeRunningForThisResource = false;

	protected final SRD performLoadResourceData() throws LoadResourceException, FileNotFoundException, ProjectLoadingCancelledException {
		boolean wasLoaded = isLoaded();
		if (!wasLoaded) {
			notifyListenersResourceWillBeLoaded();
		}
		SRD data = performLoadResourceData(null, getLoadingHandler());
		if (!wasLoaded) {
			notifyListenersResourceHasBeenLoaded();
		}
		return data;
	}

	protected abstract SRD performLoadResourceData(FlexoProgress progress, ProjectLoadingHandler loadingHandler)
			throws LoadResourceException, FileNotFoundException, ProjectLoadingCancelledException;

	public final SRD loadResourceData(FlexoProgress progress, ProjectLoadingHandler loadingHandler) throws FlexoException {
		if (_resolveDependanciesSchemeRunningForThisResource) {
			(new Exception("Loop in dependancies")).printStackTrace();
			logger.warning("Found loop in dependancies. Automatic rebuild dependancies is required !");
			getProject().setRebuildDependanciesIsRequired();
			boolean wasLoaded = isLoaded();
			if (!wasLoaded) {
				notifyListenersResourceWillBeLoaded();
			}
			try {
				_resourceData = performLoadResourceData(progress, loadingHandler);
				if (!wasLoaded) {
					notifyListenersResourceHasBeenLoaded();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				throw new InvalidFileException(e.getMessage());
			}
			return _resourceData;
		}
		try {
			update();
		} catch (ResourceDependancyLoopException e1) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.log(Level.SEVERE, "Loop in dependant resources of " + this + "!", e1);
			}
			e1.printStackTrace();
			// BMA VERY EXPERIMENTAL : ignore LOOP, just load the resource
			if (!isLoaded()) {
				try {
					notifyListenersResourceWillBeLoaded();
					_resourceData = performLoadResourceData();
					notifyListenersResourceHasBeenLoaded();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					throw new InvalidFileException(e.getMessage());
				}
			}

		} catch (FileNotFoundException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, "File not found exception.", e);
			}
			e.printStackTrace();
		}
		return _resourceData;
	}

	public final SRD loadResourceData() throws FlexoException {
		return loadResourceData(null, getLoadingHandler());
	}

	/**
	 * Perform update of specified resource data from data read from a updated resource on disk
	 */
	@Override
	public synchronized void performDiskUpdate() throws FlexoException {
		reloadResourceData();
		lastKnownMemoryUpdate = new Date(); // update of the last memory update to now
		super.performDiskUpdate();
	}

	@Override
	protected Date getRequestDateToBeUsedForOptimisticDependencyChecking(FlexoResource resource) {
		Date returned = getLastSynchronizedWithResource(resource);
		if (returned.getTime() == 0) {
			// If never synchronized, consider last update
			returned = getLastUpdate();
		}
		return returned;
	}

	@Override
	protected boolean requireUpdateBecauseOf(FlexoResource resource) {
		if (super.requireUpdateBecauseOf(resource)) {
			// OK, at first sight 'resource' of which 'this' depends is newer than 'this' so it seems that we need to update 'this'. BUT,
			// maybe
			// the modifications of 'resource' does not affect 'this', therefore we will look at the lastSynchronizationDate
			Date lastBackSynchronizationDate = getLastSynchronizedWithResource(resource);
			if (!project.getTimestampsHaveBeenLoaded()) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Time stamps not loaded yet!!! Impossible to say if we have to do something or not");
				}
			}
			if ((lastBackSynchronizationDate == null) || (resource.getLastUpdate().after(lastBackSynchronizationDate))) {
				if (lastBackSynchronizationDate != null) {
					if (logger.isLoggable(Level.FINER)) {
						logger.finer("Resource " + this + " is to be backward synchronized for " + resource + " because "
								+ (new SimpleDateFormat("dd/MM HH:mm:ss SSS")).format(resource.getLastUpdate()) + " is more recent than "
								+ (new SimpleDateFormat("dd/MM HH:mm:ss SSS")).format(lastBackSynchronizationDate));
					}
				}
				return true;
			} else {
				if (logger.isLoggable(Level.FINER)) {
					logger.finer("NOT: Resource " + this + " must NOT be BS with " + resource + " because "
							+ (new SimpleDateFormat("dd/MM HH:mm:ss SSS")).format(resource.getLastUpdate()) + " is more anterior than "
							+ (new SimpleDateFormat("dd/MM HH:mm:ss SSS")).format(lastBackSynchronizationDate));
				}
			}
		}
		return false;
	}

	public final void backwardSynchronizeWith(FlexoResourceTree updatedResources) throws ResourceDependancyLoopException, FlexoException,
			FileNotFoundException {
		if (!updatedResources.isEmpty()) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Resource " + this + " requires backward synchronization for " + updatedResources.getChildNodes().size()
						+ " resources :");
			}
			for (FlexoResourceTree resourceTrees : updatedResources.getChildNodes()) {
				FlexoResource resource = resourceTrees.getRootNode();
				resource.update();
				if (FlexoResourceManager.getBackwardSynchronizationHook() != null) {
					FlexoResourceManager.getBackwardSynchronizationHook().notifyBackwardSynchronization(this, resource);
				}
				backwardSynchronizeWith(resource);
			}
		}
	}

	public void backwardSynchronizeWith(FlexoResource aResource) throws FlexoException /*throws FlexoException*/
	{
		// Must be called sub sub-classes implementation: must be overriden in subclasses !
		// At this level, only set last synchronized date
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("Backsynchronizing " + this + " with " + aResource);
		}
		setLastSynchronizedWithResource(aResource, new Date());
	}

	@Override
	protected void performUpdating(FlexoResourceTree updatedResources) throws ResourceDependancyLoopException, FlexoException,
			LoadResourceException, FileNotFoundException, ProjectLoadingCancelledException {
		if (!isLoaded()) {
			try {
				_resourceData = performLoadResourceData();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				throw new InvalidFileException(e.getMessage());
			}
		}
		backwardSynchronizeWith(updatedResources);
	}

}
