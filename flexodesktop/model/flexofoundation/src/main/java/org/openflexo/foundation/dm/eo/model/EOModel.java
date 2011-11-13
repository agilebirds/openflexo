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
package org.openflexo.foundation.dm.eo.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cayenne.wocompat.PropertyListSerialization;

import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileUtils;

/**
 * @author gpolet
 * 
 */
public class EOModel extends EOObject {
	private static final Logger logger = FlexoLogger.getLogger(EOModel.class.getPackage().getName());

	private static final String EOMODEL_VERSION_KEY = "EOModelVersion";

	private static final String EOPROTOTYPES = "EOPrototypes";

	public static final String INDEX_EOMODELD = "index.eomodeld";

	private static final String ADAPTOR_NAME_KEY = "adaptorName";

	private static final String ENTITIES_KEY = "entities";

	private static final String CONNECTION_DICTIONARY = "connectionDictionary";

	private static final String CLASS_NAME_KEY = "className";

	private Vector<String> missingEntities;

	private String adaptorName;

	private Map<String, Object> connectionDictionary;

	private List<EOEntity> entities;

	private EOModelGroup modelGroup;

	private Vector<File> filesToDelete;

	private File file;

	@SuppressWarnings("unchecked")
	public static EOModel createEOModelFromFile(File file, String name, EOModelGroup group) throws FileNotFoundException,
			PropertyListDeserializationException {
		File dir = file;
		if (file.isDirectory())
			file = new File(file, INDEX_EOMODELD);
		else
			dir = file.getParentFile();
		if (name == null) {// If we don't know the name, we compute it from the
			// file
			name = file.getParentFile().getName();
			if (name.endsWith(".eomodeld")) {
				name = name.substring(0, name.indexOf(".eomodeld"));
			}
		}
		Map<Object, Object> map = (Map<Object, Object>) PropertyListSerialization.propertyListFromFile(file);
		if (map == null)
			throw new PropertyListDeserializationException(file);
		if (map.get(NAME_KEY) != null) // If the name was stored in the file,
			// then it is the real model name
			name = (String) map.get(NAME_KEY);
		EOModel model = new EOModel();
		model.setName(name);
		model.setAdaptorName((String) map.get(ADAPTOR_NAME_KEY));
		model.setConnectionDictionary((Map<String, Object>) map.get(CONNECTION_DICTIONARY));
		model.setOriginalMap(map);
		model.setFile(dir);
		model.loadEntities();
		model.setModelGroup(group != null ? group : EOModelGroup.getDefaultGroup());
		return model;

	}

	/**
     * 
     */
	public EOModel() {
		entities = new Vector<EOEntity>();
		filesToDelete = new Vector<File>();
		createHashMap();
	}

	public Map<Object, Object> getMapRepresentation() {
		Map<Object, Object> map = new HashMap<Object, Object>();
		if (map.get(EOMODEL_VERSION_KEY) == null)
			map.put(EOMODEL_VERSION_KEY, "2.1");
		map.put(NAME_KEY, getName());
		if (getAdaptorName() != null)
			map.put(ADAPTOR_NAME_KEY, getAdaptorName());
		else
			map.remove(ADAPTOR_NAME_KEY);
		if (getConnectionDictionary() != null)
			map.put(CONNECTION_DICTIONARY, getConnectionDictionary());
		else
			map.remove(CONNECTION_DICTIONARY);

		Vector<HashMap<String, String>> entityVector = new Vector<HashMap<String, String>>();
		Iterator<EOEntity> i = getEntities().iterator();
		while (i.hasNext()) {
			HashMap<String, String> map1 = new HashMap<String, String>();
			EOEntity entity = i.next();
			map1.put(NAME_KEY, entity.getName());
			if (entity.getClassName() != null)
				map1.put(CLASS_NAME_KEY, entity.getClassName());
			entityVector.add(map1);
		}
		map.put(ENTITIES_KEY, entityVector);
		return map;
	}

	public String getPListRepresentation() {
		return FlexoPropertyListSerialization.getPListRepresentation(getMapRepresentation());
	}

