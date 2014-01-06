package org.openflexo.model.exceptions;

/**
 * Thrown when model definition is inconsistent
 * 
 * @author sylvain
 * 
 */
@SuppressWarnings("serial")
public class ModelDefinitionException extends Exception {

	public ModelDefinitionException(String message) {
		super(message);
	}

	public ModelDefinitionException(String message, Exception cause) {
		super(message, cause);
	}
}
