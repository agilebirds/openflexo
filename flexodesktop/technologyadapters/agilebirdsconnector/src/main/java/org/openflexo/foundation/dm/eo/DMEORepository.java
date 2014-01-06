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

import java.io.File;
import java.io.IOException;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.DMRepository;
import org.openflexo.foundation.dm.dm.EOModelRegistered;
import org.openflexo.foundation.dm.dm.EOModelUnregistered;
import org.openflexo.foundation.dm.eo.model.EOEntity;
import org.openflexo.foundation.dm.eo.model.EOModel;
import org.openflexo.foundation.dm.eo.model.EOModelGroup;
import org.openflexo.foundation.resource.InvalidFileNameException;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoDMResource;
import org.openflexo.foundation.rm.ProjectRestructuration;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.toolbox.FileUtils;

/**
 * Represents a database implementation for a group of objects stored in a list of DMEOModel Contains a list of DMEOModel
 * 
 * @author sguerin
 * 
 */
public abstract class DMEORepository extends DMRepository {

	static final Logger logger = Logger.getLogger(DMEORepository.class.getPackage().getName());

	// ==========================================================================
	// ============================= Instance variables
	// =========================
	// ==========================================================================

	private DMEOModelHashtable _DMEOModels;

	private Hashtable<EOEntity, DMEOEntity> _entitiesForEOEntity;

	private String entitySubPath;

	private class DMEOModelHashtable extends Hashtable<String, DMEOModel> {
		public DMEOModelHashtable() {
			super();
		}

		public DMEOModelHashtable(Hashtable<String, DMEOModel> ht) {
			super(ht);
		}

