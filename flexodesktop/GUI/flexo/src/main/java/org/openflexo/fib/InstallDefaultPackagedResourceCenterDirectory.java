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
package org.openflexo.fib;

import java.io.File;
import java.io.IOException;

import org.openflexo.foundation.resource.DirectoryResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.FileUtils.CopyStrategy;

public class InstallDefaultPackagedResourceCenterDirectory {

	public static final FileResource FIB_FILE = new FileResource("Fib/InstallDefaultPackagedResourceCenterDirectory.fib");

	private static final File ONTOLOGIES_DIR = new FileResource("Ontologies");
	private static final File VIEWPOINT_LIBRARY_DIR = new FileResource("ViewPoints");

	private File resourceCenterDirectory;

	public File getResourceCenterDirectory() {
		if (resourceCenterDirectory == null) {
			File attempt = new File(FileUtils.getApplicationDataDirectory(), "FlexoResourceCenter");
			int id = 2;
			while (attempt.exists()) {
				attempt = new File(FileUtils.getApplicationDataDirectory(), "FlexoResourceCenter" + id);
				System.out.println("id=" + id);
				id++;
			}
			resourceCenterDirectory = attempt;
		}
		return resourceCenterDirectory;
	}

	public void setResourceCenterDirectory(File resourceCenterDirectory) {
		this.resourceCenterDirectory = resourceCenterDirectory;
	}

	public void installDefaultPackagedResourceCenter(FlexoResourceCenterService rcService) {
		getResourceCenterDirectory().mkdirs();
		copyViewPoints(VIEWPOINT_LIBRARY_DIR, getResourceCenterDirectory(), CopyStrategy.REPLACE_OLD_ONLY);
		copyOntologies(ONTOLOGIES_DIR, getResourceCenterDirectory(), CopyStrategy.REPLACE_OLD_ONLY);
		DirectoryResourceCenter newRC = new DirectoryResourceCenter(getResourceCenterDirectory());
		rcService.addToResourceCenters(newRC);
		rcService.storeDirectoryResourceCenterLocations();
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
