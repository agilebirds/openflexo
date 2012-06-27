package org.openflexo;

import java.io.File;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoEditor.FlexoEditorFactory;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.module.ModuleLoader;
import org.openflexo.module.ProjectLoader;

public abstract class ApplicationContext implements FlexoEditorFactory {

	private ModuleLoader moduleLoader;

	private ProjectLoader projectLoader;

	private FlexoEditor applicationEditor;

	public ApplicationContext() {
		applicationEditor = createApplicationEditor();
		projectLoader = new ProjectLoader(this);
		moduleLoader = new ModuleLoader(this);
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

	public final FlexoEditor getApplicationEditor() {
		return applicationEditor;
	}

	public abstract ProjectLoadingHandler getProjectLoadingHandler(File projectDirectory);

	public abstract FlexoEditor createApplicationEditor();

}
