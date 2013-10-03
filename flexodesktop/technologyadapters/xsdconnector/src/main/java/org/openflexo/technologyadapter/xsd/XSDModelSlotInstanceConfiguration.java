package org.openflexo.technologyadapter.xsd;

import org.openflexo.foundation.technologyadapter.TypeAwareModelSlotInstanceConfiguration;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.technologyadapter.xsd.metamodel.XSDMetaModel;
import org.openflexo.technologyadapter.xsd.model.XMLXSDModel;

public class XSDModelSlotInstanceConfiguration extends TypeAwareModelSlotInstanceConfiguration<XMLXSDModel, XSDMetaModel, XSDModelSlot> {

	protected XSDModelSlotInstanceConfiguration(XSDModelSlot ms, CreateVirtualModelInstance<?> action) {
		super(ms, action);
	}

	@Override
	public void setOption(org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration.ModelSlotInstanceConfigurationOption option) {
		super.setOption(option);
		if (option == DefaultModelSlotInstanceConfigurationOption.CreatePrivateNewModel) {
			modelUri = getAction().getFocusedObject().getProject().getURI() + "/Models/myXMLFile";
			relativePath = "/";
			filename = "myXMLFile" + getModelSlot().getTechnologyAdapter().getExpectedModelExtension(getModelSlot().getMetaModelResource());
		} /*else if (option == DefaultModelSlotInstanceConfigurationOption.CreateSharedNewModel) {
			modelUri = "ResourceCenter/Models/";
			relativePath = "/";
			filename = "myXMLFile"
					+ getModelSlot().getTechnologyAdapter().getExpectedModelExtension(
							(XSDMetaModelResource) getModelSlot().getMetaModelResource());
			}*/
	}

	@Override
	public boolean isURIEditable() {
		return false;
	}

}