	/**
	 * @param file
	 * @throws IOException
	 */
	public void writeToFile(File file) throws IOException {
		makeBackup();
		// We delete the files before writing the entities so that if the user
		// deletes an entity and then decides to add it again, the file won't
		// get deleted by mistake
		Iterator<File> it = getFilesToDelete().iterator();
		while (it.hasNext()) {
			File f = it.next();
			if (f.delete())
				it.remove();
		}
		if (getOriginalMap().get(EOMODEL_VERSION_KEY) == null)
			getOriginalMap().put(EOMODEL_VERSION_KEY, "2.1");
		if (entityNamed(EOPROTOTYPES) != null)
			if (entityNamed(EOPROTOTYPES).getModel() == this)
				return;
		getOriginalMap().put(NAME_KEY, getName());
		if (getAdaptorName() != null)
			getOriginalMap().put(ADAPTOR_NAME_KEY, getAdaptorName());
		else
			getOriginalMap().remove(ADAPTOR_NAME_KEY);
		if (getConnectionDictionary() != null)
			getOriginalMap().put(CONNECTION_DICTIONARY, getConnectionDictionary());
		else
			getOriginalMap().remove(CONNECTION_DICTIONARY);
		// Model serialization
		if (!file.exists())
			file.mkdirs();
		Vector<HashMap<String, String>> entityVector = new Vector<HashMap<String, String>>();
		Iterator<EOEntity> i = getEntities().iterator();
		while (i.hasNext()) {
			HashMap<String, String> map = new HashMap<String, String>();
			EOEntity entity = i.next();
			map.put(NAME_KEY, entity.getName());
			if (entity.getClassName() != null)
				map.put(CLASS_NAME_KEY, entity.getClassName());
			entityVector.add(map);
		}
		getOriginalMap().put(ENTITIES_KEY, entityVector);
		File f = new File(file, INDEX_EOMODELD);
		if (!f.exists())
			f.createNewFile();
		FlexoPropertyListSerialization.propertyListToFile(f, getOriginalMap());

		// Entities serialization
		i = getEntities().iterator();
		while (i.hasNext()) {
			EOEntity entity = i.next();
			entity.writeToFile(file);
		}
		setFile(file);
	}

