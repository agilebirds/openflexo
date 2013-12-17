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
package org.openflexo.foundation.dm.action;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.AgileBirdsObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.ExternalRepository;
import org.openflexo.foundation.dm.eo.DMEOModel;
import org.openflexo.foundation.dm.eo.DMEORepository;
import org.openflexo.foundation.toc.TOCEntry;

public class DMDelete extends FlexoUndoableAction<DMDelete, DMObject, DMObject> {

	private static final Logger logger = Logger.getLogger(DMDelete.class.getPackage().getName());

	public static FlexoActionType<DMDelete, DMObject, DMObject> actionType = new FlexoActionType<DMDelete, DMObject, DMObject>("delete",
			FlexoActionType.editGroup, FlexoActionType.DELETE_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DMDelete makeNewAction(DMObject focusedObject, Vector<DMObject> globalSelection, FlexoEditor editor) {
			return new DMDelete(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(DMObject object, Vector<DMObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(DMObject object, Vector<DMObject> globalSelection) {
			if (globalSelection == null) {
				return false;
			}
			boolean isEnabled = false;
			for (Enumeration en = getGlobalSelectionAndFocusedObject(object, globalSelection).elements(); en.hasMoreElements();) {
				AgileBirdsObject next = (AgileBirdsObject) en.nextElement();
				if (next instanceof DMObject) {
					if (((DMObject) next).isDeletable()) {
						isEnabled = true;
					}
				} else {
					return false;
				}
			}
			return isEnabled;
		}

	};

	static {
		AgileBirdsObject.addActionForClass(actionType, DMObject.class);
	}

	protected DMDelete(DMObject focusedObject, Vector<DMObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		_deletionContexts = new Hashtable<DMObject, Boolean>();
	}

	@Override
	protected void doAction(Object context) {
		if (entriesToDelete != null) {
			for (TOCEntry entry : entriesToDelete) {
				if (!entry.isDeleted()) {
					entry.delete();
				}
			}
		}
		logger.info("DELETE on DM");
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("selection is: " + getGlobalSelection());
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("selection to delete is: " + getObjectsToDelete());
		}
		for (Enumeration en = getObjectsToDelete().elements(); en.hasMoreElements();) {
			DMObject next = (DMObject) en.nextElement();
			deleteObject(next);
		}
	}

	@Override
	protected void undoAction(Object context) {
		logger.warning("UNDO DELETE on DM not implemented yet !");
	}

	@Override
	protected void redoAction(Object context) {
		logger.warning("REDO DELETE on DM not implemented yet !");
	}

	private Vector<DMObject> _objectsToDelete;

	public Vector<DMObject> getObjectsToDelete() {
		if (_objectsToDelete == null) {
			_objectsToDelete = new Vector<DMObject>();
			Enumeration<DMObject> en = getGlobalSelection().elements();
			while (en.hasMoreElements()) {
				DMObject object = en.nextElement();
				if (object.isDeletable()) {
					boolean includesIt = true;
					for (Enumeration en2 = getGlobalSelectionAndFocusedObject().elements(); en2.hasMoreElements();) {
						AgileBirdsObject next = (AgileBirdsObject) en2.nextElement();
						if (next instanceof DMObject && ((DMObject) next).isDeletable() && next != object) {
							if (((DMObject) next).getAllEmbeddedDeleted().contains(object)) {
								includesIt = false;
							}
						}
					}
					if (includesIt) {
						_objectsToDelete.add(object);
					}
				}
			}
		}
		return _objectsToDelete;
	}

	// ==========================================================
	// ============= Deletion contexts management ===============
	// ==========================================================

	private Hashtable<DMObject, Boolean> _deletionContexts;

	private Vector<TOCEntry> entriesToDelete;

	public void setDeletionContextForObject(Boolean deletionContext, DMObject object) {
		_deletionContexts.put(object, deletionContext);
	}

	public void setEntriesToDelete(Vector<TOCEntry> entriesToDelete) {
		this.entriesToDelete = entriesToDelete;
	}

	// ==========================================================
	// ================== Deletion procedure ====================
	// ==========================================================

	private void deleteObject(DMObject object) {
		if (object instanceof DMEOModel) {
			Boolean deleteEOModelFile = _deletionContexts.get(object);
			((DMEOModel) object).delete(deleteEOModelFile != null ? deleteEOModelFile.booleanValue() : false);
		} else if (object instanceof DMEORepository) {
			Boolean deleteEOModelFiles = _deletionContexts.get(object);
			if (deleteEOModelFiles == null) {
				deleteEOModelFiles = new Boolean(false);
			}
			((DMEORepository) object).delete(deleteEOModelFiles.booleanValue());
		} else if (object instanceof ExternalRepository) {
			Boolean deleteJarFile = _deletionContexts.get(object);
			((ExternalRepository) object).delete(deleteJarFile.booleanValue());
		} else {
			object.delete();
		}

	}
}
