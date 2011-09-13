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
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.icon.GeneratorIconLibrary;
import org.openflexo.javaparser.FJPDMSet;
import org.openflexo.javaparser.FJPJavaClass;
import org.openflexo.javaparser.FJPJavaSource;
import org.openflexo.javaparser.FJPTypeResolver;
import org.openflexo.javaparser.FJPJavaParseException.FJPParseException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.sg.action.AcceptDiskUpdateAndReinjectInModel;
import org.openflexo.sg.action.ReinjectInModel;
import org.openflexo.sg.file.SGJavaFile;
import org.openflexo.sgmodule.view.popup.ModelReinjectionPopup;
import org.openflexo.sgmodule.view.popup.SelectFilesPopup;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;


import org.openflexo.components.MultipleObjectSelectorPopup;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.ModelReinjectableFile;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.generator.file.AbstractCGFile;


public class ReinjectInModelInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	ReinjectInModelInitializer(SGControllerActionInitializer actionInitializer)
	{
		super(ReinjectInModel.actionType,actionInitializer);
	}

	@Override
	protected SGControllerActionInitializer getControllerActionInitializer() 
	{
		return (SGControllerActionInitializer)super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<ReinjectInModel> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<ReinjectInModel>() {
			@Override
			public boolean run(ActionEvent e, ReinjectInModel action)
			{
				if (action.getFilesToReinjectInModel().size() == 0) {
					FlexoController.notify(FlexoLocalization.localizedForKey("no_files_selected"));
					return false;
				}
				else if ((action.getFilesToReinjectInModel().size() > 1 
						|| (!(action.getFocusedObject() instanceof CGFile)))
						&& (!(action.getContext() instanceof AcceptDiskUpdateAndReinjectInModel)))
				{
					SelectFilesPopup popup 
					= new SelectFilesPopup(FlexoLocalization.localizedForKey("reinject_in_model"),
							FlexoLocalization.localizedForKey("reinject_in_model_description"),
							"reinject_in_model",
							action.getFilesToReinjectInModel(),
							action.getFocusedObject().getProject(),
							getControllerActionInitializer().getSGController());
					popup.setVisible(true);
					if ((popup.getStatus() == MultipleObjectSelectorPopup.VALIDATE) 
							&& (popup.getFileSet().getSelectedFiles().size() > 0)) {
						action.setFilesToReinjectInModel(popup.getFileSet().getSelectedFiles());
					}
					else {
						return false;
					}
				}
				else {
					// Continue without confirmation nor selection of files
				}

				Vector<SGJavaFile> selectedJavaFiles = new Vector<SGJavaFile>();
				for (AbstractCGFile f : action.getFilesToReinjectInModel()) {
					if (f instanceof ModelReinjectableFile) {
						if (f instanceof SGJavaFile) {
							selectedJavaFiles.add((SGJavaFile)f);
						}
					}
				}

				if (action.getAskReinjectionContext()) { // Interactive mode: ask context
					ModelReinjectionPopup popup;
					try {
						popup = new ModelReinjectionPopup(
								action.getLocalizedName(),
								FlexoLocalization.localizedForKey("please_select_properties_and_methods_to_reinject_in_model"),
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
				else { // Non-interactive mode: build default context
					// TODO build context in action, and use this in popup also
					Hashtable<FJPJavaSource,DMEntity> entries = new Hashtable<FJPJavaSource,DMEntity>();
					for (SGJavaFile javaFile : selectedJavaFiles) {
						if (javaFile.getParsedJavaSource() != null)
							entries.put(javaFile.getParsedJavaSource(),javaFile.getModelEntity());
					}
					FJPDMSet dmSet = new FJPDMSet(getProject(),"updated_classes",entries);
					Hashtable<FJPJavaClass,Vector<String>> ignoredProperties = new Hashtable<FJPJavaClass,Vector<String>>();
					Hashtable<FJPJavaClass,Vector<String>> ignoredMethods = new Hashtable<FJPJavaClass,Vector<String>>();
					for (SGJavaFile javaFile : selectedJavaFiles) {
						if (javaFile.getParseException() != null) {
							FlexoController.showError(javaFile.getParseException().getParseException().getLocalizedMessage());
							return false;
						}
						FJPJavaClass parsedClass = javaFile.getParsedJavaSource().getRootClass();
						ignoredProperties.put(parsedClass, javaFile.getPropertiesKnownAndIgnored());
						ignoredMethods.put(parsedClass, javaFile.getMethodsKnownAndIgnored());
					}
					dmSet.notifyKnownAndIgnoredProperties(ignoredProperties);
					dmSet.notifyKnownAndIgnoredMethods(ignoredMethods);
					dmSet.selectAllNewlyDiscoveredPropertiesAndMethods();
					action.setUpdatedSet(dmSet);
					action.getProjectGenerator().startHandleLogs();
					return true;
				}
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<ReinjectInModel> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<ReinjectInModel>() {
			@Override
			public boolean run(ActionEvent e, ReinjectInModel action)
			{
				action.getProjectGenerator().stopHandleLogs();
				action.getProjectGenerator().flushLogs();
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<ReinjectInModel> getDefaultExceptionHandler() 
	{
		return new FlexoExceptionHandler<ReinjectInModel>() {
			@Override
			public boolean handleException(FlexoException exception, ReinjectInModel action) {
				getControllerActionInitializer().getSGController().disposeProgressWindow();
				if (exception instanceof FJPTypeResolver.UnresolvedTypeException) {
					FlexoController.showError(FlexoLocalization.localizedForKey("cannot_resolve") + " "
							+ ((FJPTypeResolver.UnresolvedTypeException)exception).getUnresolvedType());
					return true;
				}                
				exception.printStackTrace();
				FlexoController.showError(FlexoLocalization.localizedForKey("file_reinjecting_failed") + ":\n"
						+ exception.getLocalizedMessage());
				return true;
			}
		};
	}


	@Override
	protected Icon getEnabledIcon() 
	{
		return GeneratorIconLibrary.REINJECT_IN_MODEL_ICON;
	}

	@Override
	protected Icon getDisabledIcon() 
	{
		return GeneratorIconLibrary.REINJECT_IN_MODEL_DISABLED_ICON;
	}

}
