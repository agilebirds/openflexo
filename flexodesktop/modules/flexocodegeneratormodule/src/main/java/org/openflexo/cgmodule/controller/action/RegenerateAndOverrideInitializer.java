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

import org.openflexo.cgmodule.GeneratorPreferences;
import org.openflexo.cgmodule.view.GeneratorMainPane;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.generator.action.RegenerateAndOverride;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class RegenerateAndOverrideInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	RegenerateAndOverrideInitializer(GeneratorControllerActionInitializer actionInitializer) {
		super(RegenerateAndOverride.actionType, actionInitializer);
	}

	@Override
	protected GeneratorControllerActionInitializer getControllerActionInitializer() {
		return (GeneratorControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<RegenerateAndOverride> getDefaultInitializer() {
		return new FlexoActionInitializer<RegenerateAndOverride>() {
			@Override
			public boolean run(ActionEvent e, RegenerateAndOverride action) {
				if (action.getRepository().getDirectory() == null) {
					FlexoController.notify(FlexoLocalization.localizedForKey("please_supply_valid_directory"));
					return false;
				}
				action.setSaveBeforeGenerating(GeneratorPreferences.getSaveBeforeGenerating());
				action.getProjectGenerator().startHandleLogs();
				((GeneratorMainPane) getController().getMainPane()).getBrowserView().getBrowser().setHoldStructure();
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<RegenerateAndOverride> getDefaultFinalizer() {
		return new FlexoActionFinalizer<RegenerateAndOverride>() {
			@Override
			public boolean run(ActionEvent e, RegenerateAndOverride action) {
				action.getProjectGenerator().stopHandleLogs();
				action.getProjectGenerator().flushLogs();
				((GeneratorMainPane) getController().getMainPane()).getBrowserView().getBrowser().resetHoldStructure();
				((GeneratorMainPane) getController().getMainPane()).getBrowserView().getBrowser().update();
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<RegenerateAndOverride> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<RegenerateAndOverride>() {
			@Override
			public boolean handleException(FlexoException exception, RegenerateAndOverride action) {
				((GeneratorMainPane) getController().getMainPane()).getBrowserView().getBrowser().resetHoldStructure();
				((GeneratorMainPane) getController().getMainPane()).getBrowserView().getBrowser().update();
				getControllerActionInitializer().getGeneratorController().disposeProgressWindow();
				exception.printStackTrace();
				FlexoController.showError(FlexoLocalization.localizedForKey("code_generation_synchronization_for_repository_failed")
						+ ":\n" + exception.getLocalizedMessage());
				return true;
			}
		};
	}

}
