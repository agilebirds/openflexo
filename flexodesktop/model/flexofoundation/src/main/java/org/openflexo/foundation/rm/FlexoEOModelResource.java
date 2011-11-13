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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.eo.DMEOAdaptorType;
import org.openflexo.foundation.dm.eo.DMEOModel;
import org.openflexo.foundation.dm.eo.EOAccessException;
import org.openflexo.foundation.dm.eo.EOModelResourceData;
import org.openflexo.foundation.dm.eo.InvalidEOModelFileException;
import org.openflexo.foundation.dm.eo.LoadEOModelException;
import org.openflexo.foundation.dm.eo.model.EOModel;
import org.openflexo.foundation.dm.eo.model.EOModelGroup;
import org.openflexo.foundation.dm.eo.model.PropertyListDeserializationException;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.utils.ProjectLoadingHandler;

/**
 * Represents an EOModel resource
 * 
 * @author sguerin
 * 
 */
public class FlexoEOModelResource extends FlexoStorageResource<EOModelResourceData> {

	private static final Logger logger = Logger.getLogger(FlexoEOModelResource.class.getPackage().getName());

	private DMModel _dmModel;

	/**
	 * Constructor used for XML Serialization: never try to instanciate resource from this constructor
	 * 
	 * @param builder
	 */
	public FlexoEOModelResource(FlexoProjectBuilder builder) {
		this(builder.project);
		builder.notifyResourceLoading(this);
	}

	public FlexoEOModelResource(FlexoProject aProject) {
		super(aProject);
	}

	public FlexoEOModelResource(FlexoProject aProject, FlexoDMResource dmResource, FlexoProjectFile eoModelFile)
			throws InvalidFileNameException {
		this(aProject);
		setResourceFile(eoModelFile);
		addToSynchronizedResources(dmResource);
		if (logger.isLoggable(Level.INFO))
			logger.info("Build new FlexoEOModelResource");
	}

	public FlexoEOModelResource(FlexoProject aProject, EOModelResourceData anEOModelResourceData, FlexoDMResource dmResource,
			FlexoProjectFile eoModelFile) throws InvalidFileNameException {
		this(aProject, dmResource, eoModelFile);
		_resourceData = anEOModelResourceData;
		anEOModelResourceData.setFlexoResource(this);
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.EOMODEL;
	}

	@Override
	public String getName() {
		if (getFile() != null)
			return getFile().getName();
		return null;
	}

	@Override
	public void setName(String aName) {
		// Not allowed
	}

	public Class getResourceDataClass() {
		return EOModelResourceData.class;
	}

