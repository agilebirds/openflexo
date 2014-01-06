package org.openflexo.model.undo;

import java.beans.PropertyChangeSupport;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import org.openflexo.model.factory.ModelFactory;
import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * A PAMELA {@link UndoManager} tracks and record PAMELA atomic edits into aggregates named compound edit.<br>
 * It provides a way to undo or redo the appropriate edits.<br>
 * 
 * To instanciate and enable an {@link UndoManager}, use the {@link ModelFactory#createUndoManager()} method.<br>
 * 
 * {@link UndoManager} internally manages a list of {@link CompoundEdit}, which are aggregates of PAMELA atomic events ({@link AtomicEdit}).<br>
 * You should use {{@link #startRecording(String)} and {{@link #stopRecording(CompoundEdit)} methods to semantically control undo/redo
 * sequences.<br>
 * 
 * The {@link UndoManager} is automatically tracking all PAMELA atomic events (see {@link AtomicEdit}). If an {@link AtomicEdit} is received
 * outside a declared recording edit, then a new edition (a {@link CompoundEdit}) is automatically instantiated. No inconsisency should be
 * raised because of unregistered edits.<br>
 * 
 * {@link UndoManager}> maintains an ordered list of edits and the index of the next edit in that list. The index of the next edit is either
 * the size of the current list of edits, or if {@link UndoManager#undo()} has been invoked it corresponds to the index of the last
 * significant edit that was undone. When {@link UndoManager#undo()} is invoked all edits from the index of the next edit to the last
 * significant edit are undone, in reverse order. <br>
 * 
 * Invoking {@link UndoManager#redo()} results in invoking {@link UndoManager#redo()} on all edits between the index of the next edit and
 * the next significant edit (or the end of the list). <br>
 * 
 * Adding an edit to an <code>UndoManager</code> results in removing all edits from the index of the next edit to the end of the list.<br>
 * 
 * TODO: WARNING: if you are dealing with custom implementation of PAMELA objects with methods involved in setter/adder/remover and declared
 * as final, those calls won't be intercepted by the UndoManager, and thus results will be of undetermined state. Please fix this.<br>
 * 
 * This class is thread safe.
 * 
 * @author sylvain
 */
@SuppressWarnings("serial")
public class UndoManager extends javax.swing.undo.UndoManager implements HasPropertyChangeSupport {

	private CompoundEdit currentEdition = null;
	private boolean undoInProgress = false;
	private boolean redoInProgress = false;

	private PropertyChangeSupport pcSupport;

	public UndoManager() {
		pcSupport = new PropertyChangeSupport(this);
	}

	/**
	 * Start a new labelled edit tracking<br>
	 * All PAMELA atomic events that will be received from now will be aggregated into newly created {@link CompoundEdit}
	 * 
	 * @param presentationName
	 * @return the newly created {@link CompoundEdit}
	 */
	public synchronized CompoundEdit startRecording(String presentationName) {
		if (currentEdition != null) {
			(new Exception("UndoManager exception: already recording")).printStackTrace();
			stopRecording(currentEdition);
		}
		currentEdition = new CompoundEdit(presentationName);
		addEdit(currentEdition);

		getPropertyChangeSupport().firePropertyChange("StartRecording", null, currentEdition);

		return currentEdition;
	}

	/**
	 * Stops supplied edit tracking<br>
	 * After this method, this edit will be available for undo/redo
	 * 
	 * @param edit
	 * @return
	 */
	public synchronized CompoundEdit stopRecording(CompoundEdit edit) {
		if (currentEdition == null) {
			(new Exception("UndoManager exception: was not recording")).printStackTrace();
			return null;
		} else if (currentEdition != edit) {
			(new Exception("UndoManager exception: was not recording this edit")).printStackTrace();
			return null;
		}

		currentEdition.end();
		// Thread.dumpStack();
		System.out.println("----------------> Stop recording " + currentEdition.getPresentationName());
		// System.out.println(currentEdition.describe());
		currentEdition = null;

		getPropertyChangeSupport().firePropertyChange("StopRecording", null, edit);

		return currentEdition;
	}

	/**
	 * Return flag indicating if {@link UndoManager} is currently recording.<br>
	 * Return true if start recording has been called
	 * 
	 * @return
	 */
	public synchronized boolean isBeeingRecording() {
		return currentEdition != null;
	}

	/**
	 * Returns true if edits may be undone.
	 * 
	 * @return true if there are edits to be undone
	 * @see CompoundEdit#canUndo
	 * @see #editToBeUndone
	 */
	@Override
	public synchronized boolean canUndo() {
		if (currentEdition != null) {
			return false;
		}
		if (undoInProgress || redoInProgress) {
			return false;
		}
		return super.canUndo();
	}

	/**
	 * Returns true if edits may be undone.<br>
	 * If en edition is in progress, return true if stopping this edition will cause UndoManager to be able to undo
	 * 
	 * 
	 * @return true if there are edits to be undone
	 * @see CompoundEdit#canUndo
	 * @see #editToBeUndone
	 */
	public synchronized boolean canUndoIfStoppingCurrentEdition() {
		if (undoInProgress || redoInProgress) {
			return false;
		}
		return super.canUndo();
	}

	/**
	 * Returns true if edits may be redone.
	 * 
	 * @return true if there are edits to be redone
	 * @see CompoundEdit#canRedo
	 * @see #editToBeRedone
	 */
	@Override
	public synchronized boolean canRedo() {
		if (currentEdition != null) {
			return false;
		}
		if (undoInProgress || redoInProgress) {
			return false;
		}
		return super.canRedo();
	}

	/**
	 * Adds an <code>UndoableEdit</code> to this {@link UndoManager}, if it's possible.
	 */
	@Override
	public synchronized boolean addEdit(UndoableEdit anEdit) {
		if (anEdit instanceof AtomicEdit) {
			if (undoInProgress) {
				// System.out.println("Ignoring " + anEdit.getPresentationName() + " because UNDO in progress");
				anEdit.die();
				return false;
			}
			if (redoInProgress) {
				// System.out.println("Ignoring " + anEdit.getPresentationName() + " because REDO in progress");
				anEdit.die();
				return false;
			}
			if (!anEdit.isSignificant()) {
				anEdit.die();
				return false;
			}
			// This is an atomic edit, therefore, i should agglomerate it in current edition
			if (currentEdition == null) {
				System.out.println("!!!!!!!!!! PAMELA edit received outside official recording. Create a default one !!!");
				startRecording("<Unidentified recording>");
			}
			// System.out.println("[PAMELA] Received: " + anEdit.getPresentationName());
			currentEdition.addEdit(anEdit);
			return true;
		} else if (anEdit instanceof CompoundEdit) {
			if (undoInProgress) {
				(new Exception("UndoManager exception: received CompoundEdit while UNDO in progress")).printStackTrace();
				return false;
			}
			if (redoInProgress) {
				(new Exception("UndoManager exception: received CompoundEdit while REDO in progress")).printStackTrace();
				return false;
			}
			return super.addEdit(anEdit);
		} else {
			System.out.println("???? Unexpected Edit " + anEdit);
			return false;
		}
	}

	/**
	 * Returns the the next significant edit to be undone if <code>undo</code> is invoked. This returns <code>null</code> if there are no
	 * edits to be undone.
	 * 
	 * @return the next significant edit to be undone
	 */
	@Override
	public CompoundEdit editToBeUndone() {
		return (CompoundEdit) super.editToBeUndone();
	}

	/**
	 * Returns the the next significant edit to be redone if <code>redo</code> is invoked. This returns <code>null</code> if there are no
	 * edits to be redone.
	 * 
	 * @return the next significant edit to be redone
	 */
	@Override
	public CompoundEdit editToBeRedone() {
		return (CompoundEdit) super.editToBeRedone();
	}

	/**
	 * Undoes the appropriate edits.<br>
	 * Invokes <code>undo</code> on all edits between the index of the next edit and the last significant edit, updating the index of the
	 * next edit appropriately.
	 * 
	 * @throws CannotUndoException
	 *             if one of the edits throws <code>CannotUndoException</code> or there are no edits to be undone
	 * @see CompoundEdit#end
	 * @see #canUndo
	 * @see #editToBeUndone
	 */
	@Override
	public synchronized void undo() throws CannotUndoException {
		try {
			// System.out.println("Will UNDO " + editToBeUndone().getPresentationName());
			// System.out.println(editToBeUndone().describe());
			// System.out.println("START UNDO " + editToBeUndone().getPresentationName());
			undoInProgress = true;
			super.undo();
			undoInProgress = false;
			getPropertyChangeSupport().firePropertyChange("undone", null, this);
			// System.out.println("END UNDO ");
		} catch (Exception e) {
			e.printStackTrace();
			undoInProgress = false;
			discardAllEdits();
			throw new CannotUndoException();
		}
	}

	/**
	 * Redoes the appropriate edits.<br>
	 * Invokes <code>redo</code> on all edits between the index of the next edit and the next significant edit, updating the index of the
	 * next edit appropriately.
	 * 
	 * @throws CannotRedoException
	 *             if one of the edits throws <code>CannotRedoException</code> or there are no edits to be redone
	 * @see CompoundEdit#end
	 * @see #canRedo
	 * @see #editToBeRedone
	 */
	@Override
	public synchronized void redo() throws CannotUndoException {
		try {
			// System.out.println("Will REDO " + editToBeRedone().getPresentationName());
			// System.out.println(editToBeUndone().describe());
			// System.out.println("START REDO " + editToBeRedone().getPresentationName());
			redoInProgress = true;
			super.redo();
			redoInProgress = false;
			getPropertyChangeSupport().firePropertyChange("redone", null, this);
			// System.out.println("END REDO ");
		} catch (Exception e) {
			e.printStackTrace();
			redoInProgress = false;
			discardAllEdits();
			throw new CannotRedoException();
		}
	}

	/**
	 * Return current edition
	 * 
	 * @return
	 */
	public CompoundEdit getCurrentEdition() {
		return currentEdition;
	}

	// Debug
	public void debug() {
		if (currentEdition != null) {
			System.out.println("UndoManager, currently recording " + currentEdition.getPresentationName());
		} else {
			System.out.println("UndoManager, currently NOT recording ");
		}
		System.out.println("Edits=" + edits.size());
		for (UndoableEdit e : edits) {
			if (e instanceof CompoundEdit) {
				System.out.println(e.getPresentationName() + " with " + ((CompoundEdit) e).getEdits().size() + " atomic edits");
			}
		}

	}

	@Override
	public String getDeletedProperty() {
		return null;
	}

	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	};

	/**
	 * Returns a description of the undoable form of this edit.
	 * 
	 * @return a description of the undoable form of this edit
	 */
	@Override
	public synchronized String getUndoPresentationName() {
		if (currentEdition != null) {
			return currentEdition.getPresentationName();
		}
		return super.getUndoPresentationName();
	}

	/**
	 * Return boolean indicating if undo is currently beeing processed
	 * 
	 * @return
	 */
	public boolean isUndoInProgress() {
		return undoInProgress;
	}

	/**
	 * Return boolean indicating if redo is currently beeing processed
	 * 
	 * @return
	 */
	public boolean isRedoInProgress() {
		return redoInProgress;
	}
}
