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
package org.openflexo.foundation.resource;

import java.util.Hashtable;
import java.util.logging.Logger;

import org.openflexo.foundation.DataFlexoObserver;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.TemporaryFlexoModelObject;
import org.openflexo.inspector.InspectableObject;

/**
 * A {@link ResourceRepository} stores all resources of a particular type.<br>
 * Resources are organized with a folder hierarchy inside a repository
 * 
 * @author sylvain
 * 
 * @param <R>
 * @param <TA>
 */
public class ResourceRepository<R extends FlexoResource<?>> extends TemporaryFlexoModelObject implements InspectableObject,
		DataFlexoObserver {

	private static final Logger logger = Logger.getLogger(ResourceRepository.class.getPackage().getName());

	/**
	 * Hashtable where resources are stored, used key is the URI of the resource
	 */
	protected Hashtable<String, R> resources;

	private RepositoryFolder<R> rootFolder;

	public RepositoryFolder<R> getRootFolder() {
		return rootFolder;
	}

	/**
	 * Creates a new {@link ResourceRepository}
	 */
	public ResourceRepository() {
		rootFolder = new RepositoryFolder<R>("root", null, this);
	}

	/**
	 * Register supplied resource in default root folder
	 * 
	 * @param resource
	 */
	public void registerResource(R resource) {
		registerResource(resource, getRootFolder());
	}

	/**
	 * Register supplied resource in supplied folder
	 * 
	 * @param resource
	 * @param parentFolder
	 */
	public void registerResource(R resource, RepositoryFolder<R> parentFolder) {
		resources.put(resource.getURI(), resource);
		parentFolder.addToResources(resource);
	}

	/**
	 * Creates new folder with supplied name in supplied parent folder
	 * 
	 * @param folderName
	 * @param parentFolder
	 * @return the newly created folder
	 */
	public RepositoryFolder<R> createNewFolder(String folderName, RepositoryFolder<R> parentFolder) {
		RepositoryFolder<R> newFolder = new RepositoryFolder<R>(folderName, parentFolder, this);
		return newFolder;
	}

	/**
	 * Creates new folder with supplied name in default root folder
	 * 
	 * @param folderName
	 * @return the newly created folder
	 */
	public RepositoryFolder<R> createNewFolder(String folderName) {
		return createNewFolder(folderName, getRootFolder());
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
	}

	@Deprecated
	@Override
	public String getInspectorName() {
		return null;
	}
}
