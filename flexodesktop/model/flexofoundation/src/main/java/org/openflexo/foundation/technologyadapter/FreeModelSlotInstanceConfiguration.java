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

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceData;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.rm.ResourceDependencyLoopException;
import org.openflexo.foundation.view.FreeModelSlotInstance;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration;
import org.openflexo.toolbox.StringUtils;

/**
 * This class is used to stored the configuration of a {@link FreeModelSlot} which has to be instantiated
 * 
 * 
 * @author sylvain, vincent
 * 
 */
public class FreeModelSlotInstanceConfiguration<RD extends ResourceData<RD>, MS extends FreeModelSlot<RD>> extends
		ModelSlotInstanceConfiguration<MS, RD> {

	private static final Logger logger = Logger.getLogger(FreeModelSlotInstanceConfiguration.class.getPackage().getName());

	protected List<ModelSlotInstanceConfigurationOption> options;

	protected FlexoResourceCenter<?> resourceCenter;
	private TechnologyAdapterResource<RD, ?> resource;
	protected String resourceUri;
	protected String relativePath;
	protected String filename;

	protected FreeModelSlotInstanceConfiguration(MS ms, CreateVirtualModelInstance<?> action) {
		super(ms, action);
		resourceCenter = action.getFocusedObject().getViewPoint().getViewPointLibrary().getServiceManager().getResourceCenterService()
				.getUserResourceCenter();
		options = new ArrayList<ModelSlotInstanceConfiguration.ModelSlotInstanceConfigurationOption>();
		options.add(DefaultModelSlotInstanceConfigurationOption.SelectExistingResource);
		options.add(DefaultModelSlotInstanceConfigurationOption.CreatePrivateNewResource);
		// options.add(DefaultModelSlotInstanceConfigurationOption.CreateSharedNewResource);
		if (!ms.getIsRequired()) {
			options.add(DefaultModelSlotInstanceConfigurationOption.LeaveEmpty);
		}
	}

	@Override
	public void setOption(org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration.ModelSlotInstanceConfigurationOption option) {
		super.setOption(option);
		if (option == DefaultModelSlotInstanceConfigurationOption.SelectExistingResource) {
			resourceUri = null;
			relativePath = null;
			filename = null;
		}
	}

	@Override
	public List<ModelSlotInstanceConfigurationOption> getAvailableOptions() {
		return options;
	}

	@Override
	public FreeModelSlotInstance<RD, MS> createModelSlotInstance(VirtualModelInstance vmInstance) {
		FreeModelSlotInstance<RD, MS> returned = new FreeModelSlotInstance<RD, MS>(vmInstance, getModelSlot());
		configureModelSlotInstance(returned);
		return returned;
	}

	protected FreeModelSlotInstance<RD, MS> configureModelSlotInstance(FreeModelSlotInstance<RD, MS> msInstance) {
		try {
			if (getOption() == DefaultModelSlotInstanceConfigurationOption.SelectExistingResource) {
				if (resource != null) {
					msInstance.setResourceData(getResource().getResourceData(null));
				} else {
					logger.warning("No resource for model slot " + getModelSlot());
				}
			} else if (getOption() == DefaultModelSlotInstanceConfigurationOption.CreatePrivateNewResource) {
				resource = createProjectSpecificEmptyResource(msInstance, getModelSlot());
				System.out.println("***** modelResource = " + resource);
				if (resource != null) {
					msInstance.setResourceData(getResource().getResourceData(null));
				} else {
					logger.warning("Could not create ProjectSpecificEmtpyResource for model slot " + getModelSlot());
				}
			}/* else if (getOption() == DefaultModelSlotInstanceConfigurationOption.CreateSharedNewResource) {
				// resource = createSharedEmptyResource(msInstance, getModelSlot());
				if (resource != null) {
					msInstance.setResourceData(getResource().getResourceData(null));
				}
				} else {
				logger.warning("Could not create SharedEmptyResource for model slot " + getModelSlot());
				}*/
			return msInstance;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ResourceLoadingCancelledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ResourceDependencyLoopException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FlexoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private TechnologyAdapterResource<RD, ?> createProjectSpecificEmptyResource(FreeModelSlotInstance<RD, MS> msInstance, MS modelSlot) {
		return modelSlot.createProjectSpecificEmptyResource(msInstance.getView(), getFilename(), getResourceUri());
	}

	private TechnologyAdapterResource<RD, ?> createSharedEmptyResource(FreeModelSlotInstance<RD, MS> msInstance, MS modelSlot) {
		return modelSlot.createSharedEmptyResource(getResourceCenter(), getRelativePath(), getFilename(), getResourceUri());
	}

	public FlexoResourceCenter getResourceCenter() {
		return resourceCenter;
	}

	public void setResourceCenter(FlexoResourceCenter resourceCenter) {
		this.resourceCenter = resourceCenter;
	}

	public String getResourceUri() {
		return resourceUri;
	}

	public void setResourceUri(String resourceUri) {
		this.resourceUri = resourceUri;
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

	public TechnologyAdapterResource<RD, ?> getResource() {
		return resource;
	}

	public void setResource(TechnologyAdapterResource<RD, ?> resource) {
		this.resource = resource;
	}

	@Override
	public boolean isValidConfiguration() {
		if (!super.isValidConfiguration()) {
			return false;
		}
		if (getOption() == DefaultModelSlotInstanceConfigurationOption.SelectExistingResource) {
			if (getResourceCenter() == null) {
				logger.warning("Null resource center");
			}
			if (getResource() == null) {
				logger.warning("Null resource");
			}
			return getResourceCenter() != null && getResource() != null;
		} else if (getOption() == DefaultModelSlotInstanceConfigurationOption.CreatePrivateNewResource) {
			return StringUtils.isNotEmpty(getResourceUri()) && StringUtils.isNotEmpty(getRelativePath())
					&& StringUtils.isNotEmpty(getFilename());

		} /*else if (getOption() == DefaultModelSlotInstanceConfigurationOption.CreateSharedNewResource) {
			return getResourceCenter() != null && StringUtils.isNotEmpty(getResourceUri()) && StringUtils.isNotEmpty(getRelativePath())
					&& StringUtils.isNotEmpty(getFilename());

			}*/
		return false;
	}
}
