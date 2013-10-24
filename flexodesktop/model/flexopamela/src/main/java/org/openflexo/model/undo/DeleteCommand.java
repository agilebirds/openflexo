package org.openflexo.model.undo;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.openflexo.model.ModelEntity;
import org.openflexo.model.factory.ModelFactory;

/**
 * This command corresponds to the DELETION of an object.<br>
 * This command addresses one model entity (type of deleted object)
 * 
 * @author sylvain
 * 
 * @param <I>
 *            type of object beeing deleted
 */
public class DeleteCommand<I> extends AtomicEdit<I> {

	private Object deletedObject;

	public DeleteCommand(I deletedObject, ModelEntity<I> modelEntity, ModelFactory modelFactory) {
		super(modelEntity, modelFactory);
		this.deletedObject = deletedObject;
	}

	@Override
	public void undo() throws CannotUndoException {
		getModelFactory().getHandler(deletedObject).invokeUndeleter();
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public void redo() throws CannotRedoException {
		getModelFactory().getHandler(deletedObject).invokeDeleter(deletedObject);
	}

	@Override
	public boolean canRedo() {
		return true;
	}

	@Override
	public void die() {
		deletedObject = null;
		super.die();
	}

	@Override
	public boolean isSignificant() {
		return true;
	}

	@Override
	public String getPresentationName() {
		return "DELETE " + deletedObject;
	}

}
