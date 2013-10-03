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
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.DMPackage;
import org.openflexo.foundation.dm.dm.DMAttributeDataModification;
import org.openflexo.foundation.dm.dm.EOEntityInserted;
import org.openflexo.foundation.dm.dm.EntityRegistered;
import org.openflexo.foundation.dm.dm.EntityUnregistered;
import org.openflexo.foundation.dm.eo.model.EOEntity;
import org.openflexo.foundation.dm.eo.model.EOModel;
import org.openflexo.foundation.dm.eo.model.EOModelGroup;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoDMResource;
import org.openflexo.foundation.rm.FlexoEOModelResource;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.stats.DMEOModelStatistics;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.foundation.xml.FlexoDMBuilder;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.ImageIconResource;
import org.openflexo.toolbox.ToolBox;

/**
 * Represents a database implementation for a group of objects (maps an EOModel)
 * 
 * @author sguerin
 * 
 */
public class DMEOModel extends DMObject implements DMEOObject {

	static final Logger logger = Logger.getLogger(DMEOModel.class.getPackage().getName());

	public static final EOModelFileFilter EOMODEL_FILE_FILTER = new EOModelFileFilter();

	public static final EOModelFileView EOMODEL_FILE_VIEW = new EOModelFileView();

	// ==========================================================================
	// ============================= Instance variables
	// =========================
	// ==========================================================================

	protected DMEORepository _repository;

	protected FlexoEOModelResource _eoModelResource;

	// List of all entities declared in this eomodel (hashtable of DMEOEntity)
	private final Hashtable<String, DMEOEntity> entities;

	private String entitySubPath;

