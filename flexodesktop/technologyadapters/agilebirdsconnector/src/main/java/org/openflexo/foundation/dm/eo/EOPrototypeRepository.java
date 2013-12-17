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
package org.openflexo.foundation.dm.eo;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;

import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.DMRepositoryFolder;
import org.openflexo.foundation.dm.eo.model.EOAttribute;
import org.openflexo.foundation.resource.InvalidFileNameException;
import org.openflexo.foundation.rm.FlexoDMResource;
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
public class EOPrototypeRepository extends DMEORepository {

	public static final File EOPROTOTYPE_REPOSITORY_DIR = new FileResource("Library/EOPrototypes.eomodeld");

	/**
	 * Constructor used during deserialization
	 */
	public EOPrototypeRepository(FlexoDMBuilder builder) {
		this(builder.dmModel);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public EOPrototypeRepository(DMModel dmModel) {
		super(dmModel);
	}

	@Override
	public DMRepositoryFolder getRepositoryFolder() {
		return getDMModel().getInternalRepositoryFolder();
	}

	/**
	 * @param dmModel
	 * @return
	 */
	public static EOPrototypeRepository createNewEOPrototypeRepository(DMModel dmModel, FlexoDMResource dmRes) {
		EOPrototypeRepository newEOPrototypeRepository = new EOPrototypeRepository(dmModel);

		File eoPrototypeFile = EOPROTOTYPE_REPOSITORY_DIR;

		File copiedDirectory = ProjectRestructuration.getExpectedDataModelDirectory(dmModel.getProject().getProjectDirectory());
		File toCopy = eoPrototypeFile;
		File copy = new File(copiedDirectory, toCopy.getName());
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Copying file " + toCopy.getAbsolutePath() + " to " + copy.getAbsolutePath());
		}
		try {
			FileUtils.copyDirToDir(toCopy, copiedDirectory);
		} catch (IOException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not copy " + eoPrototypeFile.getAbsolutePath() + " to " + copy.getAbsolutePath());
			}
		}

		FlexoProjectFile eoPrototypeEOModelFile = new FlexoProjectFile(copy, dmModel.getProject());

		dmModel.setEOPrototypeRepository(newEOPrototypeRepository);
		try {
			newEOPrototypeRepository.importEOModelFile(eoPrototypeEOModelFile, dmModel, dmRes);
		} catch (InvalidEOModelFileException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not import EOModel:" + eoPrototypeEOModelFile.getFile().getName());
			}
		} catch (EOModelAlreadyRegisteredException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not import EOModel:" + eoPrototypeEOModelFile.getFile().getName() + " already registered");
			}
		} catch (InvalidFileNameException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("EOprototypes could not be created because their name is invalid.");
			}
		}

		DMEOModel eoPrototypeEOModel = newEOPrototypeRepository.getEOPrototypeEOModel();
		try {
			eoPrototypeEOModel.updateFromEOModel();
		} catch (EOAccessException e1) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e1.getClass().getName() + ". See console for details.");
			}
			e1.printStackTrace();
		}

		return newEOPrototypeRepository;
	}

	@Override
	public String getFullyQualifiedName() {
		return getDMModel().getFullyQualifiedName() + ".EO_PROTOTYPES";
	}

	@Override
	public String getName() {
		return "eoprototype_repository";
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
		return 11;
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public boolean isDeletable() {
		return false;
	}

	public DMEOModel getEOPrototypeEOModel() {
		if (getDMEOModels().size() == 1) {
			Enumeration en = getDMEOModels().elements();
			return (DMEOModel) en.nextElement();
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Inconsistant data in EOPrototype repository");
			}
		}
		return null;
	}

	public DMEOEntity getEOPrototypeEntity() {
		if (getEOPrototypeEOModel().getEntities().size() == 1) {
			return getEOPrototypeEOModel().getEntities().firstElement();
		} else {
			// Don't warn, it might be normal during project creation
			/*if (logger.isLoggable(Level.WARNING))
			    logger.warning("Inconsistant data in EOPrototype repository: "+getEOPrototypeEOModel().getEntities().size()+" entities");*/
		}
		return null;
	}

	public Vector getPrototypes() {
		return getOrderedChildren();
	}

	@Override
	public Vector<DMEOAttribute> getOrderedChildren() {
		if (getEOPrototypeEntity() != null) {
			return getEOPrototypeEntity().getOrderedAttributes();
		}
		return null;
	}

	public DMEOPrototype getPrototype(EOAttribute prototypeAttribute) {
		return (DMEOPrototype) getEOPrototypeEntity().getAttribute(prototypeAttribute);
	}

	public DMEOPrototype getPrototypeNamed(String protoName) {
		if (getEOPrototypeEntity() == null) {
			return null;
		}
		Enumeration<DMEOAttribute> en = getOrderedChildren().elements();
		while (en.hasMoreElements()) {
			DMEOPrototype p = (DMEOPrototype) en.nextElement();
			if (p.getName() != null && p.getName().equals(protoName)) {
				return p;
			}
		}
		return null;
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.AgileBirdsObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return getName();
	}

}
