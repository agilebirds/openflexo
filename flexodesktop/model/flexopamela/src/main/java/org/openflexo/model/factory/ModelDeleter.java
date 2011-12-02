package org.openflexo.model.factory;

import java.lang.reflect.Method;

import org.openflexo.model.annotations.Deleter;

public class ModelDeleter<I> {

	private ModelEntity<I> modelEntity;

	private Deleter deleter;
	private Method deleterMethod;

	private ModelDeleter<? super I> superDeleter = null;

	protected ModelDeleter(ModelEntity<I> modelEntity, Method deleterMethod) throws ModelDefinitionException {
		this.modelEntity = modelEntity;
		this.deleterMethod = deleterMethod;
		deleter = deleterMethod.getAnnotation(Deleter.class);
		if (deleter == null) {
			throw new IllegalArgumentException("Method " + deleterMethod + " is not annotated with " + Deleter.class.getName());
		}
		ModelEntity<? super I> superEntity = getModelEntity().getSuperEntity();
		while (superEntity != null) {
			if (superEntity.declaresModelDeleter()) {
				superDeleter = superEntity.getDeclaredDeleter();
				break;
			}
			superEntity = superEntity.getSuperEntity();
		}
	}

	public ModelFactory getModelFactory() {
		return getModelEntity().getModelFactory();
	}

	public ModelEntity<I> getModelEntity() {
		return modelEntity;
	}

	public Class<I> getImplementedInterface() {
		return getModelEntity().getImplementedInterface();
	}

	public Deleter getDeleter() {
		if (deleter == null && override()) {
			return superDeleter.getDeleter();
		}
		return deleter;
	}

	public Method getDeleterMethod() {
		if (deleterMethod == null && override()) {
			return superDeleter.getDeleterMethod();
		}
		return deleterMethod;
	}

	@Override
	public String toString() {
		return "ModelDeleter[" + getModelEntity().getImplementedInterface().getSimpleName() + "." + deleterMethod.getName() + "]";
	}

	public ModelDeleter<? super I> getSuperDeleter() {
		return superDeleter;
	}

	public boolean override() {
		return superDeleter != null;
	}

}