	@Override
	public EOModelResourceData performLoadResourceData(FlexoProgress progress, ProjectLoadingHandler loadingHandler)
			throws LoadResourceException {
		EOModel eoModel = null;
		try {
			eoModel = loadEOModel(getModelGroup());
		} catch (InvalidEOModelFileException e) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("Invalid EOModel, remove resource !");
			this.delete();
		} catch (EOAccessException e) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING))
				logger.warning("EOAccessException raised: " + e.getMessage());
			throw new LoadEOModelException(this, e);
		}
		_resourceData = new EOModelResourceData(project, this);
		_resourceData.setEOModel(eoModel);
		notifyResourceStatusChanged();
		return _resourceData;
	}

	public EOModelResourceData createsNewEOModel(DMEOAdaptorType adaptor, String username, String passwd, String databaseServerURL,
			String plugin, String driver) throws InvalidEOModelFileException {
		EOModel eoModel = createsEOModel(getModelGroup(), adaptor, username, passwd, databaseServerURL, plugin, driver);
		_resourceData = new EOModelResourceData(project, this);
		_resourceData.setEOModel(eoModel);
		_resourceData.setIsModified();
		notifyResourceStatusChanged();
		return _resourceData;
	}

	private EOModel createsEOModel(EOModelGroup modelGroup, DMEOAdaptorType adaptor, String username, String passwd,
			String databaseServerURL, String plugin, String driver) throws InvalidEOModelFileException {
		URL eoModelUrl;
		try {
			eoModelUrl = getFile().toURL();
		} catch (MalformedURLException e) {
			throw new InvalidEOModelFileException(this);
		}
		EOModel newEOModel = new EOModel();
		String eoModelName = getName().substring(0, getName().lastIndexOf(".eomodeld"));
		newEOModel.setName(eoModelName);
		newEOModel.setAdaptorName(adaptor.getName());
		newEOModel.setConnectionDictionary(adaptor.getDefaultConnectionDictionary(username, passwd, databaseServerURL, plugin, driver));
		modelGroup.addModel(newEOModel);
		return newEOModel;
	}

	public EOModel reloadEOModel() throws EOAccessException {
		if (_resourceData == null) {
			// GPO: Without the will to be too sarcastic but I am pretty sure that nobody has ever come into this block...
			return _resourceData.getEOModel();
		} else {
			EOModel oldEOModel = _resourceData.getEOModel();
			if (oldEOModel != null) {
				getModelGroup().removeModel(oldEOModel);
			}
			_resourceData.setEOModel(null);
			try {
				EOModel eoModel = loadEOModel(getModelGroup());
				_resourceData.setEOModel(eoModel);
				notifyResourceStatusChanged();
				return eoModel;
			} catch (InvalidEOModelFileException e) {
				if (logger.isLoggable(Level.WARNING))
					logger.warning("Invalid EOModel, remove resource !");
				this.delete();
			}
			notifyResourceStatusChanged();
			return null;
		}
	}

	private EOModel loadEOModel(EOModelGroup modelGroup) throws InvalidEOModelFileException, EOAccessException {
		if (getFile() != null) {
			if (getFile().exists()) {
				try {
					if (logger.isLoggable(Level.FINE))
						logger.fine("Loading EOMODEL with EOModelGroup " + modelGroup);
					EOModel eoModel = modelGroup.addModel(getFile());
					if (eoModel == null) {
						if (logger.isLoggable(Level.WARNING))
							logger.warning("Error while loading model:" + getFile().getName());
						throw new InvalidEOModelFileException(this);
					} else {
						if (logger.isLoggable(Level.INFO))
							logger.info("Succesfully loaded:" + getFile().getName());
						return eoModel;
					}
				} catch (IllegalArgumentException e) {
					if (logger.isLoggable(Level.WARNING))
						logger.warning("Could not load EOModel" + getFile().getName() + " Exception: " + e.getMessage());
					throw new EOAccessException(e);
				} catch (IllegalStateException e) {
					if (logger.isLoggable(Level.WARNING))
						logger.warning("Could not load EOModel" + getFile().getName() + " Exception: " + e.getMessage());
					throw new EOAccessException(e);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					throw new EOAccessException(e);
				} catch (PropertyListDeserializationException e) {
					e.printStackTrace();
					throw new EOAccessException(e);
				}
			} else {
				if (logger.isLoggable(Level.WARNING))
					logger.warning("Could not load EOModel: EOModel file " + getFile().getName() + " does not exist !");
				throw new InvalidEOModelFileException(this);
			}
		} else {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("Could not load EOModel: EOModel file not set !");
			throw new InvalidEOModelFileException(null);
		}
	}

	public EOModelResourceData getEOModelResourceData() {
		return getResourceData();
	}

	/**
	 * Overrides hasWritePermission
	 * 
	 * @see org.openflexo.foundation.rm.FlexoFileResource#hasWritePermission()
	 */
	@Override
	public synchronized boolean hasWritePermission() {
		if (!getFile().canWrite()) {
			if (logger.isLoggable(Level.INFO))
				logger.info("Cannot Write :" + getFile().getAbsolutePath());
			return false;
		}
		if (!getFile().isDirectory()) {
			if (logger.isLoggable(Level.INFO))
				logger.info("Not a directory :" + getFile().getAbsolutePath());
			return false;
		}
		File[] f = getFile().listFiles();
		if (f == null) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("Cannot list files inside directory: " + getFile().getAbsolutePath());
			return false;
		}
		for (int i = 0; i < f.length; i++) {
			File file = f[i];
			if (!file.canWrite()) {
				if (logger.isLoggable(Level.INFO))
					logger.info("Cannot write this file: " + file.getAbsolutePath());
				return false;
			}
		}
		return true;
	}

	/**
	 * Implements
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResource#saveResourceData()
	 * @see org.openflexo.foundation.rm.FlexoResource#saveResourceData()
	 */
	@Override
	protected void saveResourceData(boolean clearIsModified) throws SaveResourceException {
		if (!hasWritePermission()) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("Permission denied : " + getFile().getAbsolutePath());
			throw new SaveResourcePermissionDeniedException(this);
		}
		if (_resourceData != null) {
			FileWritingLock lock = willWriteOnDisk();
			try {
				getEOModelResourceData().getEOModel().writeToFile(getFile());
			} catch (IOException e) {
				throw new SaveResourceException(null) {

				};
			}
			hasWrittenOnDisk(lock);
			notifyResourceStatusChanged();
			if (logger.isLoggable(Level.INFO))
				logger.info("Succeeding to save Resource " + getResourceIdentifier() + " : " + getFile().getName());
		}
		if (clearIsModified)
			getResourceData().clearIsModified(false);
	}

	public EOModelGroup getModelGroup() {
		return getDMModel().getModelGroup();
	}

	public DMModel getDMModel() {
		if (_dmModel == null) {
			_dmModel = project.getDataModel();
		}
		return _dmModel;
	}

	public void setDMModel(DMModel dmModel) {
		_dmModel = dmModel;
	}

	/**
	 * Rebuild resource dependancies for this resource
	 */
	@Override
	public void rebuildDependancies() {
		super.rebuildDependancies();
		addToSynchronizedResources(getProject().getFlexoDMResource());
	}

	/**
	 * Perform update of specified resource data from data read from a updated resource on disk NOTE 1: If no custom scheme is defined, disk
	 * update will force the entire project to be reloaded NOTE 2: must be overriden in subclasses if relevant
	 */
	@Override
	public synchronized void performDiskUpdate() throws EOAccessException {
		EOModel eoModel = getEOModelResourceData().getEOModel();
		DMEOModel dmEOModel = getProject().getDataModel().getDMEOModel(eoModel);
		// First read the EOModel and replace it in data
		dmEOModel.reloadEOModel();
		// Update then the DataModel according to the newly read update
		dmEOModel.updateFromEOModel();
	}

}
