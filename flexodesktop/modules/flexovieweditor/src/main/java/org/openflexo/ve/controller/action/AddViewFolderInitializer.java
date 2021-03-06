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
package org.openflexo.ve.controller.action;

import java.awt.Component;
import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JTree;

import org.openflexo.components.browser.view.BrowserView;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.view.ViewDefinition;
import org.openflexo.foundation.view.ViewFolder;
import org.openflexo.foundation.view.ViewLibrary;
import org.openflexo.foundation.view.action.AddViewFolder;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class AddViewFolderInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	AddViewFolderInitializer(VEControllerActionInitializer actionInitializer) {
		super(AddViewFolder.actionType, actionInitializer);
	}

	@Override
	protected VEControllerActionInitializer getControllerActionInitializer() {
		return (VEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<AddViewFolder> getDefaultInitializer() {
		return new FlexoActionInitializer<AddViewFolder>() {
			@Override
			public boolean run(EventObject e, AddViewFolder action) {
				ViewFolder parentFolder = null;
				if (action.getFocusedObject() != null && action.getFocusedObject() instanceof ViewDefinition) {
					parentFolder = ((ViewDefinition) action.getFocusedObject()).getFolder();
				} else if (action.getFocusedObject() != null && action.getFocusedObject() instanceof ViewFolder) {
					parentFolder = (ViewFolder) action.getFocusedObject();
				} else if (action.getFocusedObject() != null && action.getFocusedObject() instanceof ViewLibrary) {
					parentFolder = ((ViewLibrary) action.getFocusedObject()).getRootFolder();
				}
				if (parentFolder != null) {
					String newFolderName = null;
					while (newFolderName == null) {
						/*newFolderName = FlexoController.askForStringMatchingPattern(
								FlexoLocalization.localizedForKey("enter_name_for_the_new_folder"),
								Pattern.compile(FileUtils.GOOD_CHARACTERS_REG_EXP + "+"),
								FlexoLocalization.localizedForKey("folder_name_cannot_contain_:_\\_\"_:_*_?_<_>_/"));*/
						newFolderName = FlexoController.askForString(FlexoLocalization.localizedForKey("enter_name_for_the_new_folder"));
						if (newFolderName == null) {
							return false;
						}
						if (newFolderName.trim().length() == 0) {
							FlexoController.showError(FlexoLocalization.localizedForKey("a_folder_name_cannot_be_empty"));
							return false;
						}
						if (parentFolder.getFolderNamed(newFolderName) != null) {
							FlexoController.notify(FlexoLocalization.localizedForKey("there_is_already_a_folder_with that name"));
							newFolderName = null;
						}
					}
					action.setParentFolder(parentFolder);
					action.setNewFolderName(newFolderName);
					return true;
				} else {
					return false;
				}
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<AddViewFolder> getDefaultFinalizer() {
		return new FlexoActionFinalizer<AddViewFolder>() {
			@Override
			public boolean run(EventObject e, AddViewFolder action) {
				// Update ProjectBrowser (normally it should be done with a
				// notification)
				// TODO: do it properly with a notification
				if (action.getInvoker() instanceof JTree) {
					Component current = (JTree) action.getInvoker();
					while (current != null) {
						if (current instanceof BrowserView) {
							((BrowserView) current).getBrowser().update();
							return true;
						}
						current = current.getParent();
					}
				}
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return IconLibrary.FOLDER_ICON;
	}

}