		@Override
		public Enumeration<String> keys() {
			if (isSerializing()) {
				// Order keys in this case
				Vector<String> orderedKeys = new Vector<String>();
				for (Enumeration<String> en = super.keys(); en.hasMoreElements();) {
					orderedKeys.add(en.nextElement());
				}
				Collections.sort(orderedKeys, new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						return Collator.getInstance().compare(o1, o2);
					}
				});
				return orderedKeys.elements();
			}
			return super.keys();
		}
	}

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Default constructor
	 */
	public DMEORepository(DMModel dmModel) {
		super(dmModel);
		_DMEOModels = new DMEOModelHashtable();
		_entitiesForEOEntity = new Hashtable<EOEntity, DMEOEntity>();
	}

	public Hashtable<String, DMEOModel> getDMEOModels() {
		return _DMEOModels;
	}

	public void setDMEOModels(Hashtable<String, DMEOModel> eoModels) {
		// Transtype to DMEntityHashtable
		_DMEOModels = new DMEOModelHashtable(eoModels);
		needsReordering = true;
		setChanged();
	}

	public void setDMEOModelForKey(DMEOModel eoModel, String eoModelName) {
		eoModel.setRepository(this);
		_DMEOModels.put(eoModelName, eoModel);
		needsReordering = true;
	}

	public void removeDMEOModelWithKey(String eoModelName) {
		_DMEOModels.remove(eoModelName);
		needsReordering = true;
		setChanged();
	}

	/**
	 * Overrides getEmbeddedDMObjects
	 * 
	 * @see org.openflexo.foundation.dm.DMRepository#getEmbeddedDMObjects()
	 */
	@Override
	public Vector<DMObject> getEmbeddedDMObjects() {
		Vector<DMObject> v = super.getEmbeddedDMObjects();
		v.addAll(getDMEOModels().values());
		return v;
	}

	public void internallyRegisterDMEOEntity(DMEOEntity dmEOEntity) {
		if (dmEOEntity.getEOEntity() != null) {
			if (_entitiesForEOEntity.get(dmEOEntity.getEOEntity()) != null) {
				if (_entitiesForEOEntity.get(dmEOEntity.getEOEntity()) != dmEOEntity) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Trying to redefine DMEOEntity: operation not allowed !");
					}
				} else {
					// Ignore it !
				}
			} else {
				_entitiesForEOEntity.put(dmEOEntity.getEOEntity(), dmEOEntity);
			}
		}
	}

	private void internallyUnregisterDMEOEntity(DMEOEntity dmEOEntity) {
		if (dmEOEntity.getEOEntity() != null) {
			_entitiesForEOEntity.remove(dmEOEntity.getEOEntity());
		}

	}

	@Override
	public void setEntityForKey(DMEntity entity, String entityName) {
		if (entity instanceof DMEOEntity) {
			DMEOEntity dmEOEntity = (DMEOEntity) entity;
			internallyRegisterDMEOEntity(dmEOEntity);
			if (dmEOEntity.getDMEOModel() != null) {
				dmEOEntity.getDMEOModel().registerEntity(dmEOEntity);
			}
		}
		super.setEntityForKey(entity, entityName);
	}

	@Override
	public void removeEntityWithKey(String entityName, boolean notify) {
		DMEntity entity = getDMEntity(entityName);
		if (entity != null && entity instanceof DMEOEntity) {
			DMEOEntity dmEOEntity = (DMEOEntity) entity;
			internallyUnregisterDMEOEntity(dmEOEntity);
			if (dmEOEntity.getDMEOModel() != null) {
				dmEOEntity.getDMEOModel().unregisterEntity(dmEOEntity, notify);
			}
		}
		super.removeEntityWithKey(entityName, notify);
	}

	public DMEOEntity getDMEOEntity(EOEntity eoEntity) {
		return _entitiesForEOEntity.get(eoEntity);
	}

	public DMEOModel importEOModelFile(FlexoProjectFile eoModelDir, DMModel dmModel, FlexoDMResource dmRes)
			throws InvalidEOModelFileException, EOModelAlreadyRegisteredException, InvalidFileNameException {
		DMEOModel newEOModel = null;
		try {
			newEOModel = DMEOModel.createsNewDMEOModelFromExistingEOModel(dmModel, this, eoModelDir, dmRes);
			EOModel model = newEOModel.loadEOModel();
			if (model == null) {
				if (newEOModel.getEOModelResource() != null) {
					getProject().removeResource(newEOModel.getEOModelResource());
				}
				throw new InvalidEOModelFileException(newEOModel.getEOModelResource());
			}
			registerEOModel(newEOModel);
			return newEOModel;
		} catch (DuplicateResourceException e) {
			throw new EOModelAlreadyRegisteredException(eoModelDir);
		}
	}

	public DMEOModel importEOModelFile(FlexoProjectFile eoModelDir) throws InvalidEOModelFileException, EOModelAlreadyRegisteredException,
			InvalidFileNameException {
		return importEOModelFile(eoModelDir, getDMModel(), null);
	}

	public DMEOModel copyAndImportEOModel(File eoModelFile) throws EOAccessException, InvalidEOModelFileException,
			EOModelAlreadyRegisteredException, InvalidFileNameException, UnresovedEntitiesException {
		DMEOModel importedDMEOModel = null;
		File dataModelDir = ProjectRestructuration.getExpectedDataModelDirectory(getProject().getProjectDirectory());
		if (!dataModelDir.equals(eoModelFile.getParentFile())) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Copying file " + eoModelFile.getAbsolutePath() + " to " + dataModelDir.getAbsolutePath());
			}
			File dest = new File(dataModelDir, eoModelFile.getName());
			for (DMEOModel model : getDMModel().getAllDMEOModel()) {
				if (model.getEOModelFile().getFile().equals(dest)) {
					throw new EOModelAlreadyRegisteredException(model.getEOModelFile());
				}
			}
			try {
				FileUtils.copyDirToDir(eoModelFile, dataModelDir);
			} catch (IOException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not copy file " + eoModelFile.getAbsolutePath() + " to " + dataModelDir.getAbsolutePath());
				}
			}
		}
		try {
			FlexoProjectFile eoModelDirectory = new FlexoProjectFile(new File(dataModelDir, eoModelFile.getName()), getProject());
			importedDMEOModel = importEOModelFile(eoModelDirectory);
			importedDMEOModel.updateFromEOModel();
			if (importedDMEOModel.getEOModel().getMissingEntities().size() > 0) {
				throw new UnresovedEntitiesException(importedDMEOModel.getEOModel().getMissingEntities());
			}
		} catch (InvalidEOModelFileException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not import EOModel:" + dataModelDir.getName());
			}
			if (importedDMEOModel != null) {
				importedDMEOModel.delete();
			}
			throw e;
		} catch (EOAccessException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not import EOModel:" + dataModelDir.getName());
			}
			if (importedDMEOModel != null) {
				importedDMEOModel.delete();
			}
			throw e;
		} catch (EOModelAlreadyRegisteredException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not import EOModel:" + dataModelDir.getName());
			}
			if (importedDMEOModel != null) {
				importedDMEOModel.delete();
			}
			throw e;
		}
		return importedDMEOModel;
	}

	public DMEOModel createsNewEOModel(File eoModelFile, DMEOAdaptorType adaptor, String username, String passwd, String databaseServerURL,
			String plugin, String driver) throws InvalidEOModelFileException, DuplicateResourceException, InvalidFileNameException,
			IOException {
		File dataModelDir = ProjectRestructuration.getExpectedDataModelDirectory(getProject().getProjectDirectory());
		FlexoProjectFile eoModelDirectory = new FlexoProjectFile(new File(dataModelDir, eoModelFile.getName()), getProject());
		eoModelDirectory.getFile().mkdirs();
		DMEOModel newEOModel;
		newEOModel = DMEOModel.createsNewDMEOModelForNewEOModel(getDMModel(), this, eoModelDirectory, adaptor, username, passwd,
				databaseServerURL, plugin, driver);
		registerEOModel(newEOModel);
		return newEOModel;
	}

	public void registerEOModel(DMEOModel eoModel) {
		eoModel.setRepository(this);
		if (_DMEOModels.get(eoModel.getName()) == null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Register EOModel " + eoModel.getName());
			}
			setDMEOModelForKey(eoModel, eoModel.getName());
			setChanged();
			notifyObservers(new EOModelRegistered(eoModel));
		} else if (eoModel != _DMEOModels.get(eoModel.getName())) {
			eoModel.setRepository(null);
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Trying to redefine EOModel " + eoModel.getName() + ": operation not allowed !");
			}
		}
	}

	public void unregisterEOModel(DMEOModel eoModel) {
		removeDMEOModelWithKey(eoModel.getName());
		eoModel.setRepository(null);
		setChanged();
		notifyObservers(new EOModelUnregistered(eoModel));
	}

	protected void renameEOModel(DMEOModel eoModel, String oldName, String newName) {
		if (_DMEOModels.get(oldName + ".eomodeld") == eoModel) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Rename EOModel from " + oldName + ".eomodeld" + " to " + newName + ".eomodeld");
			}
			removeDMEOModelWithKey(oldName + ".eomodeld");
			setDMEOModelForKey(eoModel, newName + ".eomodeld");
		} else {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("oldName=" + oldName);
				logger.fine("newName=" + newName);
				logger.fine("_DMEOModels.get(oldName)=" + _DMEOModels.get(oldName));
				logger.fine("eoModel=" + eoModel);
			}
			logger.warning("Inconsistant data when trying to rename EOModel from " + oldName + " to " + newName);
		}
	}

	public EOModelGroup getModelGroup() {
		return getDMModel().getModelGroup();
	}

	/**
	 * Return all EOModels declared for this repository, as a Vector of DMEOModels
	 * 
	 * @return
	 */
	public Vector<DMEOModel> getOrderedDMEOModels() {
		return (Vector<DMEOModel>) getOrderedChildren();
	}

	@Override
	public final boolean delete() {
		return delete(false);
	}

	public boolean delete(boolean deleteEOModelFiles) {
		Vector eoModelsToDelete = new Vector();
		eoModelsToDelete.addAll(getDMEOModels().values());
		for (Enumeration en = eoModelsToDelete.elements(); en.hasMoreElements();) {
			DMEOModel next = (DMEOModel) en.nextElement();
			next.delete(deleteEOModelFiles);
		}
		_DMEOModels.clear();
		_DMEOModels = null;
		_entitiesForEOEntity.clear();
		_entitiesForEOEntity = null;
		return super.delete();
		// Observers are deleted in super implementation
		// deleteObservers();
	}

	// ==========================================================================
	// ============================= Sorting stuff
	// ==============================
	// ==========================================================================

	private Vector<DMEOModel> orderedEOModels;

	private boolean needsReordering = true;

	@Override
	public Vector<? extends DMObject> getOrderedChildren() {
		if (needsReordering) {
			reorderEOModels();
		}
		return orderedEOModels;
	}

	private void reorderEOModels() {
		if (needsReordering) {
			if (orderedEOModels != null) {
				orderedEOModels.removeAllElements();
			} else {
				orderedEOModels = new Vector<DMEOModel>();
			}
			orderedEOModels.addAll(_DMEOModels.values());
			Collections.sort(orderedEOModels, eoModelComparator);
			needsReordering = false;
		}
	}

	protected static final EOModelComparator eoModelComparator = new EOModelComparator();

	/**
	 * Used to sort entities according to name alphabetic ordering
	 * 
	 * @author sguerin
	 * 
	 */
	protected static class EOModelComparator implements Comparator<DMEOModel> {

		/**
		 * Implements
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(DMEOModel o1, DMEOModel o2) {

			String s1 = o1.getName();
			String s2 = o2.getName();
			if (s1 != null && s2 != null) {
				return Collator.getInstance().compare(s1, s2);
			} else {
				return 0;
			}
		}

	}

	@Override
	public void notifyReordering(DMObject cause) {
		needsReordering = true;
		super.notifyReordering(cause);
	}

	public String getEntitySubPath() {
		if (entitySubPath == null) {
			entitySubPath = "src/main/java/EO" + FileUtils.getValidFileName(this.getName());
		}
		return entitySubPath;
	}

	public void setEntitySubPath(String entitySubPath) {
		String old = this.entitySubPath;
		this.entitySubPath = entitySubPath;
		setChanged();
		notifyObservers(new WKFAttributeDataModification("entitySubPath", old, entitySubPath));
	}

	public Hashtable<EOEntity, DMEOEntity> getEntitiesForEOEntity() {
		return _entitiesForEOEntity;
	}

}
