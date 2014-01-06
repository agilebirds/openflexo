package org.openflexo.fge.control.exceptions;

import org.openflexo.model.factory.ModelFactory;

@SuppressWarnings("serial")
public class CopyException extends ClipboardException {

	public CopyException(String message, ModelFactory factory) {
		super(message, factory);
	}

	public CopyException(String message, Throwable cause, ModelFactory factory) {
		super(message, cause, factory);
		cause.printStackTrace();
	}

	public CopyException(Throwable cause, ModelFactory factory) {
		super("CopyException raised because of exception " + cause.getClass().getSimpleName() + " message: " + cause.getMessage(), cause,
				factory);
		cause.printStackTrace();
	}
}
