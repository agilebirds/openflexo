package org.openflexo.foundation;

import java.io.File;
import java.io.FileNotFoundException;

import org.openflexo.foundation.ProjectData.ProjectDataFactory;
import org.openflexo.foundation.resource.FlexoFileNotFoundException;
import org.openflexo.foundation.resource.FlexoProjectResource;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.PamelaResource;
import org.openflexo.foundation.resource.PamelaResourceImpl;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;

/**
 * This is the {@link FlexoResource} encoding a {@link ProjectData}
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(ProjectDataResource.ProjectDataResourceImpl.class)
@XMLElement
public interface ProjectDataResource extends PamelaResource<ProjectData, ProjectDataFactory>, FlexoProjectResource<ProjectData> {

	public static final String FILE_NAME = "ProjectData.xml";

	public ProjectData getProjectData();

	/**
	 * Default implementation for {@link ProjectDataResource}
	 * 
	 * 
	 * @author Sylvain
	 * 
	 */
	public static abstract class ProjectDataResourceImpl extends PamelaResourceImpl<ProjectData, ProjectDataFactory> implements
			ProjectDataResource {

		private static ProjectDataFactory PROJECT_DATA_FACTORY;

		static {
			try {
				PROJECT_DATA_FACTORY = new ProjectDataFactory();
			} catch (ModelDefinitionException e) {
				e.printStackTrace();
			}
		}

		public static ProjectDataResource makeProjectDataResource(FlexoProject project) {
			try {
				ModelFactory resourceFactory = new ModelFactory(ProjectDataResource.class);
				ProjectDataResourceImpl returned = (ProjectDataResourceImpl) resourceFactory.newInstance(ProjectDataResource.class);
				File xmlFile = new File(project.getProjectDirectory(), FILE_NAME);
				returned.setProject(project);
				returned.setFactory(PROJECT_DATA_FACTORY);
				returned.setName(project.getProjectName() + "-data");
				returned.setFile(xmlFile);
				returned.setURI(project.getURI() + "/ProjectData");
				returned.setServiceManager(project.getServiceManager());
				if (xmlFile.exists()) {
					returned.loadResourceData(null);
				} else {
					ProjectData newProjectData = returned.getFactory().newInstance(ProjectData.class);
					returned.setResourceData(newProjectData);
				}
				return returned;
			} catch (ModelDefinitionException e) {
				e.printStackTrace();
			} catch (FlexoFileNotFoundException e) {
				e.printStackTrace();
			} catch (IOFlexoException e) {
				e.printStackTrace();
			} catch (InvalidXMLException e) {
				e.printStackTrace();
			} catch (InconsistentDataException e) {
				e.printStackTrace();
			} catch (InvalidModelDefinitionException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public ProjectData getProjectData() {
			try {
				return getResourceData(null);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (ResourceLoadingCancelledException e) {
				e.printStackTrace();
			} catch (FlexoException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
