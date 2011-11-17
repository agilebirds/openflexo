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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.xmlcode.XMLSerializable;

/**
 * Represents a list of resources sharing the same relationship, related to a given resource.
 * 
 * @author sguerin
 * 
 */
public abstract class ResourceList extends Vector<FlexoResource<FlexoResourceData>> implements XMLSerializable {
	private static final Logger logger = Logger.getLogger(ResourceList.class.getPackage().getName());

	private FlexoResource<? extends FlexoResourceData> relatedResource;

	public ResourceList() {
		super();
	}

	public ResourceList(FlexoResource<? extends FlexoResourceData> relatedResource) {
		super();
		setRelatedResource(relatedResource);
	}

	/**
	 * The resource to which this resource list is connected.
	 * 
	 * @return
	 */
	public FlexoResource<? extends FlexoResourceData> getRelatedResource() {
		return relatedResource;
	}

	public void setRelatedResource(FlexoResource<? extends FlexoResourceData> relatedResource) {
		this.relatedResource = relatedResource;
	}

	public List<FlexoResource<FlexoResourceData>> getResources() {
		return this;
	}

	public void setResources(List<FlexoResource<FlexoResourceData>> aVector) {
		removeAllElements();
		for (FlexoResource<FlexoResourceData> r : aVector) {
			addToResources(r);
		}
		if (getRelatedResource() != null) {
			getRelatedResource().getProject().notifyResourceChanged(getRelatedResource());
		}
		update();
	}

	public List<FlexoResource<FlexoResourceData>> getSerialisationResources() {
		List<FlexoResource<FlexoResourceData>> resources = new ArrayList<FlexoResource<FlexoResourceData>>(size());
		for (FlexoResource<FlexoResourceData> resource : this) {
			if (!resource.isToBeSerialized() || getRelatedResource().getProject().getFlexoResource() != null
					&& !getRelatedResource().getProject().getFlexoResource().isInitializingProject() && !resource.checkIntegrity()) {
				continue;
			}
			resources.add(resource);
		}
		return resources;
	}

	public void setSerialisationResources(List<FlexoResource<FlexoResourceData>> resources) {
		setResources(resources);
	}

	public void addToSerialisationResources(FlexoResource<FlexoResourceData> resource) {
		addToResources(resource);
	}

	public void removeFromSerialisationResources(FlexoResource<FlexoResourceData> resource) {
		removeFromResources(resource);
	}

	public void addToResources(FlexoResource resource) {
		if (resource.isDeleted()) {
			return;
		}
		if (!contains(resource)) {
			add(resource);
			if (getRelatedResource() != null) {
				getRelatedResource().getProject().notifyResourceChanged(getRelatedResource());
			}
			update();
		}
	}

	public void removeFromResources(FlexoResource resource) {
		if (contains(resource)) {
			remove(resource);
			if (getRelatedResource() != null) {
				getRelatedResource().getProject().notifyResourceChanged(getRelatedResource());
			}
			update();
		}
	}

	public abstract void update();

	/**
	 * This method overrides
	 * 
	 * @see java.util.Vector#equals(java.lang.Object) in order to avoid XMLCoDe to consider an empty vector is equals to an other one, and
	 *      avoid misleading references.
	 * 
	 *      Overrides
	 * @see java.lang.Object#equals(java.lang.Object)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return obj == this;
	}

	@Override
	public synchronized int hashCode() {
		return super.hashCode();
	}

	public abstract String getSerializationIdentifier();

}
