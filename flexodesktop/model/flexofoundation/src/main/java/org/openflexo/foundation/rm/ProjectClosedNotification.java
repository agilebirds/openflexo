package org.openflexo.foundation.rm;

import org.openflexo.foundation.DataModification;

public class ProjectClosedNotification extends DataModification {

	public static final String CLOSE = "close";

	public ProjectClosedNotification(FlexoProject project) {
		super(CLOSE, project, project);
	}

}
