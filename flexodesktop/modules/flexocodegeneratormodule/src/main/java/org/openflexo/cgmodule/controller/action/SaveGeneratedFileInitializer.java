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
package org.openflexo.cgmodule.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.generator.action.SaveGeneratedFile;
import org.openflexo.icon.IconLibrary;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class SaveGeneratedFileInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	SaveGeneratedFileInitializer(GeneratorControllerActionInitializer actionInitializer) {
		super(SaveGeneratedFile.actionType, actionInitializer);
	}

	@Override
	protected GeneratorControllerActionInitializer getControllerActionInitializer() {
		return (GeneratorControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<SaveGeneratedFile> getDefaultInitializer() {
		return new FlexoActionInitializer<SaveGeneratedFile>() {
			@Override
			public boolean run(ActionEvent e, SaveGeneratedFile action) {
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<SaveGeneratedFile> getDefaultFinalizer() {
		return new FlexoActionFinalizer<SaveGeneratedFile>() {
			@Override
			public boolean run(ActionEvent e, SaveGeneratedFile action) {
				getControllerActionInitializer().getGeneratorController().selectAndFocusObject(action.getFocusedObject());
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return IconLibrary.SAVE_ICON;
	}

	@Override
	protected Icon getDisabledIcon() {
		return IconLibrary.SAVE_DISABLED_ICON;
	}

}
