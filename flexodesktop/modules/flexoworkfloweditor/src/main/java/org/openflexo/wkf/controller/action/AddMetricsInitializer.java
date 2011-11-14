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

import java.awt.event.ActionEvent;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.param.DynamicDropDownParameter;
import org.openflexo.foundation.param.EnumDropDownParameter;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.RadioButtonListParameter;
import org.openflexo.foundation.param.TextAreaParameter;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.MetricsDefinition;
import org.openflexo.foundation.wkf.MetricsDefinition.MetricsType;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.WorkflowModelObject;
import org.openflexo.foundation.wkf.action.AddMetricsDefinition;
import org.openflexo.foundation.wkf.action.AddMetricsValue;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public abstract class AddMetricsInitializer<A extends AddMetricsValue<A, T>, T extends WKFObject> extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	AddMetricsInitializer(WKFControllerActionInitializer actionInitializer, FlexoActionType<A, T, WKFObject> actionType) {
		super(actionType, actionInitializer);
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() {
		return (WKFControllerActionInitializer) super.getControllerActionInitializer();
	}

	protected abstract Vector<MetricsDefinition> getAvailableMetricsDefinitions(T object);

	protected abstract FlexoActionType<AddMetricsDefinition, FlexoWorkflow, WorkflowModelObject> getAddMetricsDefinitionActionType();

	protected MetricsDefinition createMetricsDefinitionWithName(String name, MetricsType type, String description) {
		AddMetricsDefinition add = getAddMetricsDefinitionActionType().makeNewAction(getProject().getWorkflow(), null, getEditor());
		add.setNewMetricsName(name);
		add.setType(type);
		add.setDescription(description);
		add.doAction();
		return add.getNewMetricsDefinition();
	}

	@Override
	protected FlexoActionInitializer<A> getDefaultInitializer() {
		return new FlexoActionInitializer<A>() {
			@Override
			public boolean run(ActionEvent e, A action) {
				ParameterDefinition[] params;
				TextFieldParameter metricsName = new TextFieldParameter("name", "name", null);
				EnumDropDownParameter<MetricsType> type = new EnumDropDownParameter<MetricsType>("type", "type", MetricsType.TEXT,
						MetricsType.values());
				TextAreaParameter description = new TextAreaParameter("description", "description", null);
				String NEW_METRICS = FlexoLocalization.localizedForKey("new_metrics");
				String SELECT_EXISTING = FlexoLocalization.localizedForKey("select_existing");
				String[] contexts = { SELECT_EXISTING, NEW_METRICS };
				DynamicDropDownParameter<MetricsDefinition> metricsDefinition = null;
				RadioButtonListParameter choice = null;
				Vector<MetricsDefinition> availableMetricsDefinitions = getAvailableMetricsDefinitions(action.getFocusedObject());
				if (availableMetricsDefinitions.size() > 0) {
					params = new ParameterDefinition[5];
					choice = new RadioButtonListParameter("choice", "choice", SELECT_EXISTING, contexts);
					params[0] = choice;
					metricsDefinition = new DynamicDropDownParameter<MetricsDefinition>("definition", "select_a_metrics",
							availableMetricsDefinitions, availableMetricsDefinitions.firstElement());
					metricsDefinition.setShowReset(false);
					metricsDefinition.setFormatter("name");
					params[1] = metricsDefinition;
					params[1].setDepends("choice");
					params[1].setConditional("choice=\"" + SELECT_EXISTING + "\"");
					params[2] = metricsName;
					params[2].setDepends("choice");
					params[2].setConditional("choice=\"" + NEW_METRICS + "\"");
					params[3] = type;
					params[3].setDepends("choice");
					params[3].setConditional("choice=\"" + NEW_METRICS + "\"");
					params[4] = description;
					params[4].setDepends("choice");
					params[4].setConditional("choice=\"" + NEW_METRICS + "\"");
				} else {
					params = new ParameterDefinition[3];
					params[0] = metricsName;
					params[1] = type;
					params[2] = description;
				}
				AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
						FlexoLocalization.localizedForKey("add_metrics"),
						FlexoLocalization.localizedForKey("enter_parameters_for_metrics"), params);
				if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
					boolean createNew = choice == null || choice.getValue().equals(NEW_METRICS);
					MetricsDefinition md = null;
					if (createNew) {
						if (metricsName.getValue() == null || metricsName.getValue().trim().length() == 0) {
							FlexoController.notify(FlexoLocalization.localizedForKey("name_cannot_be_empty"));
							return false;
						}
						md = createMetricsDefinitionWithName(metricsName.getValue(), type.getValue(), description.getValue());
					} else {
						md = metricsDefinition.getValue();
					}
					if (md != null) {
						action.setMetricsDefinition(md);
						return true;
					}
					return false;
				} else {
					return false;
				}
			}
		};
	}

}
