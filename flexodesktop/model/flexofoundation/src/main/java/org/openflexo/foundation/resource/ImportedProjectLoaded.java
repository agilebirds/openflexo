package org.openflexo.foundation.resource;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoProject;

public class ImportedProjectLoaded extends DataModification {

	public ImportedProjectLoaded(FlexoProject project) {
		super(null, project);
	}

	public FlexoProject getProject() {
		return (FlexoProject) newValue();
	}

}
