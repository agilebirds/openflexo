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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;

public abstract class FlexoImportedResource<RD extends ImportedResourceData> extends FlexoFileResource<RD> {

	private static final Logger logger = Logger.getLogger(FlexoImportedResource.class.getPackage().getName());

	private Date _lastImportDate;

	public FlexoImportedResource(FlexoProject aProject) {
		super(aProject);
	}

	protected abstract RD doImport() throws FlexoException;

	@Override
	protected void performUpdating(FlexoResourceTree updatedResources) throws FileNotFoundException, ResourceDependancyLoopException,
			FlexoException {
		if (!isLoaded()) {
			importResourceData();
		}
		backwardSynchronizeWith(updatedResources);
	}

	public RD importResourceData() throws LoadResourceException {
		try {
			_resourceData = doImport();
			_lastImportDate = new Date();
			notifyResourceChanged();
			return _resourceData;
		} catch (FlexoException e) {
			e.printStackTrace();
			throw new ImportResourceException(this);
		}
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

	public void backwardSynchronizeWith(FlexoResource aResource) throws FlexoException {
		// Must be called sub sub-classes implementation: must be overriden in subclasses !
		// At this level, only set last synchronized date
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("Backsynchronizing " + this + " with " + aResource);
		}
		setLastSynchronizedWithResource(aResource, new Date());
		getProject().notifyResourceHasBeenBackwardSynchronized(this);
	}

	/**
	 * Return a flag indicating if resource is loaded in memory
	 * 
	 * @return
	 */
	public synchronized boolean isLoaded() {
		return _resourceData != null;
	}

	public Date getLastImportDate() {
		return _lastImportDate;
	}

	public void setLastImportDate(Date aDate) {
		_lastImportDate = aDate;
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
			if (lastBackSynchronizationDate == null || resource.getLastUpdate().after(lastBackSynchronizationDate)) {
				if (lastBackSynchronizationDate != null) {
					if (logger.isLoggable(Level.FINER)) {
						logger.finer("Resource " + this + " is to be backward synchronized for " + resource + " because "
								+ new SimpleDateFormat("dd/MM HH:mm:ss SSS").format(resource.getLastUpdate()) + " is more recent than "
								+ new SimpleDateFormat("dd/MM HH:mm:ss SSS").format(lastBackSynchronizationDate));
					}
				}
				return true;
			} else {
				if (logger.isLoggable(Level.FINER)) {
					logger.finer("NOT: Resource " + this + " must NOT be BS with " + resource + " because "
							+ new SimpleDateFormat("dd/MM HH:mm:ss SSS").format(resource.getLastUpdate()) + " is more anterior than "
							+ new SimpleDateFormat("dd/MM HH:mm:ss SSS").format(lastBackSynchronizationDate));
				}
			}
		}
		return false;
	}

	/**
	 * This date is VERY IMPORTANT and CRITICAL since this is the date used by ResourceManager to compute dependancies between resources.
	 * This method returns the date that must be considered as last known update for this resource
	 * 
	 * @return a Date object
	 */
	@Override
	public synchronized Date getLastUpdate() {
		return getDiskLastModifiedDate();
	}

	/**
	 * Return data related to this resource, as an instance of an object implementing
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResourceData
	 * @return
	 * @throws FlexoException
	 * @throws ProjectLoadingCancelledException
	 * @throws FileNotFoundException
	 */
	public synchronized RD getImportedData() throws FileNotFoundException, ProjectLoadingCancelledException, FlexoException {
		if (_resourceData == null) {
			try {
				// logger.info("getImportedData() in "+this);
				update();
				_resourceData = importResourceData();
			} catch (LoadResourceException e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not load resource data for resource " + getResourceIdentifier());
				}
				e.printStackTrace();
			} catch (ResourceDependancyLoopException e) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.log(Level.SEVERE, "Loop in dependant resources of " + this + "!", e);
				}
			}
		}
		return _resourceData;
	}

}
