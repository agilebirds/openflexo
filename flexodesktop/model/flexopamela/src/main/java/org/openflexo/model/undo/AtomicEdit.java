package org.openflexo.model.undo;

import javax.swing.undo.UndoableEdit;

import org.openflexo.model.ModelEntity;
import org.openflexo.model.factory.ModelFactory;

/**
 * This is an atomic edit managed by PAMELA.<br>
 * This edit is defined in the context of a given {@link ModelFactory} which should be passed at construction<br>
 * An {@link AtomicEdit} always addresses a {@link ModelEntity}
 * 
 * @author sylvain
 * 
 * @param <I>
 */
public abstract class AtomicEdit<I> implements UndoableEdit {

	private ModelFactory modelFactory;
	private ModelEntity<I> modelEntity;

	public AtomicEdit(ModelEntity<I> modelEntity, ModelFactory modelFactory) {
		this.modelEntity = modelEntity;
		this.modelFactory = modelFactory;
	}

	public ModelFactory getModelFactory() {
		return modelFactory;
	}

	public ModelEntity<I> getModelEntity() {
		return modelEntity;
	}

	@Override
	public final boolean addEdit(UndoableEdit anEdit) {
		// Atomic edits are not aggregable
		return false;
	}

	@Override
	public final boolean replaceEdit(UndoableEdit anEdit) {
		// Atomic edits are not aggregable
		return false;
	}

	@Override
	public void die() {
		modelEntity = null;
		modelFactory = null;
	}

	@Override
	public String getUndoPresentationName() {
		return "UNDO " + getPresentationName();
	}

	@Override
	public String getRedoPresentationName() {
		return "REDO" + getPresentationName();
	}

	@Override
	public String toString() {
		return getPresentationName();
	}
}
