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

import java.util.logging.Logger;

import org.openflexo.foundation.view.rm.VirtualModelInstanceResource;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.VirtualModelModelSlot;
import org.openflexo.toolbox.StringUtils;

/**
 * 
 * Concretize the binding of a {@link VirtualModelModelSlot} to a concrete {@link VirtualModelInstance} conform to a given
 * {@link VirtualModel}<br>
 * 
 * @author Sylvain Guerin
 * @see VirtualModelModelSlot
 * 
 */
public class VirtualModelModelSlotInstance extends ModelSlotInstance<VirtualModelModelSlot, VirtualModelInstance> {

	private static final Logger logger = Logger.getLogger(VirtualModelModelSlotInstance.class.getPackage().getName());

	// Serialization/deserialization only, do not use
	private String virtualModelInstanceURI;

	/*public VirtualModelModelSlotInstance(View view, VirtualModelModelSlot modelSlot) {
		super(view, modelSlot);
	}*/

	public VirtualModelModelSlotInstance(VirtualModelInstance vmInstance, VirtualModelModelSlot modelSlot) {
		super(vmInstance, modelSlot);
	}

	@Override
	public VirtualModelInstance getAccessedResourceData() {
		if (getVirtualModelInstance() != null && accessedResourceData == null && StringUtils.isNotEmpty(virtualModelInstanceURI)) {
			VirtualModelInstanceResource vmiResource = getProject().getViewLibrary().getVirtualModelInstance(virtualModelInstanceURI);
			if (vmiResource != null) {
				accessedResourceData = vmiResource.getVirtualModelInstance();
				resource = vmiResource;
			}
		}
		// Special case to handle reflexive model slots
		if (accessedResourceData == null && getVirtualModelInstance() != null
				&& getModelSlot().equals(getVirtualModelInstance().getVirtualModel().getReflexiveModelSlot())) {
			accessedResourceData = getVirtualModelInstance();
			if (accessedResourceData != null) {
				resource = accessedResourceData.getResource();
			}
		}
		if (accessedResourceData == null && StringUtils.isNotEmpty(virtualModelInstanceURI)) {
			logger.warning("Cannot find virtual model instance " + virtualModelInstanceURI);
		}
		return accessedResourceData;
	}

	// Serialization/deserialization only, do not use
	public String getVirtualModelInstanceURI() {
		if (getResource() != null) {
			return getResource().getURI();
		}
		return virtualModelInstanceURI;
	}

	// Serialization/deserialization only, do not use
	public void setVirtualModelInstanceURI(String virtualModelInstanceURI) {
		this.virtualModelInstanceURI = virtualModelInstanceURI;
	}

	@Override
	public String getBindingDescription() {
		return getVirtualModelInstanceURI();
	}

}
