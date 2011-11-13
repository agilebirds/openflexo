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

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoActionVisibleCondition;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.action.HideRole;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.wkf.controller.WKFController;
import org.openflexo.wkf.swleditor.SwimmingLaneRepresentation;

public class HideRoleInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	HideRoleInitializer(WKFControllerActionInitializer actionInitializer) {
		super(HideRole.actionType, actionInitializer);
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() {
		return (WKFControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	public WKFController getController() {
		return (WKFController) super.getController();
	}

	@Override
	protected FlexoActionInitializer getDefaultInitializer() {
		return new FlexoActionInitializer<HideRole>() {

			@Override
			public boolean run(ActionEvent event, HideRole action) {
				if (action.getRole() != null) {
					action.getRole().setIsVisible(false,
							SwimmingLaneRepresentation.getRoleVisibilityContextForProcess(getController().getCurrentFlexoProcess()));
					if (action.getGlobalSelection() != null) {
						for (Role role : action.getGlobalSelection())
							if (!SwimmingLaneRepresentation.roleMustBeShown(role, getController().getCurrentFlexoProcess()))
								role.setIsVisible(false, SwimmingLaneRepresentation.getRoleVisibilityContextForProcess(getController()
										.getCurrentFlexoProcess()));
					}
				}
				return false;
			}

		};
	}

	@Override
	protected FlexoActionVisibleCondition<HideRole, Role, Role> getVisibleCondition() {
		return new FlexoActionVisibleCondition<HideRole, Role, Role>() {

			@Override
			public boolean isVisible(FlexoActionType<HideRole, Role, Role> actionType, Role object, Vector<Role> globalSelection,
					FlexoEditor editor) {
				return getController().getCurrentPerspective() == getController().SWIMMING_LANE_PERSPECTIVE
						&& !SwimmingLaneRepresentation.roleMustBeShown(object, getController().getCurrentFlexoProcess());
			}

		};
	}

	@Override
	protected FlexoActionFinalizer<HideRole> getDefaultFinalizer() {
		return new FlexoActionFinalizer<HideRole>() {
			@Override
			public boolean run(ActionEvent e, HideRole action) {
				return true;
			}
		};
	}

}
