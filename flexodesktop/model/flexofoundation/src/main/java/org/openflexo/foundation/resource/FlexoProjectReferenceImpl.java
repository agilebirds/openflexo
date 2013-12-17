package org.openflexo.foundation.resource;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FlexoVersion;

public abstract class FlexoProjectReferenceImpl implements FlexoProjectReference, PropertyChangeListener {

	private static final Logger logger = FlexoLogger.getLogger(FlexoModelObjectReference.class.getPackage().getName());

	public static interface ReferenceOwner {

		public void projectDeleted(FlexoProjectReference reference);

	}

	@Override
	public FlexoProject getReferredProject(boolean force) {
		FlexoProject project = getInternalReferredProject();
		if (project == null && getReferringProject() != null) {
			project = getReferringProject().loadProjectReference(this, !force);
			if (project != null) {
				setReferredProject(project);
			}
		}
		return project;
	}

	private FlexoProject getInternalReferredProject() {
		return (FlexoProject) performSuperGetter(REFERRED_PROJECT);
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
		/*if (project != null) {
			FlexoWorkflowResource importedWorkflowResource = getReferringProject().getImportedWorkflowResource(this, true);
			importedWorkflowResource.replaceWithWorkflow(project.getWorkflow());
			getPropertyChangeSupport().firePropertyChange("workflow", null, project.getWorkflow());
		}*/
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
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == getReferredProject()) {
			setModified(true);
		}
	}

	@Override
	public boolean delete() {
		// FlexoWorkflowResource workflowResource = getReferringProject().getImportedWorkflowResource(this);
		/*if (workflowResource != null) {
			workflowResource.delete();
		}*/
		return performSuperDelete();
	}

	@Override
	public String toString() {
		return "FlexoProjectReference " + getName() + " " + getRevision() + " " + getURI();
	}

	/**
	 * @see org.openflexo.foundation.resource.FlexoProjectReference#getName()
	 */
	@Override
	public String getName() {
		if (getReferredProject() != null) {
			return getReferredProject().getDisplayName();
		}
		return getInternalName();
	}

	private String getInternalName() {
		return (String) performSuperGetter(NAME);
	}

	/**
	 * @see org.openflexo.foundation.resource.FlexoProjectReference#getURI()
	 */
	@Override
	public String getURI() {
		if (getReferredProject() != null) {
			return getReferredProject().getProjectURI();
		}
		return (String) performSuperGetter(URI);
	}

	/**
	 * @see org.openflexo.foundation.resource.FlexoProjectReference#getProjectVersion()
	 */
	@Override
	public FlexoVersion getVersion() {
		if (getReferredProject() != null) {
			return getReferredProject().getVersion();
		}
		return (FlexoVersion) performSuperGetter(VERSION);
	}

	/**
	 * @see org.openflexo.foundation.resource.FlexoProjectReference#getProjectRevision()
	 */
	@Override
	public Long getRevision() {
		if (getReferredProject() != null) {
			return getReferredProject().getRevision();
		}
		return getInternalRevision();
	}

	private Long getInternalRevision() {
		return (Long) performSuperGetter(REVISION);
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

	/*@Override
	public FlexoWorkflow getWorkflow() {
		if (getReferredProject() != null) {
			return getReferredProject().getWorkflow();
		} else {
			return getReferringProject().getImportedWorkflow(this);
		}
	}*/
}
