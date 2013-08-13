package org.openflexo;

import java.io.File;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoEditor.FlexoEditorFactory;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.rm.FlexoProject.FlexoProjectReferenceLoader;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.module.ModuleLoader;
import org.openflexo.module.ProjectLoader;
import org.openflexo.rest.client.ServerRestService;

public abstract class ApplicationContext implements FlexoEditorFactory {

	private ModuleLoader moduleLoader;

	private ProjectLoader projectLoader;

	private FlexoEditor applicationEditor;

	private FlexoProjectReferenceLoader projectReferenceLoader;

	private FlexoResourceCenterService resourceCenterService;

	private ServerRestService serverRestService;

	public ApplicationContext() {
		applicationEditor = createApplicationEditor();
		try {
			projectLoader = new ProjectLoader(this);
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		moduleLoader = new ModuleLoader(this);
		projectReferenceLoader = createProjectReferenceLoader();
		resourceCenterService = createResourceCenterService();
	}

	public ModuleLoader getModuleLoader() {
		return moduleLoader;
	}

	public ProjectLoader getProjectLoader() {
		return projectLoader;
	}

	public final FlexoProjectReferenceLoader getProjectReferenceLoader() {
		return projectReferenceLoader;
	}

	public final FlexoResourceCenterService getResourceCenterService() {
		return resourceCenterService;
	}

	public final FlexoEditor getApplicationEditor() {
		return applicationEditor;
	}

	public ServerRestService getServerRestService() {
		if (serverRestService == null) {
			serverRestService = new ServerRestService(getProjectLoader());
		}
		return serverRestService;
	}

	public boolean isAutoSaveServiceEnabled() {
		return false;
	}

	public abstract ProjectLoadingHandler getProjectLoadingHandler(File projectDirectory);

	protected abstract FlexoEditor createApplicationEditor();

	protected abstract FlexoProjectReferenceLoader createProjectReferenceLoader();

	protected abstract FlexoResourceCenterService createResourceCenterService();

}
