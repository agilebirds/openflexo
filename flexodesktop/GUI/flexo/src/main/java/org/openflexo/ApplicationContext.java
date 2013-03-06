package org.openflexo;

import java.io.File;

import org.openflexo.foundation.DefaultFlexoServiceManager;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoEditor.FlexoEditorFactory;
import org.openflexo.foundation.resource.DefaultResourceCenterService;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.rm.FlexoProject.FlexoProjectReferenceLoader;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.module.ModuleLoader;
import org.openflexo.module.ProjectLoader;
import org.openflexo.view.controller.TechnologyAdapterControllerService;

public abstract class ApplicationContext extends DefaultFlexoServiceManager implements FlexoEditorFactory {

	private FlexoEditor applicationEditor;

	public ApplicationContext() {
		super();
		applicationEditor = createApplicationEditor();
		try {
			ProjectLoader projectLoader = new ProjectLoader(this);
			registerService(projectLoader);
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		ModuleLoader moduleLoader = new ModuleLoader(this);
		registerService(moduleLoader);
		TechnologyAdapterControllerService technologyAdapterControllerService = createTechnologyAdapterControllerService();
		registerService(technologyAdapterControllerService);
	}

	public ModuleLoader getModuleLoader() {
		return getService(ModuleLoader.class);
	}

	public ProjectLoader getProjectLoader() {
		return getService(ProjectLoader.class);
	}

	@Override
	public final FlexoProjectReferenceLoader getProjectReferenceLoader() {
		return getService(FlexoProjectReferenceLoader.class);
	}

	public final TechnologyAdapterControllerService getTechnologyAdapterControllerService() {
		return getService(TechnologyAdapterControllerService.class);
	}

	public final FlexoEditor getApplicationEditor() {
		return applicationEditor;
	}

	public boolean isAutoSaveServiceEnabled() {
		return false;
	}

	public abstract ProjectLoadingHandler getProjectLoadingHandler(File projectDirectory);

	@Override
	protected abstract FlexoEditor createApplicationEditor();

	@Override
	protected abstract FlexoProjectReferenceLoader createProjectReferenceLoader();

	protected abstract TechnologyAdapterControllerService createTechnologyAdapterControllerService();

	@Override
	protected FlexoResourceCenterService createResourceCenterService() {
		return DefaultResourceCenterService.getNewInstance(GeneralPreferences.getLocalResourceCenterDirectory());
	}

}
