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
package org.openflexo.foundation.resource;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.FileUtils.CopyStrategy;
import org.openflexo.toolbox.FlexoVersion;
import org.openflexo.toolbox.IProgress;

/**
 * Default implementation for a {@link FlexoResourceCenter} bound to a directory on the file system
 * 
 * @author sylvain
 * 
 */
public class DirectoryResourceCenter extends FileSystemBasedResourceCenter implements FlexoResourceCenter {

	protected static final Logger logger = Logger.getLogger(DirectoryResourceCenter.class.getPackage().getName());

	private static final File ONTOLOGIES_DIR = new FileResource("Ontologies");
	private static final File VIEWPOINT_LIBRARY_DIR = new FileResource("ViewPoints");

	// private File newViewPointSandboxDirectory;

	public DirectoryResourceCenter(File resourceCenterDirectory) {
		super(resourceCenterDirectory);
		// newViewPointSandboxDirectory = new File(resourceCenterDirectory, "ViewPoints");
	}

	public static DirectoryResourceCenter instanciateNewDirectoryResourceCenter(File resourceCenterDirectory) {
		logger.info("Instanciate ResourceCenter from " + resourceCenterDirectory.getAbsolutePath());
		DirectoryResourceCenter localResourceCenterImplementation = new DirectoryResourceCenter(resourceCenterDirectory);
		try {
			localResourceCenterImplementation.update();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return localResourceCenterImplementation;
	}

	public static DirectoryResourceCenter instanciateTestDirectoryResourceCenter(File resourceCenterDirectory) {
		logger.info("Instanciate TEST ResourceCenter from " + resourceCenterDirectory.getAbsolutePath());
		DirectoryResourceCenter localResourceCenterImplementation = new DirectoryResourceCenter(resourceCenterDirectory);
		return localResourceCenterImplementation;
	}

	@Override
	public final void initialize(TechnologyAdapterService technologyAdapterService) {
		super.initialize(technologyAdapterService);
	}

	@Override
	public List<FlexoResource<?>> getAllResources(IProgress progress) {
		return Collections.emptyList();
	}

	@Override
	public <T extends ResourceData<T>> List<FlexoResource<T>> retrieveResource(String uri, Class<T> type, IProgress progress) {
		return Collections.emptyList();
	}

	@Override
	public <T extends ResourceData<T>> FlexoResource<T> retrieveResource(String uri, FlexoVersion version, Class<T> type, IProgress progress) {
		return null;
	}

	@Override
	public void publishResource(FlexoResource<?> resource, FlexoVersion newVersion, IProgress progress) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void update() throws IOException {
		copyViewPoints(VIEWPOINT_LIBRARY_DIR, getRootDirectory(), CopyStrategy.REPLACE_OLD_ONLY);
		copyOntologies(ONTOLOGIES_DIR, getRootDirectory(), CopyStrategy.REPLACE_OLD_ONLY);
	}

	@Deprecated
	private static void copyViewPoints(File initialDirectory, File resourceCenterDirectory, CopyStrategy copyStrategy) {
		if (initialDirectory.getParentFile().equals(resourceCenterDirectory)) {
			return;
		}

		try {
			FileUtils.copyDirToDir(VIEWPOINT_LIBRARY_DIR, resourceCenterDirectory, copyStrategy);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Deprecated
	private static void copyOntologies(File initialDirectory, File resourceCenterDirectory, CopyStrategy copyStrategy) {
		if (initialDirectory.getParentFile().equals(resourceCenterDirectory)) {
			return;
		}
		try {
			FileUtils.copyDirToDir(initialDirectory, resourceCenterDirectory, copyStrategy);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
