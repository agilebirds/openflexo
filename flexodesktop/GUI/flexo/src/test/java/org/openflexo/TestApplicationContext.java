package org.openflexo;

import java.io.File;

import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.resource.UserResourceCenter;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProject.FlexoProjectReferenceLoader;
import org.openflexo.foundation.technologyadapter.DefaultTechnologyAdapterService;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.foundation.utils.ProjectLoadingHandler;

/**
 * Test purposes: implements an ApplicationContext with a unique ResourceCenter
 * 
 * @author sylvain
 * 
 */
public class TestApplicationContext extends ApplicationContext {

	private File resourceCenterDirectory;

	public static class FlexoTestEditor extends DefaultFlexoEditor {
		public FlexoTestEditor(FlexoProject project) {
			super(project);
		}

	}

	public TestApplicationContext(File resourceCenterDirectory) {
		this.resourceCenterDirectory = resourceCenterDirectory;
		FlexoResourceCenter testResourceCenter = new UserResourceCenter(resourceCenterDirectory);
		getResourceCenterService().addToResourceCenters(testResourceCenter);
		getResourceCenterService().registerTechnologyAdapterService(getTechnologyAdapterService());
	}

	@Override
	public FlexoEditor makeFlexoEditor(FlexoProject project) {
		return new FlexoTestEditor(project);
	}

	@Override
	protected FlexoProjectReferenceLoader createProjectReferenceLoader() {
		// return new InteractiveFlexoProjectReferenceLoader(this);
		return null;
	}

	@Override
	protected FlexoEditor createApplicationEditor() {
		return new FlexoTestEditor(null);
	}

	@Override
	protected FlexoResourceCenterService createResourceCenterService() {
		return TestResourceCenterService.getNewInstance();
	}

	@Override
	public ProjectLoadingHandler getProjectLoadingHandler(File projectDirectory) {
		/*if (UserType.isCustomerRelease() || UserType.isAnalystRelease()) {
			return new BasicInteractiveProjectLoadingHandler(projectDirectory);
		} else {
			return new FullInteractiveProjectLoadingHandler(projectDirectory);
		}*/
		return null;
	}

	@Override
	protected TechnologyAdapterService createTechnologyAdapterService(FlexoResourceCenterService resourceCenterService) {
		TechnologyAdapterService returned = DefaultTechnologyAdapterService.getNewInstance();
		returned.setFlexoResourceCenterService(resourceCenterService);
		returned.loadAvailableTechnologyAdapters();
		return returned;
	}

}
