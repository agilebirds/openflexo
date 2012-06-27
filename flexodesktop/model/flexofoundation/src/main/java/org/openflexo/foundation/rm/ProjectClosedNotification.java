package org.openflexo.foundation.rm;

import org.openflexo.foundation.DataModification;

public class ProjectClosedNotification extends DataModification {

	public ProjectClosedNotification(FlexoProject project) {
		super("close", project, project);
	}

}
