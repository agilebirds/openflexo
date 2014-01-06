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
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 * 
 */
public class EOModelGroup extends EOObject {
	private static final Logger logger = FlexoLogger.getLogger(EOModelGroup.class.getPackage().getName());

	private static EOModelGroup defaultGroup;

	private Vector<EOModel> models = new Vector<EOModel>();

	public static EOModelGroup getDefaultGroup() {
		if (defaultGroup == null) {
			defaultGroup = new EOModelGroup();
		}
		return defaultGroup;
	}

	/**
	 * @param modelGroup
	 */
	public static void setDefaultGroup(EOModelGroup modelGroup) {
		defaultGroup = modelGroup;
	}

	/**
	 * @param eoModelUrl
	 * @return
	 * @throws PropertyListDeserializationException
	 * @throws FileNotFoundException
	 */
	public EOModel addModel(File file) throws FileNotFoundException, PropertyListDeserializationException {
		EOModel model = EOModel.createEOModelFromFile(file, null, this);
		addModel(model);
		return model;
	}

	/**
	 * @param newEOModel
	 */
	public void addModel(EOModel newEOModel) {
		Iterator<EOEntity> i = newEOModel.getEntities().iterator();
		while (i.hasNext()) {
			EOEntity e = i.next();
			if (entityNamed(e.getName()) != null) {
				throw new IllegalArgumentException("The entity named " + e.getName() + " already exists in this model group.");
			}
		}
		newEOModel.setModelGroup(this);
		models.add(newEOModel);
	}

	/**
	 * @param oldEOModel
	 */
	public void removeModel(EOModel oldEOModel) {
		models.remove(oldEOModel);
	}

	/**
	 * Overrides resolveObjects
	 * 
	 * @see org.openflexo.foundation.dm.eo.model.EOObject#resolveObjects()
	 */
	@Override
	protected void resolveObjects() {

	}

	public Vector<EOModel> getModels() {
		return models;
	}

	public EOEntity entityNamed(String name) {
		Iterator<EOModel> i = getModels().iterator();
		while (i.hasNext()) {
			EOModel model = i.next();
			EOEntity e = model._entityNamed(name);
			if (e != null) {
				return e;
			}
		}
		return null;
	}

	public EOEntity entityNamedIgnoreCase(String name) {
		Iterator<EOModel> i = getModels().iterator();
		while (i.hasNext()) {
			EOModel model = i.next();
			EOEntity e = model._entityNamedIgnoreCase(name);
			if (e != null) {
				return e;
			}
		}
		return null;
	}

	/**
	 * Overrides delete
	 * 
	 * @see org.openflexo.foundation.dm.eo.model.EOObject#delete()
	 */
	@Override
	public void delete() {
		if (logger.isLoggable(Level.SEVERE)) {
			logger.severe("Delete should never be called on a EOModelGroup");
		}
	}

	/**
	 * Overrides clearObjects
	 * 
	 * @see org.openflexo.foundation.dm.eo.model.EOObject#clearObjects()
	 */
	@Override
	protected void clearObjects() {
		Iterator<EOModel> i = getModels().iterator();
		while (i.hasNext()) {
			EOModel model = i.next();
			model.clearObjects();
		}
	}

	/**
     * 
     */
	public void loadAllModelObjects(EOModel invoker) {
		Iterator<EOModel> i = getModels().iterator();
		while (i.hasNext()) {
			EOModel model = i.next();
			model.clearObjects();
		}
		invoker.loadEntities();
		i = getModels().iterator();
		while (i.hasNext()) {
			EOModel model = i.next();
			model.resolveObjects();
		}
	}

}
