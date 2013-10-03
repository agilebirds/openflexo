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
import org.openflexo.foundation.view.TypeAwareModelSlotInstance;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration;
import org.openflexo.toolbox.StringUtils;

/**
 * This class is used to stored the configuration of a {@link TypeAwareModelSlot} which has to be instantiated
 * 
 * 
 * @author sylvain
 * 
 */
public abstract class TypeAwareModelSlotInstanceConfiguration<M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>, MS extends TypeAwareModelSlot<M, MM>>
		extends ModelSlotInstanceConfiguration<MS, M> {

	private static final Logger logger = Logger.getLogger(TypeAwareModelSlotInstanceConfiguration.class.getPackage().getName());

	protected List<ModelSlotInstanceConfigurationOption> options;

	protected FlexoResourceCenter<?> resourceCenter;
	protected FlexoModelResource<M, MM> modelResource;
	protected String modelUri;
	protected String relativePath;
	protected String filename;

	protected TypeAwareModelSlotInstanceConfiguration(MS ms, CreateVirtualModelInstance<?> action) {
		super(ms, action);
		resourceCenter = action.getFocusedObject().getViewPoint().getViewPointLibrary().getServiceManager().getResourceCenterService()
				.getUserResourceCenter();
		options = new ArrayList<ModelSlotInstanceConfiguration.ModelSlotInstanceConfigurationOption>();
		options.add(DefaultModelSlotInstanceConfigurationOption.SelectExistingModel);
		options.add(DefaultModelSlotInstanceConfigurationOption.CreatePrivateNewModel);
		//options.add(DefaultModelSlotInstanceConfigurationOption.CreateSharedNewModel);
		if (!ms.getIsRequired()) {
			options.add(DefaultModelSlotInstanceConfigurationOption.LeaveEmpty);
		}
	}

	@Override
	public void setOption(org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration.ModelSlotInstanceConfigurationOption option) {
		super.setOption(option);
		if (option == DefaultModelSlotInstanceConfigurationOption.SelectExistingModel) {
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
	public TypeAwareModelSlotInstance<M, MM, MS> createModelSlotInstance(VirtualModelInstance<?, ?> vmInstance) {
		TypeAwareModelSlotInstance<M, MM, MS> returned = new TypeAwareModelSlotInstance<M, MM, MS>(vmInstance, getModelSlot());
		configureModelSlotInstance(returned);
		return returned;
	}

	protected TypeAwareModelSlotInstance<M, MM, MS> configureModelSlotInstance(TypeAwareModelSlotInstance<M, MM, MS> msInstance) {
		if (getOption() == DefaultModelSlotInstanceConfigurationOption.SelectExistingModel) {
			if (modelResource != null) {
				System.out.println("Select model with uri " + getModelResource().getURI());
				msInstance.setResourceData(getModelResource().getModel());
				msInstance.setModelURI(getModelResource().getURI());
			} else {
				logger.warning("No model for model slot " + getModelSlot());
			}
		} else if (getOption() == DefaultModelSlotInstanceConfigurationOption.CreatePrivateNewModel) {
			modelResource = createProjectSpecificEmptyModel(msInstance, getModelSlot());
			System.out.println("***** modelResource = " + modelResource);
			if (modelResource != null) {
				msInstance.setResourceData(getModelResource().getModel());
				msInstance.setModelURI(getModelResource().getURI());
				System.out.println("***** Created model resource " + getModelResource());
				System.out.println("***** Created model " + getModelResource().getModel());
				System.out.println("***** Created model with uri=" + getModelResource().getModel().getURI());
			} else {
				logger.warning("Could not create ProjectSpecificEmtpyModel for model slot " + getModelSlot());
			}
		} /*else if (getOption() == DefaultModelSlotInstanceConfigurationOption.CreateSharedNewModel) {
			modelResource = createSharedEmptyModel(msInstance, getModelSlot());
			if (modelResource != null) {
				msInstance.setResourceData(getModelResource().getModel());
				msInstance.setModelURI(getModelResource().getURI());
			} else {
				logger.warning("Could not create SharedEmptyModel for model slot " + getModelSlot());
			}
			return msInstance;
		}*/
		return null;
	}

	private FlexoModelResource<M, MM> createProjectSpecificEmptyModel(TypeAwareModelSlotInstance<M, MM, MS> msInstance, MS modelSlot) {
		return modelSlot.createProjectSpecificEmptyModel(msInstance.getView(), getFilename(), getModelUri(),
				modelSlot.getMetaModelResource());
	}

	/*private FlexoModelResource<M, MM> createSharedEmptyModel(TypeAwareModelSlotInstance<M, MM, MS> msInstance, MS modelSlot) {
		return modelSlot.createSharedEmptyModel(getResourceCenter(), getRelativePath(), getFilename(), getModelUri(),
				modelSlot.getMetaModelResource());
	}*/

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

	public FlexoModelResource<M, MM> getModelResource() {
		return modelResource;
	}

	public void setModelResource(FlexoModelResource<M, MM> modelResource) {
		this.modelResource = modelResource;
	}

	@Override
	public boolean isValidConfiguration() {
		if (!super.isValidConfiguration()) {
			return false;
		}
		if (getOption() == DefaultModelSlotInstanceConfigurationOption.SelectExistingModel) {
			if (getResourceCenter() == null) {
				logger.warning("Null resource center");
			}
			if (getModelResource() == null) {
				logger.warning("Null model resource");
			}
			return getResourceCenter() != null && getModelResource() != null;
		} else if (getOption() == DefaultModelSlotInstanceConfigurationOption.CreatePrivateNewModel) {
			return StringUtils.isNotEmpty(getModelUri()) && StringUtils.isNotEmpty(getRelativePath())
					&& StringUtils.isNotEmpty(getFilename());

		}/* else if (getOption() == DefaultModelSlotInstanceConfigurationOption.CreateSharedNewModel) {
			return getResourceCenter() != null && StringUtils.isNotEmpty(getModelUri()) && StringUtils.isNotEmpty(getRelativePath())
					&& StringUtils.isNotEmpty(getFilename());

		}*/
		return false;
	}
	
	public abstract boolean isURIEditable();
}
