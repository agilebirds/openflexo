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
package org.openflexo.action;

import java.awt.Component;
import java.io.File;
import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.openflexo.GeneralPreferences;
import org.openflexo.components.AskParametersDialog;
import org.openflexo.components.AskParametersDialog.ValidationCondition;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.ie.action.ImportImage;
import org.openflexo.foundation.param.ParametersModel;
import org.openflexo.foundation.param.RadioButtonListParameter;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.prefs.FlexoPreferences;
import org.openflexo.swing.FlexoFileChooser;
import org.openflexo.swing.ImagePreview;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class ImportImageInitializer extends ActionInitializer<ImportImage, FlexoModelObject, FlexoModelObject> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	public ImportImageInitializer(ControllerActionInitializer actionInitializer) {
		super(ImportImage.actionType, actionInitializer);
	}

	@Override
	protected FlexoActionInitializer<ImportImage> getDefaultInitializer() {
		return new FlexoActionInitializer<ImportImage>() {
			@Override
			public boolean run(EventObject e, ImportImage action) {
				if (action.getFileToImport() != null) {
					return true;
				}
				JFileChooser chooser = FlexoFileChooser
						.getFileChooser(GeneralPreferences.getLastImageDirectory() != null ? GeneralPreferences.getLastImageDirectory()
								.getAbsolutePath() : null);
				chooser.setAccessory(new ImagePreview(chooser));
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setFileFilter(new FileFilter() {
					@Override
					public boolean accept(File f) {
						if (f.isDirectory()) {
							return true;
						}
						String ext = f.getName().toLowerCase();
						return ext.endsWith(".gif") || ext.endsWith(".png") || ext.endsWith(".jpg");
					}

					@Override
					public String getDescription() {
						return "*.jpg, *.png, *.gif";
					}
				});
				chooser.setAcceptAllFileFilterUsed(false);
				int returnVal = chooser.showOpenDialog(e != null && e.getSource() instanceof Component ? (Component) e.getSource()
						: FlexoFrame.getActiveFrame());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					GeneralPreferences.setLastImageDirectory(chooser.getSelectedFile().getParentFile());
					FlexoPreferences.savePreferences(true);
					action.setFileToImport(chooser.getSelectedFile());
					return true;
				}
				return false;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<ImportImage> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<ImportImage>() {
			@Override
			public boolean handleException(FlexoException exception, ImportImage action) {
				if (exception instanceof DuplicateResourceException) {
					String RENAME = FlexoLocalization.localizedForKey("rename");
					final String OVERWRITE = FlexoLocalization.localizedForKey("overwrite");
					final RadioButtonListParameter<String> rbl = new RadioButtonListParameter<String>("choice",
							"what_would_you_like_to_do", RENAME, RENAME, OVERWRITE);
					final TextFieldParameter newImageName = new TextFieldParameter("name", "image_name", getProject().getUnusedImageName(
							action.getFileToImport().getName()));
					newImageName.setDepends("choice");
					newImageName.setConditional("choice=" + RENAME);
					AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), getController()
							.getFlexoFrame(), FlexoLocalization.localizedForKey("image_already_exist"), FlexoLocalization
							.localizedForKey("image_already_exist"), new ValidationCondition() {

						@Override
						public boolean isValid(ParametersModel model) {
							if (rbl.getValue().equals(OVERWRITE)) {
								return true;
							} else {
								return newImageName.getValue() != null
										&& (newImageName.getValue().endsWith(".png") || newImageName.getValue().endsWith(".jpg") || newImageName
												.getValue().endsWith(".gif"));
							}
						}

					}, rbl, newImageName);
					if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
						ImportImage image = ImportImage.actionType.makeNewAction(action.getFocusedObject(), action.getGlobalSelection(),
								getEditor());
						if (rbl.getValue().equals(OVERWRITE)) {
							image.setOverwrite(true);
						} else if (rbl.getValue().equals(RENAME)) {
							image.setImageName(newImageName.getValue());
						}
						image.setFileToImport(action.getFileToImport());
						image.doAction();
						if (image.hasActionExecutionSucceeded()) {
							action.setCreatedResource(image.getCreatedResource());
						}
						return true;
					} else {
						return true;
					}

				}
				return false;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<ImportImage> getDefaultFinalizer() {
		return new FlexoActionFinalizer<ImportImage>() {
			@Override
			public boolean run(EventObject e, ImportImage action) {
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return IconLibrary.EXPORT_ICON;
	}

}
