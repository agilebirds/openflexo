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
package org.openflexo.wkf.controller.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.MetricsDefinition;
import org.openflexo.foundation.wkf.MetricsValue;
import org.openflexo.foundation.wkf.WorkflowModelObject;
import org.openflexo.foundation.wkf.action.AddActivityMetricsDefinition;
import org.openflexo.foundation.wkf.action.AddActivityMetricsValue;
import org.openflexo.foundation.wkf.action.AddMetricsDefinition;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.view.controller.ControllerActionInitializer;


public class AddActivityMetricsInitializer extends AddMetricsInitializer<AddActivityMetricsValue,AbstractActivityNode> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	AddActivityMetricsInitializer(WKFControllerActionInitializer actionInitializer)
	{
		super(actionInitializer,AddActivityMetricsValue.actionType);
	}

	@Override
	protected FlexoActionType<AddMetricsDefinition, FlexoWorkflow, WorkflowModelObject> getAddMetricsDefinitionActionType() {
		return AddActivityMetricsDefinition.actionType;
	}

	@Override
	protected Vector<MetricsDefinition> getAvailableMetricsDefinitions(AbstractActivityNode activity) {
		Vector<MetricsDefinition> v = new Vector<MetricsDefinition>(getProject().getWorkflow().getActivityMetricsDefinitions());
		for (MetricsValue mv: activity.getMetricsValues()) {
			v.remove(mv.getMetricsDefinition());
		}
		return v;
	}
	
}
