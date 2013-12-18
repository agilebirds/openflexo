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
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterResource;
import org.openflexo.toolbox.StringUtils;

/**
 * Concretize the binding of a {@link ModelSlot} to a concrete {@link FlexoModel}<br>
 * This is the binding point between a {@link ModelSlot} and its concretization in a {@link VirtualModelInstance}
 * 
 * The {@link ModelSlotInstance} are instantiated inside a {@link View}
 * 
 * @author Luka Le Roux, Sylvain Guerin
 * @see ModelSlot
 * @see FlexoModel
 * @see View
 * 
 */
public abstract class ModelSlotInstance<MS extends ModelSlot<RD>, RD extends ResourceData<RD>> extends VirtualModelInstanceObject {

	private static final Logger logger = Logger.getLogger(ModelSlotInstance.class.getPackage().getName());

	private View view;
	private VirtualModelInstance vmInstance;
	private MS modelSlot;
	protected RD accessedResourceData;
	protected TechnologyAdapterResource<RD, ?> resource;
	// Serialization/deserialization only, do not use
	private String modelSlotName;

	/*public ModelSlotInstance(FlexoProject project) {
		super(project);
	}*/

	/*public ModelSlotInstance(View view, MS modelSlot) {
		super(view.getProject());
		this.view = view;
		this.modelSlot = modelSlot;
	}*/

	public ModelSlotInstance(VirtualModelInstance vmInstance, MS modelSlot) {
		super(vmInstance.getProject());
		this.vmInstance = vmInstance;
		this.view = vmInstance.getView();
		this.modelSlot = modelSlot;
	}

	@Override
	public VirtualModelInstance getResourceData() {
		return getVirtualModelInstance();
	}

	public void setView(View view) {
		this.view = view;
	}

	@Override
	public View getView() {
		return view;
	}

	@Override
	public VirtualModelInstance getVirtualModelInstance() {
		return vmInstance;
	}

	public void setVirtualModelInstance(VirtualModelInstance vmInstance) {
		this.vmInstance = vmInstance;
	}

	public void setModelSlot(MS modelSlot) {
		this.modelSlot = modelSlot;
	}

	public MS getModelSlot() {
		if (getVirtualModelInstance() != null && modelSlot == null && StringUtils.isNotEmpty(modelSlotName)) {
			modelSlot = (MS) getVirtualModelInstance().getVirtualModel().getModelSlot(modelSlotName);
		}
		return modelSlot;
	}

	public void setAccessedResourceData(RD accessedResourceData) {
		this.accessedResourceData = accessedResourceData;
		this.resource = (TechnologyAdapterResource<RD, ?>) accessedResourceData.getResource();
	}

	public RD getAccessedResourceData() {
		return accessedResourceData;
	}

	public TechnologyAdapterResource<RD, ?> getResource() {
		return resource;
	}

	// Serialization/deserialization only, do not use
	public String getModelSlotName() {
		if (getModelSlot() != null) {
			return getModelSlot().getName();
		}
		return modelSlotName;
	}

	// Serialization/deserialization only, do not use
	public void setModelSlotName(String modelSlotName) {
		this.modelSlotName = modelSlotName;
	}

	@Override
	public String toString() {
		return "ModelSlotInstance:"
				+ (getModelSlot() != null ? getModelSlot().getName() + ":" + getModelSlot().getClass().getSimpleName() + "_" + getFlexoID()
						: "null");
	}

	/**
	 * Returns a string describing how the model slot instance is bound to a data source
	 * 
	 * @return
	 */
	public abstract String getBindingDescription();
}
