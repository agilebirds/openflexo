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
package org.openflexo.foundation.technologyadapter;

import java.util.ArrayList;
import java.util.List;

import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration;

/**
 * This class is used to stored the configuration of a {@link FlexoOntologyModelSlot} which has to be instantiated
 * 
 * 
 * @author sylvain
 * 
 */
public class FlexoOntologyModelSlotInstanceConfiguration<MS extends FlexoOntologyModelSlot<?, ?>> extends
		ModelSlotInstanceConfiguration<MS> {

	private List<ModelSlotInstanceConfigurationOption> options;

	protected FlexoOntologyModelSlotInstanceConfiguration(MS ms, CreateVirtualModelInstance action) {
		super(ms, action);
		options = new ArrayList<ModelSlotInstanceConfiguration.ModelSlotInstanceConfigurationOption>();
		options.add(DefaultModelSlotInstanceConfigurationOption.SelectExistingModel);
		options.add(DefaultModelSlotInstanceConfigurationOption.CreatePrivateNewModel);
		options.add(DefaultModelSlotInstanceConfigurationOption.CreateSharedNewModel);
		if (!ms.getIsRequired()) {
			options.add(DefaultModelSlotInstanceConfigurationOption.LeaveEmpty);
		}
	}

	@Override
	public List<ModelSlotInstanceConfigurationOption> getAvailableOptions() {
		return options;
	}

}
