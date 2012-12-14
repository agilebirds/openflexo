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

import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.components.ProjectChooserComponent;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.ImportProject;
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class ImportProjectInitializer extends ActionInitializer<ImportProject, FlexoModelObject, FlexoModelObject> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	public ImportProjectInitializer(ControllerActionInitializer actionInitializer) {
		super(ImportProject.actionType, actionInitializer);
	}

	@Override
	protected FlexoActionInitializer<ImportProject> getDefaultInitializer() {
		return new FlexoActionInitializer<ImportProject>() {
			@Override
			public boolean run(EventObject e, ImportProject action) {
				if (action.getProjectToImport() != null) {
					return true;
				}
				ProjectChooserComponent chooser = new ProjectChooserComponent(FlexoFrame.getActiveFrame()) {
				};
				while (true) {
					chooser.showOpenDialog();
					if (chooser.getSelectedFile() != null) {
						FlexoEditor editor = null;
						try {
							editor = getController().getApplicationContext().getProjectLoader()
									.loadProject(chooser.getSelectedFile(), true);
						} catch (ProjectLoadingCancelledException e1) {
							e1.printStackTrace();
							// User chose not to load this project
							return false;
						} catch (ProjectInitializerException e1) {
							e1.printStackTrace();
							// Failed to load the project
							FlexoController.notify(FlexoLocalization.localizedForKey("could_not_open_project_located_at")
									+ e1.getProjectDirectory().getAbsolutePath());
						}
						if (editor == null) {
							return false;
						}

						String reason = action.getImportingProject().canImportProject(editor.getProject());
						if (reason == null) {
							action.setProjectToImport(editor.getProject());
							return true;
						} else {
							FlexoController.notify(reason);
						}
					} else {
						// User chose "Cancel"
						return false;
					}
				}
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return IconLibrary.IMPORT_ICON;
	}

}
