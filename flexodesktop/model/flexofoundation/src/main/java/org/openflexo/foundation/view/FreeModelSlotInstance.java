/*
 * (c) Copyright 2010-2012 AgileBirds
 * (c) Copyright 2013 Openflexo
 *
 * This file is part of Openflexo.
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
 * along with Openflexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openflexo.foundation.view;

import java.io.FileNotFoundException;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.ResourceData;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.technologyadapter.FreeModelSlot;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterResource;
import org.openflexo.toolbox.StringUtils;

/**
 * Concretize the binding of a {@link ModelSlot} to a concrete {@link FlexoModel}<br>
 * This is the binding point between a {@link FreeModelSlot} and its concretization in a {@link VirtualModelInstance}
 * 
 * @author Sylvain Guerin, Vincent Leildé
 * @see FreeModelSlot
 * 
 */
public class FreeModelSlotInstance<RD extends ResourceData<RD>, MS extends FreeModelSlot<RD>> extends ModelSlotInstance<MS, RD> {

	private static final Logger logger = Logger.getLogger(FreeModelSlotInstance.class.getPackage().getName());

	// Serialization/deserialization only, do not use
	private String resourceURI;

	/*public FreeModelSlotInstance(View view, MS modelSlot) {
		super(view, modelSlot);
	}*/

	public FreeModelSlotInstance(VirtualModelInstance vmInstance, MS modelSlot) {
		super(vmInstance, modelSlot);
	}

	@Override
	public RD getAccessedResourceData() {
		if (getVirtualModelInstance() != null && accessedResourceData == null && StringUtils.isNotEmpty(resourceURI)) {
			TechnologyAdapterResource<RD, ?> resource = (TechnologyAdapterResource<RD, ?>) getVirtualModelInstance().getInformationSpace()
					.getResource(resourceURI);
			if (resource != null) {
				try {
					accessedResourceData = resource.getResourceData(null);
					this.resource = resource;
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ResourceLoadingCancelledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FlexoException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (accessedResourceData == null && StringUtils.isNotEmpty(resourceURI)) {
			logger.warning("cannot find resource " + resourceURI);
		}
		return accessedResourceData;
	}

	// Serialization/deserialization only, do not use
	public String getResourceURI() {
		if (getResource() != null) {
			return getResource().getURI();
		}
		return resourceURI;
	}

	// Serialization/deserialization only, do not use
	public void setResourceURI(String resourceURI) {
		this.resourceURI = resourceURI;
	}

	public RD getModel() {
		return getAccessedResourceData();
	}

	@Override
	public String getBindingDescription() {
		return getResourceURI();
	}
}
