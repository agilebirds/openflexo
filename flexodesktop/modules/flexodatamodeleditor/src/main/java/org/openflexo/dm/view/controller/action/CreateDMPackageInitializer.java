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
package org.openflexo.dm.view.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

import org.openflexo.dm.view.DMRepositoryView;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.dm.DMRegExp;
import org.openflexo.foundation.dm.action.CreateDMPackage;

public class CreateDMPackageInitializer extends ActionInitializer {

	static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	CreateDMPackageInitializer(DMControllerActionInitializer actionInitializer) {
		super(CreateDMPackage.actionType, actionInitializer);
	}

	@Override
	protected DMControllerActionInitializer getControllerActionInitializer() {
		return (DMControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<CreateDMPackage> getDefaultInitializer() {
		return new FlexoActionInitializer<CreateDMPackage>() {
			@Override
			public boolean run(ActionEvent e, CreateDMPackage action) {
				String newPackageName = FlexoController.askForStringMatchingPattern(
						FlexoLocalization.localizedForKey("please_enter_a_package_name"), DMRegExp.PACKAGE_NAME_PATTERN,
						FlexoLocalization.localizedForKey("package_must_start_with_a_letter_followed_by_any_letter_or_number"));
				if (newPackageName != null) {
					action.setNewPackageName(newPackageName);
					return true;
				}
				return false;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<CreateDMPackage> getDefaultFinalizer() {
		return new FlexoActionFinalizer<CreateDMPackage>() {
			@Override
			public boolean run(ActionEvent e, CreateDMPackage action) {
				if (getControllerActionInitializer().getDMController().getCurrentEditedObject() == action.getRepository()) {
					if (logger.isLoggable(Level.FINE))
						logger.fine("Finalizer for CreateDMPackage in DMRepositoryView");
					DMRepositoryView repView = (DMRepositoryView) getControllerActionInitializer().getDMController()
							.getCurrentEditedObjectView();
					repView.getPackageTable().selectObject(action.getNewPackage());
				}
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return DMEIconLibrary.DM_PACKAGE_ICON;
	}

}
