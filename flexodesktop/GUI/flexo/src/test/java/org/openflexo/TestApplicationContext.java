package org.openflexo;

import java.io.File;

import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.resource.DefaultResourceCenterService;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.resource.DirectoryResourceCenter;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProject.FlexoProjectReferenceLoader;
import org.openflexo.foundation.technologyadapter.DefaultTechnologyAdapterService;
import org.openflexo.foundation.technologyadapter.InformationSpace;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.foundation.xml.XMLSerializationService;
import org.openflexo.view.controller.DefaultTechnologyAdapterControllerService;
import org.openflexo.view.controller.TechnologyAdapterControllerService;

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
		super();
		this.resourceCenterDirectory = resourceCenterDirectory;
		// getResourceCenterService().addToResourceCenters(new UserResourceCenter(resourceCenterDirectory));
		getResourceCenterService().addToResourceCenters(new DirectoryResourceCenter(resourceCenterDirectory));
	}

	@Override
	protected XMLSerializationService createXMLSerializationService() {
		return XMLSerializationService.createInstance();
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
		return DefaultResourceCenterService.getNewInstance();
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

}
