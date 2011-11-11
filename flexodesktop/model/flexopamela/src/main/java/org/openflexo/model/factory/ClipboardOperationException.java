package org.openflexo.model.factory;

import java.lang.reflect.InvocationTargetException;

public class ClipboardOperationException extends RuntimeException {

	public ClipboardOperationException(String message) {
		super(message);
	}

	public ClipboardOperationException(InvocationTargetException e) {
		this(e.getTargetException());
	}

	public ClipboardOperationException(Throwable e) {
		super("ClipboardOperationException raised because of exception " + e.getClass().getSimpleName() + " message: " + e.getMessage());
		e.printStackTrace();
	}
}
