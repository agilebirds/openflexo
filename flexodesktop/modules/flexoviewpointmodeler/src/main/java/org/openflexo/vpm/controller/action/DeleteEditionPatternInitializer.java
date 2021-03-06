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
import javax.swing.JOptionPane;

import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.ViewPointObject;
import org.openflexo.foundation.viewpoint.action.DeleteEditionPattern;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class DeleteEditionPatternInitializer extends ActionInitializer<DeleteEditionPattern, EditionPattern, ViewPointObject> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	DeleteEditionPatternInitializer(CEDControllerActionInitializer actionInitializer) {
		super(DeleteEditionPattern.actionType, actionInitializer);
	}

	@Override
	protected CEDControllerActionInitializer getControllerActionInitializer() {
		return (CEDControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<DeleteEditionPattern> getDefaultInitializer() {
		return new FlexoActionInitializer<DeleteEditionPattern>() {
			@Override
			public boolean run(EventObject e, DeleteEditionPattern action) {
				if (FlexoController.confirm(FlexoLocalization.localizedForKey("would_you_really_like_to_delete_this_edition_pattern"))) {
					if (action.getFocusedObject().getChildEditionPatterns().size() > 0) {
						int choice = FlexoController.confirmYesNoCancel(FlexoLocalization
								.localizedForKey("would_you_like_to_delete_its_children_too"));
						switch (choice) {
						case JOptionPane.YES_OPTION:
							action.setDeleteChildren(true);
							break;
						case JOptionPane.NO_OPTION:
							action.setDeleteChildren(false);
							break;
						default:
							return false;
						}
						return true;
					} else {
						return true;
					}
				}
				return false;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return IconLibrary.DELETE_ICON;
	}

}
