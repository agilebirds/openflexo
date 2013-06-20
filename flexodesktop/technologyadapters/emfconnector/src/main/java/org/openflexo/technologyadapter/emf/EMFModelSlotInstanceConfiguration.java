package org.openflexo.technologyadapter.emf;

import org.openflexo.foundation.technologyadapter.TypeSafeModelSlotInstanceConfiguration;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.model.EMFModel;
import org.openflexo.technologyadapter.emf.rm.EMFMetaModelResource;

public class EMFModelSlotInstanceConfiguration extends TypeSafeModelSlotInstanceConfiguration<EMFModel, EMFMetaModel, EMFModelSlot> {

	protected EMFModelSlotInstanceConfiguration(EMFModelSlot ms, CreateVirtualModelInstance<?> action) {
		super(ms, action);
	}

	@Override
	public void setOption(org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration.ModelSlotInstanceConfigurationOption option) {
		super.setOption(option);
		if (option == DefaultModelSlotInstanceConfigurationOption.CreatePrivateNewModel) {
			modelUri = getAction().getFocusedObject().getProject().getURI() + "/Models/myEMFModel";
			relativePath = "/";
			filename = "myEMFModel"
					+ getModelSlot().getTechnologyAdapter().getExpectedModelExtension(
							(EMFMetaModelResource) getModelSlot().getMetaModelResource());
		} else if (option == DefaultModelSlotInstanceConfigurationOption.CreateSharedNewModel) {
			modelUri = "ResourceCenter/Models/";
			relativePath = "/";
			filename = "myEMFModel"
					+ getModelSlot().getTechnologyAdapter().getExpectedModelExtension(
							(EMFMetaModelResource) getModelSlot().getMetaModelResource());
		}
	}

}
