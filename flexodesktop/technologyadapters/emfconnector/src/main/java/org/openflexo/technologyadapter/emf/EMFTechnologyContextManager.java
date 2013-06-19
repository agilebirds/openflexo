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
package org.openflexo.technologyadapter.emf;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.technologyadapter.emf.rm.EMFMetaModelResource;
import org.openflexo.technologyadapter.emf.rm.EMFModelResource;

public class EMFTechnologyContextManager extends TechnologyContextManager {

	/** Stores all known metamodels where key is the URI of metamodel */
	protected Map<String, EMFMetaModelResource> metamodels = new HashMap<String, EMFMetaModelResource>();
	/** Stores all known models where key is the URI of model */
	protected Map<String, EMFModelResource> models = new HashMap<String, EMFModelResource>();

	public EMFTechnologyContextManager(EMFTechnologyAdapter adapter, FlexoResourceCenterService resourceCenterService) {
		super(adapter, resourceCenterService);
	}

	@Override
	public EMFTechnologyAdapter getTechnologyAdapter() {
		return (EMFTechnologyAdapter) super.getTechnologyAdapter();
	}

	public EMFModelResource getModel(File modelFile) {
		return models.get(modelFile);
	}

	/**
	 * Called when a new meta model was registered, notify the {@link TechnologyContextManager}
	 * 
	 * @param newModel
	 */
	public void registerMetaModel(EMFMetaModelResource newMetaModelResource) {
		registerResource(newMetaModelResource);
		metamodels.put(newMetaModelResource.getURI(), newMetaModelResource);
	}

	/**
	 * Called when a new model was registered, notify the {@link TechnologyContextManager}
	 * 
	 * @param newModel
	 */
	public void registerModel(EMFModelResource newModelResource) {
		registerResource(newModelResource);
		models.put(newModelResource.getURI(), newModelResource);
	}

}
