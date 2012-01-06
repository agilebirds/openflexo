package org.openflexo.view.controller;

import java.io.File;

import org.openflexo.GeneralPreferences;
import org.openflexo.components.NewProjectComponent;
import org.openflexo.components.OpenProjectComponent;
import org.openflexo.components.WelcomeDialog;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.utils.ProjectExitingCancelledException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.module.Module;
import org.openflexo.module.ModuleLoader;
import org.openflexo.module.ModuleLoadingException;
import org.openflexo.module.ProjectLoader;

public class WelcomePanelController extends FlexoFIBController {

	public WelcomePanelController(FIBComponent component) {
		super(component);
	}

	public void exit() {
		try {
			getModuleLoader().quit(true);
		} catch (ProjectExitingCancelledException e) {
		}
	}

	public void openModule(Module module) {
		validateAndDispose();
		try {
			getModuleLoader().switchToModule(module, null);
		} catch (ModuleLoadingException e) {
			FlexoController.notify("Cannot load module " + module);
			new WelcomeDialog();
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

		GeneralPreferences.addToLastOpenedProjects(projectDirectory);
		validateAndDispose();
		try {
			FlexoEditor editor = getProjectLoader().loadProject(projectDirectory);
			getModuleLoader().switchToModule(module, editor.getProject());
		} catch (ModuleLoadingException e) {
			FlexoController.notify("Cannot load module " + module);
			new WelcomeDialog();
		} catch (ProjectLoadingCancelledException e) {
			// project need a conversion, but user cancelled the conversion.
			new WelcomeDialog();
		}
	}

	public void newProject(Module module) {
		File project;
		try {
			project = NewProjectComponent.getProjectDirectory();
		} catch (ProjectLoadingCancelledException e1) {
			return;
		}

		GeneralPreferences.addToLastOpenedProjects(project);
		validateAndDispose();
		FlexoEditor editor = getProjectLoader().newProject(project);
		try {
			getModuleLoader().switchToModule(module, editor.getProject());
		} catch (ModuleLoadingException e) {
			FlexoController.notify("Cannot load module " + module);
			new WelcomeDialog();
		}
	}

	private ModuleLoader getModuleLoader() {
		return ModuleLoader.instance();
	}

	private ProjectLoader getProjectLoader() {
		return ProjectLoader.instance();
	}

}
