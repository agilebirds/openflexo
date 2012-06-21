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
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.foundation.wkf.action.MakeActivityGroup;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class MakeActivityGroupInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	MakeActivityGroupInitializer(WKFControllerActionInitializer actionInitializer) {
		super(MakeActivityGroup.actionType, actionInitializer);
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() {
		return (WKFControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<MakeActivityGroup> getDefaultInitializer() {
		return new FlexoActionInitializer<MakeActivityGroup>() {
			@Override
			public boolean run(EventObject e, MakeActivityGroup action) {
				action.setNewGroupName(FlexoLocalization.localizedForKey("activity_group"));
				TextFieldParameter newGroupNameParam = new TextFieldParameter("newGroupNameParam", "group_name", action.getNewGroupName());
				AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null, action.getLocalizedName(),
						FlexoLocalization.localizedForKey("please_enter_name_for_newly_created_activity_group"), newGroupNameParam);
				if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
					action.setNewGroupName(newGroupNameParam.getValue());
					return true;
				} else {
					return false;
				}
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<MakeActivityGroup> getDefaultFinalizer() {
		return new FlexoActionFinalizer<MakeActivityGroup>() {
			@Override
			public boolean run(EventObject e, MakeActivityGroup action) {
				if (getControllerActionInitializer().getWKFController().getSelectionManager() != null) {
					getControllerActionInitializer().getWKFController().getSelectionManager()
							.setSelectedObject(action.getNewActivityGroup());
				}
				return true;
			}
		};
	}

}
