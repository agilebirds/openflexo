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
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.wkf.MetricsValue;
import org.openflexo.foundation.wkf.action.DeleteMetricsValue;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class DeleteMetricsInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	DeleteMetricsInitializer(WKFControllerActionInitializer actionInitializer) {
		super(DeleteMetricsValue.actionType, actionInitializer);
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() {
		return (WKFControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<DeleteMetricsValue> getDefaultInitializer() {
		return new FlexoActionInitializer<DeleteMetricsValue>() {
			@Override
			public boolean run(EventObject e, DeleteMetricsValue action) {
				boolean doIt;
				Vector<MetricsValue> metricsValueToDelete = action.getMetricsValueToDelete();
				if (metricsValueToDelete.size() == 0) {
					return false;
				}
				if (metricsValueToDelete.size() == 1) {
					doIt = FlexoController.confirm(FlexoLocalization.localizedForKey("would_you_like_to_delete_this_metrics"));
				} else {
					doIt = FlexoController.confirm(FlexoLocalization.localizedForKey("would_you_like_to_delete_these_metrics"));
				}
				return doIt;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<DeleteMetricsValue> getDefaultFinalizer() {
		return new FlexoActionFinalizer<DeleteMetricsValue>() {
			@Override
			public boolean run(EventObject e, DeleteMetricsValue action) {
				if (getControllerActionInitializer().getWKFController().getSelectionManager().getLastSelectedObject() != null
						&& getControllerActionInitializer().getWKFController().getSelectionManager().getLastSelectedObject().isDeleted()) {
					getControllerActionInitializer().getWKFController().getSelectionManager().resetSelection();
				}
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return IconLibrary.DELETE_ICON;
	}

}
