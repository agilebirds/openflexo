package org.openflexo.model.undo;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.openflexo.model.ModelEntity;
import org.openflexo.model.ModelProperty;
import org.openflexo.model.factory.CloneableProxyObject;
import org.openflexo.model.factory.ModelFactory;

/**
 * This command corresponds to the SETTING of a value of an object.<br>
 * This command addresses one property, the old value and the new value
 * 
 * @author sylvain
 * 
 * @param <I>
 *            type of object on which this adding occurs
 */
public class SetCommand<I> extends AtomicEdit<I> {

	private I updatedObject;
	private Object oldValue;
	private Object newValue;
	private ModelProperty<? super I> modelProperty;

	public SetCommand(I updatedObject, ModelEntity<I> modelEntity, ModelProperty<? super I> modelProperty, Object oldValue,
			Object newValue, ModelFactory modelFactory) {
		super(modelEntity, modelFactory);
		this.updatedObject = updatedObject;
		this.modelProperty = modelProperty;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	@Override
	public void undo() throws CannotUndoException {
		getModelFactory().getHandler(updatedObject).invokeSetter(modelProperty, oldValue);
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public void redo() throws CannotRedoException {
		getModelFactory().getHandler(updatedObject).invokeSetter(modelProperty, newValue);
	}

	@Override
	public boolean canRedo() {
		return true;
	}

	@Override
	public void die() {
		super.die();
		modelProperty = null;
		updatedObject = null;
		oldValue = null;
		newValue = null;
	}

	/**
	 * Returns true if this edit is considered significant.<br>
	 * A significant SET command is a set changing the current value for a new value. If both values are equals, edit is not signifiant. If
	 * both values equals, but declare to be {@link CloneableProxyObject}, edit is considered to be signifiant. Otherwise, the edit is
	 * signifiant
	 * 
	 * @return true if this edit is significant
	 */
	@Override
	public boolean isSignificant() {
		if (oldValue == newValue) {
			return false;
		}
		if (oldValue == null) {
			return newValue != null;
		}
		if (oldValue instanceof CloneableProxyObject) {
			return true;
		}
		return !oldValue.equals(newValue);
	}

	@Override
	public String getPresentationName() {
		return "SET " + updatedObject + " property=" + modelProperty.getPropertyIdentifier() + " oldValue=" + oldValue + " newValue="
				+ newValue;
	}

}
