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
package org.openflexo.foundation.technologyadapter;

import java.util.logging.Logger;

import org.openflexo.foundation.resource.FileResourceRepository;
import org.openflexo.foundation.resource.FileSystemBasedResourceCenter;
import org.openflexo.foundation.resource.FlexoFileResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceData;
import org.openflexo.foundation.resource.ResourceRepository;

/**
 * A {@link TechnologyAdapterFileResourceRepository} stores all resources storing resources relative to a given technology<br>
 * Resources are organized with a folder hierarchy inside a {@link ResourceRepository}
 * 
 * @author sylvain
 * 
 * @param <R>
 * @param <TA>
 */
public abstract class TechnologyAdapterFileResourceRepository<R extends TechnologyAdapterResource<RD, TA> & FlexoFileResource<RD>, TA extends TechnologyAdapter, RD extends ResourceData<RD>>
		extends FileResourceRepository<R> {

	private static final Logger logger = Logger.getLogger(TechnologyAdapterFileResourceRepository.class.getPackage().getName());

	private final TA technologyAdapter;
	private FlexoResourceCenter<?> resourceCenter;

	public TechnologyAdapterFileResourceRepository(TA technologyAdapter, FlexoResourceCenter<?> resourceCenter) {
		super(resourceCenter, resourceCenter instanceof FileSystemBasedResourceCenter ? ((FileSystemBasedResourceCenter) resourceCenter)
				.getRootDirectory() : null);
		this.technologyAdapter = technologyAdapter;
		this.resourceCenter = resourceCenter;
		getRootFolder().setName(resourceCenter.getName());
		getRootFolder().setDescription(
				"ModelRepository for technology " + technologyAdapter.getName() + " resource center: " + resourceCenter);
	}

	public TA getTechnologyAdapter() {
		return technologyAdapter;
	}

	public FlexoResourceCenter getResourceCenter() {
		return resourceCenter;
	}

	public void setResourceCenter(FlexoResourceCenter resourceCenter) {
		this.resourceCenter = resourceCenter;
	}

}
