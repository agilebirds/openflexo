package org.openflexo.model;

import java.lang.reflect.Method;

import org.openflexo.model.exceptions.ModelDefinitionException;

/**
 * Initializer used for a method that should be called immediately after the object has been created in a deserialization phase
 * 
 * @author sylvain
 * 
 */
public class DeserializationInitializer {

	private final org.openflexo.model.annotations.DeserializationInitializer initializer;
	private final Method deserializationInitializerMethod;

	public DeserializationInitializer(org.openflexo.model.annotations.DeserializationInitializer initializer,
			Method deserializationInitializerMethod) throws ModelDefinitionException {
		this.initializer = initializer;
		this.deserializationInitializerMethod = deserializationInitializerMethod;
	}

	public Method getDeserializationInitializerMethod() {
		return deserializationInitializerMethod;
	}
}