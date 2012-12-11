package org.openflexo;

import java.io.File;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.resource.DefaultResourceCenterService;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProject.FlexoProjectReferenceLoader;
import org.openflexo.foundation.technologyadapter.DefaultTechnologyAdapterService;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.module.InteractiveFlexoProjectReferenceLoader;
import org.openflexo.module.UserType;
import org.openflexo.view.controller.BasicInteractiveProjectLoadingHandler;
import org.openflexo.view.controller.DefaultTechnologyAdapterControllerService;
import org.openflexo.view.controller.FullInteractiveProjectLoadingHandler;
import org.openflexo.view.controller.InteractiveFlexoEditor;
import org.openflexo.view.controller.TechnologyAdapterControllerService;

public class InteractiveApplicationContext extends ApplicationContext {
	@Override
	public FlexoEditor makeFlexoEditor(FlexoProject project) {
		return new InteractiveFlexoEditor(this, project);
	}

	@Override
	protected FlexoProjectReferenceLoader createProjectReferenceLoader() {
		return new InteractiveFlexoProjectReferenceLoader(this);
	}

	@Override
	protected FlexoEditor createApplicationEditor() {
		return new InteractiveFlexoEditor(this, null);
	}

	@Override
	protected FlexoResourceCenterService createResourceCenterService() {
		return DefaultResourceCenterService.getNewInstance(GeneralPreferences.getLocalResourceCenterDirectory());
	}

	@Override
	public ProjectLoadingHandler getProjectLoadingHandler(File projectDirectory) {
		if (UserType.isCustomerRelease() || UserType.isAnalystRelease()) {
			return new BasicInteractiveProjectLoadingHandler(projectDirectory);
		} else {
			return new FullInteractiveProjectLoadingHandler(projectDirectory);
		}
	}

	@Override
	protected TechnologyAdapterService createTechnologyAdapterService(FlexoResourceCenterService resourceCenterService) {
		return DefaultTechnologyAdapterService.getNewInstance(resourceCenterService);
	}

	@Override
	protected TechnologyAdapterControllerService createTechnologyAdapterControllerService() {
		return DefaultTechnologyAdapterControllerService.getNewInstance();
	}

	@Override
	protected ViewPointLibrary createViewPointLibraryService() {
		return new ViewPointLibrary();
	}
}