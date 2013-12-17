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
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.resource.InvalidFileNameException;
import org.openflexo.foundation.utils.FlexoProjectFile;

/**
 * This class represents a Directory Flexo resource. A Directory FlexoResource represent some objects handled by Flexo Application Suite
 * (all concerned modules), which are stored in a directory, generally located in related {@link FlexoProject} project directory.
 * 
 * @author sguerin
 */
public abstract class FlexoDirectoryResource extends FlexoFileResource {
	private static final Logger logger = Logger.getLogger(FlexoDirectoryResource.class.getPackage().getName());

	/**
	 * Constructor used for XML Serialization: never try to instanciate resource from this constructor
	 * 
	 * @param builder
	 */
	public FlexoDirectoryResource(FlexoProjectBuilder builder) {
		this(builder.project, builder.serviceManager);
		builder.notifyResourceLoading(this);
	}

	public FlexoDirectoryResource(FlexoProject aProject, FlexoServiceManager serviceManager) {
		super(aProject, serviceManager);
	}

	public FlexoDirectoryResource(FlexoProject aProject, FlexoServiceManager serviceManager, FlexoProjectFile directory)
			throws InvalidFileNameException {
		this(aProject, serviceManager);
		setResourceFile(directory);
	}

	public File getResourceDirectory() {
		return getFile();
	}

	public File getDirectory() {
		return getFile();
	}

	@Override
	public File getFile() {
		File returned = super.getFile();
		if (returned.isDirectory()) {
			return returned;
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("File " + returned.getAbsolutePath() + " is supposed to be a directory");
			}
			return null;
		}
	}

	/**
	 * This date is VERY IMPORTANT and CRITICAL since this is the date used by ResourceManager to compute dependancies between resources.
	 * This method returns the date that must be considered as last known update for this resource
	 * 
	 * Here simply returns disk last modified date
	 * 
	 * @return a Date object
	 */
	@Override
	public Date getLastUpdate() {
		return getDiskLastModifiedDate();
	}

}
