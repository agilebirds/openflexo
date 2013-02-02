package org.openflexo.foundation.rm;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.logging.Logger;

import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.logging.FlexoLogger;

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
		if (getReferredProject() != null) {
			getReferredProject().getPropertyChangeSupport().removePropertyChangeListener(FlexoProject.PROJECT_DIRECTORY, this);
			getReferredProject().getPropertyChangeSupport().removePropertyChangeListener(FlexoProject.PROJECT_URI, this);
			getReferredProject().getPropertyChangeSupport().removePropertyChangeListener(FlexoProject.REVISION, this);
			getReferredProject().getPropertyChangeSupport().removePropertyChangeListener(FlexoProject.VERSION, this);
		}
		performSuperSetter(REFERRED_PROJECT, project);
		if (project != null) {
			getReferredProject().getPropertyChangeSupport().addPropertyChangeListener(FlexoProject.PROJECT_DIRECTORY, this);
			getReferredProject().getPropertyChangeSupport().addPropertyChangeListener(FlexoProject.PROJECT_URI, this);
			getReferredProject().getPropertyChangeSupport().addPropertyChangeListener(FlexoProject.REVISION, this);
			getReferredProject().getPropertyChangeSupport().addPropertyChangeListener(FlexoProject.VERSION, this);
			FlexoWorkflowResource importedWorkflowResource = getReferringProject().getImportedWorkflowResource(this, true);
			importedWorkflowResource.replaceWithWorkflow(project.getWorkflow());
			firePropertyChange(WORKFLOW, null, project.getWorkflow());
			firePropertyChange(NAME, getInternalName(), getName());
			firePropertyChange(REVISION, getInternalRevision(), getRevision());
			firePropertyChange(NAME, getInternalVersion(), getVersion());
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
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == getReferredProject()) {
			setModified(true);
		}
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

	/**
	 * @see org.openflexo.foundation.rm.FlexoProjectReference#getName()
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
	 * @see org.openflexo.foundation.rm.FlexoProjectReference#getURI()
	 */
	@Override
	public String getURI() {
		if (getReferredProject() != null) {
			return getReferredProject().getProjectURI();
		}
		return (String) performSuperGetter(URI);
	}

	/**
	 * @see org.openflexo.foundation.rm.FlexoProjectReference#getProjectVersion()
	 */
	@Override
	public String getVersion() {
		if (getReferredProject() != null) {
			return getReferredProject().getVersion();
		}
		return getInternalVersion();
	}

	private String getInternalVersion() {
		return (String) performSuperGetter(VERSION);
	}

	/**
	 * @see org.openflexo.foundation.rm.FlexoProjectReference#getProjectRevision()
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

	@Override
	public FlexoWorkflow getWorkflow() {
		if (getReferredProject() != null) {
			return getReferredProject().getWorkflow();
		} else {
			return getReferringProject().getImportedWorkflow(this);
		}
	}
}