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
package org.openflexo.view.controller.action;

import java.util.EventObject;

import javax.help.BadIDException;
import javax.swing.Icon;

import org.openflexo.action.HelpAction;
import org.openflexo.drm.DocItem;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.help.FlexoHelp;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class HelpActionizer extends ActionInitializer<HelpAction, FlexoObject, FlexoObject> {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(HelpActionizer.class.getPackage()
			.getName());

	public HelpActionizer(ControllerActionInitializer actionInitializer) {
		super(HelpAction.actionType, actionInitializer);
	}

	@Override
	protected FlexoActionInitializer<HelpAction> getDefaultInitializer() {
		return new FlexoActionInitializer<HelpAction>() {
			@Override
			public boolean run(EventObject e, HelpAction action) {
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<HelpAction> getDefaultFinalizer() {
		return new FlexoActionFinalizer<HelpAction>() {
			@Override
			public boolean run(EventObject e, HelpAction action) {
				DocItem item = getController().getApplicationContext().getDocResourceManager().getDocItemFor(action.getFocusedObject());
				if (item != null) {
					try {
						logger.info("Trying to display help for " + item.getIdentifier());
						FlexoHelp.getHelpBroker().setCurrentID(item.getIdentifier());
						FlexoHelp.getHelpBroker().setDisplayed(true);
					} catch (BadIDException exception) {
						FlexoController.showError(FlexoLocalization.localizedForKey("sorry_no_help_available_for") + " "
								+ item.getIdentifier());
						return false;
					}
					return true;
				}

				return false;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return IconLibrary.HELP_ICON;
	}
}
