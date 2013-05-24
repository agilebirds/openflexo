package org.openflexo.foundation.rm;

import org.openflexo.foundation.DataModification;

public class ImportedProjectLoaded extends DataModification {

	public ImportedProjectLoaded(FlexoProject project) {
		super(null, project);
	}

	public FlexoProject getProject() {
		return (FlexoProject) newValue();
	}

}
