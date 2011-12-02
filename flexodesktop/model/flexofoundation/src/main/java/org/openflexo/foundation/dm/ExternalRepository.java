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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoJarResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.ProjectRestructuration;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.xml.FlexoDMBuilder;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileUtils;

/**
 * Represents a logical group of objects stored in an external jar file
 * 
 * @author sguerin
 * 
 */
public class ExternalRepository extends DMRepository {

	private static final Logger logger = Logger.getLogger(ExternalRepository.class.getPackage().getName());

	protected FlexoJarResource _jarResource;
	private boolean isImportedByUser = false;

	public boolean getIsImportedByUser() {
		return isImportedByUser;
	}

	public void setIsImportedByUser(boolean isImportedByUser) {
		this.isImportedByUser = isImportedByUser;
	}

	/**
	 * Constructor used during deserialization
	 */
	public ExternalRepository(FlexoDMBuilder builder) {
		this(builder.dmModel);
		initializeDeserialization(builder);
	}

	@Override
	protected FlexoDMBuilder getBuilder() {
		return (FlexoDMBuilder) super.getBuilder();
	}

	/**
	 * Default constructor
	 */
	private ExternalRepository(DMModel dmModel) {
		super(dmModel);
	}

	@Override
	public DMRepositoryFolder getRepositoryFolder() {
		return getDMModel().getLibraryRepositoryFolder();
	}

	/**
	 * @param dmModel
	 * @return
	 */
	public static ExternalRepository createNewExternalRepository(DMModel dmModel, File aJarFile, DMSet importedClassSet)
			throws DuplicateResourceException {
		return createNewExternalRepository(dmModel, aJarFile, importedClassSet, null);
	}

	public static Enumeration<Class<?>> getContainedClasses(File aJarFile, ExternalRepository jarRepository, FlexoProject project,
			FlexoProgress progress) {
		// Parse the JAR file
		JarLoader jarLoader = new JarLoader(aJarFile, jarRepository, project, progress);
		return jarLoader.getContainedClasses().elements();
	}

