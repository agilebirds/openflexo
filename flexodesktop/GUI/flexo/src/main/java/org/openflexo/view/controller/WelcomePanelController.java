package org.openflexo.view.controller;

import java.io.File;

import org.openflexo.ApplicationData;
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
import org.openflexo.module.ProjectLoader;

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
		} catch (ProjectExitingCancelledException e) {
		}
	}

	public void openModule(Module module) {
		hide();
		try {
			getModuleLoader().getModuleInstance(module).activateModule();
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
			getModuleLoader().getModuleInstance(module).activateModule();
			getProjectLoader().loadProject(projectDirectory);
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
		File projectDirectory;
		projectDirectory = NewProjectComponent.getProjectDirectory();
		if (projectDirectory == null) {
			return;
		}
		hide();
		try {
			getModuleLoader().getModuleInstance(module).activateModule();
			getProjectLoader().newProject(projectDirectory);
			validateAndDispose();
		} catch (ModuleLoadingException e) {
			e.printStackTrace();
			FlexoController.notify(FlexoLocalization.localizedForKey("could_not_load_module") + " " + e.getModule());
			show();
		}
	}

}
