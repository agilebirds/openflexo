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
package org.openflexo.dgmodule.controller.action;

import java.awt.event.KeyEvent;
import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.openflexo.FlexoCst;
import org.openflexo.dgmodule.DGPreferences;
import org.openflexo.dgmodule.controller.DGController;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.generator.action.DismissUnchangedGeneratedFiles;
import org.openflexo.generator.action.GenerateSourceCode;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.icon.GeneratorIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class GenerateSourceCodeInitializer extends ActionInitializer<GenerateSourceCode, CGObject, CGObject> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	GenerateSourceCodeInitializer(DGControllerActionInitializer actionInitializer) {
		super(GenerateSourceCode.actionType, actionInitializer);
	}

	@Override
	protected DGControllerActionInitializer getControllerActionInitializer() {
		return (DGControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	public DGController getController() {
		return (DGController) super.getController();
	}

	@Override
	protected FlexoActionInitializer<GenerateSourceCode> getDefaultInitializer() {
		return new FlexoActionInitializer<GenerateSourceCode>() {
			@Override
			public boolean run(EventObject e, GenerateSourceCode action) {
				if (action.getRepository().getDirectory() == null) {
					FlexoController.notify(FlexoLocalization.localizedForKey("please_supply_valid_directory"));
					return false;
				}
				getController().getBrowser().setHoldStructure();
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<GenerateSourceCode> getDefaultFinalizer() {
		return new FlexoActionFinalizer<GenerateSourceCode>() {
			@Override
			public boolean run(EventObject e, GenerateSourceCode action) {
				getControllerActionInitializer().getDGController().disposeProgressWindow();
				action.setSaveBeforeGenerating(DGPreferences.getSaveBeforeGenerating());
				if (DGPreferences.getAutomaticallyDismissUnchangedFiles()) {
					DismissUnchangedGeneratedFiles.actionType.makeNewAction(action.getFocusedObject(), action.getGlobalSelection(),
							action.getEditor()).doAction();
				}
				getController().getBrowser().resetHoldStructure();
				getController().getBrowser().update();
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<GenerateSourceCode> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<GenerateSourceCode>() {
			@Override
			public boolean handleException(FlexoException exception, GenerateSourceCode action) {
				getControllerActionInitializer().getDGController().disposeProgressWindow();
				getController().getBrowser().resetHoldStructure();
				getController().getBrowser().update();
				if (exception instanceof GenerationException) {
					FlexoController.showError(FlexoLocalization.localizedForKey("generation_failed") + ":\n"
							+ ((GenerationException) exception).getLocalizedMessage());
					return true;
				}
				exception.printStackTrace();
				return false;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return GeneratorIconLibrary.GENERATE_CODE_ICON;
	}

	@Override
	protected Icon getDisabledIcon() {
		return GeneratorIconLibrary.GENERATE_CODE_DISABLED_ICON;
	}

	@Override
	protected KeyStroke getShortcut() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_G, FlexoCst.META_MASK);
	}
}
