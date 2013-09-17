package org.openflexo.technologyadapter.owl;

import org.openflexo.foundation.technologyadapter.TypeSafeModelSlotInstanceConfiguration;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.technologyadapter.owl.model.OWLOntology;

public class OWLModelSlotInstanceConfiguration extends TypeSafeModelSlotInstanceConfiguration<OWLOntology, OWLOntology, OWLModelSlot> {

	protected OWLModelSlotInstanceConfiguration(OWLModelSlot ms, CreateVirtualModelInstance<?> action) {
		super(ms, action);
	}

	@Override
	public void setOption(org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration.ModelSlotInstanceConfigurationOption option) {
		super.setOption(option);
		if (option == DefaultModelSlotInstanceConfigurationOption.CreatePrivateNewModel) {
			modelUri = getAction().getFocusedObject().getProject().getURI() + "/Models/myOntology";
			relativePath = "/";
			filename = "myOntology" + getModelSlot().getTechnologyAdapter().getExpectedOntologyExtension();
		} else if (option == DefaultModelSlotInstanceConfigurationOption.CreateSharedNewModel) {
			modelUri = "ResourceCenter/Models/";
			relativePath = "/";
			filename = "myOntology" + getModelSlot().getTechnologyAdapter().getExpectedOntologyExtension();
		}
	}

}
