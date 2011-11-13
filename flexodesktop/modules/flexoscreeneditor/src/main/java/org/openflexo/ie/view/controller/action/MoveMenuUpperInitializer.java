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

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.ie.menu.FlexoItemMenu;
import org.openflexo.foundation.ie.menu.action.MoveMenuUpperLevel;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class MoveMenuUpperInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	MoveMenuUpperInitializer(IEControllerActionInitializer actionInitializer) {
		super(MoveMenuUpperLevel.actionType, actionInitializer);
	}

	@Override
	protected IEControllerActionInitializer getControllerActionInitializer() {
		return (IEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<MoveMenuUpperLevel> getDefaultInitializer() {
		return new FlexoActionInitializer<MoveMenuUpperLevel>() {
			@Override
			public boolean run(ActionEvent e, MoveMenuUpperLevel action) {
				boolean doable = false;
				if (action.getFocusedObject() instanceof FlexoItemMenu) {
					FlexoItemMenu item = (FlexoItemMenu) action.getFocusedObject();
					doable = item.getFather() != null && item.getFather().getFather() != null;
					if (doable)
						(action).setItemMenu(item);
				}
				return doable;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<MoveMenuUpperLevel> getDefaultFinalizer() {
		return new FlexoActionFinalizer<MoveMenuUpperLevel>() {
			@Override
			public boolean run(ActionEvent e, MoveMenuUpperLevel action) {
				return true;
			}
		};
	}

}
