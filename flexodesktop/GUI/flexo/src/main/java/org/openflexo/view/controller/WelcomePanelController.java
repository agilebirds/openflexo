package org.openflexo.view.controller;

import java.io.File;

import org.openflexo.components.NewProjectComponent;
import org.openflexo.components.OpenProjectComponent;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.foundation.utils.ProjectExitingCancelledException;
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.Module;
import org.openflexo.module.ModuleLoader;
import org.openflexo.module.ModuleLoadingException;

public class WelcomePanelController extends FlexoFIBController {

	public WelcomePanelController(FIBComponent component) {
		super(component);
	}

	public void exit() {
		try {
			getModuleLoader().quit(false);
		} catch (ProjectExitingCancelledException e) {
		}
	}

	public void openModule(Module module) {
		hide();
		try {
			getModuleLoader().switchToModule(module, null);
			validateAndDispose();
		} catch (ModuleLoadingException e) {
			e.printStackTrace();
			FlexoController.notify(FlexoLocalization.localizedForKey("could_not_load_module") + " " + e.getModule());
			show();
		}
	}

	public void openProject(File projectDirectory, Module module) {
		if (projectDirectory == null) {
			try {
				projectDirectory = OpenProjectComponent.getProjectDirectory();
			} catch (ProjectLoadingCancelledException e1) {
				return;
			}
		}
		hide();
		try {
			getModuleLoader().openProject(projectDirectory, module);
			validateAndDispose();
		} catch (ProjectLoadingCancelledException e) {
			show();
		} catch (ModuleLoadingException e) {
			e.printStackTrace();
			FlexoController.notify(FlexoLocalization.localizedForKey("could_not_load_module") + " " + e.getModule());
			show();
		} catch (ProjectInitializerException e) {
			e.printStackTrace();
			FlexoController.notify(FlexoLocalization.localizedForKey("could_not_open_project_located_at")
					+ e.getProjectDirectory().getAbsolutePath());
			show();
		}
	}

	public void newProject(Module module) {
		File project;
		try {
			project = NewProjectComponent.getProjectDirectory();
		} catch (ProjectLoadingCancelledException e1) {
			return;
		}
		hide();
		try {
			getModuleLoader().newProject(project, module);
			validateAndDispose();
		} catch (ProjectLoadingCancelledException e) {
			show();
		} catch (ModuleLoadingException e) {
			e.printStackTrace();
			FlexoController.notify(FlexoLocalization.localizedForKey("could_not_load_module") + " " + e.getModule());
			show();
		}
	}

	private ModuleLoader getModuleLoader() {
		return ModuleLoader.instance();
	}

}
