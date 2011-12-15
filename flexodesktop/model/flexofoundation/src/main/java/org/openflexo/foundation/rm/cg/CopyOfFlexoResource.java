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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGFile.FileContentEditor;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.generator.IFlexoResourceGenerator;
import org.openflexo.foundation.rm.FlexoFileResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.rm.GeneratedResourceData;
import org.openflexo.foundation.rm.ResourceRemoved;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.toolbox.FileFormat;

public class CopyOfFlexoResource<G extends IFlexoResourceGenerator, F extends CGFile> extends
		CGRepositoryFileResource<GeneratedResourceData, G, F> implements FlexoObserver {

	private static final Logger logger = Logger.getLogger(CopyOfFlexoResource.class.getPackage().getName());

	private FlexoFileResource resourceToCopy;

	/**
	 * @param aProject
	 */
	public CopyOfFlexoResource(FlexoProject aProject, FlexoFileResource resourceToCopy) {
		this(aProject);
		setResourceToCopy(resourceToCopy);
	}

	private CopyOfFlexoResource(FlexoProject aProject) {
		super(aProject);
	}

	/**
     *
     */
	public CopyOfFlexoResource(FlexoProjectBuilder builder) {
		this(builder.project);
	}

	@Override
	public synchronized void delete(boolean deleteFile) {
		if ((getCGFile() != null) && !getCGFile().isMarkedForDeletion()) {
			getCGFile().setMarkedForDeletion(true);
			getCGFile().delete(deleteFile);
		}
		super.delete(deleteFile);
	}

	@Override
	public boolean checkIntegrity() {
		if (isDeleted()) {
			return false;
		}
		if (!isDeleted() && !project.isDeserializing() && (getProject() != null) && (getProject().getResourceManagerInstance() != null)
				&& !getProject().getResourceManagerInstance().isLoadingAProject()) {
			if (resourceToCopy == null) {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("This copied resource is no more acceptable because resourceToCopy is null");
				}
				return false;
			} else if (!resourceToCopy.checkIntegrity()) {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("This copied resource is no more acceptable because resourceToCopy "
							+ resourceToCopy.getFullyQualifiedName() + " does not pass integrity check");
				}
				return false;
			} else if (getProject() == null) {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("This copied resource is no more acceptable because project is null");
				}
				return false;
			} else if (getProject().resourceForKey(resourceToCopy.getFullyQualifiedName()) == null) {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("This copied resource is no more acceptable because resourceToCopy is not in project anymore");
				}
				return false;
			}
		}
		return super.checkIntegrity();
	}

	/**
	 * Overrides isGeneratedResourceDataReadable
	 * 
	 * @see org.openflexo.foundation.rm.FlexoGeneratedResource#isGeneratedResourceDataReadable()
	 */
	@Override
	public boolean isGeneratedResourceDataReadable() {
		return true;
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
		if (_lastGenerationDate == null) {
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
	protected GeneratedResourceData createGeneratedResourceData() {
		return null;
	}

	@Override
	public GeneratedResourceData readGeneratedResourceData() throws FlexoException {
		return createGeneratedResourceData();
	}

	@Override
	public void rebuildDependancies() {
		super.rebuildDependancies();
		if (getResourceToCopy() != null) {
			resourceToCopy.rebuildDependancies();
			addToDependentResources(resourceToCopy);
		}
	}

	public FlexoFileResource getResourceToCopy() {
		if ((resourceToCopy != null) && resourceToCopy.isDeleted()) {
			return null;
		}
		return resourceToCopy;
	}

	public void setResourceToCopy(FlexoFileResource res) {
		resourceToCopy = res;
		if (resourceToCopy != null) {
			resourceToCopy.getProject().addObserver(this);
		}
	}

	public static String nameForRepositoryAndResource(GenerationRepository repository, FlexoFileResource resourceToCopy) {
		return repository.getName() + ".COPY_OF." + resourceToCopy.getFullyQualifiedName();
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if ((dataModification instanceof ResourceRemoved) && (((ResourceRemoved) dataModification).getRemovedResource() == resourceToCopy)) {
			if (getCGFile() == null) {
				this.delete();
			} else {
				getCGFile().setMarkedForDeletion(true);
			}
		}
	}

	@Override
	public FileFormat getResourceFormat() {
		if (resourceToCopy != null) {
			return resourceToCopy.getResourceFormat();
		}
		return super.getResourceFormat();
	}

}
