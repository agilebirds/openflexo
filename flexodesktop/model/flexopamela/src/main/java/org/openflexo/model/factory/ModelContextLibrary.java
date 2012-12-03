package org.openflexo.model.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.openflexo.model.exceptions.ModelDefinitionException;

public class ModelContextLibrary {

	private static final Map<Class<?>, ModelContext> contexts = new Hashtable<Class<?>, ModelContext>();

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

	public static List<ModelContext> getModelContext(List<Class<?>> classes) throws ModelDefinitionException {
		if (classes.size() == 1) {
			return Arrays.asList(getModelContext(classes.get(0)));
		}
		List<ModelContext> contexts = new ArrayList<ModelContext>(classes.size());
		for (Class<?> klass : classes) {
			contexts.add(getModelContext(klass));
		}
		return contexts;
	}
}
