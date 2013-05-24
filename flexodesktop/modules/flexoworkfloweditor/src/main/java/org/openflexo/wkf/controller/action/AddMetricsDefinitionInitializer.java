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

import java.util.EventObject;
import java.util.logging.Logger;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.param.CheckboxParameter;
import org.openflexo.foundation.param.EnumDropDownParameter;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.TextAreaParameter;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.MetricsDefinition.MetricsType;
import org.openflexo.foundation.wkf.WorkflowModelObject;
import org.openflexo.foundation.wkf.action.AddMetricsDefinition;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public abstract class AddMetricsDefinitionInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	AddMetricsDefinitionInitializer(WKFControllerActionInitializer actionInitializer,
			FlexoActionType<? extends AddMetricsDefinition, FlexoWorkflow, WorkflowModelObject> actionType) {
		super(actionType, actionInitializer);
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() {
		return (WKFControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<AddMetricsDefinition> getDefaultInitializer() {
		return new FlexoActionInitializer<AddMetricsDefinition>() {
			@Override
			public boolean run(EventObject e, AddMetricsDefinition action) {
				if (action.getNewMetricsName() != null && action.getType() != null) {
					return true;
				}
				EnumDropDownParameter<MetricsType> metricsType = new EnumDropDownParameter<MetricsType>("type", "type", MetricsType.TEXT,
						MetricsType.values());
				TextFieldParameter unit = new TextFieldParameter("unit", "unit", null);
				unit.setDepends("type");
				unit.setConditional("type=NUMBER or type=DOUBLE");
				TextAreaParameter description = new TextAreaParameter("description", "description", null);
				CheckboxParameter alwaysDefined = new CheckboxParameter("alwaysDefined", "alwaysDefined", false);
				ParameterDefinition[] parameters = new ParameterDefinition[5];
				parameters[0] = new TextFieldParameter("newMetricsName", "name", "");
				parameters[1] = metricsType;
				parameters[2] = unit;
				parameters[3] = description;
				parameters[4] = alwaysDefined;
				AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
						FlexoLocalization.localizedForKey("create_new_metrics"),
						FlexoLocalization.localizedForKey("enter_parameters_for_the_new_metrics"), parameters);
				if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
					String newMetricsName = (String) dialog.parameterValueWithName("newMetricsName");
					if (newMetricsName == null) {
						return false;
					}
					action.setNewMetricsName(newMetricsName);
					action.setType(metricsType.getValue());
					action.setDescription(description.getValue());
					if (unit.getValue() != null && unit.getValue().trim().length() > 0) {
						action.setUnit(unit.getValue());
					}
					action.setAlwaysDefined(alwaysDefined.getValue());
					return true;
				} else {
					return false;
				}
			}
		};
	}

}
