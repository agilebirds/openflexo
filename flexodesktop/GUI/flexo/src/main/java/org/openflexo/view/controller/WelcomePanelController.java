package org.openflexo.view.controller;

import java.io.File;

import org.openflexo.GeneralPreferences;
import org.openflexo.components.NewProjectComponent;
import org.openflexo.components.OpenProjectComponent;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.foundation.utils.ProjectExitingCancelledException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.module.Module;
import org.openflexo.module.ModuleLoader;

public class WelcomePanelController extends FlexoFIBController {

	public WelcomePanelController(FIBComponent component) 
	{
		super(component);
	}

	public void exit()
	{
		try {
			ModuleLoader.quit(true);
		} catch (ProjectExitingCancelledException e) {
		}
	}

	public void openModule(Module module)
	{
		validateAndDispose();
		ModuleLoader.switchToModule(module);
	}

	public void openProject(File project, Module module)
	{
		if (project == null) {
			try {
				project = OpenProjectComponent.getProjectDirectory();
			} catch (ProjectLoadingCancelledException e1) {
				return;
			}
		}

		GeneralPreferences.addToLastOpenedProjects(project);
		validateAndDispose();
		ModuleLoader.loadProject(project);
		ModuleLoader.switchToModule(module);
	}

	public void newProject(Module module)
	{
		File project;
		try {
			project = NewProjectComponent.getProjectDirectory();
		} catch (ProjectLoadingCancelledException e1) {
			return;
		}
		
		GeneralPreferences.addToLastOpenedProjects(project);
		validateAndDispose();
		ModuleLoader.newProject(project);
		ModuleLoader.switchToModule(module);
	}


}
