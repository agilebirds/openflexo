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

import javax.swing.Icon;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.wkf.action.UngroupActivities;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class UngroupActivitiesInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	UngroupActivitiesInitializer(WKFControllerActionInitializer actionInitializer) {
		super(UngroupActivities.actionType, actionInitializer);
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() {
		return (WKFControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<UngroupActivities> getDefaultInitializer() {
		return new FlexoActionInitializer<UngroupActivities>() {
			@Override
			public boolean run(EventObject e, UngroupActivities action) {
				return FlexoController.confirm(FlexoLocalization.localizedForKey("would_you_like_to_ungroup_those_activities"));
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<UngroupActivities> getDefaultFinalizer() {
		return new FlexoActionFinalizer<UngroupActivities>() {
			@Override
			public boolean run(EventObject e, UngroupActivities action) {
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
