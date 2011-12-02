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

import java.util.Date;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGFile.FileContentEditor;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.generator.IFlexoResourceGenerator;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.rm.GeneratedResourceData;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.toolbox.FileResource;

public class CopyOfFileResource<D extends GeneratedResourceData, G extends IFlexoResourceGenerator, F extends CGFile> extends
		CGRepositoryFileResource<D, G, F> {

	private FileResource resourceToCopy;
	private String _path;

	/**
	 * @param aProject
	 */
	public CopyOfFileResource(FlexoProject aProject, FileResource resourceToCopy) {
		super(aProject);
		this.resourceToCopy = resourceToCopy;
		_path = resourceToCopy.getInternalPath();
	}

	public CopyOfFileResource(FlexoProject aProject) {
		super(aProject);
	}

	@Override
	public boolean ensureGenerationIsUpToDate() throws FlexoException {
		return true;
	}

	/**
     *
     */
	public CopyOfFileResource(FlexoProjectBuilder builder) {
		this(builder.project);
	}

	/**
	 * Overrides isGeneratedResourceDataReadable
	 * 
	 * @see org.openflexo.foundation.rm.FlexoGeneratedResource#isGeneratedResourceDataReadable()
	 */
	@Override
	public boolean isGeneratedResourceDataReadable() {
		return false;
	}

	/**
	 * @param repository
	 * @param resourceToCopy2
	 * @return
	 */
	public static String nameForCopiedResource(GenerationRepository repository, FileResource res) {
		return "DUPLICATION_OF_" + res.getName() + "_IN_REPOSITORY_" + repository.getName();
	}

	/**
	 * Overrides getResourceType
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResource#getResourceType()
	 */
	@Override
	public ResourceType getResourceType() {
		return ResourceType.COPIED_FILE;
	}

	private Date _lastGenerationDate;

	@Override
	public Date getLastGenerationDate() {
		if (_lastGenerationDate == null || getDiskLastModifiedDate().before(_lastGenerationDate)) {
			_lastGenerationDate = getDiskLastModifiedDate();
		}
		return _lastGenerationDate;
	}

	@Override
	public void setLastGenerationDate(Date aDate) {
		_lastGenerationDate = aDate;
	}

	/**
	 * Overrides getLastAcceptingDate
	 * 
	 * @see org.openflexo.foundation.rm.cg.CGRepositoryFileResource#getLastAcceptingDate()
	 */
	@Override
	public Date getLastAcceptingDate() {
		// Copied resource cannot update from disk-->lastAcceptingDate is always diskLastModified
		return getDiskLastModifiedDate();
	}

	/**
	 * Overrides saveEditedVersion
	 * 
	 * @see org.openflexo.foundation.rm.cg.CGRepositoryFileResource#saveEditedVersion(org.openflexo.foundation.cg.CGFile.FileContentEditor)
	 */
	@Override
	public void saveEditedVersion(FileContentEditor editor) throws SaveResourceException {

	}

	@Override
	protected D createGeneratedResourceData() {
		return null;
	}

	@Override
	public D readGeneratedResourceData() throws FlexoException {
		return null;
	}

	public FileResource getResourceToCopy() {
		if (resourceToCopy == null && _path != null) {
			resourceToCopy = new FileResource(_path);
		}
		return resourceToCopy;
	}

	public void setResourceToCopyPath(String path) {
		_path = path;
	}

	public String getResourceToCopyPath() {
		if (getResourceToCopy() != null) {
			return getResourceToCopy().getInternalPath();
		}
		return _path;
	}

	public static String nameForRepositoryAndFileToCopy(GenerationRepository repository, FileResource resourceToCopy) {
		return repository.getName() + ".DUPLICATION_OF." + resourceToCopy.getName();
	}

}
