package org.openflexo.model.exceptions;

import org.openflexo.model.ModelEntity;

public class UnitializedEntityException extends ModelExecutionException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6531283676805700217L;
	private ModelEntity<?> modelEntity;

	public UnitializedEntityException(ModelEntity<?> modelEntity) {
		super("Uninitialized object. Entity " + modelEntity + " must be initialized!");
		this.modelEntity = modelEntity;
	}

	public ModelEntity<?> getModelEntity() {
		return modelEntity;
	}

}
