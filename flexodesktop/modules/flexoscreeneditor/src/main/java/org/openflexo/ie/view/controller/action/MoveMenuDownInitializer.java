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
package org.openflexo.ie.view.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.ie.menu.FlexoItemMenu;
import org.openflexo.foundation.ie.menu.action.MoveMenuDown;
import org.openflexo.icon.IconLibrary;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class MoveMenuDownInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	MoveMenuDownInitializer(IEControllerActionInitializer actionInitializer) {
		super(MoveMenuDown.actionType, actionInitializer);
	}

	@Override
	protected IEControllerActionInitializer getControllerActionInitializer() {
		return (IEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<MoveMenuDown> getDefaultInitializer() {
		return new FlexoActionInitializer<MoveMenuDown>() {
			@Override
			public boolean run(ActionEvent e, MoveMenuDown action) {
				boolean doable = false;
				if (action.getFocusedObject() instanceof FlexoItemMenu) {
					FlexoItemMenu item = (FlexoItemMenu) action.getFocusedObject();
					if (item.getFather() != null) {
						if (item.getFather().getSubItems().indexOf(item) + 1 < item.getFather().getSubItems().size()) {
							doable = true;
						}
					}
					if (doable) {
						(action).setItemMenu(item);
					}
				}
				return (getModule().isActive()) && doable;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<MoveMenuDown> getDefaultFinalizer() {
		return new FlexoActionFinalizer<MoveMenuDown>() {
			@Override
			public boolean run(ActionEvent e, MoveMenuDown action) {
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return IconLibrary.MOVE_DOWN_ICON;
	}

}
