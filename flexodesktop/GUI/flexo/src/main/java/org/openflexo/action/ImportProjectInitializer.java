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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JFileChooser;

import org.openflexo.components.ProjectChooserComponent;
import org.openflexo.fib.controller.FIBController.Status;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.action.ImportProject;
import org.openflexo.foundation.rm.ProjectImportLoopException;
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.rest.client.ServerRestProjectListModel;
import org.openflexo.rest.client.model.Project;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class ImportProjectInitializer extends ActionInitializer<ImportProject, FlexoModelObject, FlexoModelObject> {

	public class ImportProjectActionInitializer extends FlexoActionInitializer<ImportProject> {
		@Override
		public boolean run(EventObject e, ImportProject action) {
			if (action.getProjectToImport() != null) {
				return true;
			}
			ProjectChooserComponent chooser = null;
			while (true) {
				if (action.isFromDisk()) {
					if (chooser == null) {
						chooser = new ProjectChooserComponent(FlexoFrame.getActiveFrame()) {
						};
					}
					File selectedFile;
					if (chooser.showOpenDialog() == JFileChooser.APPROVE_OPTION && (selectedFile = chooser.getSelectedFile()) != null) {
						if (!loadAndSetProject(action, selectedFile)) {
							return false;
						} else if (action.getProjectToImport() != null) {
							return true;
						}
					} else {
						// User chose "Cancel"
						return false;
					}
				} else {
					final ServerRestProjectListModel model = new ServerRestProjectListModel(getController().getApplicationContext()
							.getServerRestService(), getController().getFlexoFrame(), false);

					final FIBDialog<ServerRestProjectListModel> dialog = FIBDialog.instanciateDialog(ServerRestProjectListModel.FIB_FILE,
							model, getController().getFlexoFrame(), true, FlexoLocalization.getMainLocalizer());
					dialog.addWindowListener(new WindowAdapter() {

						@Override
						public void windowActivated(WindowEvent e) {
							dialog.removeWindowListener(this);
							model.refresh();
						}
					});
					dialog.setLocationRelativeTo(getController().getFlexoFrame());
					dialog.setVisible(true);
					if (dialog.getStatus() == Status.VALIDATED) {
						Project project = model.getSelectedProject();
						if (project == null) {
							return false;
						}
						try {
							File projectDirectory = model.downloadToFolder(project, model.getProjectDownloadDirectory());
							if (!loadAndSetProject(action, projectDirectory)) {
								return false;
							} else if (action.getProjectToImport() != null) {
								return true;
							}
						} catch (IOException ex) {
							ex.printStackTrace();
							FlexoController.notify(FlexoLocalization.localizedForKey("could_not_download_project") + " "
									+ project.getName() + " (" + ex.getMessage() + ")");
						}
					} else {
						return false;
					}
				}
			}
		}

		private boolean loadAndSetProject(ImportProject action, File selectedFile) {
			FlexoEditor editor = null;
			try {
				editor = getController().getApplicationContext().getProjectLoader().loadProject(selectedFile, true);
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
			} else {
				FlexoController.notify(reason);
			}
			return true;
		}
	}

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	public ImportProjectInitializer(ControllerActionInitializer actionInitializer) {
		super(ImportProject.actionType, actionInitializer);
	}

	@Override
	protected FlexoExceptionHandler<ImportProject> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<ImportProject>() {

			@Override
			public boolean handleException(FlexoException exception, ImportProject action) {
				if (action.getThrownException() instanceof ProjectImportLoopException) {
					FlexoController.notify(FlexoLocalization.localizedForKey("project_already_imported") + " "
							+ action.getProjectToImport().getDisplayName());
				}
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<ImportProject> getDefaultFinalizer() {
		return new FlexoActionFinalizer<ImportProject>() {
			@Override
			public boolean run(EventObject event, ImportProject action) {
				if (action.hasActionExecutionSucceeded()) {
					FlexoController.notify(FlexoLocalization.localizedForKey("successfully_imported_project") + " "
							+ action.getProjectToImport().getDisplayName());
				}
				return true;
			}
		};
	}

	@Override
	protected FlexoActionInitializer<ImportProject> getDefaultInitializer() {
		return new ImportProjectActionInitializer();
	}

	@Override
	protected Icon getEnabledIcon() {
		return IconLibrary.IMPORT_ICON;
	}

}
