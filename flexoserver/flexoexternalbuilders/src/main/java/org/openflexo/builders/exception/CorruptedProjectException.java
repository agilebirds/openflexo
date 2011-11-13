package org.openflexo.builders.exception;

public class CorruptedProjectException extends FlexoRunException {

	public CorruptedProjectException() {
		super();
	}

	public CorruptedProjectException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public CorruptedProjectException(Throwable arg0) {
		super(arg0);
	}

	public CorruptedProjectException(String message) {
		super(message);
	}
}
