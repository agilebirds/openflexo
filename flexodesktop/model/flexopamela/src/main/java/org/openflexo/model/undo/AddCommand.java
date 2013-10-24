package org.openflexo.model.undo;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.openflexo.model.ModelEntity;
import org.openflexo.model.ModelProperty;
import org.openflexo.model.factory.ModelFactory;

/**
 * This command corresponds to the ADDING of an object.<br>
 * This command addresses one property and one value (the value beeing added)
 * 
 * @author sylvain
 * 
 * @param <I>
 *            type of object on which this adding occurs
 */
public class AddCommand<I> extends AtomicEdit<I> {

	private I updatedObject;
	private Object addedValue;
	private ModelProperty<? super I> modelProperty;

	public AddCommand(I updatedObject, ModelEntity<I> modelEntity, ModelProperty<? super I> modelProperty, Object addedValue,
			ModelFactory modelFactory) {
		super(modelEntity, modelFactory);
		this.updatedObject = updatedObject;
		this.modelProperty = modelProperty;
		this.addedValue = addedValue;
	}

	@Override
	public void undo() throws CannotUndoException {
		getModelFactory().getHandler(updatedObject).invokeRemover(modelProperty, addedValue);
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public void redo() throws CannotRedoException {
		getModelFactory().getHandler(updatedObject).invokeAdder(modelProperty, addedValue);
	}

	@Override
	public boolean canRedo() {
		return true;
	}

	@Override
	public void die() {
		modelProperty = null;
		updatedObject = null;
		addedValue = null;
		super.die();
	}

	@Override
	public boolean isSignificant() {
		return true;
	}

	@Override
	public String getPresentationName() {
		return "ADD " + updatedObject + " property=" + modelProperty.getPropertyIdentifier() + " addedValue=" + addedValue;
	}

}
