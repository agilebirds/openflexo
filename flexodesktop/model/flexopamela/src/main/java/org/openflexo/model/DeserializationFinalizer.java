package org.openflexo.model;

import java.lang.reflect.Method;

import org.openflexo.model.exceptions.ModelDefinitionException;

/**
 * Finalizer used for a method that should be called after the whole graph of objects has been deserialized<br>
 * Order of calls of these methods just respect the order where objects were created
 * 
 * @author sylvain
 * 
 */
public class DeserializationFinalizer {

	private final org.openflexo.model.annotations.DeserializationFinalizer finalizer;
	private final Method deserializationFinalizerMethod;

	public DeserializationFinalizer(org.openflexo.model.annotations.DeserializationFinalizer finalizer,
			Method deserializationFinalizerMethod) throws ModelDefinitionException {
		this.finalizer = finalizer;
		this.deserializationFinalizerMethod = deserializationFinalizerMethod;
	}

	public Method getDeserializationFinalizerMethod() {
		return deserializationFinalizerMethod;
	}
}