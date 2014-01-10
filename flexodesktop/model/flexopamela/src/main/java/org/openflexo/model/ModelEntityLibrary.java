package org.openflexo.model;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.openflexo.model.exceptions.ModelDefinitionException;

public class ModelEntityLibrary {

	private static Map<Class<?>, ModelEntity<?>> entities = new Hashtable<Class<?>, ModelEntity<?>>();

	private static List<ModelEntity<?>> newEntities = new ArrayList<ModelEntity<?>>();

	static synchronized <I> ModelEntity<I> importEntity(Class<I> implementedInterface) throws ModelDefinitionException {
		ModelEntity<I> modelEntity = (ModelEntity<I>) entities.get(implementedInterface);
		if (modelEntity == null) {
			modelEntity = get(implementedInterface, true);
			for (ModelEntity<?> e : newEntities) {
				e.mergeProperties();
			}
			newEntities.clear();
		}
		return modelEntity;
	}

	static <I> ModelEntity<I> get(Class<I> implementedInterface, boolean create) throws ModelDefinitionException {
		ModelEntity<I> modelEntity = (ModelEntity<I>) entities.get(implementedInterface);
		if (modelEntity == null && create) {
			if (!ModelEntity.isModelEntity(implementedInterface)) {
				throw new ModelDefinitionException("Class " + implementedInterface + " is not a ModelEntity.");
			}
			synchronized (ModelEntityLibrary.class) {
				entities.put(implementedInterface, modelEntity = new ModelEntity<I>(implementedInterface));
				modelEntity.init();
				newEntities.add(modelEntity);
			}
		}
		return modelEntity;
	}

	static <I> ModelEntity<I> get(Class<I> implementedInterface) {
		try {
			return get(implementedInterface, false);
		} catch (ModelDefinitionException e) {
			// Never happens
			return null;
		}
	}

	static boolean has(Class<?> implementedInterface) {
		return entities.containsKey(implementedInterface);
	}

	/**
	 * For testings purposes only.
	 */
	public static void clear() {
		entities.clear();
	}
}
