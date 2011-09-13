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
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.openflexo.FlexoCst;
import org.openflexo.icon.GeneratorIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;


import org.openflexo.cgmodule.GeneratorPreferences;
import org.openflexo.cgmodule.controller.GeneratorController;
import org.openflexo.cgmodule.view.GeneratorMainPane;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.generator.action.DismissUnchangedGeneratedFiles;
import org.openflexo.generator.action.GenerateSourceCode;
import org.openflexo.generator.exception.GenerationException;


public class GenerateSourceCodeInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	GenerateSourceCodeInitializer(GeneratorControllerActionInitializer actionInitializer)
	{
		super(GenerateSourceCode.actionType,actionInitializer);
	}

	@Override
	protected GeneratorControllerActionInitializer getControllerActionInitializer() 
	{
		return (GeneratorControllerActionInitializer)super.getControllerActionInitializer();
	}

	@Override
	public GeneratorController getController() {
		return (GeneratorController) super.getController();
	}
	
	@Override
	protected FlexoActionInitializer<GenerateSourceCode> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<GenerateSourceCode>() {
			@Override
			public boolean run(ActionEvent e, GenerateSourceCode action)
			{
				if (action.getRepository().getDirectory() == null) {
					FlexoController.notify(FlexoLocalization.localizedForKey("please_supply_valid_directory"));
					return false;
				}
                if (!action.getRepository().getDirectory().exists()) {
                    if (FlexoController.confirm(FlexoLocalization.localizedForKey("directory")+" "+action.getRepository().getDirectory().getAbsolutePath()+" "+FlexoLocalization.localizedForKey("does_not_exist")+"\n"+FlexoLocalization.localizedForKey("would_you_like_to_create_it_and_continue?"))) {
                        action.getRepository().getDirectory().mkdirs();
                    } else {
                        return false;
                    }
                }
				action.setSaveBeforeGenerating(GeneratorPreferences.getSaveBeforeGenerating());
				action.getProjectGenerator().startHandleLogs();
				((GeneratorMainPane)getController().getMainPane()).getBrowserView().getBrowser().setHoldStructure();
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<GenerateSourceCode> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<GenerateSourceCode>() {
			@Override
			public boolean run(ActionEvent e, GenerateSourceCode action)
			{
				((GeneratorMainPane)getController().getMainPane()).getBrowserView().getBrowser().resetHoldStructure();
				((GeneratorMainPane)getController().getMainPane()).getBrowserView().getBrowser().update();
				action.getProjectGenerator().stopHandleLogs();
				action.getProjectGenerator().flushLogs();
				getControllerActionInitializer().getGeneratorController().disposeProgressWindow();

				if (GeneratorPreferences.getAutomaticallyDismissUnchangedFiles())
					DismissUnchangedGeneratedFiles.actionType.makeNewAction(
							action.getFocusedObject(), action.getGlobalSelection(), action.getEditor()).doAction();

				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<GenerateSourceCode> getDefaultExceptionHandler() 
	{
		return new FlexoExceptionHandler<GenerateSourceCode>() {
			@Override
			public boolean handleException(FlexoException exception, GenerateSourceCode action) {
				((GeneratorMainPane)getController().getMainPane()).getBrowserView().getBrowser().resetHoldStructure();
				((GeneratorMainPane)getController().getMainPane()).getBrowserView().getBrowser().update();
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
	protected Icon getEnabledIcon() 
	{
		return GeneratorIconLibrary.GENERATE_CODE_ICON;
	}

	@Override
	protected Icon getDisabledIcon() 
	{
		return GeneratorIconLibrary.GENERATE_CODE_DISABLED_ICON;
	}
	@Override
	protected KeyStroke getShortcut()
	{
		return KeyStroke.getKeyStroke(KeyEvent.VK_G, FlexoCst.META_MASK);
	}
}
