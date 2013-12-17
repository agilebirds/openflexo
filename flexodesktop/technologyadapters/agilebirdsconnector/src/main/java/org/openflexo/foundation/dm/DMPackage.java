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
package org.openflexo.foundation.dm;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.dm.dm.EntityDeleted;
import org.openflexo.foundation.dm.dm.EntityRegistered;
import org.openflexo.foundation.dm.dm.EntityUnregistered;
import org.openflexo.foundation.xml.FlexoDMBuilder;
import org.openflexo.localization.FlexoLocalization;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class DMPackage extends DMObject {
	static final Logger logger = Logger.getLogger(DMPackage.class.getPackage().getName());

	public static final String DEFAULT_PACKAGE_NAME = "default_package";

	// ==========================================================================
	// ============================= Instance variables
	// =========================
	// ==========================================================================

	private String name;

	private DMRepository repository;

	// List of all entities declared in this package (hashtable of DMEntity)
	private Hashtable<String, DMEntity> entities;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Constructor used during deserialization
	 */
	public DMPackage(FlexoDMBuilder builder) {
		this(builder.dmModel);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public DMPackage(DMModel dmModel) {
		super(dmModel);
		entities = new Hashtable<String, DMEntity>();
	}

	/**
	 * Constructor used for dynamic creation
	 */
	public DMPackage(DMModel dmModel, DMRepository repository, String packageName) {
		this(dmModel);
		this.name = packageName;
		this.repository = repository;
	}

	@Override
	public String getFullyQualifiedName() {
		return name;
	}

	/**
	 * Return String uniquely identifying inspector template which must be applied when trying to inspect this object
	 * 
	 * @return a String value
	 */
	@Override
	public String getInspectorName() {
		if (getRepository() == null || getRepository().isReadOnly()) {
			return Inspectors.DM.DM_RO_PACKAGE_INSPECTOR;
		} else {
			return Inspectors.DM.DM_PACKAGE_INSPECTOR;
		}
	}

	/**
	 * Return a Vector of embedded DMObjects at this level.
	 * 
	 * @return a Vector of embedded DMPackage instances
	 */
	@Override
	public Vector<DMObject> getEmbeddedDMObjects() {
		Vector<DMObject> returned = new Vector<DMObject>();
		returned.addAll(getOrderedChildren());
		return returned;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getLocalizedName() {
		if (getName().equals(DEFAULT_PACKAGE_NAME)) {
			return FlexoLocalization.localizedForKey(getName());
		}
		return getName();
	}

	@Override
	public void setName(String name) {
		this.name = name;
		setChanged();
		if (getRepository() != null) {
			getRepository().notifyReordering(this);
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

	@Override
	public boolean isDeletable() {
		for (DMEntity entity : getEntities()) {
			if (!entity.isDeletable()) {
				return false;
			}
		}

		return getRepository() != null && !getRepository().isReadOnly();
	}

	public DMRepository getRepository() {
		return repository;
	}

	public void setRepository(DMRepository repository) {
		this.repository = repository;
		setChanged();
	}

	/**
	 * Return all the entities contained in this package, as a Vector of DMEntity
	 * 
	 * @return a Vector of DMEntity
	 */
	public Vector<DMEntity> getEntities() {
		return getOrderedChildren();
	}

	public void registerEntity(DMEntity entity) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Register Entity " + entity.getFullyQualifiedName());
		}
		if (entities.get(entity.getFullyQualifiedName()) == null) {
			entities.put(entity.getFullyQualifiedName(), entity);
			entity.addObserver(this);
			needsReordering = true;
			setChanged();
			notifyObservers(new EntityRegistered(entity));
		} else if (entity != entities.get(entity.getFullyQualifiedName())) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Trying to redefine entity " + entity.getFullyQualifiedName() + ": operation not allowed !");
			}
		}
	}

	public void unregisterEntity(DMEntity entity) {
		unregisterEntity(entity, true);
	}

	public void unregisterEntity(DMEntity entity, boolean notify) {
		entities.remove(entity.getFullyQualifiedName());
		entity.deleteObserver(this);
		needsReordering = true;
		if (notify) {
			setChanged();
			notifyObservers(new EntityUnregistered(entity));
		}
		/*if (entities.size() == 0) {
		    getRepository().deletePackage(this);
		}*/
	}

	@Override
	public boolean delete() {
		Vector<DMEntity> entitiesToDelete = new Vector<DMEntity>();
		entitiesToDelete.addAll(getEntities());
		for (Enumeration en = entitiesToDelete.elements(); en.hasMoreElements();) {
			DMEntity next = (DMEntity) en.nextElement();
			next.delete();
		}
		if (getRepository() != null) {
			getRepository().deletePackage(this);
		}
		name = null;
		repository = null;
		entities.clear();
		entities = null;
		super.delete();
		deleteObservers();
		return true;
	}

	public String getRelativePath() {
		if (getRepository() == null) {
			return "";
		}
		if (this == getRepository().getDefaultPackage()) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		StringTokenizer st = new StringTokenizer(getName(), ".");
		boolean isFirst = true;
		while (st.hasMoreTokens()) {
			sb.append((isFirst ? "" : "/") + st.nextToken());
			isFirst = false;
		}
		return sb.toString();
	}

	public boolean isDefaultPackage() {
		return getName().equals(DMPackage.DEFAULT_PACKAGE_NAME);
		// return (getRepository().packages.get(DMPackage.DEFAULT_PACKAGE_NAME) == this);
	}

	// ==========================================================================
	// ============================= Sorting stuff
	// ==============================
	// ==========================================================================

	private Vector<DMEntity> orderedEntities;

	private boolean needsReordering = true;

	@Override
	public synchronized Vector<DMEntity> getOrderedChildren() {
		if (needsReordering) {
			reorderEntities();
		}
		return orderedEntities;
	}

	private synchronized void reorderEntities() {
		if (needsReordering) {
			if (orderedEntities != null) {
				orderedEntities.removeAllElements();
			} else {
				orderedEntities = new Vector<DMEntity>();
			}
			orderedEntities.addAll(entities.values());
			Collections.sort(orderedEntities, entityComparator);
			needsReordering = false;
		}
	}

	protected static final EntityComparator entityComparator = new EntityComparator();

	/**
	 * Used to sort entities according to name alphabetic ordering
	 * 
	 * @author sguerin
	 * 
	 */
	protected static class EntityComparator implements Comparator<DMEntity> {

		/**
		 * Implements
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(DMEntity o1, DMEntity o2) {
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
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (dataModification instanceof EntityDeleted) {
			unregisterEntity(((EntityDeleted) dataModification).getEntity());
		}
		super.update(observable, dataModification);
	}

	@Override
	public DMRepository getParent() {
		return repository;
	}

	@Override
	public void notifyReordering(DMObject cause) {
		needsReordering = true;
		super.notifyReordering(cause);
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.AgileBirdsObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "dm_package";
	}

	public String getJavaStringRepresentation() {
		if (isDefaultPackage()) {
			return "";
		}
		return getName();
	}
}
