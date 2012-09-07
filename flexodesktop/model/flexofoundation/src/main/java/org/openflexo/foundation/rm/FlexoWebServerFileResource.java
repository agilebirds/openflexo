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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.toolbox.FileFormat;
import org.openflexo.toolbox.FileUtils;

public class FlexoWebServerFileResource extends FlexoImportedResource<FlexoWebServerResourceData> {

	private static final Logger logger = Logger.getLogger(FlexoWebServerFileResource.class.getPackage().getName());

	public static FlexoWebServerFileResource createNewWebServerFileResource(File src, FlexoProject project) {
		FlexoWebServerFileResource returned = new FlexoWebServerFileResource(project);
		String folderPath = project.getImportedImagesDir().getName();
		String name = FileUtils.lowerCaseExtension(src.getName());
		FlexoProjectFile file = new FlexoProjectFile(folderPath + "/" + name);
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
			project.registerResource(returned);
		} catch (DuplicateResourceException e) {
			// Warns about the exception
			logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			e.printStackTrace();
		}
		returned._setLastWrittenOnDisk(new Date());
		returned.setLastImportDate(new Date());
		returned.rebuildDependancies();

		return returned;
	}

	public FlexoWebServerFileResource(FlexoProjectBuilder builder) {
		this(builder.project);
		builder.notifyResourceLoading(this);
	}

	public FlexoWebServerFileResource(FlexoProject aProject) {
		super(aProject);
	}

	@Override
	public String getName() {
		if (getFile() != null) {
			return getFile().getName();
		}
		return null;
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.WEBSERVER;
	}

	@Override
	public FileFormat getResourceFormat() {
		String fileName = getFileName();
		if (fileName == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Null filename for FlexoFileResource of type " + this.getClass().getName());
			}
			return super.getResourceFormat();
		}
		int index = fileName.lastIndexOf('.');
		if ((index == -1) || (index == fileName.length())) {
			return super.getResourceFormat();
		}
		FileFormat returned = FileFormat.getDefaultFileFormatByExtension(fileName.substring(index + 1).toLowerCase());
		// logger.warning("Pour le WebServerFileResource le format obtenu d'apres "+fileName+" c'est "+returned);
		return returned;
	}

	@Override
	protected void performUpdating(FlexoResourceTree updatedResources) throws ResourceDependencyLoopException, LoadResourceException,
			FileNotFoundException, ProjectLoadingCancelledException, FlexoException {

	}

	@Override
	protected FlexoWebServerResourceData doImport() throws FlexoException {
		if (_resourceData == null) {
			_resourceData = new FlexoWebServerResourceData();
			_resourceData.setProject(getProject());
			_resourceData.setFlexoResource(this);
		}
		return _resourceData;
	}

	public static void importSpecificButtonsIntoResources(FlexoProject project) {
		File[] files = project.getSpecificButtonDirectory().listFiles();
		for (int i = 0; i < files.length; i++) {
			if (!files[i].isDirectory()) {
				if (project.getWebServerResource(files[i].getName()) == null) {
					try {
						FileUtils.copyFileToDir(files[i], project.getImportedImagesDir());
						createNewWebServerFileResource(files[i], project);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
