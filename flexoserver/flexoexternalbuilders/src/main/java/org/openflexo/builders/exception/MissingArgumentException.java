package org.openflexo.builders.exception;

public class MissingArgumentException extends Exception {

	private String argument;

	public MissingArgumentException(String argument) {
		this.argument = argument;
	}

	@Override
	public String getMessage() {
		if (argument != null) {
			return "The following argument '" + argument + "' is missing.";
		} else {
			return "There is a missing argument";
		}
	}

}
