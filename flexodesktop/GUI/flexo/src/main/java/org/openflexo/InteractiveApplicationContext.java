package org.openflexo;

import java.io.File;

import org.openflexo.br.BugReportService;
import org.openflexo.ch.DocResourceManager;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.technologyadapter.DefaultTechnologyAdapterService;
import org.openflexo.foundation.technologyadapter.InformationSpace;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.prefs.PreferencesService;
import org.openflexo.view.controller.DefaultTechnologyAdapterControllerService;
import org.openflexo.view.controller.FullInteractiveProjectLoadingHandler;
import org.openflexo.view.controller.InteractiveFlexoEditor;
import org.openflexo.view.controller.TechnologyAdapterControllerService;

public class InteractiveApplicationContext extends ApplicationContext {
	@Override
	public FlexoEditor makeFlexoEditor(FlexoProject project, FlexoServiceManager sm) {
		return new InteractiveFlexoEditor(this, project);
	}

	@Override
	protected FlexoEditor createApplicationEditor() {
		return new InteractiveFlexoEditor(this, null);
	}

	@Override
	public ProjectLoadingHandler getProjectLoadingHandler(File projectDirectory) {
		/*if (UserType.isCustomerRelease() || UserType.isAnalystRelease()) {
			return new BasicInteractiveProjectLoadingHandler(projectDirectory);
		} else {*/
		return new FullInteractiveProjectLoadingHandler(projectDirectory);
		// }
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

	@Override
	protected InformationSpace createInformationSpace() {
		return new InformationSpace();
	}

	@Override
	protected BugReportService createBugReportService() {
		return new BugReportService();
	}

	@Override
	protected PreferencesService createPreferencesService() {
		return new PreferencesService();
	}

	@Override
	protected DocResourceManager createDocResourceManager() {
		return new DocResourceManager();
	}
}