	private DMEOModelStatistics statistics;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Constructor used during deserialization
	 */
	public DMEOModel(FlexoDMBuilder builder) {
		this(builder.dmModel);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor: never use it
	 */
	private DMEOModel(DMModel dmModel) {
		super(dmModel);
		entities = new Hashtable<String, DMEOEntity>();
	}

	/**
	 * Constructor for dynamic creation: the resource is explicitely created and registered
	 * 
	 * @throws DuplicateResourceException
	 * @throws InvalidFileNameException
	 */
	private DMEOModel(DMModel dmModel, DMEORepository repository, FlexoProjectFile eoModelFile, FlexoDMResource dmRes)
			throws InvalidEOModelFileException, DuplicateResourceException, InvalidFileNameException {
		this(dmModel);
		_repository = repository;
		FlexoDMResource dataModelResource = dmRes;
		if (dataModelResource == null) {
			dataModelResource = getProject().getFlexoDMResource();
		}
		_eoModelResource = new FlexoEOModelResource(getProject(), getProject().getServiceManager(), dataModelResource, eoModelFile);
		_eoModelResource.setDMModel(dmModel);
		getProject().registerResource(_eoModelResource);
		// EOModel reply = _eoModelResource.createsNewEOModel(adaptor, username, passwd, databaseServerURL, plugin, driver).getEOModel();
		// reply.writeToFile(_eoModelResource.getFile());

	}

	/**
	 * Constructor for dynamic creation: the resource is explicitely created and registered
	 * 
	 * @throws DuplicateResourceException
	 * @throws InvalidFileNameException
	 */
	public static DMEOModel createsNewDMEOModelFromExistingEOModel(DMModel dmModel, DMEORepository repository,
			FlexoProjectFile eoModelFile, FlexoDMResource dmRes) throws InvalidEOModelFileException, DuplicateResourceException,
			InvalidFileNameException {
		DMEOModel reply = new DMEOModel(dmModel, repository, eoModelFile, dmRes);
		reply.setEOModelFile(eoModelFile);
		return reply;
	}

	/**
	 * Constructor for dynamic creation: the resource is explicitely created and registered and the EOModel is created from scratch
	 * 
	 * @throws DuplicateResourceException
	 * @throws InvalidFileNameException
	 * @throws IOException
	 */
	public static DMEOModel createsNewDMEOModelForNewEOModel(DMModel dmModel, DMEORepository repository, FlexoProjectFile eoModelFile,
			DMEOAdaptorType adaptor, String username, String passwd, String databaseServerURL, String plugin, String driver)
			throws InvalidEOModelFileException, DuplicateResourceException, InvalidFileNameException, IOException {
		DMEOModel returned = new DMEOModel(dmModel, repository, eoModelFile, null);
		returned.createsEOModel(adaptor, username, passwd, databaseServerURL, plugin, driver);
		returned.setEOModelFile(eoModelFile);
		return returned;
	}

	@Override
	public final boolean delete() {
		return delete(false);
	}

	public String derivePackageName() {
		return "org.openflexo." + getProject().getPrefix().toLowerCase() + "."
				+ ToolBox.convertStringToJavaString(getNameWithoutSuffix()).toLowerCase() + ".db";
	}

	public final boolean delete(boolean deleteFile) {
		if (getEOModel() != null) {
			try {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Remove model " + getEOModel().getName() + " from ModelGroup");
				}
				getDMModel().getModelGroup().removeModel(getEOModel());
			} catch (IllegalArgumentException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("EOAccess management failed :" + e.getMessage());
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("EOAccess management failed : unexpected NullPointerException in EOF layer : " + e.getMessage());
				}
			}
		}
		Vector<DMEOEntity> entitiesToDelete = new Vector<DMEOEntity>();
		entitiesToDelete.addAll(getEntities());
		for (Enumeration<DMEOEntity> en = entitiesToDelete.elements(); en.hasMoreElements();) {
			en.nextElement().delete();
		}
		if (getRepository() != null) {
			getRepository().unregisterEOModel(this);
		}
		if (getEOModelResource() != null) {
			getEOModelResource().delete(deleteFile);
		}
		_eoModelResource = null;
		super.delete();
		deleteObservers();
		return true;
	}

	@Override
	public String getFullyQualifiedName() {
		if (getRepository() != null) {
			return getRepository().getFullyQualifiedName() + "." + getName();
		} else {
			return "null" + "." + getName();
		}
	}

	@Override
	public DMEOModel getDMEOModel() {
		return this;
	}

	/**
	 * Return String uniquely identifying inspector template which must be applied when trying to inspect this object
	 * 
	 * @return a String value
	 */
	@Override
	public String getInspectorName() {
		return Inspectors.DM.DM_EO_MODEL_INSPECTOR;
	}

	/**
	 * Return a Vector of embedded DMObjects at this level.
	 * 
	 * @return a Vector of embedded DMEOEntity instances
	 */
	@Override
	public Vector<DMEOEntity> getEmbeddedDMObjects() {
		Vector<DMEOEntity> returned = new Vector<DMEOEntity>();
		returned.addAll(getOrderedChildren());
		return returned;
	}

	@Override
	public String getName() {
		if (getEOModel() != null) {
			return getEOModel().getName() + ".eomodeld";
		} else if (getEOModelFile() != null && getEOModelFile().getFile() != null) {
			return getEOModelFile().getFile().getName();
		} else {
			return "???";
		}
	}

	/**
	 * Overrides isNameValid
	 * 
	 * @see org.openflexo.foundation.dm.DMObject#isNameValid()
	 */
	@Override
	public boolean isNameValid() {
		return true;
	}

	public String getNameWithoutSuffix() {
		if (getEOModel() != null) {
			return getEOModel().getName();
		} else if (getEOModelFile() != null && getEOModelFile().getFile() != null) {
			String name = getEOModelFile().getFile().getName();
			if (name.lastIndexOf(".eomodeld") > -1) {
				name = name.substring(0, name.lastIndexOf(".eomodeld"));
			}
			return name;
		} else {
			return "???";
		}
	}

	public boolean isReadOnly() {
		return isPrototypes() || isExecutionModel();
	}

	public boolean isPrototypes() {
		return getNameWithoutSuffix().equals("EOPrototypes");
	}

	public boolean isNotPrototypes() {
		return !isPrototypes();
	}

	public boolean isExecutionModel() {
		return getRepository() == getDMModel().getExecutionModelRepository();
	}

	public boolean isNotExecutionModel() {
		return !isExecutionModel();
	}

	@Override
	public void setName(String aName) throws FlexoException {
		if (getEOModel() != null) {
			String oldName = getEOModel().getName();
			int ind = aName.lastIndexOf(".eomodeld");
			if (ind > -1) {
				aName = aName.substring(0, ind);
			}
			if (!isDeserializing()) {
				getRepository().renameEOModel(this, oldName, aName);
				try {
					getEOModelResource().renameFileTo(aName + ".eomodeld");
					getProject().renameResource(getEOModelResource(), getEOModelResource().getName());
				} catch (InvalidFileNameException e) {
					// TODO: gerer ca mieux un jour
					e.printStackTrace();
					logger.warning("Unexpected InvalidFileNameException raised when trying to rename EOModel from " + oldName + " to "
							+ aName);
					throw new FlexoException(FlexoLocalization.localizedForKey("invalid_name"), e);
				} catch (DuplicateResourceException e) {
					// TODO: gerer ca mieux un jour
					e.printStackTrace();
					logger.warning("Unexpected DuplicateResourceException raised when trying to rename EOModel from " + oldName + " to "
							+ aName);
					throw new FlexoException(FlexoLocalization.localizedForKey("duplicate_name"), e);
				} catch (IOException e) {
					e.printStackTrace();
					throw new FlexoException(FlexoLocalization.localizedForKey("io_exception") + " " + e.getMessage(), e);
				}
			}
			getEOModel().setName(aName);
			if (!isDeserializing()) {
				setChanged();
				notifyObservers(new DMAttributeDataModification("name", oldName, aName));
				setChanged();
				notifyObservers(new DMAttributeDataModification("EOModelFile", oldName, aName));
			}

			if (getRepository() != null) {
				getRepository().notifyReordering(this);
			}
		}
	}

	@Override
	public boolean isDeletable() {
		return !getRepository().isReadOnly();
	}

	public DMEORepository getRepository() {
		return _repository;
	}

	public void setRepository(DMEORepository repository) {
		_repository = repository;
	}

	public FlexoEOModelResource getEOModelResource() {
		return _eoModelResource;
	}

	public void setEOModelResource(FlexoEOModelResource eoModelResource) {
		_eoModelResource = eoModelResource;
	}

	public EOModelResourceData getEOModelResourceData() {
		if (getEOModelResource() != null) {
			return getEOModelResource().getEOModelResourceData();
		}
		return null;
	}

	public FlexoProjectFile getEOModelFile() {
		if (getEOModelResource() != null) {
			return getEOModelResource().getResourceFile();
		}
		return null;
	}

	public void setEOModelFile(FlexoProjectFile eoModelFile) throws InvalidEOModelFileException {
		eoModelFile.setProject(getProject());
		if (_eoModelResource == null) {
			_eoModelResource = getProject().getEOModelResource(eoModelFile);
			if (_eoModelResource == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not find resource for EOMODEL " + eoModelFile.getFile().getAbsolutePath());
				}
				return;
			} else {
				_eoModelResource.setDMModel(getDMModel());
			}
		}
		if (!eoModelFile.getFile().exists()) {
			throw new InvalidEOModelFileException(_eoModelResource);
		} else if (!new File(eoModelFile.getFile(), EOModel.INDEX_EOMODELD).exists()) {
			throw new InvalidEOModelFileException(_eoModelResource);
		}
	}

	public EOModel getEOModel() {
		if (getEOModelResource() != null && getEOModelResourceData() != null) {
			return getEOModelResourceData().getEOModel();
		}
		/*
		 * else { if (logger.isLoggable(Level.WARNING)) logger.warning("Unable to access EOMODEL "); }
		 */
		return null;
	}

	public DMEOAdaptorType getAdaptorType() {
		if (getEOModel() != null) {
			return DMEOAdaptorType.get(getEOModel().getAdaptorName());
		}
		return null;
	}

	public void setAdaptorType(DMEOAdaptorType adaptor) {
		if (getEOModel() != null) {
			getEOModel().setAdaptorName(adaptor.getName());
			setChanged();
		}
	}

	public String getDatabaseServer() {
		if (getEOModel() != null && getConnectionDictionary() != null) {
			return (String) getConnectionDictionary().get(DMEOAdaptorType.DATABASE_SERVER);
		}
		return null;
	}

	public void setDatabaseServer(String server) {
		if (getEOModel() != null && getConnectionDictionary() != null) {
			getConnectionDictionary().put(DMEOAdaptorType.DATABASE_SERVER, server);
			setChanged();
		}
	}

	public String getUsername() {
		if (getEOModel() != null && getConnectionDictionary() != null) {
			return (String) getConnectionDictionary().get(DMEOAdaptorType.USERNAME);
		}
		return null;
	}

	public void setUsername(String username) {
		if (getEOModel() != null && getConnectionDictionary() != null) {
			getConnectionDictionary().put(DMEOAdaptorType.USERNAME, username);
			setChanged();
		}
	}

	public String getPasswd() {
		if (getEOModel() != null && getConnectionDictionary() != null) {
			return (String) getConnectionDictionary().get(DMEOAdaptorType.PASSWORD);
		}
		return null;
	}

	public void setPasswd(String password) {
		if (getEOModel() != null && getConnectionDictionary() != null) {
			getConnectionDictionary().put(DMEOAdaptorType.PASSWORD, password);
			setChanged();
		}
	}

	public String getPlugin() {
		if (getEOModel() != null && getConnectionDictionary() != null) {
			return (String) getConnectionDictionary().get(DMEOAdaptorType.PLUGIN);
		}
		return null;
	}

	public void setPlugin(String plugin) {
		if (getEOModel() != null && getConnectionDictionary() != null) {
			getConnectionDictionary().put(DMEOAdaptorType.PLUGIN, plugin);
			setChanged();
		}
	}

	public String getDriver() {
		if (getEOModel() != null && getConnectionDictionary() != null) {
			return (String) getConnectionDictionary().get(DMEOAdaptorType.DRIVER);
		}
		return null;
	}

	public void setDriver(String driver) {
		if (getEOModel() != null && getConnectionDictionary() != null) {
			getConnectionDictionary().put(DMEOAdaptorType.DRIVER, driver);
			setChanged();
		}
	}

	public Map<String, Object> getConnectionDictionary() {
		if (getEOModel() != null) {
			return getEOModel().getConnectionDictionary();
		}
		return null;

	}

	public void setConnectionDictionary(Map<String, Object> aConnectionDictionary) {
		if (getEOModel() != null) {
			getEOModel().setConnectionDictionary(aConnectionDictionary);
		}
	}

	/**
	 * Return all the entities contained in this eomodel, as a Vector of DMEntity
	 * 
	 * @return a Vector of DMEOEntity
	 */
	public Vector<DMEOEntity> getEntities() {
		return getOrderedChildren();
	}

	public void registerEntity(DMEOEntity entity) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Register Entity " + entity.getFullyQualifiedName());
		}
		if (entities.get(entity.getFullyQualifiedName()) == null) {
			entities.put(entity.getFullyQualifiedName(), entity);
			needsReordering = true;
			setChanged();
			notifyObservers(new EntityRegistered(entity));
		} else if (entity != entities.get(entity.getFullyQualifiedName())) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Trying to redefine entity " + entity.getFullyQualifiedName() + ": operation not allowed !");
			}
		}
	}

	public void unregisterEntity(DMEOEntity entity, boolean notify) {
		entities.remove(entity.getFullyQualifiedName());
		needsReordering = true;
		if (notify) {
			setChanged();
			notifyObservers(new EntityUnregistered(entity));
		}
	}

	// ==========================================================================
	// =================== EOModel/EODMModel synchronisation
	// ====================
	// ==========================================================================

	public EOModelGroup getModelGroup() {
		return getRepository().getModelGroup();
	}

	public EOModel loadEOModel() throws InvalidEOModelFileException {
		if (_eoModelResource != null && _eoModelResource.getEOModelResourceData() != null) {
			return _eoModelResource.getEOModelResourceData().getEOModel();
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not load EOModel: resource is not set !");
			}
		}
		return null;
	}

	private EOModel createsEOModel(DMEOAdaptorType adaptor, String username, String passwd, String databaseServerURL, String plugin,
			String driver) throws InvalidEOModelFileException, IOException {
		if (_eoModelResource != null) {
			EOModel reply = _eoModelResource.createsNewEOModel(adaptor, username, passwd, databaseServerURL, plugin, driver).getEOModel();
			reply.writeToFile(_eoModelResource.getFile());
			return reply;
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not creates EOModel: resource is not set !");
			}
		}
		return null;
	}

	public EOModel reloadEOModel() throws EOAccessException {
		if (_eoModelResource != null) {
			return _eoModelResource.reloadEOModel();
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not reload EOModel: resource is not set !");
			}
		}
		return null;
	}

	public void updateFromEOModel() throws EOAccessException {
		if (getEOModel() != null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("updateFromEOModel()");
			}
			try {
				getEOModel().loadAllModelObjects();
			} catch (IllegalArgumentException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("EOAccess management failed :" + e.getMessage());
				}
				throw new EOAccessException(e);
			}

			Vector<DMEOEntity> entitiesToDelete = new Vector<DMEOEntity>();
			entitiesToDelete.addAll(getEntities());

			for (Iterator<EOEntity> i = getEOModel().getEntities().iterator(); i.hasNext();) {
				EOEntity eoEntity = i.next();
				DMEOEntity foundEntity = lookupDMEOEntityWithFullyQualifiedName(eoEntity);
				if (foundEntity != null && foundEntity.getDMEOModel() != this) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Lookup dereferenced EOEntity " + foundEntity.getName() + "! Trying to repair...");
					}
					foundEntity.delete();
					foundEntity.getDMEOModel().delete();
					foundEntity = null;
				}
				if (foundEntity == null) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Found NEW EOEntity " + eoEntity.getName() + "(" + eoEntity.getClassName()
								+ "). Creates the related DMEOEntity.");
					}
					DMEOEntity newDMEOEntity = new DMEOEntity(getDMModel(), this, eoEntity,
							getRepository() instanceof EOPrototypeRepository);
					getRepository().registerEntity(newDMEOEntity);
				} else {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Lookup EOEntity " + foundEntity.getName());
					}
					getRepository().unregisterEntity(foundEntity);
					foundEntity.setEOEntity(eoEntity);
					getRepository().registerEntity(foundEntity);
					entitiesToDelete.remove(foundEntity);
					foundEntity.updateFromEOEntity();
				}
			}

			for (Enumeration en = entitiesToDelete.elements(); en.hasMoreElements();) {
				DMEOEntity toDelete = (DMEOEntity) en.nextElement();
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Delete DMEOEntity " + toDelete.getName());
				}
				toDelete.delete();
			}
		}
		// Sets EOModel to be up-to-date
		if (_eoModelResource != null) {
			// GPO: TODO: check that false is correct here not totally sure but I don't think it matters much
			_eoModelResource.getResourceData().clearIsModified(false);
		}
	}

	public DMEOEntity getDMEOEntity(EOEntity eoEntity) {
		DMEOEntity returned = getRepository().getDMEOEntity(eoEntity);
		if (returned != null && returned.getDMEOModel() != this) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Found DMEOEntity matching " + eoEntity.getClassName() + " but not in the expected DMEOModel !");
			}
		}
		return returned;
	}

	private DMEOEntity lookupDMEOEntityWithFullyQualifiedName(EOEntity eoEntity) {
		String entityPackageName = null;
		String entityClassName = null;
		StringTokenizer st = new StringTokenizer(eoEntity.getClassName(), ".");
		while (st.hasMoreTokens()) {
			String nextToken = st.nextToken();
			if (st.hasMoreTokens()) {
				if (entityPackageName == null) {
					entityPackageName = nextToken;
				} else {
					entityPackageName += "." + nextToken;
				}
			} else {
				entityClassName = nextToken;
			}
		}
		String expectedKey = null;
		if (entityPackageName == null) {
			expectedKey = getRepository().getDefaultPackage().getName();
		} else {
			expectedKey = entityPackageName;
		}
		if (eoEntity.getClassName().equals("EOGenericRecord")) {
			expectedKey = expectedKey + ".EOGenericRecord$" + eoEntity.getName();
		} else {
			expectedKey = expectedKey + "." + entityClassName;
		}

		return (DMEOEntity) getRepository().getDMEntity(expectedKey);
	}

	private Vector<DMEOEntity> orderedEntities;

	private boolean needsReordering = true;

	@Override
	public Vector<DMEOEntity> getOrderedChildren() {
		if (needsReordering) {
			reorderEntities();
		}
		return orderedEntities;
	}

	private void reorderEntities() {
		if (needsReordering) {
			if (orderedEntities != null) {
				orderedEntities.removeAllElements();
			} else {
				orderedEntities = new Vector<DMEOEntity>();
			}
			orderedEntities.addAll(entities.values());
			Collections.sort(orderedEntities, entityComparator);
			needsReordering = false;
		}
	}

	private static final EntityComparator entityComparator = new EntityComparator();

	/**
	 * Used to sort entities according to name alphabetic ordering
	 * 
	 * @author sguerin
	 * 
	 */
	static class EntityComparator implements Comparator<DMEOEntity> {

		/**
		 * Implements
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(DMEOEntity o1, DMEOEntity o2) {
			if (o1 == null || o2 == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Cannot compare null entities");
				}
				return 0;
			} else {
				String s1 = o1.getName();
				String s2 = o2.getName();
				if (s1 != null && s2 != null) {
					return Collator.getInstance().compare(s1, s2);
				} else {
					return 0;
				}
			}
		}

	}

	// ==========================================================================
	// ======================== TreeNode implementation
	// =========================
	// ==========================================================================

	@Override
	public DMObject getParent() {
		return getRepository();
	}

	public static class EOModelFileFilter extends FileFilter {

		@Override
		public boolean accept(File f) {
			if (f.isDirectory()) {
				if (ToolBox.getPLATFORM().equals(ToolBox.WINDOWS)) {
					return f.isDirectory();
				}
				return f.getName().endsWith(".eomodeld");
			}
			return false;
		}

		@Override
		public String getDescription() {
			return FlexoLocalization.localizedForKey("eomodel_files");
		}

	}

	/**
	 * @author gpolet
	 * 
	 */
	public static class EOModelFileView extends FileView {

		protected EOModelFileView() {

		}

		/**
		 * Overrides isTraversable
		 * 
		 * @see javax.swing.filechooser.FileView#isTraversable(java.io.File)
		 */
		@Override
		public Boolean isTraversable(File f) {
			if (f.getName().toLowerCase().endsWith(".eomodeld")) {
				return Boolean.FALSE;
			}
			/*
			 * if (f.listFiles(new java.io.FileFilter() {
			 * 
			 * public boolean accept(File f) { return !f.isDirectory() && f.getName().toLowerCase().endsWith("index.eomodeld"); } }).length
			 * > 0) return Boolean.FALSE;
			 */
			return super.isTraversable(f);
		}

		@Deprecated
		public static final ImageIcon EOMODEL_ICON = new ImageIconResource("Icons/Model/DM/EOModel.png");

		/**
		 * Overrides getIcon
		 * 
		 * @see javax.swing.filechooser.FileView#getIcon(java.io.File)
		 */
		@Override
		public Icon getIcon(File f) {
			if (f.getName().toLowerCase().endsWith(".eomodeld")) {
				return EOMODEL_ICON;
			}/*
				* else if (f.isDirectory() && f.listFiles(new java.io.FileFilter() {
				* 
				* public boolean accept(File f) { return !f.isDirectory() && f.getName().toLowerCase().endsWith("index.eomodeld"); } }).length
				* > 0) return DM_EOMODEL_ICON;
				*/else {
				return super.getIcon(f);
			}
		}
	}

	@Override
	public void notifyReordering(DMObject cause) {
		needsReordering = true;
		super.notifyReordering(cause);
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "dm_eomodel";
	}

	/**
	 * Returns the entity sub path. If this is null, you should use the repository sub path.
	 * 
	 * @return
	 */
	public String getEntitySubPath() {
		return entitySubPath;
	}

	public void setEntitySubPath(String entitySubPath) {
		String old = this.entitySubPath;
		this.entitySubPath = entitySubPath;
		setChanged();
		notifyObservers(new WKFAttributeDataModification("entitySubPath", old, entitySubPath));
	}

	/**
	 * Overrides getDontGenerate
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getDontGenerate()
	 */
	@Override
	public boolean getDontGenerate() {
		if (this.getRepository() == getDMModel().getEOPrototypeRepository()) {
			return true;
		}
		return super.getDontGenerate();
	}

	public DMEOModelStatistics getStatistics() {
		if (statistics == null) {
			statistics = new DMEOModelStatistics(this);
		}
		return statistics;
	}

	public File createTemporaryCopyOfMemory() {
		File returned = new File(System.getProperty("java.io.tmpdir"), getEOModel().getFile().getName());
		returned.mkdirs();
		File index = new File(returned, EOModel.INDEX_EOMODELD);
		try {
			FileUtils.saveToFile(index, getEOModel().getPListRepresentation());
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		for (EOEntity e : getEOModel().getEntities()) {
			try {
				FileUtils.saveToFile(new File(returned, e.getFile().getName()), e.getPListRepresentation());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return returned;
	}

	public void notifyEOEntityAdded(DMEOEntity entity) {
		setChanged();
		notifyObservers(new EOEntityInserted(entity));
	}

	public DMEOEntity getDMEOEntityNamed(String eoentityName) {
		Iterator<DMEOEntity> it = getEntities().iterator();
		DMEOEntity candidate = null;
		while (it.hasNext()) {
			candidate = it.next();
			if (candidate.getName().equals(eoentityName)) {
				return candidate;
			}
		}
		return null;
	}

	public String getNextDefautEntityName(DMPackage aPackage) {
		String baseName = FlexoLocalization.localizedForKey("default_new_entity_name");
		String testMe = baseName;
		int test = 0;
		while (entities.get(aPackage + "." + testMe) != null || getEOModel() != null && getEOModel()._entityNamedIgnoreCase(testMe) != null
				|| getEOModel() != null && getEOModel().getModelGroup().entityNamedIgnoreCase(testMe) != null) {
			test++;
			testMe = baseName + test;
		}
		return testMe;
	}

}
