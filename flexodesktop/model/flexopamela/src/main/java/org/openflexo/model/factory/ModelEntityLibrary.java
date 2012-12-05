package org.openflexo.model.factory;

import java.util.Hashtable;
import java.util.Map;

import org.openflexo.model.exceptions.ModelDefinitionException;

public class ModelEntityLibrary {

	private static Map<Class<?>, ModelEntity<?>> entities = new Hashtable<Class<?>, ModelEntity<?>>();

	static synchronized <I> ModelEntity<I> importEntity(Class<I> implementedInterface) throws ModelDefinitionException {
		ModelEntity<I> modelEntity = (ModelEntity<I>) entities.get(implementedInterface);
		if (modelEntity == null) {
			if (!ModelEntity.isModelEntity(implementedInterface)) {
				throw new ModelDefinitionException("Class " + implementedInterface + " is not a ModelEntity.");
			}
			entities.put(implementedInterface, modelEntity = new ModelEntity<I>(implementedInterface));
			modelEntity.init();
		}
		return modelEntity;
	}

	static <I> ModelEntity<I> get(Class<I> implementedInterface) {
		return (ModelEntity<I>) entities.get(implementedInterface);
	}

	static boolean has(Class<?> implementedInterface) {
		return entities.containsKey(implementedInterface);
	}
}