	/**
     * 
     */
	private void makeBackup() {
		if (getFile() == null || !getFile().exists())
			return;
		File bak = new File(getFile().getParentFile(), getFile().getName() + ".bak");
		FileUtils.deleteFilesInDir(bak, true);
		try {
			FileUtils.copyContentDirToDir(getFile(), bak);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the entity named <code>name</code>. This methods looks in the model and all the other registered models of this model group.
	 * 
	 * @param name
	 *            - the name of the entity to return
	 * @return the entity named <code>name</code>
	 */
	public EOEntity entityNamed(String name) {
		EOEntity e = _entityNamed(name);
		if (e != null)
			return e;
		else
			return modelGroup.entityNamed(name);
	}

	/**
	 * Returns the entity name <code>name</code> if it is found in this model. This method can be used by third-party but is first intended
	 * toward the model.
	 * 
	 * @param name
	 *            - the name of the entity to be found
	 * @return the entity named <code>name</code> if it is found in this model
	 * @see #entityNamed(String)
	 */
	public EOEntity _entityNamed(String name) {
		if (name == null) {
			return null;
		}
		Iterator<EOEntity> i = entities.iterator();
		while (i.hasNext()) {
			EOEntity e = i.next();
			if (name.equals(e.getName()))
				return e;
		}
		if (modelGroup != null) {

		} else if (logger.isLoggable(Level.WARNING))
			logger.warning("Found an EOModel outside of a model group.");
		return null;
	}

	/**
	 * Returns the entity named <code>name</code>. This methods looks in the model and all the other registered models of this model group.
	 * 
	 * @param name
	 *            - the name of the entity to return
	 * @return the entity named <code>name</code>
	 */
	public EOEntity entityNamedIgnoreCase(String name) {
		EOEntity e = _entityNamedIgnoreCase(name);
		if (e != null)
			return e;
		else
			return modelGroup.entityNamedIgnoreCase(name);
	}

	/**
	 * Returns the entity name <code>name</code> if it is found in this model. This method can be used by third-party but is first intended
	 * toward the model.
	 * 
	 * @param name
	 *            - the name of the entity to be found
	 * @return the entity named <code>name</code> if it is found in this model
	 * @see #entityNamed(String)
	 */
	public EOEntity _entityNamedIgnoreCase(String name) {
		if (name == null) {
			return null;
		}
		Iterator<EOEntity> i = entities.iterator();
		while (i.hasNext()) {
			EOEntity e = i.next();
			if (name.equalsIgnoreCase(e.getName()))
				return e;
		}
		if (modelGroup != null) {

		} else if (logger.isLoggable(Level.WARNING))
			logger.warning("Found an EOModel outside of a model group.");
		return null;
	}

	public String getAdaptorName() {
		return adaptorName;
	}

	public void setAdaptorName(String adaptorName) {
		this.adaptorName = adaptorName;
	}

	public Map<String, Object> getConnectionDictionary() {
		return connectionDictionary;
	}

	public void setConnectionDictionary(Map<String, Object> connectionDictionary) {
		this.connectionDictionary = connectionDictionary;
	}

	public List<EOEntity> getEntities() {
		return entities;
	}

	public void setEntities(List<EOEntity> entities) {
		this.entities = entities;
	}

	/**
	 * @param newEOEntity
	 */
	public void addEntity(EOEntity entity) {
		if (entities.contains(entity))
			throw new IllegalArgumentException("Entity " + entity.getName() + " is already in model " + getName());
		if (entityNamedIgnoreCase(entity.getName()) != null)
			throw new IllegalArgumentException("An other entity named " + entityNamedIgnoreCase(entity.getName()).getName()
					+ " already exists in model " + getName());
		entities.add(entity);
		entity.setModel(this);
	}

	public void removeEntity(EOEntity entity) {
		if (entities.contains(entity)) {
			entities.remove(entity);
			entity.delete();
		}
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public File getIndexFile() {
		return new File(getFile(), INDEX_EOMODELD);
	}

	public EOModelGroup getModelGroup() {
		return modelGroup;
	}

	public void setModelGroup(EOModelGroup modelGroup) {
		this.modelGroup = modelGroup;
	}

	/**
	 * Overrides resolveObjects
	 * 
	 * @throws FileNotFoundException
	 * 
	 * @see org.openflexo.foundation.dm.eo.model.EOObject#resolveObjects()
	 */
	@Override
	protected void resolveObjects() {
		missingEntities = new Vector<String>();
		Iterator<EOEntity> i = getEntities().iterator();
		while (i.hasNext()) {
			EOEntity e = i.next();
			e.resolveObjects();
		}
	}

	/**
	 * Overrides delete
	 * 
	 * @see org.openflexo.foundation.dm.eo.model.EOObject#delete()
	 */
	@Override
	public void delete() {
		Iterator<EOEntity> i = getEntities().iterator();
		while (i.hasNext()) {
			EOEntity e = i.next();
			e.delete();
		}
	}

	/**
	 * @param prototype
	 * @return
	 */
	public EOAttribute getPrototypeNamed(String prototype) {
		EOEntity e = entityNamed(EOPROTOTYPES);
		if (e != null) {
			return e.attributeNamed(prototype);
		} else if (logger.isLoggable(Level.WARNING))
			logger.warning("Prototypes could not be found");
		return null;
	}

	/**
	 * Overrides clearObjects
	 * 
	 * @see org.openflexo.foundation.dm.eo.model.EOObject#clearObjects()
	 */
	@Override
	protected void clearObjects() {
		Iterator<EOEntity> i = getEntities().iterator();
		while (i.hasNext()) {
			EOEntity e = i.next();
			e.clearObjects();
		}
	}

	/**
     * 
     */
	public void loadAllModelObjects() {
		getModelGroup().loadAllModelObjects(this);
	}

	/**
	 * This call will load all entities from the original map.
	 */
	@SuppressWarnings("unchecked")
	protected void loadEntities() {
		Map<Object, Object> map = getOriginalMap();
		List<Map<String, String>> entitiesList = (List<Map<String, String>>) map.get(ENTITIES_KEY);
		if (entitiesList != null) {
			Iterator<Map<String, String>> i = entitiesList.iterator();
			List<EOEntity> entities = new Vector<EOEntity>();
			while (i.hasNext()) {
				Map<String, String> m = i.next();
				EOEntity e = null;
				try {
					e = EOEntity.createEntityFromFile(this, new File(getFile(), m.get(NAME_KEY) + ".plist"));
				} catch (FileNotFoundException e1) {
					if (logger.isLoggable(Level.WARNING))
						logger.warning("Could not find property list file "
								+ new File(getFile(), m.get(NAME_KEY) + ".plist").getAbsolutePath());
				} catch (Exception e2) {
					e2.printStackTrace();
					if (logger.isLoggable(Level.WARNING))
						logger.warning("Could not deserialize property list file "
								+ new File(getFile(), m.get(NAME_KEY) + ".plist").getAbsolutePath());
				} finally {
					if (e == null) {
						if (logger.isLoggable(Level.INFO))
							logger.info("Creating EOEntity " + m.get(NAME_KEY));
						e = new EOEntity();
						e.setName(m.get(NAME_KEY));
						if (m.get(CLASS_NAME_KEY) != null)
							e.setClassName(m.get(CLASS_NAME_KEY));
						else
							e.setClassName(m.get(NAME_KEY));
					}
				}
				entities.add(e);
			}
			setEntities(entities);
		}
	}

	public void addToMissingEntities(String entityName) {
		if (!missingEntities.contains(entityName))
			missingEntities.add(entityName);
	}

	public Vector<String> getMissingEntities() {
		return missingEntities;
	}

	public Vector<File> getFilesToDelete() {
		return filesToDelete;
	}

	public void setFilesToDelete(Vector<File> filesToDelete) {
		this.filesToDelete = filesToDelete;
	}

	public void addToFilesToDelete(File fileToDelete) {
		filesToDelete.add(fileToDelete);
	}

	public void removeFilesToDelete(File fileToDelete) {
		filesToDelete.remove(fileToDelete);
	}

}
