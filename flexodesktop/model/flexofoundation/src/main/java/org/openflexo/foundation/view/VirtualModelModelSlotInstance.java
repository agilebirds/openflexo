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

import org.openflexo.foundation.rm.VirtualModelInstanceResource;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.VirtualModelModelSlot;
import org.openflexo.foundation.xml.ViewBuilder;
import org.openflexo.foundation.xml.VirtualModelInstanceBuilder;
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
public class VirtualModelModelSlotInstance<VMI extends VirtualModelInstance<VMI, VM>, VM extends VirtualModel<VM>> extends
		ModelSlotInstance<VirtualModelModelSlot<VMI, VM>, VMI> {

	private static final Logger logger = Logger.getLogger(VirtualModelModelSlotInstance.class.getPackage().getName());

	// Serialization/deserialization only, do not use
	private String virtualModelInstanceURI;

	/**
	 * Constructor invoked during deserialization
	 * 
	 */
	public VirtualModelModelSlotInstance(ViewBuilder builder) {
		super(builder.getProject());
		initializeDeserialization(builder);
	}

	/**
	 * Constructor invoked during deserialization
	 * 
	 */
	public VirtualModelModelSlotInstance(VirtualModelInstanceBuilder builder) {
		super(builder.getProject());
		initializeDeserialization(builder);
	}

	public VirtualModelModelSlotInstance(View view, VirtualModelModelSlot<VMI, VM> modelSlot) {
		super(view, modelSlot);
	}

	public VirtualModelModelSlotInstance(VirtualModelInstance<?, ?> vmInstance, VirtualModelModelSlot<VMI, VM> modelSlot) {
		super(vmInstance, modelSlot);
	}

	@Override
	public VMI getResourceData() {
		if (getVirtualModelInstance() != null && resourceData == null && StringUtils.isNotEmpty(virtualModelInstanceURI)) {
			VirtualModelInstanceResource<VMI> vmiResource = getProject().getViewLibrary().getVirtualModelInstance(virtualModelInstanceURI);
			if (vmiResource != null) {
				resourceData = vmiResource.getVirtualModelInstance();
				resource = vmiResource;
			}
		}
		// Special case to handle reflexive model slots
		if (resourceData == null && getVirtualModelInstance() != null
				&& getModelSlot().equals(getVirtualModelInstance().getVirtualModel().getReflexiveModelSlot())) {
			resourceData = (VMI) getVirtualModelInstance();
			if (resourceData != null) {
				resource = resourceData.getResource();
			}
		}
		if (resourceData == null && StringUtils.isNotEmpty(virtualModelInstanceURI)) {
			logger.warning("Cannot find virtual model instance " + virtualModelInstanceURI);
		}
		return resourceData;
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

}
