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
package org.openflexo.sgmodule.controller.action;

import java.awt.event.ActionEvent;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.icon.GeneratorIconLibrary;
import org.openflexo.javaparser.FJPTypeResolver;
import org.openflexo.javaparser.FJPJavaParseException.FJPParseException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.sg.action.UpdateModel;
import org.openflexo.sg.file.SGJavaFile;
import org.openflexo.sgmodule.view.popup.ModelReinjectionPopup;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;


import org.openflexo.components.MultipleObjectSelectorPopup;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.cg.ModelReinjectableFile;


public class UpdateModelInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	UpdateModelInitializer(SGControllerActionInitializer actionInitializer)
	{
		super(UpdateModel.actionType,actionInitializer);
	}

	@Override
	protected SGControllerActionInitializer getControllerActionInitializer() 
	{
		return (SGControllerActionInitializer)super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<UpdateModel> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<UpdateModel>() {
			@Override
			public boolean run(ActionEvent e, UpdateModel action)
			{
				Vector<SGJavaFile> selectedJavaFiles = new Vector<SGJavaFile>();
				for (ModelReinjectableFile f : action.getFilesToUpdate()) {
					if (f instanceof SGJavaFile) {
						selectedJavaFiles.add((SGJavaFile)f);
					}
				}
				ModelReinjectionPopup popup;
				try {
					popup = new ModelReinjectionPopup(
							action.getLocalizedName(),
							FlexoLocalization.localizedForKey("please_select_properties_and_methods_to_update"),
							selectedJavaFiles, getProject(), getControllerActionInitializer().getSGController());
					popup.setVisible(true);
					if ((popup.getStatus() == MultipleObjectSelectorPopup.VALIDATE) && (popup.getDMSet().getSelectedObjects().size() > 0)) {
						action.setUpdatedSet(popup.getDMSet());
						action.getProjectGenerator().startHandleLogs();
						return true;
					} else {
						return false;
					}
				} catch (FJPParseException e2) {
					FlexoController.showError(e2.getLocalizedMessage());
					return false;
				}
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<UpdateModel> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<UpdateModel>() {
			@Override
			public boolean run(ActionEvent e, UpdateModel action)
			{
				action.getProjectGenerator().stopHandleLogs();
				action.getProjectGenerator().flushLogs();
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<UpdateModel> getDefaultExceptionHandler() 
	{
		return new FlexoExceptionHandler<UpdateModel>() {
			@Override
			public boolean handleException(FlexoException exception, UpdateModel action) {
				getControllerActionInitializer().getSGController().disposeProgressWindow();
				if (exception instanceof FJPTypeResolver.UnresolvedTypeException) {
					FlexoController.showError(FlexoLocalization.localizedForKey("cannot_resolve") + " "
							+ ((FJPTypeResolver.UnresolvedTypeException)exception).getUnresolvedType());
					return true;
				}                
				exception.printStackTrace();
				FlexoController.showError(FlexoLocalization.localizedForKey("file_updating_failed") + ":\n"
						+ exception.getLocalizedMessage());
				return true;
			}
		};
	}


	@Override
	protected Icon getEnabledIcon() 
	{
		return GeneratorIconLibrary.UPDATE_MODEL_ICON;
	}

	@Override
	protected Icon getDisabledIcon() 
	{
		return GeneratorIconLibrary.UPDATE_MODEL_DISABLED_ICON;
	}

}
