package org.openflexo.foundation.resource;

import org.openflexo.foundation.FlexoService.ServiceNotification;
import org.openflexo.foundation.rm.FlexoProject;

public final class ProjectLoaded implements ServiceNotification {
	private FlexoProject project;

	public ProjectLoaded(FlexoProject project) {
		this.project = project;
	}

	public FlexoProject getProject() {
		return project;
	}
}