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

/**
 * Represents all the resources which are required to be in sync with a given resource. This relation generally means that some information
 * is redondant between two resources. Redondant information should always be avoided but is sometime required (for example to perform a
 * lookup with a name between different resources).
 * 
 * A modification on related resource may modify one or more of synchronized resources.
 * 
 * @author sguerin
 * 
 */
public class SynchronizedResources extends ResourceList {

	public SynchronizedResources() {
		super();
	}

	public SynchronizedResources(FlexoResource relatedResource) {
		super(relatedResource);
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
	public void addToResources(FlexoResource<FlexoResourceData> resource) {
		super.addToResources(resource);
		if (getRelatedResource() != null) {
			if (!resource.getSynchronizedResources().contains(getRelatedResource())) {
				resource.addToSynchronizedResources(getRelatedResource());
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
	public void removeFromResources(FlexoResource<FlexoResourceData> resource) {
		super.removeFromResources(resource);
		if (getRelatedResource() != null) {
			if (resource.getSynchronizedResources().contains(getRelatedResource())) {
				resource.removeFromSynchronizedResources(getRelatedResource());
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
			if (!next.getSynchronizedResources().contains(getRelatedResource())) {
				next.addToSynchronizedResources(getRelatedResource());
			}
		}
	}

	@Override
	public String getSerializationIdentifier() {
		return getRelatedResource().getSerializationIdentifier() + "_SR";
	}

	/**
	 * Clear cache scheme
	 */
	@Override
	public void update() {
		// No cache implemented for synchronized resources
	}

}
