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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.dm.eo.DMEOModel;
import org.openflexo.foundation.dm.eo.DMEORepository;
import org.openflexo.foundation.dm.eo.EOAccessException;
import org.openflexo.foundation.dm.eo.EOModelAlreadyRegisteredException;
import org.openflexo.foundation.dm.eo.InvalidEOModelFileException;
import org.openflexo.foundation.rm.FlexoDMResource;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.ProjectRestructuration;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.xml.FlexoDMBuilder;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.FileUtils;

/**
 * Represents the EOPrototype repository
 * 
 * @author sguerin
 * 
 */
public class FlexoExecutionModelRepository extends DMEORepository {

	private static final Logger logger = Logger.getLogger(FlexoExecutionModelRepository.class.getPackage().getName());

	public static final File EXECUTION_MODEL_DIR = new FileResource("Library/FlexoExecutionModel.eomodeld");

	// ==========================================================================
	// ============================= Instance variables
	// =========================
	// ==========================================================================

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Constructor used during deserialization
	 */
	public FlexoExecutionModelRepository(FlexoDMBuilder builder) {
		this(builder.dmModel);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public FlexoExecutionModelRepository(DMModel dmModel) {
		super(dmModel);
	}

	@Override
	public DMRepositoryFolder getRepositoryFolder() {
		return getDMModel().getInternalRepositoryFolder();
	}

	/**
	 * Overrides getInspectorName
	 * 
	 * @see org.openflexo.foundation.dm.DMRepository#getInspectorName()
	 */
	@Override
	public String getInspectorName() {
		return Inspectors.DM.DM_RO_EO_REPOSITORY_INSPECTOR;
	}

	public static File copyExecutionModelIntoPrj(File projectDirectory) {

		File executionModelFile = EXECUTION_MODEL_DIR;

		File copiedDirectory = ProjectRestructuration.getExpectedDataModelDirectory(projectDirectory);
		File toCopy = executionModelFile;
		File copy = new File(copiedDirectory, toCopy.getName());
		if (logger.isLoggable(Level.INFO))
			logger.info("Copying file " + toCopy.getAbsolutePath() + " to " + copy.getAbsolutePath());
		try {
			FileUtils.copyDirToDir(toCopy, copiedDirectory);
		} catch (IOException e) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("Could not copy " + executionModelFile.getAbsolutePath() + " to " + copy.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return copy;
	}

	/**
	 * @param dmModel
	 * @return
	 */
	public static FlexoExecutionModelRepository createNewExecutionModelRepository(DMModel dmModel, FlexoDMResource dmRes) {
		FlexoExecutionModelRepository newExecutionModelRepository = new FlexoExecutionModelRepository(dmModel);
		newExecutionModelRepository.setDontGenerate(true);
		File copy = copyExecutionModelIntoPrj(dmModel.getProject().getProjectDirectory());

		FlexoProjectFile eoPrototypeEOModelFile = new FlexoProjectFile(copy, dmModel.getProject());

		dmModel.setExecutionModelRepository(newExecutionModelRepository);
		try {
			newExecutionModelRepository.importEOModelFile(eoPrototypeEOModelFile, dmModel, dmRes);
		} catch (InvalidEOModelFileException e) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("Could not import EOModel:" + eoPrototypeEOModelFile.getFile().getName());
		} catch (EOModelAlreadyRegisteredException e) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("Could not import EOModel:" + eoPrototypeEOModelFile.getFile().getName() + " already registered");
		} catch (InvalidFileNameException e) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("Could not import EOModel:" + eoPrototypeEOModelFile.getFile().getName() + " is not a valid file name");
		}

		DMEOModel executionModelEOModel = newExecutionModelRepository.getExecutionModelEOModel();
		try {
			executionModelEOModel.updateFromEOModel();
		} catch (EOAccessException e) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING))
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			e.printStackTrace();
		}

		return newExecutionModelRepository;
	}

	@Override
	public String getFullyQualifiedName() {
		return getDMModel().getFullyQualifiedName() + ".EXECUTION_MODEL";
	}

	@Override
	public String getName() {
		return "execution_model_repository";
	}

	@Override
	public String getLocalizedName() {
		return FlexoLocalization.localizedForKey(getName());
	}

	@Override
	public void setName(String name) {
		// Not allowed
	}

	@Override
	public int getOrder() {
		return 13;
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public boolean isDeletable() {
		return false;
	}

	public DMEOModel getExecutionModelEOModel() {
		if (getDMEOModels().size() == 1) {
			Enumeration en = getDMEOModels().elements();
			return (DMEOModel) en.nextElement();
		} else {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("Inconsistant data in FlexoExecutionModel repository");
		}
		return null;
	}

	public DMEOEntity getProcessInstanceEntity() {
		DMEOEntity returned = (DMEOEntity) getDMEntity("be.denali.flexo.engine.db.ProcessInstance");
		// if (returned == null) {
		// // Try with default package
		// returned = (DMEOEntity) getDMEntity(getDefaultPackage(), "ProcessInstance");
		// }
		if (returned == null) {
			// Ca va foutre la zone, on previent...
			logger.severe("Could not find entity ProcessInstance: is FlexoExecutionModelRepository loaded ?");
		}
		return returned;
	}

	public DMEOEntity getActivityTaskEntity() {
		return (DMEOEntity) getDMEntity("be.denali.flexo.engine.db.ActivityTask");
	}

	public DMEOEntity getOperationTaskEntity() {
		return (DMEOEntity) getDMEntity("be.denali.flexo.engine.db.OperationTask");
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return getName();
	}

}
