package org.openflexo.view.controller;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import org.openflexo.ApplicationData;
import org.openflexo.components.NewProjectComponent;
import org.openflexo.components.OpenProjectComponent;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.foundation.utils.OperationCancelledException;
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.Module;
import org.openflexo.module.ModuleLoader;
import org.openflexo.module.ModuleLoadingException;
import org.openflexo.module.ProjectLoader;
import org.openflexo.rest.client.ServerRestProjectListModel;
import org.openflexo.rest.client.model.Project;
import org.openflexo.toolbox.FileUtils;

public class WelcomePanelController extends FlexoFIBController {

	public WelcomePanelController(FIBComponent component) {
		super(component);
	}

	@Override
	public ApplicationData getDataObject() {
		return (ApplicationData) super.getDataObject();
	}

	private ModuleLoader getModuleLoader() {
		return getDataObject().getApplicationContext().getModuleLoader();
	}

	private ProjectLoader getProjectLoader() {
		return getDataObject().getApplicationContext().getProjectLoader();
	}

	public void exit() {
		try {
			getModuleLoader().quit(false);
		} catch (OperationCancelledException e) {
		}
	}

	public void openModule(Module module) {
		hide();
		try {
			getModuleLoader().switchToModule(module);
			validateAndDispose();
		} catch (ModuleLoadingException e) {
			e.printStackTrace();
			FlexoController.notify(FlexoLocalization.localizedForKey("could_not_load_module") + " " + e.getModule());
			show();
		}
	}

	public void openProject(File projectDirectory, Module module) {
		if (projectDirectory == null) {
			projectDirectory = OpenProjectComponent.getProjectDirectory();
			if (projectDirectory == null) {
				return;
			}
		}
		hide();
		try {
			getModuleLoader().switchToModule(module);
		} catch (ModuleLoadingException e) {
			e.printStackTrace();
			FlexoController.notify(FlexoLocalization.localizedForKey("could_not_load_module") + " " + e.getModule());
			show();
		}
		try {
			getProjectLoader().loadProject(projectDirectory);
			validateAndDispose();
		} catch (ProjectLoadingCancelledException e) {
		} catch (ProjectInitializerException e) {
			e.printStackTrace();
			FlexoController.notify(FlexoLocalization.localizedForKey("could_not_open_project_located_at")
					+ e.getProjectDirectory().getAbsolutePath());
		}
	}

	public void newProject(Module module) {
		File projectDirectory;
		projectDirectory = NewProjectComponent.getProjectDirectory();
		if (projectDirectory == null) {
			return;
		}
		hide();
		try {
			getModuleLoader().switchToModule(module);
			getProjectLoader().newProject(projectDirectory);
			validateAndDispose();
		} catch (ModuleLoadingException e) {
			e.printStackTrace();
			FlexoController.notify(FlexoLocalization.localizedForKey("could_not_load_module") + " " + e.getModule());
			show();
		} catch (ProjectInitializerException e) {
			e.printStackTrace();
			FlexoController.notify(e.getMessage());
			show();
		}
	}

	public void openServerProject(Module module) {
		final ServerRestProjectListModel model = new ServerRestProjectListModel(getDataObject().getApplicationContext()
				.getServerRestService(), getWindow(), true);

		final FIBDialog<ServerRestProjectListModel> dialog = FIBDialog.instanciateDialog(ServerRestProjectListModel.FIB_FILE, model,
				getWindow(), true, FlexoLocalization.getMainLocalizer());
		dialog.addWindowListener(new WindowAdapter() {

			@Override
			public void windowActivated(WindowEvent e) {
				dialog.removeWindowListener(this);
				model.refresh();
			}
		});
		dialog.setLocationRelativeTo(getWindow());
		dialog.setVisible(true);
		if (dialog.getStatus() == Status.VALIDATED) {
			hide();
			Project project = model.getSelectedProject();
			if (project == null) {
				return;
			}
			try {
				File documentDirectory = FileUtils.getDocumentDirectory();
				if (!documentDirectory.exists() || !documentDirectory.canWrite()) {
					documentDirectory = org.apache.commons.io.FileUtils.getUserDirectory();
				}
				File downloadToFolder = model.downloadToFolder(project, new File(documentDirectory, "OpenFlexo Projects"));
				if (downloadToFolder != null) {
					openProject(downloadToFolder, module);
				}
			} catch (IOException e) {
				e.printStackTrace();
				show();
				FlexoController.notify(FlexoLocalization.localizedForKey("could_not_download_project") + " " + project.getName() + " ("
						+ e.getMessage() + ")");
			}
		}
	}
}
