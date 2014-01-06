package org.openflexo.fge.control.exceptions;

import org.openflexo.model.factory.ModelFactory;

@SuppressWarnings("serial")
public abstract class ClipboardException extends Exception {

	private ModelFactory factory;

	public ClipboardException(String message, ModelFactory factory) {
		super(message);
		this.factory = factory;
	}

	public ClipboardException(String message, Throwable cause, ModelFactory factory) {
		super(message, cause);
		this.factory = factory;
	}

}
