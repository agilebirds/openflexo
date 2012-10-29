package org.openflexo.model.exceptions;

import java.lang.reflect.InvocationTargetException;

public class ModelExecutionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5752071302025002810L;

	public ModelExecutionException(String message) {
		super(message);
	}

	public ModelExecutionException(InvocationTargetException e) {
		this(e.getTargetException());
	}

	public ModelExecutionException(String message, Throwable e) {
		super(message, e);
		e.printStackTrace();
	}

	public ModelExecutionException(Throwable e) {
		super("ModelExecutionException raised because of exception " + e.getClass().getSimpleName() + " message: " + e.getMessage());
		e.printStackTrace();
	}
}
