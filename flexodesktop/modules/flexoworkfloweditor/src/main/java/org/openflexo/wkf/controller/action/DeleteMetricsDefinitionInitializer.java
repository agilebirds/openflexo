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

import javax.swing.Icon;

import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.wkf.MetricsDefinition;
import org.openflexo.foundation.wkf.action.DeleteMetricsDefinition;

public class DeleteMetricsDefinitionInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	DeleteMetricsDefinitionInitializer(WKFControllerActionInitializer actionInitializer) {
		super(DeleteMetricsDefinition.actionType, actionInitializer);
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() {
		return (WKFControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<DeleteMetricsDefinition> getDefaultInitializer() {
		return new FlexoActionInitializer<DeleteMetricsDefinition>() {
			@Override
			public boolean run(ActionEvent e, DeleteMetricsDefinition action) {
				boolean doIt;
				Vector<MetricsDefinition> metricsDefinitionToDelete = action.getMetricsDefinitionToDelete();
				if (metricsDefinitionToDelete.size() == 0)
					return false;
				/* TODO: tell if the metric definition is used somewhere using referencers-->referenceOwner-->getDisplayableName()
				Vector<FlexoModelObjectReference.ReferenceOwner> nodes = new Vector<ReferenceOwner>();
				for (MetricsDefinition r : metricsDefinitionToDelete) {
					
					nodes.addAll(r.getReferencers());
				}
				if (nodes.size()>0) {
					StringBuilder sb = new StringBuilder();
					for (AbstractActivityNode node : nodes) {
						sb.append("* ").append(node.getName()).append("\n");
					}
					if (roleToDelete.size() == 1)
						doIt = FlexoController.confirmWithWarning(FlexoLocalization
								.localizedForKey("would_you_like_to_delete_this_role")+"\n"+FlexoLocalization.localizedForKey("it_is_used_in:")+"\n"+sb.toString());
					else doIt = FlexoController.confirmWithWarning(FlexoLocalization
							.localizedForKey("would_you_like_to_delete_these_roles")+"\n"+FlexoLocalization.localizedForKey("they_are_used_in:")+"\n"+sb.toString());
				} else {
					if (roleToDelete.size() == 1)
						doIt = FlexoController.confirm(FlexoLocalization.localizedForKey("would_you_like_to_delete_this_metrics_definition"));
					else 
						doIt = FlexoController.confirm(FlexoLocalization.localizedForKey("would_you_like_to_delete_these_metrics_definitions"));

				} */
				doIt = FlexoController.confirm(FlexoLocalization.localizedForKey("would_you_like_to_delete_this_metrics_definition"));
				return doIt;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<DeleteMetricsDefinition> getDefaultFinalizer() {
		return new FlexoActionFinalizer<DeleteMetricsDefinition>() {
			@Override
			public boolean run(ActionEvent e, DeleteMetricsDefinition action) {
				if (getControllerActionInitializer().getWKFController().getSelectionManager().getLastSelectedObject() != null
						&& getControllerActionInitializer().getWKFController().getSelectionManager().getLastSelectedObject().isDeleted())
					getControllerActionInitializer().getWKFController().getSelectionManager().resetSelection();
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return IconLibrary.DELETE_ICON;
	}

}
