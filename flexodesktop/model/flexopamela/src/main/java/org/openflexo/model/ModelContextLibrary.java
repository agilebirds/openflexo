package org.openflexo.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.openflexo.model.exceptions.ModelDefinitionException;

public class ModelContextLibrary {

	private static final Map<Class<?>, ModelContext> contexts = new Hashtable<Class<?>, ModelContext>();
	private static final Map<Set<Class<?>>, ModelContext> setContexts = new Hashtable<Set<Class<?>>, ModelContext>();

	public static synchronized ModelContext getModelContext(Class<?> baseClass) throws ModelDefinitionException {
		ModelContext context = contexts.get(baseClass);
		if (context == null) {
			contexts.put(baseClass, context = new ModelContext(baseClass));
		}
		return context;
	}

	public static boolean hasContext(Class<?> baseClass) {
		return contexts.get(baseClass) != null;
	}

	public static ModelContext getCompoundModelContext(Class<?>... classes) throws ModelDefinitionException {
		if (classes.length == 1) {
			return getModelContext(classes[0]);
		}

		Set<Class<?>> set = new HashSet<Class<?>>(Arrays.asList(classes));
		ModelContext context = setContexts.get(set);
		if (context == null) {
			setContexts.put(set, context = new ModelContext(classes));
		}
		return context;
	}

	public static ModelContext getCompoundModelContext(Class<?> baseClass, Class<?>[] classes) throws ModelDefinitionException {
		Class[] newArray = new Class[classes.length + 1];
		for (int i = 0; i < classes.length; i++) {
			newArray[i] = classes[i];
		}
		newArray[classes.length] = baseClass;

		return getCompoundModelContext(newArray);
	}

}
