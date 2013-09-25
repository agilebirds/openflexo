package org.openflexo.technologyadapter.xsd;

import org.openflexo.foundation.technologyadapter.TypeSafeModelSlotInstanceConfiguration;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.technologyadapter.xsd.metamodel.XSDMetaModel;
import org.openflexo.technologyadapter.xsd.model.XMLXSDModel;
import org.openflexo.technologyadapter.xsd.rm.XSDMetaModelResource;

public class XSDModelSlotInstanceConfiguration extends TypeSafeModelSlotInstanceConfiguration<XMLXSDModel, XSDMetaModel, XSDModelSlot> {

	protected XSDModelSlotInstanceConfiguration(XSDModelSlot ms, CreateVirtualModelInstance<?> action) {
		super(ms, action);
	}

	@Override
	public void setOption(org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration.ModelSlotInstanceConfigurationOption option) {
		super.setOption(option);
		if (option == DefaultModelSlotInstanceConfigurationOption.CreatePrivateNewModel) {
			modelUri = getAction().getFocusedObject().getProject().getURI() + "/Models/myXMLFile";
			relativePath = "/";
			filename = "myXMLFile"
					+ getModelSlot().getTechnologyAdapter().getExpectedModelExtension(
							(XSDMetaModelResource) getModelSlot().getMetaModelResource());
		} /*else if (option == DefaultModelSlotInstanceConfigurationOption.CreateSharedNewModel) {
			modelUri = "ResourceCenter/Models/";
			relativePath = "/";
			filename = "myXMLFile"
					+ getModelSlot().getTechnologyAdapter().getExpectedModelExtension(
							(XSDMetaModelResource) getModelSlot().getMetaModelResource());
		}*/
	}

}
