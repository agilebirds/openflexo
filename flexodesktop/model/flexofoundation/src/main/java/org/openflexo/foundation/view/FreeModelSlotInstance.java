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

import org.openflexo.foundation.resource.ResourceData;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.technologyadapter.FreeModelSlot;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.xml.ViewBuilder;
import org.openflexo.foundation.xml.VirtualModelInstanceBuilder;

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
	private String modelURI;

	/**
	 * Constructor invoked during deserialization
	 * 
	 */
	public FreeModelSlotInstance(ViewBuilder builder) {
		super(builder.getProject());
		initializeDeserialization(builder);
	}

	/**
	 * Constructor invoked during deserialization
	 * 
	 */
	public FreeModelSlotInstance(VirtualModelInstanceBuilder builder) {
		super(builder.getProject());
		initializeDeserialization(builder);
	}

	public FreeModelSlotInstance(View view, MS modelSlot) {
		super(view, modelSlot);
	}

	public FreeModelSlotInstance(VirtualModelInstance<?, ?> vmInstance, MS modelSlot) {
		super(vmInstance, modelSlot);
	}

	@Override
	public RD getResourceData() {
		/*if (getVirtualModelInstance() != null && resourceData == null && StringUtils.isNotEmpty(modelURI)) {
			FlexoModelResource<M, ?> modelResource = (FlexoModelResource<M, ?>) getVirtualModelInstance().getInformationSpace()
					.getMetaModelWithURI(modelURI, getModelSlot().getTechnologyAdapter());
			if (modelResource != null) {
				resourceData = modelResource.getModel();
				resource = modelResource;
			}
		}
		if (resourceData == null && StringUtils.isNotEmpty(modelURI)) {
			logger.warning("cannot find model " + modelURI);
		}*/
		return resourceData;
	}

	// Serialization/deserialization only, do not use
	public String getModelURI() {
		if (getResource() != null) {
			return getResource().getURI();
		}
		return modelURI;
	}

	// Serialization/deserialization only, do not use
	public void setModelURI(String modelURI) {
		this.modelURI = modelURI;
	}

	public RD getModel() {
		return getResourceData();
	}
}
