package org.openflexo.technologyadapter.powerpoint;

import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.technologyadapter.FreeModelSlotInstanceConfiguration;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterResource;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointSlideshow;

public class BasicPowerpointModelSlotInstanceConfiguration extends FreeModelSlotInstanceConfiguration<PowerpointSlideshow, BasicPowerpointModelSlot> {

	private static final Logger logger = Logger.getLogger(ModelSlotInstanceConfiguration.class.getPackage().getName());

	protected List<ModelSlotInstanceConfigurationOption> options;

	protected FlexoResourceCenter<?> resourceCenter;
	protected TechnologyAdapterResource<PowerpointSlideshow> modelResource;

	protected BasicPowerpointModelSlotInstanceConfiguration(BasicPowerpointModelSlot ms, CreateVirtualModelInstance<?> action) {
		super(ms, action);
	}

	@Override
	public void setOption(org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration.ModelSlotInstanceConfigurationOption option) {
		super.setOption(option);
		if (option == DefaultModelSlotInstanceConfigurationOption.CreatePrivateNewResource) {
			resourceUri = getAction().getFocusedObject().getProject().getURI() + "/Models/myPowerpointModel";
			relativePath = "/";
			filename = "myPowerpointResource.ppt";
		} 
	}

}
