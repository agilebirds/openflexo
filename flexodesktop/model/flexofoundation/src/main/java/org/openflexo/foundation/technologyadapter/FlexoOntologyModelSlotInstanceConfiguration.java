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
package org.openflexo.foundation.technologyadapter;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.view.ModelSlotInstance;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration;
import org.openflexo.toolbox.StringUtils;

/**
 * This class is used to stored the configuration of a {@link FlexoOntologyModelSlot} which has to be instantiated
 * 
 * 
 * @author sylvain
 * 
 */
public class FlexoOntologyModelSlotInstanceConfiguration<MS extends FlexoOntologyModelSlot<?, ?>> extends
		ModelSlotInstanceConfiguration<MS> {

	private static final Logger logger = Logger.getLogger(FlexoOntologyModelSlotInstanceConfiguration.class.getPackage().getName());

	private List<ModelSlotInstanceConfigurationOption> options;

	private FlexoResourceCenter resourceCenter;
	private FlexoModelResource<?, ?> modelResource;
	private String modelUri;
	private String relativePath;
	private String filename;

	protected FlexoOntologyModelSlotInstanceConfiguration(MS ms, CreateVirtualModelInstance<?> action) {
		super(ms, action);
		resourceCenter = action.getFocusedObject().getViewPoint().getViewPointLibrary().getServiceManager().getResourceCenterService()
				.getUserResourceCenter();
		options = new ArrayList<ModelSlotInstanceConfiguration.ModelSlotInstanceConfigurationOption>();
		options.add(DefaultModelSlotInstanceConfigurationOption.SelectExistingModel);
		options.add(DefaultModelSlotInstanceConfigurationOption.CreatePrivateNewModel);
		options.add(DefaultModelSlotInstanceConfigurationOption.CreateSharedNewModel);
		if (!ms.getIsRequired()) {
			options.add(DefaultModelSlotInstanceConfigurationOption.LeaveEmpty);
		}
	}

	@Override
	public void setOption(org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration.ModelSlotInstanceConfigurationOption option) {
		super.setOption(option);
		if (option == DefaultModelSlotInstanceConfigurationOption.CreatePrivateNewModel) {
			modelUri = getAction().getFocusedObject().getProject().getURI() + "/Models/myModel";
			relativePath = "/";
			filename = "myModel.owl";
		} else if (option == DefaultModelSlotInstanceConfigurationOption.CreateSharedNewModel) {
			modelUri = "ResourceCenter/Models/";
			relativePath = "/";
			filename = "myModel.owl";
		} else if (option == DefaultModelSlotInstanceConfigurationOption.SelectExistingModel) {
			modelUri = null;
			relativePath = null;
			filename = null;
		}
	}

	@Override
	public List<ModelSlotInstanceConfigurationOption> getAvailableOptions() {
		return options;
	}

	@Override
	public ModelSlotInstance<?, ?> createModelSlotInstance(VirtualModelInstance<?, ?> vmInstance) {
		ModelSlotInstance<?, ?> returned = new ModelSlotInstance(vmInstance, getModelSlot());
		configureModelSlotInstance(returned);
		return returned;
	}

	protected <M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> ModelSlotInstance<M, MM> configureModelSlotInstance(
			ModelSlotInstance<M, MM> msInstance) {
		if (getOption() == DefaultModelSlotInstanceConfigurationOption.SelectExistingModel) {
			if (modelResource != null) {
				System.out.println("Select model with uri " + getModelResource().getURI());
				msInstance.setModel((M) getModelResource().getModel());
			} else {
				logger.warning("No model for model slot " + getModelSlot());
			}
		} else if (getOption() == DefaultModelSlotInstanceConfigurationOption.CreatePrivateNewModel) {
			modelResource = createProjectSpecificEmptyModel(msInstance, (ModelSlot<?, ?>) getModelSlot());
			if (modelResource != null) {
				msInstance.setModel((M) getModelResource().getModel());
				System.out.println("***** Created model resource " + getModelResource());
				System.out.println("***** Created model " + getModelResource().getModel());
				System.out.println("***** Created model with uri=" + getModelResource().getModel().getURI());
			} else {
				logger.warning("Could not create ProjectSpecificEmtpyModel for model slot " + getModelSlot());
			}
		} else if (getOption() == DefaultModelSlotInstanceConfigurationOption.CreateSharedNewModel) {
			modelResource = createSharedEmptyModel(msInstance, (ModelSlot<?, ?>) getModelSlot());
			if (modelResource != null) {
				msInstance.setModel((M) getModelResource().getModel());
			} else {
				logger.warning("Could not create SharedEmptyModel for model slot " + getModelSlot());
			}
			return msInstance;
		}
		return null;
	}

	private <M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> FlexoModelResource<M, MM> createProjectSpecificEmptyModel(
			ModelSlotInstance msInstance, ModelSlot<M, MM> modelSlot) {
		return (FlexoModelResource<M, MM>) modelSlot.createProjectSpecificEmptyModel(msInstance.getView(), getFilename(), getModelUri(),
				modelSlot.getMetaModelResource());
	}

	private <M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> FlexoModelResource<M, MM> createSharedEmptyModel(
			ModelSlotInstance msInstance, ModelSlot<M, MM> modelSlot) {
		return (FlexoModelResource<M, MM>) modelSlot.createSharedEmptyModel(getResourceCenter(), getRelativePath(), getFilename(),
				getModelUri(), modelSlot.getMetaModelResource());
	}

	public FlexoResourceCenter getResourceCenter() {
		return resourceCenter;
	}

	public void setResourceCenter(FlexoResourceCenter resourceCenter) {
		this.resourceCenter = resourceCenter;
	}

	public String getModelUri() {
		return modelUri;
	}

	public void setModelUri(String modelUri) {
		this.modelUri = modelUri;
	}

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public FlexoModelResource<?, ?> getModelResource() {
		return modelResource;
	}

	public void setModelResource(FlexoModelResource<?, ?> modelResource) {
		this.modelResource = modelResource;
	}

	@Override
	public boolean isValidConfiguration() {
		if (!super.isValidConfiguration()) {
			return false;
		}
		if (getOption() == DefaultModelSlotInstanceConfigurationOption.SelectExistingModel) {
			return getResourceCenter() != null && getModelResource() != null;
		} else if (getOption() == DefaultModelSlotInstanceConfigurationOption.CreatePrivateNewModel) {
			return StringUtils.isNotEmpty(getModelUri()) && StringUtils.isNotEmpty(getRelativePath())
					&& StringUtils.isNotEmpty(getFilename());

		} else if (getOption() == DefaultModelSlotInstanceConfigurationOption.CreateSharedNewModel) {
			return getResourceCenter() != null && StringUtils.isNotEmpty(getModelUri()) && StringUtils.isNotEmpty(getRelativePath())
					&& StringUtils.isNotEmpty(getFilename());

		}
		return false;
	}

}
