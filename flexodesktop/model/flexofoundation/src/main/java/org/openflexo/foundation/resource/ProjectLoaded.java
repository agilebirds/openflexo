package org.openflexo.foundation.resource;

import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.FlexoService.ServiceNotification;

public final class ProjectLoaded implements ServiceNotification {
	private FlexoProject project;

	public ProjectLoaded(FlexoProject project) {
		this.project = project;
	}

	public FlexoProject getProject() {
		return project;
	}
}