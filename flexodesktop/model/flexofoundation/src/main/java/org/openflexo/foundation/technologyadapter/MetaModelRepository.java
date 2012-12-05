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

import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceRepository;

/**
 * A {@link MetaModelRepository} stores all resources storing metamodels relative to a given technology<br>
 * Resources are organized with a folder hierarchy inside a {@link ResourceRepository}
 * 
 * @author sylvain
 * 
 * @param <R>
 * @param <TA>
 */
public abstract class MetaModelRepository<R extends FlexoResource<? extends MM>, M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>, TA extends TechnologyAdapter<M, MM>>
		extends ResourceRepository<R> {

	private static final Logger logger = Logger.getLogger(MetaModelRepository.class.getPackage().getName());

	private TA technologyAdapter;
	private FlexoResourceCenter resourceCenter;

	public MetaModelRepository(TA technologyAdapter, FlexoResourceCenter resourceCenter) {
		this.technologyAdapter = technologyAdapter;
		this.resourceCenter = resourceCenter;
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
