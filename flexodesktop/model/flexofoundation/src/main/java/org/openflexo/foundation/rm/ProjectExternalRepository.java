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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.xmlcode.XMLSerializable;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class ProjectExternalRepository extends FlexoObject implements XMLSerializable {
	private FlexoProject _project;
	private String _identifier;
	private File _directory;
	private Map<String, String> directoriesForUser = new TreeMap<String, String>();

	private static final String getUserName() {
		return System.getProperty("user.name");
	}

	/**
	 * Constructor used for XML Serialization: never try to instanciate resource from this constructor
	 * 
	 * @param builder
	 */
	public ProjectExternalRepository(FlexoProjectBuilder builder) {
		this(builder.project);
	}

	public ProjectExternalRepository(FlexoProject aProject, String identifier) {
		this(aProject);
		setIdentifier(identifier);
	}

	public ProjectExternalRepository(FlexoProject aProject, String identifier, File directory) {
		this(aProject, identifier);
		setDirectory(directory);
	}

	public ProjectExternalRepository(FlexoProject aProject) {
		super();
		_project = aProject;
	}

	public String getName() {
		return getIdentifier();
	}

	public String getIdentifier() {
		return _identifier;
	}

	public void setIdentifier(String identifier) {
		_identifier = identifier;
	}

	public File getDirectory() {
		if (_directory == null) {
			String s = directoriesForUser.get(getUserName());
			if (s != null) {
				_directory = new File(s);
			}
			_isConnected = _directory != null && _directory.exists();
		}
		return _directory;
	}

	public void setDirectory(File directory) {
		_directory = directory;
		if (_directory != null) {
			directoriesForUser.put(getUserName(), _directory.getAbsolutePath());
		} else {
			directoriesForUser.remove(getUserName());
		}
		_isConnected = _directory != null && _directory.exists();
		getProject().clearCachedFiles();
		getProject().notifyResourceChanged(null);
	}

	public FlexoProject getProject() {
		return _project;
	}

	public String getSerializationIdentifier() {
		return getProject().getUserIdentifier() + "_" + getIdentifier();
	}

	@Override
	public String toString() {
		return "ProjectExternalRepository:" + getName() + "[" + (getDirectory() != null ? getDirectory().getAbsolutePath() : "null") + "]";
	}

	private boolean _isConnected = false;
	private boolean _isNormallyConnected = false;

	public boolean isConnected() {
		return _isConnected;
	}

	public boolean shouldBeConnected() {
		return _isNormallyConnected;
	}

	public boolean _getIsConnected() {
		return isConnected();
	}

	public void _setIsConnected(boolean aBoolean) {
		_isNormallyConnected = aBoolean;
	}

	public List<FlexoFileResource<? extends FlexoResourceData>> getRelatedResources() {
		List<FlexoFileResource<? extends FlexoResourceData>> returned = new Vector<FlexoFileResource<? extends FlexoResourceData>>();
		for (FlexoResource<? extends FlexoResourceData> resource : getProject()) {
			if (resource instanceof FlexoFileResource) {
				FlexoProjectFile pFile = ((FlexoFileResource<? extends FlexoResourceData>) resource).getResourceFile();
				if (pFile.getExternalRepository() == this) {
					returned.add((FlexoFileResource<? extends FlexoResourceData>) resource);
				}
			}
		}
		return returned;
	}

	public List<FlexoFileResource<? extends FlexoResourceData>> getRelatedActiveResources() {
		List<FlexoFileResource<? extends FlexoResourceData>> returned = new Vector<FlexoFileResource<? extends FlexoResourceData>>();
		for (FlexoFileResource<? extends FlexoResourceData> resource : getRelatedResources()) {
			if (resource.isActive()) {
				returned.add(resource);
			}
		}
		return returned;
	}

	public Map<String, String> getDirectoriesForUser() {
		return directoriesForUser;
	}

	public void setDirectoriesForUser(Map<String, String> directoriesForUser) {
		this.directoriesForUser = new TreeMap<String, String>(directoriesForUser);
	}

	public void setDirectoriesForUserForKey(String directory, String user) {
		directoriesForUser.put(user, directory);
	}

	public void removeDirectoriesForUserWithKey(String key) {
		this.directoriesForUser.remove(key);
	}

}
