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
import java.util.Collection;
import java.util.logging.Logger;

import org.openflexo.toolbox.FlexoVersion;
import org.openflexo.toolbox.IProgress;

/**
 * Default implementation for a {@link FlexoResourceCenter} bound to a directory on the file system
 * 
 * @author sylvain
 * 
 */
public class DirectoryResourceCenter extends FileSystemBasedResourceCenter {

	protected static final Logger logger = Logger.getLogger(DirectoryResourceCenter.class.getPackage().getName());

	// private File newViewPointSandboxDirectory;

	public DirectoryResourceCenter(File resourceCenterDirectory) {
		super(resourceCenterDirectory);
		// newViewPointSandboxDirectory = new File(resourceCenterDirectory, "ViewPoints");
	}

	public static DirectoryResourceCenter instanciateNewDirectoryResourceCenter(File resourceCenterDirectory) {
		logger.info("Instanciate ResourceCenter from " + resourceCenterDirectory.getAbsolutePath());
		DirectoryResourceCenter directoryResourceCenter = new DirectoryResourceCenter(resourceCenterDirectory);
		try {
			directoryResourceCenter.update();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return directoryResourceCenter;
	}

	@Override
	public Collection<FlexoFileResource<?>> getAllResources(IProgress progress) {
		return getAllResources();
	}

	@Override
	public void publishResource(FlexoResource<?> resource, FlexoVersion newVersion, IProgress progress) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void update() throws IOException {
	}

}
