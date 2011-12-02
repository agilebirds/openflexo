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
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.tree.TreeNode;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.dm.action.CreateDMPackage;
import org.openflexo.foundation.dm.action.UpdateDMRepository;
import org.openflexo.foundation.dm.dm.DMObjectDeleted;
import org.openflexo.foundation.dm.dm.EntityRegistered;
import org.openflexo.foundation.dm.dm.EntityUnregistered;
import org.openflexo.foundation.dm.dm.PackageCreated;

/**
 * Represents logical group of DMEntity sharing the same implementation
 * 
 * @author sguerin
 * 
 */
public abstract class DMRepository extends DMObject {

	private static final Logger logger = Logger.getLogger(DMRepository.class.getPackage().getName());

	private String name;

	// List of all entities declared in this repository (hashtable of DMEntity)
	protected DMEntityHashtable entities;

	// List of all packages declared in this repository (hashtable of DMPackage)
	protected Hashtable<String, DMPackage> packages;

	private class DMEntityHashtable extends Hashtable<String, DMEntity> {
		public DMEntityHashtable() {
			super();
		}

		public DMEntityHashtable(Hashtable<String, DMEntity> ht) {
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
	public DMRepository(DMModel dmModel) {
		super(dmModel);
		entities = new DMEntityHashtable();
		packages = new Hashtable<String, DMPackage>();
	}

	@Override
	public String getFullyQualifiedName() {
		return getDMModel().getFullyQualifiedName() + "." + name;
	}

	public abstract boolean isReadOnly();

	public abstract int getOrder();

	/**
	 * Return String uniquely identifying inspector template which must be applied when trying to inspect this object
	 * 
	 * @return a String value
	 */
	@Override
	public String getInspectorName() {
		if (isReadOnly()) {
			return Inspectors.DM.DM_RO_REPOSITORY_INSPECTOR;
		} else {
			return Inspectors.DM.DM_REPOSITORY_INSPECTOR;
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
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		setChanged();
		notifyObservers(new NameChanged(oldName, newName));
		if (getRepositoryFolder() != null) {
			getRepositoryFolder().notifyReordering(this);
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
	protected Vector<FlexoActionType> getSpecificActionListForThatClass() {
		Vector<FlexoActionType> returned = super.getSpecificActionListForThatClass();
		returned.add(CreateDMPackage.actionType);
		returned.add(UpdateDMRepository.actionType);
		return returned;
	}

	@Override
	public boolean isDeletable() {
		return !isReadOnly();
	}

	public boolean isUpdatable() {
		return false;
	}

	public abstract DMRepositoryFolder getRepositoryFolder();

	public Hashtable<String, DMPackage> getPackages() {
		return packages;
	}

	public void setPackages(Hashtable<String, DMPackage> packages) {
		this.packages = packages;
		setChanged();
	}

	public DMPackage packageForEntity(DMEntity entity) {
		String packageName = entity.getEntityPackageName();
		if (packageName == null) {
			return getDefaultPackage();
		} else {
			return packageWithName(packageName);
		}
	}

	public DMPackage getDefaultPackage() {
		/*
		 * DMPackage defaultPackage = (DMPackage)packages.get("default"); if (defaultPackage == null) { defaultPackage = new DMPackage(getDMModel(),this,"default_package");
		 * packages.put("default",defaultPackage); } return defaultPackage;
		 */
		return packageWithName(DMPackage.DEFAULT_PACKAGE_NAME);
	}

	public DMPackage getPackageWithName(String name) {
		return packageWithName(name);
	}

	public DMPackage packageWithName(String aPackageName) {
		if (packages.get(aPackageName) == null) {
			createPackage(aPackageName, false);
		}
		return packages.get(aPackageName);
	}

	public DMPackage createPackage(String aPackageName) {
		return createPackage(aPackageName, true);
	}

	public DMPackage createPackage(String aPackageName, boolean notify) {
		// logger.info("Create package "+aPackageName+" for "+this);
		DMPackage returned = new DMPackage(getDMModel(), this, aPackageName);
		packages.put(aPackageName, returned);
		needsReordering = true;
		if (notify) {
			setChanged();
			notifyObservers(new PackageCreated(returned));
		}
		return returned;
	}

	public void deletePackage(DMPackage aPackage) {
		packages.remove(aPackage.getName());
		needsReordering = true;
		setChanged();
		notifyObservers(new DMObjectDeleted<DMPackage>(aPackage));
	}

	public Hashtable<String, DMEntity> getEntities() {
		return entities;
	}

	public void setEntities(Hashtable<String, DMEntity> someEntities) {
		// Transtype to DMEntityHashtable
		entities = new DMEntityHashtable(someEntities);
		needsReordering = true;
		setChanged();
	}

	public void setEntityForKey(DMEntity entity, String entityName) {
		entity.setRepository(this);
		entities.put(entityName, entity);
		getDMModel().registerEntity(entity);
		packageForEntity(entity).registerEntity(entity);
		needsReordering = true;
		setChanged();
	}

	public void removeEntityWithKey(String entityName) {
		removeEntityWithKey(entityName, true);
	}

	public void removeEntityWithKey(String entityName, boolean notify) {
		entities.remove(entityName);
		getDMModel().unregisterEntity(entityName);
		needsReordering = true;
		if (notify) {
			setChanged();
		}
	}

	public void registerEntity(DMEntity entity) {
		if ((entity.getEntityClassName() == null) || (entity.getEntityClassName().trim().equals(""))) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Registering entity " + entity.getFullyQualifiedName()
						+ ": className seems to be not correctely set. Doing it anyway.");
			}
		}
		entity.setRepository(this);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Register Entity " + entity.getFullyQualifiedName());
		}
		if (entities.get(entity.getFullyQualifiedName()) == null) {
			setEntityForKey(entity, entity.getFullyQualifiedName());
			setChanged();
			notifyObservers(new EntityRegistered(entity));
		} else if (entity != entities.get(entity.getFullyQualifiedName())) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Trying to redefine entity " + entity.getFullyQualifiedName() + ": operation not allowed !");
			}
		}
	}

	public final void unregisterEntity(DMEntity entity) {// GPO: This method is final. If you want to override it, override the 2-args
															// method
		unregisterEntity(entity, true);
	}

	/**
	 * Unregisters the given <code>entity</code> from this repository and from its package. A notification will be thrown if
	 * <code>notify</code> is true. The purpose of this flag is to avoid notifications for temporary states: if you plan on registering this
	 * entity back in this same repository, then the unregister call can be invoked with false (the registration will notify the observers).
	 * 
	 * @param entity
	 * @param notify
	 */
	public void unregisterEntity(DMEntity entity, boolean notify) {
		entity.setRepository(null, notify);
		removeEntityWithKey(entity.getFullyQualifiedName(), notify);
		packageForEntity(entity).unregisterEntity(entity, notify);
		if (notify) {
			setChanged();
			notifyObservers(new EntityUnregistered(entity));
		}
	}

	public DMEntity getDMEntity(String packageName, String className) {
		return getEntities().get(packageName + "." + className);
	}

	public DMEntity getDMEntity(DMPackage aPackage, String className) {
		return getDMEntity(aPackage.getName() + "." + className);
	}

	public DMEntity getDMEntity(String fullQualifiedName) {
		return getEntities().get(fullQualifiedName);
	}

	public String getStringRepresentation() {
		String returned = "DMRepository\n";
		for (Enumeration e = entities.elements(); e.hasMoreElements();) {
			DMEntity entity = (DMEntity) e.nextElement();
			returned += entity.getStringRepresentation();
		}
		return returned;
	}

	/**
	 * Delete this repository Take care because all observers are delete here !!!
	 * 
	 * Overrides @see org.openflexo.foundation.DeletableObject#delete()
	 * 
	 * @see org.openflexo.foundation.DeletableObject#delete()
	 */
	@Override
	public void delete() {
		Vector<DMObject> entitiesToDelete = new Vector<DMObject>();
		entitiesToDelete.addAll(getEntities().values());
		for (Enumeration en = entitiesToDelete.elements(); en.hasMoreElements();) {
			DMEntity next = (DMEntity) en.nextElement();
			next.delete();
		}
		Vector<DMObject> packagesToDelete = new Vector<DMObject>();
		packagesToDelete.addAll(getPackages().values());
		for (Enumeration en = packagesToDelete.elements(); en.hasMoreElements();) {
			DMPackage next = (DMPackage) en.nextElement();
			next.delete();
		}
		getDMModel().unregisterRepository(this);
		needsReordering = false;
		name = null;
		entities.clear();
		entities = null;
		packages.clear();
		packages = null;
		super.delete();
		deleteObservers();
	}

	public boolean hasDiagrams() {
		for (ERDiagram diagram : getDMModel().getDiagrams()) {
			if (diagram.getRepository() == this) {
				return true;
			}
		}
		return false;
	}

	// ==========================================================================
	// ============================= Sorting stuff
	// ==============================
	// ==========================================================================

	private Vector<DMPackage> orderedPackages;

	private boolean needsReordering = true;

	@Override
	public synchronized Vector<? extends DMObject> getOrderedChildren() {
		if (needsReordering) {
			reorderPackages();
		}
		return orderedPackages;
	}

	private synchronized void reorderPackages() {
		if (needsReordering) {
			if (orderedPackages != null) {
				orderedPackages.removeAllElements();
			} else {
				orderedPackages = new Vector<DMPackage>();
			}
			orderedPackages.addAll(packages.values());
			Collections.sort(orderedPackages, packageComparator);
			needsReordering = false;
		}
	}

	protected static final PackageComparator packageComparator = new PackageComparator();

	/**
	 * Used to sort entities according to name alphabetic ordering
	 * 
	 * @author sguerin
	 * 
	 */
	static class PackageComparator implements Comparator<DMPackage> {

		/**
		 * Implements
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(DMPackage o1, DMPackage o2) {
			if (o1.isDefaultPackage()) {
				return -1;
			}
			if (o2.isDefaultPackage()) {
				return 1;
			}
			String s1 = o1.getName();
			String s2 = o2.getName();
			if ((s1 != null) && (s2 != null)) {
				return Collator.getInstance().compare(s1, s2);
			} else {
				return 0;
			}
		}

	}

	// ==========================================================================
	// ======================== TreeNode implementation
	// =========================
	// ==========================================================================

	@Override
	public TreeNode getParent() {
		return getRepositoryFolder(); // getDMModel();
	}

	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	@Override
	public void notifyReordering(DMObject cause) {
		needsReordering = true;
		super.notifyReordering(cause);
	}

	public void changePackage(DMEntity entity, String oldFullyQualifiedName, String newFullyQualifiedName) {
		entities.remove(oldFullyQualifiedName);
		entities.put(newFullyQualifiedName, entity);

	}

}
