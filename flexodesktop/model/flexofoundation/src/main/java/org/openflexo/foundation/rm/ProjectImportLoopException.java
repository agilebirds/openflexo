package org.openflexo.foundation.rm;

import org.openflexo.foundation.FlexoException;

public class ProjectImportLoopException extends FlexoException {

	public ProjectImportLoopException(String message) {
		super(message);
	}

	@Override
	public String getLocalizedMessage() {
		return super.getMessage();
	}

}
