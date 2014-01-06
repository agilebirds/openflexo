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

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoProject;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.FileUtils;

public class FlexoFileResourceResource extends FlexoMemoryResource {

	private static final Logger logger = FlexoLogger.getLogger(FlexoFileResourceResource.class.getPackage().getName());

	private FileResource resource;

	public static FlexoFileResourceResource getResource(FileResource resource, FlexoProject project) {
		FlexoFileResourceResource r = (FlexoFileResourceResource) project.resourceForKey(ResourceType.FILE_RESOURCE, resource.getName());
		if (r == null) {
			r = new FlexoFileResourceResource(project, resource);
			try {
				project.registerResource(r);
			} catch (DuplicateResourceException e) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.log(Level.SEVERE, "This should not happen", e);
				}
				e.printStackTrace();
				r = (FlexoFileResourceResource) project.resourceForKey(r.getResourceIdentifier());
			}
		}
		return r;
	}

	private FlexoFileResourceResource(FlexoProject project, FileResource resource) {
		super(project, project.getServiceManager());
		this.resource = resource;
	}

	@Override
	public synchronized Date getLastUpdate() {
		return FileUtils.getDiskLastModifiedDate(resource);
	}

	@Override
	public String getName() {
		return resource.getName();
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.FILE_RESOURCE;
	}
}
