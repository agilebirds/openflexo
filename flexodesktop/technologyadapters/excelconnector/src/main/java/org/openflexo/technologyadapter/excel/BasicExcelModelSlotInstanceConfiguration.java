package org.openflexo.technologyadapter.excel;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.rm.ResourceDependencyLoopException;
import org.openflexo.foundation.technologyadapter.FlexoModelResource;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterResource;
import org.openflexo.foundation.technologyadapter.TypeSafeModelSlotInstanceConfiguration;
import org.openflexo.foundation.view.ModelSlotInstance;
import org.openflexo.foundation.view.TypeSafeModelSlotInstance;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration;
import org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration.DefaultModelSlotInstanceConfigurationOption;
import org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration.ModelSlotInstanceConfigurationOption;
import org.openflexo.technologyadapter.excel.model.ExcelWorkbook;
import org.openflexo.toolbox.StringUtils;

public class BasicExcelModelSlotInstanceConfiguration extends ModelSlotInstanceConfiguration<BasicExcelModelSlot,ExcelWorkbook> {

	private static final Logger logger = Logger.getLogger(ModelSlotInstanceConfiguration.class.getPackage().getName());

	protected List<ModelSlotInstanceConfigurationOption> options;

	protected FlexoResourceCenter<?> resourceCenter;
	protected TechnologyAdapterResource<ExcelWorkbook> modelResource;
	protected String modelUri;
	protected String relativePath;
	protected String filename;

	protected BasicExcelModelSlotInstanceConfiguration(BasicExcelModelSlot ms, CreateVirtualModelInstance<?> action) {
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
			modelUri = getAction().getFocusedObject().getProject().getURI() + "/Models/myEMFModel";
			relativePath = "/";
			filename = "myEMFModel";
		} else if (option == DefaultModelSlotInstanceConfigurationOption.CreateSharedNewModel) {
			modelUri = "ResourceCenter/Models/";
			relativePath = "/";
			filename = "myEMFModel";
		}
	}

	@Override
	public List<ModelSlotInstanceConfigurationOption> getAvailableOptions() {
		return options;
	}


	protected ModelSlotInstance<BasicExcelModelSlot,ExcelWorkbook> configureModelSlotInstance(ModelSlotInstance<BasicExcelModelSlot,ExcelWorkbook> msInstance) {
		if (getOption() == DefaultModelSlotInstanceConfigurationOption.SelectExistingModel) {
			if (modelResource != null) {
				System.out.println("Select model with uri " + getModelResource().getURI());
				try {
					msInstance.setResourceData(getModelResource().getResourceData(null));
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
			} else {
				logger.warning("No model for model slot " + getModelSlot());
			}
		} else if (getOption() == DefaultModelSlotInstanceConfigurationOption.CreatePrivateNewModel) {
			
		} else if (getOption() == DefaultModelSlotInstanceConfigurationOption.CreateSharedNewModel) {
			
		}
		return null;
	}

	private TechnologyAdapterResource<ExcelWorkbook> createProjectSpecificEmptyModel(ModelSlotInstance<BasicExcelModelSlot,ExcelWorkbook> msInstance, BasicExcelModelSlot modelSlot) {
		/*return modelSlot.createProjectSpecificEmptyModel(msInstance.getView(), getFilename(), getModelUri(),
				modelSlot.getMetaModelResource());*/
		return null;
	}

	private TechnologyAdapterResource<ExcelWorkbook> createSharedEmptyModel(ModelSlotInstance<BasicExcelModelSlot,ExcelWorkbook> msInstance, BasicExcelModelSlot modelSlot) {
		/*return modelSlot.createSharedEmptyModel(getResourceCenter(), getRelativePath(), getFilename(), getModelUri(),
				modelSlot.getMetaModelResource());*/
		return null;
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

	public TechnologyAdapterResource<ExcelWorkbook> getModelResource() {
		return modelResource;
	}

	public void setModelResource(TechnologyAdapterResource<ExcelWorkbook> modelResource) {
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

		} else if (getOption() == DefaultModelSlotInstanceConfigurationOption.CreateSharedNewModel) {
			return getResourceCenter() != null && StringUtils.isNotEmpty(getModelUri()) && StringUtils.isNotEmpty(getRelativePath())
					&& StringUtils.isNotEmpty(getFilename());

		}
		return false;
	}

	@Override
	public BasicExcelModelSlotInstance<BasicExcelModelSlot> createModelSlotInstance(VirtualModelInstance<?, ?> msInstance) {
		/*BasicExcelModelSlotInstance<BasicExcelModelSlot> returned = new BasicExcelModelSlotInstance<BasicExcelModelSlot>(msInstance);
		configureModelSlotInstance(returned);*/
		return null;
	}

	
}
