package org.openflexo;

import java.io.File;

import org.openflexo.foundation.FlexoEditor.FlexoEditorFactory;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.module.ModuleLoader;
import org.openflexo.module.ProjectLoader;

public abstract class ApplicationContext implements FlexoEditorFactory {

	private ModuleLoader moduleLoader;

	private ProjectLoader projectLoader;

	public ApplicationContext() {
		moduleLoader = new ModuleLoader(this);
		projectLoader = new ProjectLoader(this);
	}

	public ModuleLoader getModuleLoader() {
		return moduleLoader;
	}

	public ProjectLoader getProjectLoader() {
		return projectLoader;
	}

	public boolean isAutoSaveServiceEnabled() {
		return false;
	}

	public abstract ProjectLoadingHandler getProjectLoadingHandler(File projectDirectory);

}
