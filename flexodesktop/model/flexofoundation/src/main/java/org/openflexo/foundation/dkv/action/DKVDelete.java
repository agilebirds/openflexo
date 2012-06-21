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
package org.openflexo.foundation.dkv.action;

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.foundation.dkv.DKVObject;
import org.openflexo.foundation.toc.TOCEntry;

public class DKVDelete extends FlexoUndoableAction<DKVDelete, DKVObject, DKVObject> {

	private static final Logger logger = Logger.getLogger(DKVDelete.class.getPackage().getName());

	public static FlexoActionType<DKVDelete, DKVObject, DKVObject> actionType = new FlexoActionType<DKVDelete, DKVObject, DKVObject>(
			"delete", FlexoActionType.editGroup, FlexoActionType.DELETE_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DKVDelete makeNewAction(DKVObject focusedObject, Vector<DKVObject> globalSelection, FlexoEditor editor) {
			return new DKVDelete(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(DKVObject object, Vector<DKVObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(DKVObject object, Vector<DKVObject> globalSelection) {
			Enumeration en = globalSelection.elements();
			while (en.hasMoreElements()) {
				Object o = en.nextElement();
				if (!(o instanceof DKVObject) || !((DKVObject) o).isDeleteAble()) {
					return false;
				}
			}
			return (object.isDeleteAble()) || (globalSelection.size() > 0);
		}

		private String[] persistentProperties = { "objectsToDelete" };

		@Override
		protected String[] getPersistentProperties() {
			return persistentProperties;
		}

	};

	private Vector objectsToDelete;

	private Vector<TOCEntry> entriesToDelete;

	protected DKVDelete(DKVObject focusedObject, Vector<DKVObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	// private WeakHashMap<DKVObject, Vector> observers;
	@Override
	protected void doAction(Object context) {
		if (entriesToDelete != null) {
			for (TOCEntry entry : entriesToDelete) {
				if (!entry.isDeleted()) {
					entry.delete();
				}
			}
		}
		logger.info("DELETE on DKV");
		// observers = new WeakHashMap<DKVObject, Vector>();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("selection is: " + getGlobalSelection());
		}
		Enumeration en = getObjectsToDelete().elements();
		int i = 0;
		while (en.hasMoreElements()) {
			Object o = en.nextElement();
			if (o instanceof DKVObject) {
				// observers.put((DKVObject)o, ((DKVObject)o).getAllObservers());
				((DKVObject) o).delete();
				objectDeleted("DELETED_" + i, (DKVObject) o);
				i++;
			}
		}
	}

	@Override
	protected void undoAction(Object context) {
		Enumeration en = getObjectsToDelete().elements();
		while (en.hasMoreElements()) {
			Object o = en.nextElement();
			if (o instanceof DKVObject) {
				((DKVObject) o).undelete();
				// Vector obs = observers.get(o);
				// Enumeration en2 = obs.elements();
				// while(en2.hasMoreElements()){
				// Object observer = en2.nextElement();
				// if(observer instanceof FlexoObserver)
				// ((DKVObject)o).addObserver((FlexoObserver)observer);
				// else if (observer instanceof InspectorObserver)
				// ((DKVObject)o).addInspectorObserver((InspectorObserver)observer);
				//
				// }
			}
		}
		// observers.clear();
	}

	@Override
	protected void redoAction(Object context) {
		doAction(context);
	}

	public Vector getObjectsToDelete() {
		return objectsToDelete;
	}

	public void setObjectsToDelete(Vector objectsToDelete) {
		this.objectsToDelete = objectsToDelete;
	}

	public void setEntriesToDelete(Vector<TOCEntry> entriesToDelete) {
		this.entriesToDelete = entriesToDelete;
	}

}
