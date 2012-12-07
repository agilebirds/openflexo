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
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPointFolder;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.FileUtils.CopyStrategy;
import org.openflexo.toolbox.IProgress;

public class LocalResourceCenterImplementation extends FileSystemBasedResourceCenter implements FlexoResourceCenter {

	protected static final Logger logger = Logger.getLogger(LocalResourceCenterImplementation.class.getPackage().getName());

	private static final File VIEWPOINT_LIBRARY_DIR = new FileResource("ViewPoints");
	private static final File ONTOLOGIES_DIR = new FileResource("Ontologies");

	private ViewPointLibrary viewPointLibrary;
	private File newViewPointSandboxDirectory;

	public LocalResourceCenterImplementation(File resourceCenterDirectory) {
		super(resourceCenterDirectory);
		newViewPointSandboxDirectory = new File(resourceCenterDirectory, "ViewPoints");
	}

	public static LocalResourceCenterImplementation instanciateNewLocalResourceCenterImplementation(File resourceCenterDirectory) {
		logger.info("Instanciate ResourceCenter from " + resourceCenterDirectory.getAbsolutePath());
		LocalResourceCenterImplementation localResourceCenterImplementation = new LocalResourceCenterImplementation(resourceCenterDirectory);
		localResourceCenterImplementation.update();
		return localResourceCenterImplementation;
	}

	public static LocalResourceCenterImplementation instanciateTestLocalResourceCenterImplementation(File resourceCenterDirectory) {
		logger.info("Instanciate TEST ResourceCenter from " + resourceCenterDirectory.getAbsolutePath());
		LocalResourceCenterImplementation localResourceCenterImplementation = new LocalResourceCenterImplementation(resourceCenterDirectory);
		return localResourceCenterImplementation;
	}

	@Override
	public final void initialize(TechnologyAdapterService technologyAdapterService) {
		super.initialize(technologyAdapterService);
	}

	@Deprecated
	@Override
	public ViewPoint getOntologyCalc(String ontologyCalcUri) {
		return retrieveViewPointLibrary().getOntologyCalc(ontologyCalcUri);
	}

	@Deprecated
	@Override
	public ViewPointLibrary retrieveViewPointLibrary() {
		if (viewPointLibrary == null) {
			viewPointLibrary = new ViewPointLibrary(this);
			findViewPoints(new File(getRootDirectory(), "ViewPoints"), viewPointLibrary.getRootFolder());
		}
		return viewPointLibrary;
	}

	private void findViewPoints(File dir, ViewPointFolder folder) {
		if (!dir.exists()) {
			dir.mkdirs();
		}
		if (dir.listFiles().length == 0) {
			copyViewPoints(VIEWPOINT_LIBRARY_DIR, getRootDirectory(), CopyStrategy.REPLACE);
		}
		for (File f : dir.listFiles()) {
			if (f.isDirectory() && f.getName().endsWith(".viewpoint")) {
				if (f.listFiles().length > 0) {
					viewPointLibrary.importViewPoint(f, folder);
				}
			} else if (f.isDirectory() && !f.getName().equals("CVS")) {
				ViewPointFolder newFolder = new ViewPointFolder(f.getName(), folder, viewPointLibrary);
				findViewPoints(f, newFolder);
			}
		}
	}

	@Deprecated
	@Override
	public File getNewCalcSandboxDirectory() {
		return newViewPointSandboxDirectory;
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
	public <T extends ResourceData<T>> FlexoResource<T> retrieveResource(String uri, String version, Class<T> type, IProgress progress) {
		return null;
	}

	@Override
	public void publishResource(FlexoResource<?> resource, String newVersion, IProgress progress) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void update() {
		copyOntologies(ONTOLOGIES_DIR, getRootDirectory(), CopyStrategy.REPLACE_OLD_ONLY);
		copyViewPoints(VIEWPOINT_LIBRARY_DIR, getRootDirectory(), CopyStrategy.REPLACE_OLD_ONLY);
	}

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

}
