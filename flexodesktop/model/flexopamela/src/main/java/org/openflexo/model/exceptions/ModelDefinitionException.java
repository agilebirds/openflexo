package org.openflexo.model.exceptions;

public class ModelDefinitionException extends Exception {

	public ModelDefinitionException(String message) {
		super(message);
	}

	public ModelDefinitionException(String message, Exception cause) {
		super(message, cause);
	}
}
