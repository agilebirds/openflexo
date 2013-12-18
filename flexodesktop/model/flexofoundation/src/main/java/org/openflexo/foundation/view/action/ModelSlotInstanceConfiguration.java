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
package org.openflexo.foundation.view.action;

import java.util.List;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.resource.ResourceData;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.view.ModelSlotInstance;
import org.openflexo.foundation.view.VirtualModelInstance;

/**
 * This class is used to stored the configuration of a {@link ModelSlot} which has to be instantiated
 * 
 * 
 * @author sylvain
 * 
 */
public abstract class ModelSlotInstanceConfiguration<MS extends ModelSlot<RD>, RD extends ResourceData<RD>> extends FlexoObject {

	private final CreateVirtualModelInstance<?> action;
	private final MS modelSlot;
	private ModelSlotInstanceConfigurationOption option;

	public static interface ModelSlotInstanceConfigurationOption {
		public String name();

		public String getDescriptionKey();
	}

	public static enum DefaultModelSlotInstanceConfigurationOption implements ModelSlotInstanceConfigurationOption {

		/**
		 * Retrieve an existing resource from a ResourceCenter
		 */
		SelectExistingResource,
		/**
		 * Retrieve an existing model from a ResourceCenter
		 */
		SelectExistingModel,
		/**
		 * Retrieve an existing metamodel from a ResourceCenter
		 */
		SelectExistingMetaModel,
		/**
		 * Create a dedicated model in the scope of current {@link FlexoProject}
		 */
		CreatePrivateNewModel,
		/**
		 * Create a resource in a ResourceCenter (the resource might be shared and concurrently accessed)
		 */
		CreatePrivateNewResource,
		/**
		 * Create a model in a ResourceCenter (the model might be shared and concurrently accessed)
		 */
		CreateSharedNewModel,
		/**
		 * Create a resource in a ResourceCenter (the resource might be shared and concurrently accessed)
		 */
		CreateSharedNewResource,
		/**
		 * Retrieve an existing virtual model instance
		 */
		SelectExistingVirtualModel,
		/**
		 * Creates a new virtual model
		 */
		CreateNewVirtualModel,
		/**
		 * Leave empty and decide later
		 */
		LeaveEmpty,
		/**
		 * Let Openflexo manage this
		 */
		Autoconfigure;

		@Override
		public String getDescriptionKey() {
			return name() + "_description";
		}
	}

	protected ModelSlotInstanceConfiguration(MS ms, CreateVirtualModelInstance<?> action) {
		this.action = action;
		modelSlot = ms;
	}

	public CreateVirtualModelInstance<?> getAction() {
		return action;
	}

	public MS getModelSlot() {
		return modelSlot;
	}

	public ModelSlotInstanceConfigurationOption getOption() {
		return option;
	}

	public void setOption(ModelSlotInstanceConfigurationOption option) {
		this.option = option;
	}

	public abstract List<ModelSlotInstanceConfigurationOption> getAvailableOptions();

	public boolean isValidConfiguration() {
		return option != null;
	}

	public abstract ModelSlotInstance<MS, RD> createModelSlotInstance(VirtualModelInstance msInstance);

	private String errorMessage;

	public String getErrorMessage() {
		isValid();
		return errorMessage;
	}
}
