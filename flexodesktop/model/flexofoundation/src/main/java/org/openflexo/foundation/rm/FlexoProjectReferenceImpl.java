package org.openflexo.foundation.rm;

import java.io.File;
import java.util.logging.Logger;

import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.IProgress;

public abstract class FlexoProjectReferenceImpl implements FlexoProjectReference {

	private static final Logger logger = FlexoLogger.getLogger(FlexoModelObjectReference.class.getPackage().getName());

	public static interface ReferenceOwner {

		public void projectDeleted(FlexoProjectReference reference);

	}

	@Override
	public void syncWithResourceData() {
		if (getReferredProject(false) != null) {
			performSuperSetter(NAME, getReferredProject().getDisplayName());
			performSuperSetter(URI, getReferredProject().getProjectURI());
			performSuperSetter(VERSION, getReferredProject().getVersion());
			performSuperSetter(REVISION, getReferredProject().getRevision());
		}
	}

	@Override
	public FlexoProject getResourceData(IProgress progress) throws ResourceLoadingCancelledException {
		return getReferredProject(false);
	}

	@Override
	public FlexoProject getReferredProject(boolean force) {
		FlexoProject project = getReferredProject();
		if (project == null && getReferringProject() != null) {
			project = getReferringProject().loadProjectReference(this, !force);
			if (project != null) {
				setReferredProject(project);
			}
		}
		return project;
	}

	@Override
	public void setReferredProject(FlexoProject project) {
		if (project != null) {
			String knownURI = (String) performSuperGetter(URI);
			if (knownURI != null && !knownURI.equals(project.getURI())) {
				throw new RuntimeException("Expecting a project with URI " + knownURI + " but received a project with URI "
						+ project.getURI());
			}
		}
		performSuperSetter(REFERRED_PROJECT, project);
		if (project != null) {
			FlexoWorkflowResource importedWorkflowResource = getReferringProject().getImportedWorkflowResource(this, true);
			importedWorkflowResource.replaceWithWorkflow(project.getWorkflow());
			firePropertyChange(WORKFLOW, null, project.getWorkflow());
		}
	}

	private void firePropertyChange(String name, Object old, Object value) {
		if (old == null && value == old) {
			return;
		}
		if (old != null && old.equals(value)) {
			return;
		}
		setModified(true);
		getPropertyChangeSupport().firePropertyChange(name, old, value);
	}

	@Override
	public void delete() {
		FlexoWorkflowResource workflowResource = getReferringProject().getImportedWorkflowResource(this);
		if (workflowResource != null) {
			workflowResource.delete();
		}
		performSuperDelete();
	}

	@Override
	public String toString() {
		return "FlexoProjectReference " + getName() + " " + getRevision() + " " + getURI();
	}

	@Override
	public File getFile() {
		if (getReferredProject() != null) {
			return getReferredProject().getProjectDirectory();
		}
		return (File) performSuperGetter(FILE);
	}

	@Override
	public Class<FlexoProject> getResourceDataClass() {
		return FlexoProject.class;
	}

	@Override
	public FlexoWorkflow getWorkflow() {
		if (getReferredProject() != null) {
			return getReferredProject().getWorkflow();
		} else {
			return getReferringProject().getImportedWorkflow(this);
		}
	}
}