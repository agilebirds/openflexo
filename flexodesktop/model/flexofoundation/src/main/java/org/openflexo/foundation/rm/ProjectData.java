package org.openflexo.foundation.rm;

import java.util.List;

import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Finder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.ReturnedValue;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.model.factory.AccessibleProxyObject;

@ModelEntity
@ImplementationClass(ProjectData.ProjectDataImpl.class)
@XMLElement
public interface ProjectData extends StorageResourceData, AccessibleProxyObject {

	public static final String FLEXO_RESOURCE = "flexoResource";
	public static final String PROJECT = "project";
	public static final String IMPORTED_PROJECTS = "importedProjects";

	@Override
	@Getter(value = FLEXO_RESOURCE, ignoreType = true)
	public FlexoStorageResource getFlexoResource();

	@Setter(FLEXO_RESOURCE)
	public void setFlexoStorageResource(FlexoStorageResource resource) throws DuplicateResourceException;

	@Finder(collection = IMPORTED_PROJECTS, attribute = FlexoProjectReference.PROJECT_URI, isMultiValued = false)
	public FlexoProjectReference getProjectReferenceWithURI(String uri);

	public FlexoProject getImportedProjectWithURI(String uri);

	public FlexoProject getImportedProjectWithURI(String projectURI, boolean searchRecursively);

	@Override
	public void setFlexoResource(FlexoResource resource) throws DuplicateResourceException;

	@Override
	@Getter(value = PROJECT, ignoreType = true)
	@ReturnedValue(FLEXO_RESOURCE + ".project")
	public FlexoProject getProject();

	@Override
	public void setProject(FlexoProject aProject);

	@Getter(value = IMPORTED_PROJECTS, cardinality = Cardinality.LIST, inverse = FlexoProjectReference.PROJECT_DATA)
	@XMLElement(xmlTag = "ImportedProjects")
	public List<FlexoProjectReference> getImportedProjects();

	@Setter(value = IMPORTED_PROJECTS)
	public void setImportedProjects(List<FlexoProjectReference> importedProjects);

	public void addToImportedProjects(FlexoProject project) throws ProjectImportLoopException, ProjectLoadingCancelledException;

	@Adder(IMPORTED_PROJECTS)
	public void addToImportedProjects(FlexoProjectReference projectReference) throws ProjectImportLoopException,
			ProjectLoadingCancelledException;

	@Remover(value = IMPORTED_PROJECTS)
	public void removeFromImportedProjects(FlexoProjectReference projectReference);

	public String canImportProject(FlexoProject project);

	public void removeFromImportedProjects(FlexoProject project);

	public static abstract class ProjectDataImpl implements ProjectData, AccessibleProxyObject {

		@Override
		public FlexoProject getImportedProjectWithURI(String projectURI) {
			return getImportedProjectWithURI(projectURI, true);
		}

		@Override
		public FlexoProject getProject() {
			return getFlexoResource().getProject();
		}

		@Override
		public void setProject(FlexoProject aProject) {

		}

		@Override
		public void setFlexoResource(FlexoResource resource) throws DuplicateResourceException {
			setFlexoStorageResource((FlexoStorageResource) resource);
		}

		@Override
		public FlexoProject getImportedProjectWithURI(String projectURI, boolean searchRecursively) {
			for (FlexoProjectReference ref : getImportedProjects()) {
				if (ref.getProjectURI().equals(projectURI)) {
					try {
						return ref.getReferredProject();
					} catch (ProjectLoadingCancelledException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			if (searchRecursively) {
				for (FlexoProjectReference ref : getImportedProjects()) {
					FlexoProject projectWithURI = null;
					try {
						ProjectData projectData = ref.getReferredProject().getProjectData();
						if (projectData != null) {
							projectWithURI = projectData.getImportedProjectWithURI(projectURI, searchRecursively);
						}
						if (projectWithURI != null) {
							return projectWithURI;
						}
					} catch (ProjectLoadingCancelledException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			return null;
		}

		@Override
		public void addToImportedProjects(FlexoProject project) throws ProjectImportLoopException, ProjectLoadingCancelledException {
			FlexoProjectReference projectReference = getModelFactory().newInstance(FlexoProjectReference.class).init(project);
			addToImportedProjects(projectReference);
		}

		@Override
		public void addToImportedProjects(FlexoProjectReference projectReference) throws ProjectImportLoopException,
				ProjectLoadingCancelledException {
			if (!isDeserializing()) {
				if (getImportedProjects().contains(projectReference)) {
					return;
				}
				String reason = canImportProject(projectReference.getReferredProject());
				if (reason != null) {
					throw new ProjectImportLoopException(reason);
				}
			}
			performSuperAdder(IMPORTED_PROJECTS, projectReference);
		}

		@Override
		public String canImportProject(FlexoProject project) {
			if (project.getProjectURI().equals(getProject().getProjectURI())) {
				return FlexoLocalization.localizedForKey("cannot_import_itself");
			}
			if (getImportedProjectWithURI(project.getProjectURI(), false) != null) {
				return FlexoLocalization.localizedForKey("project_already_imported");
			}
			if (getImportedProjectWithURI(getProject().getProjectURI()) != null) {
				return FlexoLocalization.localizedForKey("imported_project_depends_of_this_project");
			}
			return null;
		}

		@Override
		public void removeFromImportedProjects(FlexoProject project) {
			for (FlexoProjectReference ref : getImportedProjects()) {
				try {
					if (ref.getReferredProject() == project) {
						removeFromImportedProjects(ref);
						break;
					}
				} catch (ProjectLoadingCancelledException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void setModified(boolean modified) {
			boolean old = isModified();
			performSuperSetModified(modified);
			if (modified && !old) {
				getFlexoResource().notifyResourceStatusChanged();
			}
		}

	}
}
