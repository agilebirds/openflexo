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
import org.openflexo.foundation.resource.FlexoResourceCenterService;

/**
 * A {@link TechnologyContextManager} manages for a technology, and shared by all {@link FlexoResourceCenter} declared in the scope of
 * {@link FlexoResourceCenterService}, all references and links between all resources managed by a given technology, it their original
 * technical space.
 * 
 * This class is responsible for maintaining consistency of related technology, in its original technological space (maintaining technical
 * links between all technology-specific stuff)
 * 
 * @author sylvain
 * 
 */
public abstract class TechnologyContextManager<M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>, MS extends ModelSlot<M, MM>> {

	private static final Logger logger = Logger.getLogger(TechnologyContextManager.class.getPackage().getName());

	/**
	 * Called when a new model was registered, notify the {@link TechnologyContextManager}
	 * 
	 * @param newModel
	 */
	public abstract void registerModel(FlexoResource<M> newModelResource);

	/**
	 * Called when a new meta model was registered, notify the {@link TechnologyContextManager}
	 * 
	 * @param newModel
	 */
	public abstract void registerMetaModel(FlexoResource<MM> newMetaModelResource);

}
