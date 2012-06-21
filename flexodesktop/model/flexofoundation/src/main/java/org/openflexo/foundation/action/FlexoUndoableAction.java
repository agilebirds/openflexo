/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.foundation.action;

import java.util.Iterator;
import java.util.Vector;
import java.util.WeakHashMap;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoStorageResource;
import org.openflexo.foundation.rm.StorageResourceData;

/**
 * 
 * Abstract representation of an action on Flexo model (model edition primitive) which can be undone and redone
 * 
 * @author sguerin
 */
public abstract class FlexoUndoableAction<A extends FlexoUndoableAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject>
		extends FlexoAction<A, T1, T2> {
	private WeakHashMap<FlexoStorageResource<?>, Object> _modifiedResources;

	public FlexoUndoableAction(FlexoActionType<A, T1, T2> actionType, T1 focusedObject, Vector<T2> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		_modifiedResources = new WeakHashMap<FlexoStorageResource<?>, Object>();
	}

	protected FlexoProject getProject() {
		if (getEditor() != null) {
			return getEditor().getProject();
		}
		return null;
	}

	/**
	 * Overrides doAction
	 * 
	 * @see org.openflexo.foundation.action.FlexoAction#doAction(java.awt.event.ActionEvent)
	 */
	@Override
	public final A doActionInContext() throws FlexoException {
		// Let's keep in memory the modified resources
		_modifiedResources.clear();
		if (getProject() != null) {
			for (FlexoStorageResource<? extends StorageResourceData> r : getProject().getStorageResources()) {
				if (!r.isModified()) {
					_modifiedResources.put(r, null);
				}
			}
		}
		A action;
		try {
			action = super.doActionInContext();
		} catch (FlexoException e1) {
			_modifiedResources.clear();
			throw e1;
		}

		// Now we remove all the resources that are still not modified. The left delta are the resources that have been directly modified by
		// this action (and the embedded ones)
		if (getProject() != null) {
			for (FlexoStorageResource<? extends StorageResourceData> r : getProject().getStorageResources()) {
				if (!r.isModified()) {
					_modifiedResources.remove(r);
				}
			}
		}

		return action;
	}

	public final A undoActionInContext() throws FlexoException {
		// Let's keep in memory the modified resources
		executionStatus = ExecutionStatus.EXECUTING_UNDO_CORE;
		_modifiedResources.clear();
		if (getProject() != null) {
			for (FlexoStorageResource<? extends StorageResourceData> r : getProject().getStorageResources()) {
				if (!r.isModified()) {
					_modifiedResources.put(r, null);
				}
			}
		}
		try {
			undoAction(getContext());
			executionStatus = ExecutionStatus.HAS_SUCCESSFULLY_UNDONE;
		} catch (FlexoException e1) {
			executionStatus = ExecutionStatus.FAILED_UNDO_EXECUTION;
			_modifiedResources.clear();
			throw e1;
		}

		// Now we remove all the resources that are still not modified. The left delta are the resources that have been directly modified by
		// this action (and the embedded ones)
		if (getProject() != null) {
			for (FlexoStorageResource<? extends StorageResourceData> r : getProject().getStorageResources()) {
				if (!r.isModified()) {
					_modifiedResources.remove(r);
				}
			}
		}

		Iterator<FlexoStorageResource<?>> i = _modifiedResources.keySet().iterator();
		while (i.hasNext()) {
			FlexoStorageResource<?> r = i.next();
			r.getResourceData().clearIsModified(true);
		}
		return (A) this;
	}

	public final A undoAction() {
		if (getEditor() != null) {
			getEditor().performUndoAction((A) this, null);
		} else {
			try {
				undoActionInContext();
			} catch (FlexoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return (A) this;
	}

	protected abstract void undoAction(Object context) throws FlexoException;

	public final A redoActionInContext() throws RedoException {
		try {
			executionStatus = ExecutionStatus.EXECUTING_REDO_CORE;
			redoAction(getContext());
			executionStatus = ExecutionStatus.HAS_SUCCESSFULLY_REDONE;
		} catch (FlexoException e) {
			executionStatus = ExecutionStatus.FAILED_REDO_EXECUTION;
			throw new RedoException(e);
		}
		return (A) this;
	}

	public final A redoAction() {
		if (getEditor() != null) {
			getEditor().performRedoAction((A) this, null);
		} else {
			try {
				redoActionInContext();
			} catch (FlexoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return (A) this;
	}

	protected abstract void redoAction(Object context) throws FlexoException;

	@Override
	public void delete() {
		_modifiedResources.clear();
		super.delete();
	}

}
