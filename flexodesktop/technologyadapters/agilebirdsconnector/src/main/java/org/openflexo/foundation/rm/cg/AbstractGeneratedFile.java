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
package org.openflexo.foundation.rm.cg;

import java.io.File;

import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.cg.dm.CGContentRegenerated;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoFileResource;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.GeneratedResourceData;
import org.openflexo.toolbox.FileFormat;

public abstract class AbstractGeneratedFile implements GeneratedResourceData {
	private File _file;
	private CGRepositoryFileResource _resource;

	/**
	 * 
	 */
	public AbstractGeneratedFile() {
		super();
	}

	/**
	 * 
	 */
	public AbstractGeneratedFile(File f) {
		this();
		setFile(f);
	}

	/**
	 * Overrides getFlexoResource
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResourceData#getFlexoResource()
	 */
	@Override
	public CGRepositoryFileResource getFlexoResource() {
		return _resource;
	}

	public void setFlexoResource(CGRepositoryFileResource resource) throws DuplicateResourceException {
		_resource = resource;
	}

	@Override
	public void setFlexoResource(FlexoResource resource) throws DuplicateResourceException {
		_resource = (CGRepositoryFileResource) resource;
	}

	public FileFormat getFileFormat() {
		return getFlexoResource().getResourceFormat();
	}

	@Override
	public FlexoProject getProject() {
		return getFlexoResource().getProject();
	}

	public File getFile() {
		return _file;
	}

	public void setFile(File file) {
		_file = file;
	}

	private FileHistory _history = null;

	public FileHistory getHistory() {
		if (_history == null) {
			_history = new FileHistory(getFlexoResource());
		}
		return _history;
	}

	public void updateHistory() {
		if (manageHistory()) {
			getHistory().update();
		}
	}

	public boolean manageHistory() {
		if (getFlexoResource() == null || getFlexoResource().getCGFile() == null || getFlexoResource().getCGFile().getRepository() == null) {
			return false;
		}
		return getFlexoResource().getCGFile().getRepository().getManageHistory();
	}

	public boolean fileOnDiskHasBeenEdited() {
		return getFlexoResource().getDiskLastModifiedDate().getTime() > getFlexoResource().getLastGenerationDate().getTime()
				+ FlexoFileResource.ACCEPTABLE_FS_DELAY;
	}

	public abstract void load() throws LoadGeneratedResourceIOException;

	public abstract boolean hasVersionOnDisk();

	public void notifyRegenerated(CGContentRegenerated notification) {
		// Override it if required
	}

	public abstract void notifyVersionChangedOnDisk();

	public abstract void acceptDiskVersion() throws SaveGeneratedResourceIOException;

	public abstract void overrideWith(ContentSource version);

	public abstract void cancelOverriding();

	public abstract boolean isOverrideScheduled();

	public abstract ContentSource getScheduledOverrideVersion();

	/**
	 * Returns flag indicating if merge for generation is actually raising conflicts (collision in changes)
	 */
	public abstract boolean isGenerationConflicting();

	public abstract boolean doesGenerationKeepFileUnchanged();

	public abstract boolean isTriviallyMergable();

	public abstract boolean areAllConflictsResolved();

}