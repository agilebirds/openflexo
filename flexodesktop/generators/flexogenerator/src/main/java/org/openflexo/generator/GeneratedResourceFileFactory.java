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
package org.openflexo.generator;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGSymbolicDirectory;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.rm.cg.CopyOfFileResource;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.generator.file.CGPackagedResourceFile;
import org.openflexo.generator.rm.FlexoCopyOfFileResource;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileUtils;

public class GeneratedResourceFileFactory {

	private static final Logger logger = FlexoLogger.getLogger(GeneratedResourceFileFactory.class.getPackage().getName());

	public static void initCGFile(CGFile cgFile, CGSymbolicDirectory symbDir, CGRepositoryFileResource returned) {
		cgFile.setSymbolicDirectory(symbDir);
		returned.setCGFile(cgFile);
		symbDir.getGeneratedCodeRepository().addToFiles(cgFile);
	}

	public static <FR extends CGRepositoryFileResource> FR registerResource(FR returned, String fileName) {
		return registerResource(returned, fileName, "");
	}

	public static <FR extends CGRepositoryFileResource> FR registerResource(FR returned, String fileName, String folderPath) {
		if (returned.getName() == null) {
			throw new NullPointerException();
		}
		FlexoProjectFile file = returned.makeFlexoProjectFile(folderPath, fileName);

		try {
			returned.setResourceFile(file);
		} catch (InvalidFileNameException e1) {
			file = new FlexoProjectFile(FileUtils.getValidFileName(file.getRelativePath()));
			try {
				returned.setResourceFile(file);
			} catch (InvalidFileNameException e) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("Invalid file name: " + file.getRelativePath() + ". This should never happen.");
				}
				return null;
			}
		}
		try {
			returned.getProject().registerResource(returned);
		} catch (DuplicateResourceException e) {
			// Warns about the exception
			logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			e.printStackTrace();
		}

		returned.rebuildDependancies();

		return returned;
	}

	public static CGRepositoryFileResource resourceForKeyWithCGFile(FlexoProject project, ResourceType type, String resourceName) {
		CGRepositoryFileResource ret = (CGRepositoryFileResource) project.resourceForKey(type, resourceName);
		if (ret != null && ret.getCGFile() == null) {
			ret.delete(false);
			ret = null;
		}
		return ret;
	}

	public static FlexoCopyOfFileResource createNewFlexoCopyOfFileResource(GenerationRepository repository,
			PackagedResourceToCopyGenerator<GenerationRepository> generator) {
		FlexoCopyOfFileResource copiedFile = (FlexoCopyOfFileResource) resourceForKeyWithCGFile(repository.getProject(),
				ResourceType.COPIED_FILE, CopyOfFileResource.nameForRepositoryAndFileToCopy(repository, generator.getSource()));
		if (copiedFile == null) {
			FlexoCopyOfFileResource returned = new FlexoCopyOfFileResource(repository.getProject(), generator.getSource());
			returned.setGenerator(generator);
			returned.setName(CopyOfFileResource.nameForRepositoryAndFileToCopy(repository, generator.getSource()));
			CGPackagedResourceFile cgFile = new CGPackagedResourceFile(repository, returned);
			cgFile.setSymbolicDirectory(generator.getSymbolicDirectory(repository));
			returned.setCGFile(cgFile);
			repository.addToFiles(cgFile);
			copiedFile = registerResource(returned, generator.getSource().getName(), generator.getRelativePath());
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Created PackagedFileResource resource " + copiedFile.getName());
			}
		} else {
			copiedFile.setGenerator(generator);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Successfully retrieved PackagedFileResource resource " + copiedFile.getName());
			}
		}
		return copiedFile;
	}
}
