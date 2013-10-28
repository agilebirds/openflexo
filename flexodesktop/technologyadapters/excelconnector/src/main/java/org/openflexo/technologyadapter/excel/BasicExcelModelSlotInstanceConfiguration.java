package org.openflexo.technologyadapter.excel;

import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.technologyadapter.FreeModelSlotInstanceConfiguration;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterResource;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration;
import org.openflexo.technologyadapter.excel.model.ExcelWorkbook;

public class BasicExcelModelSlotInstanceConfiguration extends FreeModelSlotInstanceConfiguration<ExcelWorkbook, BasicExcelModelSlot> {

	private static final Logger logger = Logger.getLogger(ModelSlotInstanceConfiguration.class.getPackage().getName());

	protected List<ModelSlotInstanceConfigurationOption> options;

	protected FlexoResourceCenter<?> resourceCenter;
	protected TechnologyAdapterResource<ExcelWorkbook> modelResource;

	protected BasicExcelModelSlotInstanceConfiguration(BasicExcelModelSlot ms, CreateVirtualModelInstance<?> action) {
		super(ms, action);
	}

	@Override
	public void setOption(org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration.ModelSlotInstanceConfigurationOption option) {
		super.setOption(option);
		if (option == DefaultModelSlotInstanceConfigurationOption.CreatePrivateNewResource) {
			resourceUri = getAction().getFocusedObject().getProject().getURI() + "/Models/myExcelModel";
			relativePath = "/";
			filename = "myExcelResource.xls";
		} 
	}

}
