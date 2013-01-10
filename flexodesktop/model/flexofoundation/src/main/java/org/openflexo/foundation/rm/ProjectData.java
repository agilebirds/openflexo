package org.openflexo.foundation.rm;

import java.util.List;

import org.openflexo.foundation.rm.FlexoProjectReference.ReferenceStatus;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Finder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.model.factory.AccessibleProxyObject;

@ModelEntity
@ImplementationClass(ProjectData.ProjectDataImpl.class)
@XMLElement
public interface ProjectData extends AccessibleProxyObject, PAMELAStorageResourceData {

	public static final String IMPORTED_PROJECTS = "importedProjects";

	@Finder(collection = IMPORTED_PROJECTS, attribute = FlexoProjectReference.URI, isMultiValued = false)
	public FlexoProjectReference getProjectReferenceWithURI(String uri);

	public FlexoProject getImportedProjectWithURI(String uri);

	public FlexoProject getImportedProjectWithURI(String projectURI, boolean searchRecursively);

	@Getter(value = IMPORTED_PROJECTS, cardinality = Cardinality.LIST, inverse = FlexoProjectReference.PROJECT_DATA)
	@XMLElement(xmlTag = "ImportedProjects")
	public List<FlexoProjectReference> getImportedProjects();

	@Setter(value = IMPORTED_PROJECTS)
	public void setImportedProjects(List<FlexoProjectReference> importedProjects);

	@Adder(IMPORTED_PROJECTS)
	public void addToImportedProjects(FlexoProjectReference projectReference) throws ProjectImportLoopException,
			ProjectLoadingCancelledException;

	@Remover(value = IMPORTED_PROJECTS)
	public void removeFromImportedProjects(FlexoProjectReference projectReference);

	public String canImportProject(FlexoProject project);

	public void removeFromImportedProjects(FlexoProject project);

	public static abstract class ProjectDataImpl extends PAMELAStorageResourceDataImpl implements ProjectData, AccessibleProxyObject {

		@Override
		public FlexoProject getImportedProjectWithURI(String projectURI) {
			return getImportedProjectWithURI(projectURI, true);
		}

		@Override
		public FlexoProject getImportedProjectWithURI(String projectURI, boolean searchRecursively) {
			for (FlexoProjectReference ref : getImportedProjects()) {
				if (ref.getURI().equals(projectURI)) {
					return ref.getReferredProject();

				}
			}
			if (searchRecursively) {
				for (FlexoProjectReference ref : getImportedProjects()) {
					FlexoProject projectWithURI = null;
					if (ref.getReferredProject() != null) {
						ProjectData projectData = ref.getReferredProject().getProjectData();
						if (projectData != null) {
							projectWithURI = projectData.getImportedProjectWithURI(projectURI, searchRecursively);
						}
						if (projectWithURI != null) {
							return projectWithURI;
						}
					}

				}
			}
			return null;
		}

		@Override
		public void addToImportedProjects(FlexoProjectReference projectReference) throws ProjectImportLoopException {
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
			if (!isDeserializing() && projectReference.getStatus() == ReferenceStatus.RESOLVED) {
				getProject().getFlexoRMResource().addToDependentResources(projectReference.getReferredProject().getFlexoRMResource());
			}
		}

		@Override
		public void removeFromImportedProjects(FlexoProjectReference projectReference) {
			getProject().getFlexoRMResource().removeFromDependentResources(projectReference.getReferredProject().getFlexoRMResource());
			performSuperRemover(IMPORTED_PROJECTS, projectReference);
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
				if (ref.getReferredProject() == project) {
					removeFromImportedProjects(ref);
					break;
				}
			}
		}

	}
}
