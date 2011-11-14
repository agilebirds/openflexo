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
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.view.BrowserActionSource;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.wkf.DeadLine;
import org.openflexo.foundation.wkf.action.AddDeadline;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

@Deprecated
public class AddDeadlineInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	AddDeadlineInitializer(WKFControllerActionInitializer actionInitializer) {
		super(AddDeadline.actionType, actionInitializer);
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() {
		return (WKFControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<AddDeadline> getDefaultInitializer() {
		return new FlexoActionInitializer<AddDeadline>() {
			@Override
			public boolean run(ActionEvent e, AddDeadline action) {
				String newDeadlineName = FlexoController.askForString(FlexoLocalization.localizedForKey("enter_name_for_the_new_deadline"));
				if (newDeadlineName == null) {
					return false;
				}
				action.setNewDeadlineName(newDeadlineName);
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<AddDeadline> getDefaultFinalizer() {
		return new FlexoActionFinalizer<AddDeadline>() {
			@Override
			public boolean run(ActionEvent e, AddDeadline action) {
				DeadLine newDeadLine = action.getNewDeadline();
				if (e.getSource() instanceof BrowserActionSource) {
					ProjectBrowser browser = ((BrowserActionSource) e.getSource()).getBrowser();
					if (!browser.activateBrowsingFor(newDeadLine)) {
						if (FlexoController.confirm(FlexoLocalization.localizedForKey("would_you_like_to_desactivate_deadline_filtering"))) {
							browser.getFilterForObject(newDeadLine).setStatus(BrowserFilterStatus.SHOW);
							browser.update();
						}
					}
				}
				getControllerActionInitializer().getWKFController().getWorkflowBrowser().focusOn(newDeadLine);
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return null;
		// return WKFIconLibrary.DEADLINE_ICON;
	}

}
