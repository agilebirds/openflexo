package org.openflexo.technologyadapter.emf;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.view.ModelSlotInstance;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;

public class EMFMetaModelSlotInstanceConfiguration extends ModelSlotInstanceConfiguration<EMFMetaModelSlot, EMFMetaModel> {

	private static final Logger logger = Logger.getLogger(EMFMetaModelSlotInstanceConfiguration.class.getPackage().getName());

	protected List<ModelSlotInstanceConfigurationOption> options;

	protected EMFMetaModelSlotInstanceConfiguration(EMFMetaModelSlot ms, CreateVirtualModelInstance<?> action) {
		super(ms, action);
		options = new ArrayList<ModelSlotInstanceConfiguration.ModelSlotInstanceConfigurationOption>();
		options.add(DefaultModelSlotInstanceConfigurationOption.SelectExistingMetaModel);
	}

	@Override
	public List<org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration.ModelSlotInstanceConfigurationOption> getAvailableOptions() {
		return options;
	}

	@Override
	public ModelSlotInstance<EMFMetaModelSlot, EMFMetaModel> createModelSlotInstance(VirtualModelInstance msInstance) {
		logger.warning("Please implement me !!!!");
		return null;
	}

}
