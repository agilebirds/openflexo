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

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.openflexo.FlexoCst;
import org.openflexo.cgmodule.GeneratorPreferences;
import org.openflexo.cgmodule.controller.GeneratorController;
import org.openflexo.cgmodule.view.GeneratorBrowserView;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.generator.action.DismissUnchangedGeneratedFiles;
import org.openflexo.generator.action.ForceRegenerateSourceCode;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.icon.GeneratorIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class ForceRegenerateSourceCodeInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	ForceRegenerateSourceCodeInitializer(GeneratorControllerActionInitializer actionInitializer) {
		super(ForceRegenerateSourceCode.actionType, actionInitializer);
	}

	@Override
	protected GeneratorControllerActionInitializer getControllerActionInitializer() {
		return (GeneratorControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<ForceRegenerateSourceCode> getDefaultInitializer() {
		return new FlexoActionInitializer<ForceRegenerateSourceCode>() {
			@Override
			public boolean run(EventObject e, ForceRegenerateSourceCode action) {
				if (action.getRepository().getDirectory() == null) {
					FlexoController.notify(FlexoLocalization.localizedForKey("please_supply_valid_directory"));
					return false;
				}
				action.setSaveBeforeGenerating(GeneratorPreferences.getSaveBeforeGenerating());
				action.getProjectGenerator().startHandleLogs();
				getBrowserView().getBrowser().setHoldStructure();
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<ForceRegenerateSourceCode> getDefaultFinalizer() {
		return new FlexoActionFinalizer<ForceRegenerateSourceCode>() {
			@Override
			public boolean run(EventObject e, ForceRegenerateSourceCode action) {
				getBrowserView().getBrowser().resetHoldStructure();
				getBrowserView().getBrowser().update();
				if (GeneratorPreferences.getAutomaticallyDismissUnchangedFiles()) {
					DismissUnchangedGeneratedFiles.actionType.makeNewAction(action.getFocusedObject(), action.getGlobalSelection(),
							action.getEditor()).doAction();
				}
				action.getProjectGenerator().stopHandleLogs();
				action.getProjectGenerator().flushLogs();
				getControllerActionInitializer().getGeneratorController().disposeProgressWindow();
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<ForceRegenerateSourceCode> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<ForceRegenerateSourceCode>() {
			@Override
			public boolean handleException(FlexoException exception, ForceRegenerateSourceCode action) {
				getBrowserView().getBrowser().resetHoldStructure();
				getBrowserView().getBrowser().update();
				getControllerActionInitializer().getGeneratorController().disposeProgressWindow();
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
		return GeneratorIconLibrary.FORCE_REGENERATE_CODE_ICON;
	}

	@Override
	protected Icon getDisabledIcon() {
		return GeneratorIconLibrary.FORCE_REGENERATE_CODE_DISABLED_ICON;
	}

	@Override
	protected KeyStroke getShortcut() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_G, FlexoCst.META_MASK | InputEvent.SHIFT_DOWN_MASK);
	}

	protected GeneratorBrowserView getBrowserView() {
		return ((GeneratorController) getController()).getBrowserView();
	}
}
