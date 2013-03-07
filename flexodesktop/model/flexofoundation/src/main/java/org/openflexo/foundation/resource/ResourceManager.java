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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.openflexo.foundation.FlexoService;
import org.openflexo.foundation.FlexoServiceImpl;

/**
 * This is the very first implementation of the new ResourceManager
 * 
 * @author sylvain
 * 
 */
public class ResourceManager extends FlexoServiceImpl implements FlexoService {

	protected static final Logger logger = Logger.getLogger(ResourceManager.class.getPackage().getName());

	private List<FlexoResource<?>> resources;

	public static ResourceManager createInstance() {
		return new ResourceManager();
	}

	private ResourceManager() {
		// Not now: will be performed by the ServiceManager
		// initialize();
		resources = new ArrayList<FlexoResource<?>>();
	}

	@Override
	public void initialize() {
		logger.info("Initialized ResourceManager...");
	}

	public void registerResource(FlexoResource<?> resource) {
		resources.add(resource);
		getServiceManager().notify(this, new ResourceRegistered(resource, null));
		if (resource.getURI() == null) {
			logger.info("Une resource avec une URI null: " + resource);
			Thread.dumpStack();
		}
	}

	public void unregisterResource(FlexoResource<?> resource) {
		resources.remove(resource);
		getServiceManager().notify(this, new ResourceUnregistered(resource, null));
	}

	public List<FlexoResource<?>> getRegisteredResources() {
		return resources;
	}

	// TODO: optimize this
	public List<FlexoResource<?>> getLoadedResources() {
		List<FlexoResource<?>> returned = new ArrayList<FlexoResource<?>>();
		for (FlexoResource<?> r : resources) {
			if (r.isLoaded()) {
				returned.add(r);
			}
		}
		return returned;
	}

	// TODO: optimize this
	public List<FlexoResource<?>> getUnsavedResources() {
		List<FlexoResource<?>> returned = new ArrayList<FlexoResource<?>>();
		for (FlexoResource<?> r : resources) {
			try {
				if (r.isLoaded() && r.getResourceData(null).isModified()) {
					returned.add(r);
				}
			} catch (Exception e) {
				// Don't care here
			}
		}
		return returned;
	}

	public FlexoResource<?> getResource(String resourceURI) {
		if (StringUtils.isEmpty(resourceURI)) {
			return null;
		}
		for (FlexoResource r : resources) {
			if (resourceURI.equals(r.getURI())) {
				return r;
			}
		}
		return null;
	}

}
