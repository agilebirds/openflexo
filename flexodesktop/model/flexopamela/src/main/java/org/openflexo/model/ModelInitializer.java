package org.openflexo.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.openflexo.model.annotations.Parameter;
import org.openflexo.model.exceptions.ModelDefinitionException;

public class ModelInitializer {

	private org.openflexo.model.annotations.Initializer initializer;
	private Method initializingMethod;
	// The name of each parameter in the order they appear on the initializing method
	private List<String> parameters;

	public ModelInitializer(org.openflexo.model.annotations.Initializer initializer, Method initializingMethod)
			throws ModelDefinitionException {
		this.initializer = initializer;
		this.initializingMethod = initializingMethod;
		this.parameters = new ArrayList<String>(initializingMethod.getParameterTypes().length);
		for (Annotation[] annotations : initializingMethod.getParameterAnnotations()) {
			boolean found = false;
			for (Annotation annotation : annotations) {
				if (annotation.annotationType() == Parameter.class) {
					parameters.add(((Parameter) annotation).value());
					found = true;
					break;
				}
			}
			if (!found) {
				// In case we don't find any annotation on this parameter, we add null to keep the list consistent.
				// We may imagine that an implementing class requires an additional initializing property which does not belong
				// to the model.
				parameters.add(null);
			}
		}
	}

	public List<String> getParameters() {
		return parameters;
	}

	public Method getInitializingMethod() {
		return initializingMethod;
	}
}