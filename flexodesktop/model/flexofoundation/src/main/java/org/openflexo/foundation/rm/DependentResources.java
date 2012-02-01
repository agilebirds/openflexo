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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.rm.FlexoResource.DependencyAlgorithmScheme;
import org.openflexo.logging.FlexoLogger;

/**
 * Represents all the resources from which related resource depends (or more exactely, MAY depends).
 * 
 * @author sguerin
 * 
 */
public class DependentResources extends ResourceList {

	private static final Logger logger = FlexoLogger.getLogger(DependentResources.class.getPackage().getName());

	public DependentResources() {
		super();
		_resourceIncludingInactive = new Hashtable<DependencyAlgorithmScheme, Vector<FlexoResource<FlexoResourceData>>>();
		_resourceExcludingInactive = new Hashtable<DependencyAlgorithmScheme, Vector<FlexoResource<FlexoResourceData>>>();
	}

	public DependentResources(FlexoResource<? extends FlexoResourceData> relatedResource) {
		super(relatedResource);
		_resourceIncludingInactive = new Hashtable<DependencyAlgorithmScheme, Vector<FlexoResource<FlexoResourceData>>>();
		_resourceExcludingInactive = new Hashtable<DependencyAlgorithmScheme, Vector<FlexoResource<FlexoResourceData>>>();
	}

	/**
	 * Overrides
	 * 
	 * @see org.openflexo.foundation.rm.ResourceList#addToResources(org.openflexo.foundation.rm.FlexoResource) by setting inverse link
	 *      (altered resource)
	 * 
	 * @see org.openflexo.foundation.rm.ResourceList#addToResources(org.openflexo.foundation.rm.FlexoResource)
	 */
	@Override
	public void addToResources(FlexoResource resource) {
		if (resource == getRelatedResource()) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("A resource attempted to add itself to its dependant resource list" + this.getClass().getName()
						+ "): this is strictly forbidden.\n\tYou must attempt to find the cause of this and FIX it!");
			}
			return;
		}
		if (getRelatedResource() != null && resource.deeplyDependsOf(getRelatedResource())) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Trying to create a loop between " + getRelatedResource() + " and " + resource);
			}
			return;
		}
		super.addToResources(resource);
		if (getRelatedResource() != null) {
			if (!resource.getAlteredResources().contains(getRelatedResource())) {
				resource.addToAlteredResources(getRelatedResource());
			}
		}
	}

	/**
	 * Overrides
	 * 
	 * @see org.openflexo.foundation.rm.ResourceList#removeFromResources(org.openflexo.foundation.rm.FlexoResource) by removing inverse link
	 *      (altered resource)
	 * 
	 * @see org.openflexo.foundation.rm.ResourceList#removeFromResources(org.openflexo.foundation.rm.FlexoResource)
	 */
	@Override
	public void removeFromResources(FlexoResource resource) {
		super.removeFromResources(resource);
		if (getRelatedResource() != null) {
			if (resource.getAlteredResources().contains(getRelatedResource())) {
				resource.removeFromAlteredResources(getRelatedResource());
			}
		}
	}

	/**
	 * Overrides
	 * 
	 * @see org.openflexo.foundation.rm.ResourceList#setRelatedResource(org.openflexo.foundation.rm.FlexoResource) by refreshing inverse
	 *      links (altered resources)
	 * 
	 * @see org.openflexo.foundation.rm.ResourceList#setRelatedResource(org.openflexo.foundation.rm.FlexoResource)
	 */
	@Override
	public void setRelatedResource(FlexoResource relatedResource) {
		super.setRelatedResource(relatedResource);
		for (Enumeration en = elements(); en.hasMoreElements();) {
			FlexoResource next = (FlexoResource) en.nextElement();
			if (!next.getAlteredResources().contains(getRelatedResource())) {
				next.addToAlteredResources(getRelatedResource());
			}
		}
	}

	@Override
	public String getSerializationIdentifier() {
		return getRelatedResource().getSerializationIdentifier() + "_DR";
	}

	public Enumeration<FlexoResource<FlexoResourceData>> elements(boolean includeInactiveResource,
			DependencyAlgorithmScheme dependancyScheme) {
		return getResources(includeInactiveResource, dependancyScheme).elements();
	}

	/**
	 * Clear cache scheme
	 */
	@Override
	public void update() {
		_resourceIncludingInactive.clear();
		_resourceExcludingInactive.clear();
	}

	private Vector<FlexoResource<FlexoResourceData>> buildResources(boolean includeInactiveResource,
			DependencyAlgorithmScheme dependancyScheme) {
		Vector<FlexoResource<FlexoResourceData>> returned = new Vector<FlexoResource<FlexoResourceData>>();
		for (FlexoResource<FlexoResourceData> resource : this) {
			if (includeInactiveResource || resource.isActive()) {
				if (getRelatedResource().dependsOf(resource, dependancyScheme)) {
					returned.add(resource);
				}
			}
		}
		return returned;
	}

	private Hashtable<DependencyAlgorithmScheme, Vector<FlexoResource<FlexoResourceData>>> _resourceIncludingInactive;
	private Hashtable<DependencyAlgorithmScheme, Vector<FlexoResource<FlexoResourceData>>> _resourceExcludingInactive;

	/**
	 * Return list of resources with supplied options
	 * 
	 * TAKE CARE that trying to retrieve dependant resources with an optimistic scheme require that an update() was done on this object
	 * after the last modifications on the model, because this method use a cache scheme. To be sure to get the good result in optimist
	 * scheme, do: update() then getResources(aBoolean,DependancyAlgorithmScheme.Optimistic).
	 * 
	 * @param includeInactiveResource
	 * @param dependancyScheme
	 * @return
	 */
	public Vector<FlexoResource<FlexoResourceData>> getResources(boolean includeInactiveResource, DependencyAlgorithmScheme dependancyScheme) {
		Vector<FlexoResource<FlexoResourceData>> returned = null;
		if (includeInactiveResource) {
			returned = _resourceIncludingInactive.get(dependancyScheme);
			if (returned == null) {
				returned = buildResources(includeInactiveResource, dependancyScheme);
				_resourceIncludingInactive.put(dependancyScheme, returned);
			}
			return returned;
		} else {
			returned = _resourceExcludingInactive.get(dependancyScheme);
			if (returned == null) {
				returned = buildResources(includeInactiveResource, dependancyScheme);
				_resourceExcludingInactive.put(dependancyScheme, returned);
			}
			return returned;
		}
	}
}
