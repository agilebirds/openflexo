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

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.CGFile.FileContentEditor;
import org.openflexo.foundation.cg.generator.IFlexoResourceGenerator;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.rm.cg.GenerationAvailableFileResourceInterface;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileFormat;

/**
 * @author gpolet
 * 
 */
public class FlexoCopiedResource extends CGRepositoryFileResource<CopiedFileData, IFlexoResourceGenerator, CGFile> implements
		GenerationAvailableFileResourceInterface, FlexoObserver {

	private static final Logger logger = FlexoLogger.getLogger(FlexoCopiedResource.class.getPackage().getName());

	private FlexoFileResource resourceToCopy;

	/**
	 * @param aProject
	 */
	private FlexoCopiedResource(FlexoProject aProject) {
		super(aProject);
	}

	/**
	 * @param aProject
	 */
	public FlexoCopiedResource(FlexoProject aProject, FlexoFileResource resourceToCopy) {
		this(aProject);
		setResourceToCopy(resourceToCopy);
	}

	/**
     *
     */
	public FlexoCopiedResource(FlexoProjectBuilder builder) {
		this(builder.project);
	}

	/**
	 * Overrides createGeneratedResourceData
	 * 
	 * @see org.openflexo.foundation.rm.FlexoGeneratedResource#createGeneratedResourceData()
	 */
	@Override
	protected CopiedFileData createGeneratedResourceData() {
		return new CopiedFileData(this);
	}

	@Override
	public CopiedFileData readGeneratedResourceData() throws FlexoException {
		return getGeneratedResourceData();
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

	public FlexoFileResource getResourceToCopy() {
		if ((resourceToCopy != null) && resourceToCopy.isDeleted()) {
			return null;
		}
		return resourceToCopy;
	}

	public void setResourceToCopy(FlexoFileResource resource) {
		resourceToCopy = resource;
		if (resourceToCopy != null) {
			resourceToCopy.getProject().addObserver(this);
		}
	}

	/**
	 * Overrides getName
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResource#getName()
	 */
	@Override
	public String getName() {
		if (getResourceToCopy() != null) {
			if ((getCGFile() != null) && getCGFile().isMarkedForDeletion()) {
				return super.getName();
			}
			if ((getCGFile() != null) && (getCGFile().getRepository() != null)) {
				String name = nameForCopiedResource(getCGFile().getRepository(), getResourceToCopy());
				if (!name.equals(super.getName()) && !project.isDeserializing() && !project.isSerializing()) {
					setName(name);
				}
			}

			if ((getFileName() != null) && (getResourceToCopy().getFileName() != null)
					&& !getFileName().equals(getResourceToCopy().getFileName()) && !isDeleted()) {
				try {
					renameFileTo(getResourceToCopy().getFileName());
				} catch (InvalidFileNameException e) {
					e.printStackTrace();
				}
			}
		}
		return super.getName();
	}

	/**
	 * Overrides setName
	 * 
	 * @see org.openflexo.foundation.rm.cg.CGRepositoryFileResource#setName(java.lang.String)
	 */
	@Override
	public void setName(String aName) {
		if (aName == null) {
			return;
		}
		if (aName.equals(super.getName())) {
			return;
		}
		String old = super.getName();
		super.setName(aName);
		if (!isDeleted() && !project.isDeserializing()) {
			if (old != null) {
				try {
					getProject().renameResource(this, aName);
				} catch (DuplicateResourceException e) {
					e.printStackTrace();
					super.setName(old);
				}
			}
			if ((getResourceToCopy() != null) && (getFileName() != null) && (getResourceToCopy().getFileName() != null)
					&& !getFileName().equals(getResourceToCopy().getFileName())) {
				try {
					renameFileTo(getResourceToCopy().getFileName());
				} catch (InvalidFileNameException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public boolean checkIntegrity() {
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
	 * @param repository
	 * @param resourceToCopy2
	 * @return
	 */
	public static String nameForCopiedResource(GenerationRepository repository, FlexoFileResource res) {
		return "COPY_OF_" + res.getName() + "_IN_REPOSITORY_" + repository.getName();
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

	@Override
	public FileFormat getResourceFormat() {
		if (getResourceToCopy() != null) {
			return getResourceToCopy().getResourceFormat();
		}
		return super.getResourceFormat();
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
	 * Overrides rebuildDependancies
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResource#rebuildDependancies()
	 */
	@Override
	public void rebuildDependancies() {
		super.rebuildDependancies();
		if (getResourceToCopy() != null) {
			addToDependantResources(getResourceToCopy());
		}
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
	public void update(FlexoObservable observable, DataModification dataModification) {
		if ((dataModification instanceof ResourceRemoved) && (((ResourceRemoved) dataModification).getRemovedResource() == resourceToCopy)) {
			if (getCGFile() == null) {
				this.delete();
			} else {
				getCGFile().setMarkedForDeletion(true);
				if (getGenerator() != null) {
					getGenerator().refreshConcernedResources();
				}
				getCGFile().getRepository().refresh();
			}
		}
	}

}
