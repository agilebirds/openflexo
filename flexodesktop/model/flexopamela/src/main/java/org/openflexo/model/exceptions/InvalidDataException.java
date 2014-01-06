package org.openflexo.model.exceptions;

/**
 * Thrown when managed data is not consistent with model definition
 * 
 * @author sylvain
 * 
 */
@SuppressWarnings("serial")
public class InvalidDataException extends Exception {

	public InvalidDataException(String message) {
		super(message);
	}
}
