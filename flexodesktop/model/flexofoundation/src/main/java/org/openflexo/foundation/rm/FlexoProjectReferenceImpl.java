package org.openflexo.foundation.rm;

import java.io.File;
import java.util.logging.Logger;

import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.logging.FlexoLogger;

public abstract class FlexoProjectReferenceImpl implements FlexoProjectReference {

	private static final Logger logger = FlexoLogger.getLogger(FlexoModelObjectReference.class.getPackage().getName());

	public static interface ReferenceOwner {

		public void projectDeleted(FlexoProjectReference reference);

	}

	private ReferenceStatus status = ReferenceStatus.UNRESOLVED;

	/**
	 * @see org.openflexo.foundation.rm.FlexoProjectReference#getStatus()
	 */
	@Override
	public ReferenceStatus getStatus() {
		return status;
	}

	@Override
	public void setReferredProject(FlexoProject project) {
		ReferenceStatus old = this.status;
		performSuperSetter(REFERRED_PROJECT, project);
		status = project != null ? ReferenceStatus.RESOLVED : ReferenceStatus.UNRESOLVED;
		getPropertyChangeSupport().firePropertyChange(STATUS, old, status);
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
			return getReferredProject().getProjectName();
		} else {
			return (String) performSuperGetter(NAME);
		}
	}

	/**
	 * @see org.openflexo.foundation.rm.FlexoProjectReference#getURI()
	 */
	@Override
	public String getURI() {
		if (getReferredProject() != null) {
			return getReferredProject().getProjectURI();
		} else {
			return (String) performSuperGetter(URI);
		}
	}

	/**
	 * @see org.openflexo.foundation.rm.FlexoProjectReference#getProjectVersion()
	 */
	@Override
	public String getVersion() {
		if (getReferredProject() != null) {
			return getReferredProject().getVersion();
		} else {
			return (String) performSuperGetter(VERSION);
		}
	}

	/**
	 * @see org.openflexo.foundation.rm.FlexoProjectReference#getProjectRevision()
	 */
	@Override
	public Long getRevision() {
		if (getReferredProject() != null) {
			return getReferredProject().getRevision();
		} else {
			return (Long) performSuperGetter(REVISION);
		}
	}

	@Override
	public File getFile() {
		if (getReferredProject() != null) {
			return getReferredProject().getProjectDirectory();
		} else {
			return (File) performSuperGetter(FILE);
		}
	}

}