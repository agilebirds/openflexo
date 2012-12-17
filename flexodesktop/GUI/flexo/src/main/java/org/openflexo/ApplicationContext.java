package org.openflexo;

import java.io.File;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoEditor.FlexoEditorFactory;
import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.rm.FlexoProject.FlexoProjectReferenceLoader;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.foundation.xml.XMLSerializationService;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.module.ModuleLoader;
import org.openflexo.module.ProjectLoader;
import org.openflexo.view.controller.TechnologyAdapterControllerService;

public abstract class ApplicationContext extends FlexoServiceManager implements FlexoEditorFactory {

	private XMLSerializationService xmlSerializationService;
	private ModuleLoader moduleLoader;
	private ProjectLoader projectLoader;
	private FlexoEditor applicationEditor;
	private FlexoProjectReferenceLoader projectReferenceLoader;
	private FlexoResourceCenterService resourceCenterService;
	private TechnologyAdapterService technologyAdapterService;
	private TechnologyAdapterControllerService technologyAdapterControllerService;
	private ViewPointLibrary viewPointLibrary;

	public ApplicationContext() {
		xmlSerializationService = XMLSerializationService.createInstance();
		registerService(xmlSerializationService);
		applicationEditor = createApplicationEditor();
		try {
			projectLoader = new ProjectLoader(this);
			registerService(projectLoader);
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		moduleLoader = new ModuleLoader(this);
		registerService(moduleLoader);
		projectReferenceLoader = createProjectReferenceLoader();
		if (projectReferenceLoader != null) {
			registerService(projectReferenceLoader);
		}
		resourceCenterService = createResourceCenterService();
		registerService(resourceCenterService);
		technologyAdapterService = createTechnologyAdapterService(resourceCenterService);
		registerService(technologyAdapterService);
		technologyAdapterControllerService = createTechnologyAdapterControllerService();
		registerService(technologyAdapterControllerService);
		viewPointLibrary = createViewPointLibraryService();
		registerService(viewPointLibrary);
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

	@Override
	public final FlexoResourceCenterService getResourceCenterService() {
		return resourceCenterService;
	}

	@Override
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

	protected abstract XMLSerializationService createXMLSerializationService();

	protected abstract FlexoEditor createApplicationEditor();

	protected abstract FlexoProjectReferenceLoader createProjectReferenceLoader();

	protected abstract FlexoResourceCenterService createResourceCenterService();

	protected abstract TechnologyAdapterService createTechnologyAdapterService(FlexoResourceCenterService flexoResourceCenterService);

	protected abstract TechnologyAdapterControllerService createTechnologyAdapterControllerService();

	protected abstract ViewPointLibrary createViewPointLibraryService();

}
