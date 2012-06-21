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
package org.openflexo.vpm.controller.action;

import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.ViewPointObject;
import org.openflexo.foundation.viewpoint.action.DuplicateEditionPattern;
import org.openflexo.icon.VPMIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.vpm.controller.VPMController;

public class DuplicateEditionPatternInitializer extends ActionInitializer<DuplicateEditionPattern, EditionPattern, ViewPointObject> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	DuplicateEditionPatternInitializer(CEDControllerActionInitializer actionInitializer) {
		super(DuplicateEditionPattern.actionType, actionInitializer);
	}

	@Override
	protected FlexoActionInitializer<DuplicateEditionPattern> getDefaultInitializer() {
		return new FlexoActionInitializer<DuplicateEditionPattern>() {
			@Override
			public boolean run(EventObject e, DuplicateEditionPattern action) {
				String s = FlexoController.askForString(FlexoLocalization.localizedForKey("please_provide_new_name"));
				if (s == null) {
					return false;
				}
				action.newName = s;
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<DuplicateEditionPattern> getDefaultFinalizer() {
		return new FlexoActionFinalizer<DuplicateEditionPattern>() {
			@Override
			public boolean run(EventObject e, DuplicateEditionPattern action) {
				((VPMController) getController()).selectAndFocusObject(action.newEditionPattern);
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return VPMIconLibrary.EDITION_PATTERN_ICON;
	}

}
