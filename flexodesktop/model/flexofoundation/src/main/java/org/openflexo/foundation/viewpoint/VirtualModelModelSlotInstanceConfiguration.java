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
package org.openflexo.foundation.viewpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.rm.VirtualModelInstanceResource;
import org.openflexo.foundation.view.ModelSlotInstance;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration;

/**
 * This class is used to stored the configuration of a {@link VirtualModelModelSlot} which has to be instantiated
 * 
 * 
 * @author sylvain
 * 
 */
public class VirtualModelModelSlotInstanceConfiguration<MS extends VirtualModelModelSlot<?, ?>> extends ModelSlotInstanceConfiguration<MS> {

	private static final Logger logger = Logger.getLogger(VirtualModelModelSlotInstanceConfiguration.class.getPackage().getName());

	private List<ModelSlotInstanceConfigurationOption> options;
	private VirtualModelInstanceResource addressedVirtualModelInstanceResource;

	protected VirtualModelModelSlotInstanceConfiguration(MS ms, CreateVirtualModelInstance<?> action) {
		super(ms, action);
		options = new ArrayList<ModelSlotInstanceConfiguration.ModelSlotInstanceConfigurationOption>();
		options.add(DefaultModelSlotInstanceConfigurationOption.SelectExistingVirtualModel);
		options.add(DefaultModelSlotInstanceConfigurationOption.CreateNewVirtualModel);
		if (!ms.getIsRequired()) {
			options.add(DefaultModelSlotInstanceConfigurationOption.LeaveEmpty);
		}
	}

	@Override
	public List<ModelSlotInstanceConfigurationOption> getAvailableOptions() {
		return options;
	}

	@Override
	public ModelSlotInstance<?, ?> createModelSlotInstance(VirtualModelInstance<?, ?> vmInstance) {
		ModelSlotInstance<?, ?> returned = new ModelSlotInstance(vmInstance, getModelSlot());
		returned.setModelURI(getAddressedVirtualModelInstanceResource().getURI());
		return returned;
	}

	public VirtualModelInstanceResource getAddressedVirtualModelInstanceResource() {
		return addressedVirtualModelInstanceResource;
	}

	public void setAddressedVirtualModelInstanceResource(VirtualModelInstanceResource addressedVirtualModelInstanceResource) {
		this.addressedVirtualModelInstanceResource = addressedVirtualModelInstanceResource;
	}

	@Override
	public boolean isValidConfiguration() {
		if (!super.isValidConfiguration()) {
			return false;
		}
		if (getOption() == DefaultModelSlotInstanceConfigurationOption.SelectExistingVirtualModel) {
			return getAddressedVirtualModelInstanceResource() != null;
		} else if (getOption() == DefaultModelSlotInstanceConfigurationOption.CreateNewVirtualModel) {
			// Not implemented yet
			return false;

		}
		return false;
	}

}