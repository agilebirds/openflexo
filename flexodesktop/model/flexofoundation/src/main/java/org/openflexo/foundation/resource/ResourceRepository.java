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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.DataFlexoObserver;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.toolbox.FileUtils;

/**
 * A {@link ResourceRepository} stores all resources of a particular type.<br>
 * Resources are organized with a folder hierarchy inside a repository
 * 
 * @author sylvain
 * 
 * @param <R>
 * @param <TA>
 */
public class ResourceRepository<R extends FlexoResource<?>> extends FlexoObject implements DataFlexoObserver {

	private static final Logger logger = Logger.getLogger(ResourceRepository.class.getPackage().getName());

	/**
	 * Hashtable where resources are stored, used key is the URI of the resource
	 */
	protected HashMap<String, R> resources;

	private RepositoryFolder<R> rootFolder;

	public RepositoryFolder<R> getRootFolder() {
		return rootFolder;
	}

	/**
	 * Creates a new {@link ResourceRepository}
	 */
	public ResourceRepository() {
		resources = new HashMap<String, R>();
		rootFolder = new RepositoryFolder<R>("root", null, this);
	}

	/**
	 * Return resource with the supplied URI, if this resource was already declared
	 * 
	 * @param resourceURI
	 * @return
	 */
	public R getResource(String resourceURI) {
		return resources.get(resourceURI);
	}

	/**
	 * Register supplied resource in default root folder
	 * 
	 * @param flexoResource
	 */
	public void registerResource(R flexoResource) {
		registerResource(flexoResource, getRootFolder());
	}

	public void unregisterResource(R flexoResource) {
		RepositoryFolder<R> parentFolder = getParentFolder(flexoResource);
		parentFolder.removeFromResources(flexoResource);
		resources.remove(flexoResource.getURI());

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
		newFolder.getFile().mkdirs();

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

	/**
	 * Delete supplied folder, asserting supplied folder is empty
	 * 
	 * @param folder
	 */
	public void deleteFolder(RepositoryFolder<R> folder) {
		RepositoryFolder<R> parentFolder = getParentFolder(folder);
		if (parentFolder != null && folder.getResources().size() == 0) {
			if (folder.getFile().exists()) {
				folder.getFile().delete();
			}
			parentFolder.removeFromChildren(folder);
			folder.delete();
		}
	}

	/**
	 * Move resource from a folder to an other one
	 * 
	 * @param resource
	 * @param fromFolder
	 * @param toFolder
	 */
	public void moveResource(R resource, RepositoryFolder<R> fromFolder, RepositoryFolder<R> toFolder) {
		if (getParentFolder(resource) == fromFolder) {
			fromFolder.removeFromResources(resource);
			toFolder.addToResources(resource);
			if (resource instanceof FlexoFileResource) {
				File fromFile = ((FlexoFileResource) resource).getFile();
				File toFile = new File(toFolder.getFile(), fromFile.getName());
				try {
					FileUtils.rename(fromFile, toFile);
					((FlexoFileResource) resource).setFile(toFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Return a collection storing all resources contained in this repository
	 * 
	 * @return
	 */
	public Collection<R> getAllResources() {
		return resources.values();
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
	}

	/**
	 * Returns the number of resources registed in this repository
	 * 
	 * @return
	 */
	public int getSize() {
		return resources.size();
	}

	@Override
	public String getFullyQualifiedName() {
		return "ResourceRepository";
	}

	/**
	 * Return an enumeration of all folders, by recursively explore the tree
	 * 
	 * @return an Enumeration of FlexoComponentFolder elements
	 */
	public Enumeration<RepositoryFolder<R>> allFolders() {
		Vector<RepositoryFolder<R>> temp = new Vector<RepositoryFolder<R>>();
		addFolders(temp, getRootFolder());
		return temp.elements();
	}

	/**
	 * Return number of folders
	 */
	public int allFoldersCount() {
		Vector<RepositoryFolder<R>> temp = new Vector<RepositoryFolder<R>>();
		addFolders(temp, getRootFolder());
		return temp.size();
	}

	private void addFolders(List<RepositoryFolder<R>> temp, RepositoryFolder<R> folder) {
		temp.add(folder);
		for (RepositoryFolder<R> currentFolder : folder.getChildren()) {
			addFolders(temp, currentFolder);
		}
	}

	public RepositoryFolder<R> getFolderWithName(String folderName) {
		for (Enumeration<RepositoryFolder<R>> e = allFolders(); e.hasMoreElements();) {
			RepositoryFolder<R> folder = e.nextElement();

			if (folder.getName().equals(folderName)) {
				return folder;
			}

		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Could not find folder named " + folderName);
		}
		return null;
	}

	public RepositoryFolder<R> getParentFolder(R resource) {
		for (Enumeration<RepositoryFolder<R>> e = allFolders(); e.hasMoreElements();) {
			RepositoryFolder<R> folder = e.nextElement();
			if (folder.getResources().contains(resource)) {
				return folder;
			}

		}
		return null;
	}

	public RepositoryFolder<R> getParentFolder(RepositoryFolder<R> aFolder) {
		for (Enumeration<RepositoryFolder<R>> e = allFolders(); e.hasMoreElements();) {
			RepositoryFolder<R> folder = e.nextElement();
			if (folder.getChildren().contains(aFolder)) {
				return folder;
			}

		}
		return null;
	}

}