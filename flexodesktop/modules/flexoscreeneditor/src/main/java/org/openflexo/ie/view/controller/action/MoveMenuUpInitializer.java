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
import org.openflexo.foundation.ie.menu.action.MoveMenuUp;
import org.openflexo.icon.IconLibrary;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class MoveMenuUpInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	MoveMenuUpInitializer(IEControllerActionInitializer actionInitializer) {
		super(MoveMenuUp.actionType, actionInitializer);
	}

	@Override
	protected IEControllerActionInitializer getControllerActionInitializer() {
		return (IEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<MoveMenuUp> getDefaultInitializer() {
		return new FlexoActionInitializer<MoveMenuUp>() {
			@Override
			public boolean run(ActionEvent e, MoveMenuUp action) {
				boolean doable = false;
				if (action.getFocusedObject() instanceof FlexoItemMenu) {
					FlexoItemMenu item = (FlexoItemMenu) action.getFocusedObject();
					if (item.getFather() != null) {
						if (item.getFather().getSubItems().indexOf(item) > 0) {
							doable = true;
						}
					}
					if (doable) {
						(action).setItemMenu(item);
					}
				}
				return doable;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<MoveMenuUp> getDefaultFinalizer() {
		return new FlexoActionFinalizer<MoveMenuUp>() {
			@Override
			public boolean run(ActionEvent e, MoveMenuUp action) {
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return IconLibrary.MOVE_UP_ICON;
	}

}
