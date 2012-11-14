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
package org.openflexo.fps;

import java.io.File;
import java.util.Vector;
import java.util.logging.Logger;

import org.netbeans.lib.cvsclient.admin.Entry;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.rm.ResourceType;

public class CVSDirectory extends CVSAbstractFile implements CVSContainer {

	private static final Logger logger = Logger.getLogger(CVSFile.class.getPackage().getName());

	private Entry _entry;

	public CVSDirectory(File localFile, Entry entry, SharedProject sharedProject) {
		this(localFile, sharedProject);
		_entry = entry;
	}

	public CVSDirectory(File localFile, SharedProject sharedProject) {
		super(localFile, sharedProject);
		_directories = new Vector<CVSDirectory>();
		_files = new Vector<CVSFile>();
	}

	@Override
	public String getInspectorName() {
		return Inspectors.FPS.CVS_DIRECTORY_INSPECTOR;
	}

	@Override
	public String getClassNameKey() {
		return "cvs_directory";
	}

	private Vector<CVSDirectory> _directories;

	@Override
	public Vector<CVSDirectory> getDirectories() {
		return _directories;
	}

	@Override
	public void setDirectories(Vector<CVSDirectory> directories) {
		_directories = directories;
	}

	@Override
	public void addToDirectories(CVSDirectory aDirectory) {
		_directories.add(aDirectory);
		aDirectory.setContainer(this);
	}

	@Override
	public void removeFromDirectories(CVSDirectory aDirectory) {
		_directories.remove(aDirectory);
		aDirectory.setContainer(null);
	}

	private Vector<CVSFile> _files;

	@Override
	public Vector<CVSFile> getFiles() {
		return _files;
	}

	@Override
	public void setFiles(Vector<CVSFile> files) {
		_files = files;
	}

	@Override
	public void addToFiles(CVSFile aFile) {
		_files.add(aFile);
		aFile.setContainer(this);
	}

	@Override
	public void removeFromFiles(CVSFile aFile) {
		_files.remove(aFile);
		aFile.setContainer(null);
	}

	@Override
	public boolean isRegistered(File aFile) {
		for (CVSFile f : getFiles()) {
			if (f.getFile().equals(aFile)) {
				return true;
			}
		}
		for (CVSDirectory d : getDirectories()) {
			if (d.getFile().equals(aFile)) {
				return true;
			}
		}
		return false;
	}

	public boolean existsOnCVSRepository() {
		return _entry != null;
	}

	public void clear() {
		// TODO remove properly (think about observers)
		for (CVSDirectory d : getDirectories()) {
			d.clear();
		}
		_directories.clear();
		_files.clear();
	}

	private ResourceType _resourceType;
	private boolean _triedToResolveResourceType = false;

	public ResourceType getResourceType() {
		if (!_triedToResolveResourceType) {
			_triedToResolveResourceType = true;
			if (getFileName().endsWith(".eomodeld")) {
				_resourceType = ResourceType.EOMODEL;
			}
			if (getFileName().endsWith(".wo.LAST_ACCEPTED")) {
				_resourceType = ResourceType.WO_FILE;
			}
			if (getFileName().endsWith(".wo.LAST_GENERATED")) {
				_resourceType = ResourceType.WO_FILE;
			}
		}
		return _resourceType;
	}

}
