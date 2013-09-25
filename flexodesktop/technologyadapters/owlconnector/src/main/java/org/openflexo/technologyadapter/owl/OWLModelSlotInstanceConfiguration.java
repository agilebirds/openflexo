package org.openflexo.technologyadapter.owl;

import java.util.logging.Logger;

import org.openflexo.foundation.resource.FileSystemBasedResourceCenter;
import org.openflexo.foundation.technologyadapter.FlexoModelResource;
import org.openflexo.foundation.technologyadapter.TypeSafeModelSlotInstanceConfiguration;
import org.openflexo.foundation.view.TypeSafeModelSlotInstance;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration.DefaultModelSlotInstanceConfigurationOption;
import org.openflexo.technologyadapter.owl.model.OWLOntology;
import org.openflexo.toolbox.StringUtils;

public class OWLModelSlotInstanceConfiguration extends TypeSafeModelSlotInstanceConfiguration<OWLOntology, OWLOntology, OWLModelSlot> {

	private static final Logger logger = Logger.getLogger(TypeSafeModelSlotInstanceConfiguration.class.getPackage().getName());

	protected OWLModelSlotInstanceConfiguration(OWLModelSlot ms, CreateVirtualModelInstance<?> action) {
		super(ms, action);
		options.add(DefaultModelSlotInstanceConfigurationOption.CreateSharedNewModel);
	}
	
	@Override
	protected TypeSafeModelSlotInstance<OWLOntology, OWLOntology, OWLModelSlot> configureModelSlotInstance(
			TypeSafeModelSlotInstance<OWLOntology, OWLOntology, OWLModelSlot> msInstance) {
		if (getOption() == DefaultModelSlotInstanceConfigurationOption.CreateSharedNewModel) {
			modelResource = createSharedEmptyModel(msInstance, getModelSlot());
			if (modelResource != null) {
				msInstance.setResourceData(getModelResource().getModel());
				msInstance.setModelURI(getModelResource().getURI());
			} else {
				logger.warning("Could not create SharedEmptyModel for model slot " + getModelSlot());
			}
			return msInstance;
		}
		else{
			return super.configureModelSlotInstance(msInstance);
		}
	}

	private FlexoModelResource<OWLOntology, OWLOntology> createSharedEmptyModel(TypeSafeModelSlotInstance<OWLOntology, OWLOntology, OWLModelSlot> msInstance, OWLModelSlot modelSlot) {
		return modelSlot.createSharedEmptyModel(getResourceCenter(), getRelativePath(), getFilename(), getModelUri(),
				modelSlot.getMetaModelResource());
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

	@Override
	public boolean isValidConfiguration() {
		if (getOption() == DefaultModelSlotInstanceConfigurationOption.CreateSharedNewModel) {
			return getResourceCenter() != null && getResourceCenter() instanceof FileSystemBasedResourceCenter && StringUtils.isNotEmpty(getModelUri()) && StringUtils.isNotEmpty(getRelativePath())
					&& StringUtils.isNotEmpty(getFilename());
		}
		else{
			return super.isValidConfiguration();
		}	
	}

	
}
