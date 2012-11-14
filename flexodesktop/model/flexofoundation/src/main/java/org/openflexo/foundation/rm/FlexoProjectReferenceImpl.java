package org.openflexo.foundation.rm;

import java.util.logging.Logger;

import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.logging.FlexoLogger;

public abstract class FlexoProjectReferenceImpl implements FlexoProjectReference {

	private static final Logger logger = FlexoLogger.getLogger(FlexoModelObjectReference.class.getPackage().getName());

	public static interface ReferenceOwner {

		public void projectDeleted(FlexoProjectReference reference);

	}

	private ReferenceStatus status = ReferenceStatus.UNRESOLVED;

	private FlexoProject referredProject;

	/**
	 * @see org.openflexo.foundation.rm.FlexoProjectReference#getStatus()
	 */
	@Override
	public ReferenceStatus getStatus() {
		return status;
	}

	@Override
	public FlexoProject getReferredProject() throws ProjectLoadingCancelledException {
		if (referredProject == null) {
			referredProject = (FlexoProject) performSuperGetter(REFERRED_PROJECT);
			if (referredProject == null && getReferringProject() != null && getProjectURI() != null && getProjectRevision() != null) {
				referredProject = getReferringProject().loadProject(this);
				if (referredProject != null) {
					status = ReferenceStatus.RESOLVED;
					getPropertyChangeSupport().firePropertyChange(STATUS, ReferenceStatus.UNRESOLVED, status);
				}
			}
		}
		return referredProject;
	}

	@Override
	public String toString() {
		return "FlexoProjectReference " + getProjectName() + " " + getProjectRevision() + " " + getProjectURI();
	}

	/**
	 * @see org.openflexo.foundation.rm.FlexoProjectReference#getProjectName()
	 */
	@Override
	public String getProjectName() {
		if (referredProject != null) {
			return referredProject.getProjectName();
		} else {
			return (String) performSuperGetter(PROJECT_NAME);
		}
	}

	/**
	 * @see org.openflexo.foundation.rm.FlexoProjectReference#getProjectURI()
	 */
	@Override
	public String getProjectURI() {
		if (referredProject != null) {
			return referredProject.getProjectURI();
		} else {
			return (String) performSuperGetter(PROJECT_URI);
		}
	}

	/**
	 * @see org.openflexo.foundation.rm.FlexoProjectReference#getProjectVersion()
	 */
	@Override
	public String getProjectVersion() {
		if (referredProject != null) {
			return referredProject.getVersion();
		} else {
			return (String) performSuperGetter(PROJECT_VERSION);
		}
	}

	/**
	 * @see org.openflexo.foundation.rm.FlexoProjectReference#getProjectRevision()
	 */
	@Override
	public Long getProjectRevision() {
		if (referredProject != null) {
			return referredProject.getRevision();
		} else {
			return (Long) performSuperGetter(PROJECT_REVISION);
		}
	}

}