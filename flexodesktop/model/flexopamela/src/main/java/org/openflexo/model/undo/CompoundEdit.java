package org.openflexo.model.undo;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

/**
 * A concrete subclass of {@link AbstractUndoableEdit}, used to assemble little PAMELA {@link AtomicEdit} into a labelled
 * {@link CompoundEdit}.<br>
 * 
 * This class is used to aggregates all atomic PAMELA events in an explicit controlled edit.
 * 
 * A {@link CompoundEdit} is labelled, asserting an explicit recording has been started on {@link UndoManager}. If an {@link AtomicEdit} is
 * received in {@link UndoManager} outside a declared recording {@link CompoundEdit}, then a new edition (a {@link CompoundEdit}) is
 * automatically instantiated. No inconsisency should be raised because of unregistered edits.<br>
 * 
 * Partially inspired from Ray Ryan's swing implementation
 * 
 * @author sylvain
 */
@SuppressWarnings("serial")
public class CompoundEdit extends AbstractUndoableEdit {

	private String presentationName;

	/**
	 * True if this edit has never received <code>end</code>.
	 */
	boolean inProgress;

	/**
	 * The collection of <code>UndoableEdit</code>s undone/redone en masse by this <code>CompoundEdit</code>.
	 */
	protected Vector<AtomicEdit<?>> edits;

	protected CompoundEdit(String presentationName) {
		super();
		this.presentationName = presentationName;
		inProgress = true;
		edits = new Vector<AtomicEdit<?>>();
	}

	/**
	 * Sends <code>undo</code> to all contained <code>AtomicEdit</code> in the reverse of the order in which they were added.
	 */
	public void undo() throws CannotUndoException {
		super.undo();
		int i = edits.size();
		while (i-- > 0) {
			AtomicEdit<?> e = (AtomicEdit<?>) edits.elementAt(i);
			// System.out.println("> UNDO AtomicEdit " + e.getPresentationName());
			e.undo();
		}
	}

	/**
	 * Sends <code>redo</code> to all contained <code>AtomicEdit</code>s in the order in which they were added.
	 */
	public void redo() throws CannotRedoException {
		super.redo();
		Enumeration<AtomicEdit<?>> cursor = edits.elements();
		while (cursor.hasMoreElements()) {
			AtomicEdit<?> e = ((AtomicEdit<?>) cursor.nextElement());
			// System.out.println("> REDO AtomicEdit " + e.getPresentationName());
			e.redo();
		}
	}

	/**
	 * Returns the last <code>AtomicEdit</code> in <code>edits</code>, or <code>null</code> if <code>edits</code> is empty.
	 */
	protected AtomicEdit<?> lastEdit() {
		int count = edits.size();
		if (count > 0)
			return (AtomicEdit<?>) edits.elementAt(count - 1);
		else
			return null;
	}

	/**
	 * Sends <code>die</code> to each subedit, in the reverse of the order that they were added.
	 */
	public void die() {
		int size = edits.size();
		for (int i = size - 1; i >= 0; i--) {
			AtomicEdit<?> e = (AtomicEdit<?>) edits.elementAt(i);
			// System.out.println("CompoundEdit(" + i + "): Discarding " +
			// e.getUndoPresentationName());
			e.die();
		}
		super.die();
	}

	/**
	 * If this edit is <code>inProgress</code>, accepts <code>anEdit</code> and returns true.
	 * 
	 * <p>
	 * The last edit added to this <code>CompoundEdit</code> is given a chance to <code>addEdit(anEdit)</code>. If it refuses (returns
	 * false), <code>anEdit</code> is given a chance to <code>replaceEdit</code> the last edit. If <code>anEdit</code> returns false here,
	 * it is added to <code>edits</code>.
	 * 
	 * @param anEdit
	 *            the edit to be added
	 * @return true if the edit is <code>inProgress</code>; otherwise returns false
	 */
	public boolean addEdit(UndoableEdit anEdit) {
		if (anEdit instanceof AtomicEdit) {
			if (!inProgress) {
				return false;
			} else {
				UndoableEdit last = lastEdit();

				// If this is the first subedit received, just add it.
				// Otherwise, give the last one a chance to absorb the new
				// one. If it won't, give the new one a chance to absorb
				// the last one.

				if (last == null) {
					edits.addElement((AtomicEdit<?>) anEdit);
				} else if (!last.addEdit(anEdit)) {
					if (anEdit.replaceEdit(last)) {
						edits.removeElementAt(edits.size() - 1);
					}
					edits.addElement((AtomicEdit<?>) anEdit);
				}

				return true;
			}
		}
		return false;
	}

	/**
	 * Return a list storing all atomic edits aggregated in this compound edit
	 * 
	 * @return
	 */
	public List<AtomicEdit<?>> getEdits() {
		return edits;
	}

	/**
	 * Sets <code>inProgress</code> to false.
	 * 
	 * @see #canUndo
	 * @see #canRedo
	 */
	public void end() {
		inProgress = false;
	}

	/**
	 * Returns false if <code>isInProgress</code> or if super returns false.
	 * 
	 * @see #isInProgress
	 */
	public boolean canUndo() {
		return !isInProgress() && super.canUndo();
	}

	/**
	 * Returns false if <code>isInProgress</code> or if super returns false.
	 * 
	 * @see #isInProgress
	 */
	public boolean canRedo() {
		return !isInProgress() && super.canRedo();
	}

	/**
	 * Returns true if this edit is in progress--that is, it has not received end. This generally means that edits are still being added to
	 * it.
	 * 
	 * @see #end
	 */
	public boolean isInProgress() {
		return inProgress;
	}

	/**
	 * Returns true if any of the <code>AtomicEdit</code>s in <code>edits</code> do. Returns false if they all return false.
	 */
	public boolean isSignificant() {
		Enumeration<AtomicEdit<?>> cursor = edits.elements();
		while (cursor.hasMoreElements()) {
			if (((AtomicEdit<?>) cursor.nextElement()).isSignificant()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns <code>getPresentationName</code>
	 */
	public String getPresentationName() {
		return presentationName;
	}

	@Override
	public String getUndoPresentationName() {
		return "Undo " + getPresentationName();
	}

	@Override
	public String getRedoPresentationName() {
		return "Redo " + getPresentationName();
	}

	/**
	 * Returns a string that displays and identifies this object's properties.
	 * 
	 * @return a String representation of this object
	 */
	public String toString() {
		return getPresentationName() + " inProgress: " + inProgress + " edits: " + edits;
	}

	public String describe() {
		StringBuffer sb = new StringBuffer();
		sb.append(toString() + "\n");
		for (AtomicEdit<?> edit : getEdits()) {
			sb.append(" > " + edit.getPresentationName() + "\n");
		}
		return sb.toString();
	}
}