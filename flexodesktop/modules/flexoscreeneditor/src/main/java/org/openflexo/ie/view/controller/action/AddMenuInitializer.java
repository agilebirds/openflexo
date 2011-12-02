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
import java.util.regex.Pattern;

import javax.swing.Icon;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.ie.menu.FlexoItemMenu;
import org.openflexo.foundation.ie.menu.action.AddMenu;
import org.openflexo.icon.SEIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class AddMenuInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	AddMenuInitializer(IEControllerActionInitializer actionInitializer) {
		super(AddMenu.actionType, actionInitializer);
	}

	@Override
	protected IEControllerActionInitializer getControllerActionInitializer() {
		return (IEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<AddMenu> getDefaultInitializer() {
		return new FlexoActionInitializer<AddMenu>() {
			@Override
			public boolean run(ActionEvent e, AddMenu action) {
				String menuLabel = FlexoController.askForStringMatchingPattern(
						FlexoLocalization.localizedForKey("enter_label_for_the_new_menu"), Pattern.compile("\\S.*"),
						FlexoLocalization.localizedForKey("cannot_be_empty"));
				if (menuLabel == null || menuLabel.trim().length() == 0) {
					return false;
				}
				if (((FlexoItemMenu) action.getFocusedObject()).getNavigationMenu().getMenuLabeled(menuLabel) != null) {
					FlexoController.notify(FlexoLocalization.localizedForKey("a_menu_with_such_label_already_exists"));
				}
				(action).setMenuLabel(menuLabel);
				(action).setFather((FlexoItemMenu) action.getFocusedObject());
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<AddMenu> getDefaultFinalizer() {
		return new FlexoActionFinalizer<AddMenu>() {
			@Override
			public boolean run(ActionEvent e, AddMenu action) {
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return SEIconLibrary.MENUITEM_ICON;
	}

}
