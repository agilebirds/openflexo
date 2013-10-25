package org.openflexo.fge.control.exceptions;

import org.openflexo.model.factory.ModelFactory;

@SuppressWarnings("serial")
public class PasteException extends ClipboardException {

	public PasteException(String message, ModelFactory factory) {
		super(message, factory);
	}

	public PasteException(String message, Throwable cause, ModelFactory factory) {
		super(message, cause, factory);
		cause.printStackTrace();
	}

	public PasteException(Throwable cause, ModelFactory factory) {
		super("PasteException raised because of exception " + cause.getClass().getSimpleName() + " message: " + cause.getMessage(), cause,
				factory);
		cause.printStackTrace();
	}
}
