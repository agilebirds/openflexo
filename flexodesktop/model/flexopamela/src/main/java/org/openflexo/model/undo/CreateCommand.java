package org.openflexo.model.undo;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.openflexo.model.ModelEntity;
import org.openflexo.model.factory.ModelFactory;

/**
 * This command corresponds to the CREATION of an object.<br>
 * This command addresses one model entity (type of newly instantiated object)
 * 
 * @author sylvain
 * 
 * @param <I>
 *            type of object beeing created
 */
public class CreateCommand<I> extends AtomicEdit<I> {

	private I createdObject;

	public CreateCommand(I createdObject, ModelEntity<I> modelEntity, ModelFactory modelFactory) {
		super(modelEntity, modelFactory);
		this.createdObject = createdObject;
	}

	@Override
	public void undo() throws CannotUndoException {
		getModelFactory().getHandler(createdObject).invokeDeleter(createdObject);
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public void redo() throws CannotRedoException {
		getModelFactory().getHandler(createdObject).invokeUndeleter();
	}

	@Override
	public boolean canRedo() {
		return true;
	}

	@Override
	public void die() {
		createdObject = null;
		super.die();
	}

	@Override
	public boolean isSignificant() {
		return true;
	}

	@Override
	public String getPresentationName() {
		return "CREATE " + createdObject;
	}

}
