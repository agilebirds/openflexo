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

import org.openflexo.icon.SEIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.dkv.DKVModel;
import org.openflexo.foundation.dkv.DuplicateDKVObjectException;
import org.openflexo.foundation.dkv.EmptyStringException;
import org.openflexo.foundation.dkv.action.AddLanguageAction;

public class AddLanguageInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	AddLanguageInitializer(IEControllerActionInitializer actionInitializer) {
		super(AddLanguageAction.actionType, actionInitializer);
	}

	@Override
	protected IEControllerActionInitializer getControllerActionInitializer() {
		return (IEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<AddLanguageAction> getDefaultInitializer() {
		return new FlexoActionInitializer<AddLanguageAction>() {
			@Override
			public boolean run(ActionEvent e, AddLanguageAction action) {
				DKVModel model = (action.getFocusedObject()).getDkvModel();
				boolean ok = false;
				while (!ok) {
					String name = FlexoController.askForString(FlexoLocalization.localizedForKey("enter_the_name_of_the_new_language"));
					if (name == null)
						return false;
					if (name.trim().length() == 0) {
						FlexoController.notify(FlexoLocalization.localizedForKey("name_of_language_cannot_be_empty"));
						continue;
					}
					try {
						if (model.isLanguageNameLegal(name)) {
							(action).setDkvModel(model);
							(action).setLanguageName(name);
						}
						ok = true;
					} catch (DuplicateDKVObjectException e2) {
						FlexoController.notify(FlexoLocalization.localizedForKey("name_of_language_already_exists"));
					} catch (EmptyStringException e2) {
					}
				}
				return ok;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<AddLanguageAction> getDefaultFinalizer() {
		return new FlexoActionFinalizer<AddLanguageAction>() {
			@Override
			public boolean run(ActionEvent e, AddLanguageAction action) {
				getControllerActionInitializer().getIEController().getIESelectionManager().setSelectedObject((action).getNewLanguage());
				// getController().setCurrentEditedObjectAsModuleView(((AddLanguageAction)
				// action).getNewLanguage());
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return SEIconLibrary.LANGUAGE_ICON;
	}

}
