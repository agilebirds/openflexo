package org.openflexo.model.exceptions;

public class NoSuchEntityException extends ModelExecutionException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8640126975025764964L;
	private Class<?> undeclaredEntityClass;

	public NoSuchEntityException(Class<?> class1) {
		super("No such entity for class " + ((Class<?>) class1).getName());
		this.undeclaredEntityClass = class1;
	}

	public Class<?> getUndeclaredEntityClass() {
		return undeclaredEntityClass;
	}
}
