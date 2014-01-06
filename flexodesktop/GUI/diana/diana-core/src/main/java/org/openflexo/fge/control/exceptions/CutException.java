package org.openflexo.fge.control.exceptions;

import org.openflexo.model.factory.ModelFactory;

@SuppressWarnings("serial")
public class CutException extends ClipboardException {

	public CutException(String message, ModelFactory factory) {
		super(message, factory);
	}

	public CutException(String message, Throwable cause, ModelFactory factory) {
		super(message, cause, factory);
		cause.printStackTrace();
	}

	public CutException(Throwable cause, ModelFactory factory) {
		super("CutException raised because of exception " + cause.getClass().getSimpleName() + " message: " + cause.getMessage(), cause,
				factory);
		cause.printStackTrace();
	}
}
