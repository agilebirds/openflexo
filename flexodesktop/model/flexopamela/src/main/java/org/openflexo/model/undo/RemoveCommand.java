package org.openflexo.model.undo;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.openflexo.model.ModelEntity;
import org.openflexo.model.ModelProperty;
import org.openflexo.model.factory.ModelFactory;

/**
 * This command corresponds to the REMOVING of an object.<br>
 * This command addresses one property and one value (the value beeing removed)
 * 
 * @author sylvain
 * 
 * @param <I>
 *            type of object on which this removing occurs
 */
public class RemoveCommand<I> extends AtomicEdit<I> {

	private I updatedObject;
	private Object removedValue;
	private ModelProperty<? super I> modelProperty;

	public RemoveCommand(I updatedObject, ModelEntity<I> modelEntity, ModelProperty<? super I> modelProperty, Object removedValue,
			ModelFactory modelFactory) {
		super(modelEntity, modelFactory);
		this.updatedObject = updatedObject;
		this.modelProperty = modelProperty;
		this.removedValue = removedValue;
	}

	@Override
	public void undo() throws CannotUndoException {
		getModelFactory().getHandler(updatedObject).invokeAdder(modelProperty, removedValue);
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public void redo() throws CannotRedoException {
		getModelFactory().getHandler(updatedObject).invokeRemover(modelProperty, removedValue);
	}

	@Override
	public boolean canRedo() {
		return true;
	}

	@Override
	public void die() {
		modelProperty = null;
		updatedObject = null;
		removedValue = null;
		super.die();
	}

	@Override
	public boolean isSignificant() {
		return true;
	}

	@Override
	public String getPresentationName() {
		return "REMOVE " + updatedObject + " property=" + modelProperty.getPropertyIdentifier() + " removedValue=" + removedValue;
	}

}