	/**
	 * @param dmModel
	 * @return
	 * @throws DuplicateResourceException
	 */
	public static ExternalRepository createNewExternalRepository(DMModel dmModel, File aJarFile, DMSet importedClassSet,
			FlexoProgress progress) throws DuplicateResourceException {
		Date jarCreationDate = new Date();

		// Creates the repository
		ExternalRepository newExternalRepository = new ExternalRepository(dmModel);

		// Copy JAR file
		File copiedFile = new File(ProjectRestructuration.getExpectedDataModelDirectory(dmModel.getProject().getProjectDirectory()),
				aJarFile.getName());
		if (progress != null) {
			progress.setProgress(FlexoLocalization.localizedForKey("copying") + " " + aJarFile.getName());
		}
		try {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Copying file " + aJarFile.getAbsolutePath() + " to " + copiedFile.getAbsolutePath());
			}
			FileUtils.copyFileToFile(aJarFile, copiedFile);
		} catch (IOException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not copy file " + aJarFile.getAbsolutePath() + " to " + copiedFile.getAbsolutePath());
			}
		}

		// Perform some settings
		FlexoProjectFile jarFile = new FlexoProjectFile(copiedFile, dmModel.getProject());
		newExternalRepository.setName(copiedFile.getName());
		newExternalRepository.setJarFile(jarFile, dmModel);
		newExternalRepository.getJarResource()._setLastWrittenOnDisk(jarCreationDate);

		// Add to the data model
		dmModel.addToExternalRepositories(newExternalRepository);

		// Parse the JAR file
		// JarLoader jarLoader = new JarLoader(copiedFile);
		// newExternalRepository.setJarLoader(jarLoader);

		JarLoader jarLoader = newExternalRepository.getJarLoader();

		if (progress != null) {
			progress.setProgress(FlexoLocalization.localizedForKey("importing_classes_from") + " " + aJarFile.getName());
			progress.resetSecondaryProgress(jarLoader.getContainedClasses().size());
		}
		for (Enumeration e = jarLoader.getContainedClasses().elements(); e.hasMoreElements();) {
			Class next = (Class) e.nextElement();
			if (next == null) {
				logger.warning("Entity: null IGNORED");
			} else {
				if (importedClassSet == null || importedClassSet.containsSelectedClass(next)) {
					if (progress != null) {
						progress.setSecondaryProgress(FlexoLocalization.localizedForKey("importing_class") + " " + next.getName());
					}
					logger.info("Import " + next);
					if (importedClassSet == null) {
						LoadableDMEntity.createLoadableDMEntity(dmModel, next);
					} else {
						LoadableDMEntity.createLoadableDMEntity(next, dmModel, importedClassSet.getImportGetOnlyProperties(),
								importedClassSet.getImportMethods());
					}
				} else {
					if (progress != null) {
						progress.setSecondaryProgress(FlexoLocalization.localizedForKey("ignoring_class") + " " + next.getName());
					}
					logger.fine("Ignore " + next);
				}
			}
		}

		// Returns
		return newExternalRepository;
	}

	@Override
	public int getOrder() {
		return 8;
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public boolean isDeletable() {
		return true;
	}

	@Override
	public boolean isUpdatable() {
		return true;
	}

	@Override
	public final void delete() {
		delete(false);
	}

	public final void delete(boolean deleteJarFile) {
		if (getJarResource() != null) {
			getJarResource().delete(deleteJarFile);
		}
		getDMModel().removeFromExternalRepositories(this);
		super.delete();
		deleteObservers();
	}

	/*public FlexoProjectFile getJarFile()
	{
	    return jarFile;
	}

	public void setJarFile(FlexoProjectFile jarFile)
	{
	    this.jarFile = jarFile;
	}

	protected JarLoader getJarLoader()
	{
	    if (jarLoader == null) {
	        jarLoader = new JarLoader(jarFile.getFile());
	    }
	    return jarLoader;
	}

	private void setJarLoader(JarLoader jarLoader)
	{
	    this.jarLoader = jarLoader;
	}

	 */

	public FlexoJarResource getJarResource() {
		return _jarResource;
	}

	public void setJarResource(FlexoJarResource jarResource) {
		_jarResource = jarResource;
	}

	public JarLoader getJarLoader() {
		if (getJarResource() != null) {
			if (getJarResource().isLoaded()) {
				try {
					return getJarResource().getImportedData();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (ProjectLoadingCancelledException e1) {
					e1.printStackTrace();
				} catch (FlexoException e1) {
					e1.printStackTrace();
				}
			} else if (!jarIsLoading) {
				jarIsLoading = true;
				JarLoader returned = null;
				try {
					returned = getJarResource().getImportedData();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (ProjectLoadingCancelledException e) {
					e.printStackTrace();
				} catch (FlexoException e) {
					e.printStackTrace();
				}
				jarIsLoading = false;
				return returned;
			}
		}
		return null;
	}

	private boolean jarIsLoading = false;

	public FlexoProjectFile getJarFile() {
		if (getJarResource() != null) {
			return getJarResource().getResourceFile();
		}
		return null;
	}

	public void setJarFile(FlexoProjectFile jarFile) throws DuplicateResourceException {
		if (isDeserializing()) {
			setJarFile(jarFile, getBuilder().dmModel);
		} else {
			setJarFile(jarFile, getProject().getDataModel());
		}
	}

	public void setJarFile(FlexoProjectFile jarFile, DMModel dmModel) throws DuplicateResourceException {
		jarFile.setProject(getProject());
		if (_jarResource == null) {
			_jarResource = getProject().getJarResource(jarFile);
			if (_jarResource == null) {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Could not find nor create resource for JAR " + jarFile.getFile().getName() + ", create entry");
				}
				_jarResource = new FlexoJarResource(getProject(), this, dmModel.getFlexoResource(), jarFile);
				_jarResource.setLastImportDate(new Date(0));
				getProject().registerResource(_jarResource);

			}
			_jarResource.setDMModel(getDMModel());
			_jarResource.setJarRepository(this);
		}
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "external_repository";
	}

}
