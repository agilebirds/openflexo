package org.openflexo.foundation;

import java.io.FileNotFoundException;

import org.openflexo.foundation.resource.FlexoFileResource;
import org.openflexo.foundation.resource.FlexoFileResourceImpl;
import org.openflexo.foundation.resource.FlexoProjectResource;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;

/**
 * A {@link ProjectDirectoryResource} is the resource denoting the {@link FlexoProject} directory on disk
 * 
 * @param <RD>
 *            the type of the resource data reference by this resource
 * @author sylvain
 * 
 */
@ModelEntity
public interface ProjectDirectoryResource extends FlexoProjectResource<FlexoProject>, FlexoFileResource<FlexoProject> {

	/**
	 * Default implementation for {@link ProjectDataResource}
	 * 
	 * 
	 * @author Sylvain
	 * 
	 */
	public static abstract class ProjectDirectoryResourceImpl extends FlexoFileResourceImpl<FlexoProject> implements
			ProjectDirectoryResource {

		public static ProjectDirectoryResource makeProjectDirectoryResource(FlexoProject project) {
			try {
				ModelFactory resourceFactory = new ModelFactory(ProjectDirectoryResource.class);
				ProjectDirectoryResource returned = resourceFactory.newInstance(ProjectDirectoryResource.class);
				returned.setProject(project);
				returned.setName(project.getProjectName());
				returned.setFile(project.getProjectDirectory());
				returned.setURI(project.getURI());
				returned.setServiceManager(project.getServiceManager());
				if (!returned.getFile().exists()) {
					returned.getFile().mkdirs();
				}
				returned.setResourceData(project);
				return returned;
			} catch (ModelDefinitionException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public FlexoProject getProject() {
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
