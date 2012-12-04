package org.openflexo;

import java.io.File;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoEditor.FlexoEditorFactory;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.rm.FlexoProject.FlexoProjectReferenceLoader;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.module.ModuleLoader;
import org.openflexo.module.ProjectLoader;

public abstract class ApplicationContext implements FlexoEditorFactory {

	private ModuleLoader moduleLoader;

	private ProjectLoader projectLoader;

	private FlexoEditor applicationEditor;

	private FlexoProjectReferenceLoader projectReferenceLoader;

	private FlexoResourceCenterService resourceCenterService;

	private TechnologyAdapterService technologyAdapterService;

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
		technologyAdapterService = createTechnologyAdapterService(resourceCenterService);

		// At this point, the resource center service is initialized, and so is the technology adapter service
		for (FlexoResourceCenter rc : resourceCenterService.getResourceCenters()) {
			rc.initialize(technologyAdapterService);
		}
		for (TechnologyAdapter<?, ?> ta : technologyAdapterService.getTechnologyAdapters()) {
			ta.initialize();
		}

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

	public final TechnologyAdapterService getTechnologyAdapterService() {
		return technologyAdapterService;
	}

	public final FlexoEditor getApplicationEditor() {
		return applicationEditor;
	}

	public boolean isAutoSaveServiceEnabled() {
		return false;
	}

	public abstract ProjectLoadingHandler getProjectLoadingHandler(File projectDirectory);

	protected abstract FlexoEditor createApplicationEditor();

	protected abstract FlexoProjectReferenceLoader createProjectReferenceLoader();

	protected abstract FlexoResourceCenterService createResourceCenterService();

	protected abstract TechnologyAdapterService createTechnologyAdapterService(FlexoResourceCenterService resourceCenterService);

}
