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
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.dm.eo.DMEOModel;
import org.openflexo.foundation.dm.eo.DMEORepository;
import org.openflexo.foundation.dm.eo.EOAccessException;
import org.openflexo.foundation.dm.eo.EOModelAlreadyRegisteredException;
import org.openflexo.foundation.dm.eo.InvalidEOModelFileException;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.ProjectRestructuration;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.xml.FlexoDMBuilder;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileUtils;

/**
 * Represents a Denali library
 * 
 * @author sguerin
 * 
 */
public class DenaliFoundationRepository extends DMEORepository {

	private static final Logger logger = Logger.getLogger(DenaliFoundationRepository.class.getPackage().getName());

	public static final String DENALI_FOUNDATION_REPOSITORY_NAME = "DenaliFoundation";

	public static final String DENALI_FLEXO_REPOSITORY_NAME = "DenaliFlexo";

	/**
	 * Constructor used during deserialization
	 */
	public DenaliFoundationRepository(FlexoDMBuilder builder) {
		this(builder.dmModel);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	private DenaliFoundationRepository(DMModel dmModel) {
		super(dmModel);
	}

	/**
	 * @param dmModel
	 * @return
	 */
	public static DenaliFoundationRepository createNewDenaliFoundationRepository(DMModel dmModel, File aDenaliFoundationDirectory)
			throws InvalidFileException {
		return createNewDenaliFoundationRepository(dmModel, aDenaliFoundationDirectory, null);
	}

	@Override
	public DMRepositoryFolder getRepositoryFolder() {
		return getDMModel().getLibraryRepositoryFolder();
	}

	/**
	 * @param dmModel
	 * @return
	 */
	public static DenaliFoundationRepository createNewDenaliFoundationRepository(DMModel dmModel, File aDenaliFoundationDirectory,
			FlexoProgress progress) throws InvalidFileException {
		Vector<File> eoModelFiles = new Vector<File>();
		DenaliFoundationRepository newDenaliFoundationRepository = new DenaliFoundationRepository(dmModel);
		File copiedDirectory = new File(ProjectRestructuration.getExpectedDataModelDirectory(dmModel.getProject().getProjectDirectory()),
				aDenaliFoundationDirectory.getName());
		File[] eoModelArray = aDenaliFoundationDirectory.listFiles(new java.io.FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.isDirectory() && f.getName().endsWith(".eomodeld");
			}
		});
		if (eoModelArray.length == 0) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("This repository contains no valid file. Operation aborted.");
			throw new InvalidFileException(aDenaliFoundationDirectory);
		}
		if (progress != null) {
			progress.setProgress(FlexoLocalization.localizedForKey("copying") + " " + aDenaliFoundationDirectory.getName());
			progress.resetSecondaryProgress(eoModelArray.length + 1);
		}
		copiedDirectory.mkdirs();
		for (int i = 0; i < eoModelArray.length; i++) {
			File toCopy = eoModelArray[i];
			File copy = new File(copiedDirectory, toCopy.getName());
			if (progress != null) {
				progress.setSecondaryProgress(FlexoLocalization.localizedForKey("copying_file") + " " + toCopy.getName());
			}
			if (logger.isLoggable(Level.INFO))
				logger.info("Copying file " + toCopy.getAbsolutePath() + " to " + copy.getAbsolutePath());
			try {
				FileUtils.copyDirToDir(toCopy, copiedDirectory);
				eoModelFiles.add(copy);
			} catch (IOException e) {
				if (logger.isLoggable(Level.WARNING))
					logger.warning("Could not copy directory " + aDenaliFoundationDirectory.getAbsolutePath() + " to "
							+ copiedDirectory.getAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		FlexoProjectFile denaliFoundationDirectory = new FlexoProjectFile(copiedDirectory, dmModel.getProject());

		// Sets name
		String fullName = copiedDirectory.getName();
		if (fullName.indexOf(".dmrepository") > 0) {
			newDenaliFoundationRepository.setName(fullName.substring(0, fullName.indexOf(".dmrepository")));
		} else {
			newDenaliFoundationRepository.setName(fullName);
		}

		newDenaliFoundationRepository.setDenaliFoundationDirectory(denaliFoundationDirectory);
		dmModel.addToDenaliFoundationRepositories(newDenaliFoundationRepository);

		if (progress != null) {
			progress.setProgress(FlexoLocalization.localizedForKey("register_eomodels") + " " + aDenaliFoundationDirectory.getName());
			progress.resetSecondaryProgress(eoModelArray.length + 1);
		}

		for (Enumeration en = eoModelFiles.elements(); en.hasMoreElements();) {
			File eoModelDir = (File) en.nextElement();
			if (progress != null) {
				progress.setSecondaryProgress(FlexoLocalization.localizedForKey("loading") + " " + eoModelDir.getName() + "...");
			}
			try {
				FlexoProjectFile eoModelDirectory = new FlexoProjectFile(eoModelDir, dmModel.getProject());
				newDenaliFoundationRepository.importEOModelFile(eoModelDirectory);
			} catch (InvalidEOModelFileException e) {
				if (logger.isLoggable(Level.WARNING))
					logger.warning("Could not import EOModel:" + eoModelDir.getName());
			} catch (EOModelAlreadyRegisteredException e) {
				if (logger.isLoggable(Level.WARNING))
					logger.warning("Could not import EOModel:" + eoModelDir.getName() + " : eomodel already registered");
			} catch (InvalidFileNameException e) {
				if (logger.isLoggable(Level.WARNING))
					logger.warning("Could not import EOModel:" + eoModelDir.getName() + " : eomodel name is invalid");
			}
		}

		if (progress != null) {
			progress.setProgress(FlexoLocalization.localizedForKey("loading_eomodels") + " " + aDenaliFoundationDirectory.getName());
			progress.resetSecondaryProgress(newDenaliFoundationRepository.getDMEOModels().size() + 1);
		}

		Enumeration en = newDenaliFoundationRepository.getDMEOModels().elements();
		if (logger.isLoggable(Level.INFO))
			logger.info("Loading attributes and relations");
		while (en.hasMoreElements()) {
			DMEOModel currentModel = (DMEOModel) en.nextElement();
			if (progress != null) {
				progress.setSecondaryProgress(FlexoLocalization.localizedForKey("loading") + " " + currentModel.getName() + "...");
			}
			try {
				currentModel.updateFromEOModel();
			} catch (EOAccessException e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING))
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				e.printStackTrace();
			}
		}

		return newDenaliFoundationRepository;
	}

	/**
	 * @param dmModel
	 * @return
	 */
	/*
	public static DenaliFoundationRepository createDenaliFoundationRepository(DMModel dmModel, FlexoProgress progress) throws InvalidFileException
	{
	    return createNewDenaliFoundationRepository(dmModel, DENALI_FOUNDATION_REPOSITORY_DIR, progress);
	}*/

	/**
	 * @param dmModel
	 * @return
	 */
	/*public static DenaliFoundationRepository createDenaliFlexoRepository(DMModel dmModel, FlexoProgress progress) throws InvalidFileException
	{
	    return createNewDenaliFoundationRepository(dmModel, DENALI_FLEXO_REPOSITORY_DIR, progress);
	}*/

	@Override
	public int getOrder() {
		return 6;
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
	public void delete(boolean deleteEOModelFiles) {
		getDMModel().removeFromDenaliFoundationRepositories(this);
		super.delete(deleteEOModelFiles);
	}

	private FlexoProjectFile _denaliFoundationDirectory;

	public FlexoProjectFile getDenaliFoundationDirectory() {
		return _denaliFoundationDirectory;
	}

	public void setDenaliFoundationDirectory(FlexoProjectFile denaliFoundationDirectory) {
		_denaliFoundationDirectory = denaliFoundationDirectory;
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "denali_foundation_repository";
	}

}
