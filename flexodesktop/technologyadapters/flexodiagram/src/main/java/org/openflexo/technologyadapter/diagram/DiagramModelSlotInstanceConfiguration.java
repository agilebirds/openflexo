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
package org.openflexo.technologyadapter.diagram;

import java.util.ArrayList;
import java.util.List;

import org.openflexo.foundation.view.ModelSlotInstance;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.view.VirtualModelModelSlotInstance;
import org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration;
import org.openflexo.foundation.viewpoint.VirtualModelModelSlotInstanceConfiguration;
import org.openflexo.technologyadapter.diagram.model.Diagram;
import org.openflexo.technologyadapter.diagram.model.DiagramSpecification;
import org.openflexo.technologyadapter.diagram.model.action.CreateDiagram;

/**
 * This class is used to stored the configuration of a {@link DiagramModelSlot} which has to be instantiated
 * 
 * 
 * @author sylvain
 * 
 */
public class DiagramModelSlotInstanceConfiguration extends
		VirtualModelModelSlotInstanceConfiguration<DiagramModelSlot, Diagram, DiagramSpecification> {

	private List<ModelSlotInstanceConfigurationOption> options;

	protected DiagramModelSlotInstanceConfiguration(DiagramModelSlot ms, CreateDiagram action) {
		super(ms, action);
		options = new ArrayList<ModelSlotInstanceConfiguration.ModelSlotInstanceConfigurationOption>();
		if (ms.isReflexiveModelSlot()) {
			options.add(DefaultModelSlotInstanceConfigurationOption.Autoconfigure);
		} else {
			options.add(DefaultModelSlotInstanceConfigurationOption.Autoconfigure);
			if (!ms.getIsRequired()) {
				options.add(DefaultModelSlotInstanceConfigurationOption.LeaveEmpty);
			}
		}
	}

	@Override
	public List<ModelSlotInstanceConfigurationOption> getAvailableOptions() {
		return options;
	}

	@Override
	public boolean isValidConfiguration() {
		return true;
		// return (getOption() == DefaultModelSlotInstanceConfigurationOption.Autoconfigure);
	}

	@Override
	public ModelSlotInstance<DiagramModelSlot, Diagram> createModelSlotInstance(VirtualModelInstance<?, ?> vmInstance) {
		VirtualModelModelSlotInstance returned = new VirtualModelModelSlotInstance<Diagram, DiagramSpecification>(vmInstance,
				getModelSlot());
		// No need to store a model, this is a built-in scheme
		return returned;
	}

}
