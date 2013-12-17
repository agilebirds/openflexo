package org.openflexo.foundation.rm;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoProject;

public class ProjectClosedNotification extends DataModification {

	public static final String CLOSE = "close";

	public ProjectClosedNotification(FlexoProject project) {
		super(CLOSE, project, project);
	}

}
