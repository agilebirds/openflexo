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
package org.openflexo.foundation.wkf.action;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.DeletableObject;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.foundation.toc.TOCEntry;
import org.openflexo.foundation.wkf.FlexoPetriGraph;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;

public class WKFDelete extends FlexoUndoableAction<WKFDelete, WKFObject, WKFObject> {

	private static final Logger logger = Logger.getLogger(WKFDelete.class.getPackage().getName());

	public static FlexoActionType<WKFDelete, WKFObject, WKFObject> actionType = new FlexoActionType<WKFDelete, WKFObject, WKFObject>(
			"delete", FlexoActionType.editGroup, FlexoActionType.DELETE_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public WKFDelete makeNewAction(WKFObject focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
			return new WKFDelete(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(WKFObject object, Vector<WKFObject> globalSelection) {
			return !(object instanceof FlexoPetriGraph);
		}

		@Override
		protected boolean isEnabledForSelection(WKFObject object, Vector<WKFObject> globalSelection) {
			if (globalSelection == null || (object == null && globalSelection.size() == 0))
				return false;
			for (Enumeration en = globalSelection.elements(); en.hasMoreElements();) {
				FlexoModelObject next = (FlexoModelObject) en.nextElement();
				if (next instanceof FlexoProcess) {
					FlexoProcess p = (FlexoProcess) next;
					if (p.isRootProcess())
						return false;
					if (p.isImported()) {
						if (!p.isTopLevelProcess())
							return false;
					} else if (p.getSubProcesses() != null && p.getSubProcesses().size() > 0)
						return false;
				}
			}
			return true;
		}

	};

	private boolean noConfirmation = false;

	public boolean isNoConfirmation() {
		return noConfirmation;
	}

	public void setNoConfirmation(boolean noConfirmation) {
		this.noConfirmation = noConfirmation;
	}

	protected WKFDelete(WKFObject focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		_deletionContexts = new Hashtable<WKFObject, Object>();
		if (logger.isLoggable(Level.FINE))
			logger.fine("Created WKFDelete action focusedObject=" + focusedObject + "globalSelection=" + globalSelection);
	}

	@Override
	protected void doAction(Object context) {
		if (logger.isLoggable(Level.INFO))
			logger.info("DELETE on WKF");
		if (logger.isLoggable(Level.INFO))
			logger.info("selection is: " + getGlobalSelection());
		if (logger.isLoggable(Level.INFO))
			logger.info("selection to delete is: " + getObjectsToDelete());
		for (Enumeration en = getObjectsToDelete().elements(); en.hasMoreElements();) {
			WKFObject next = (WKFObject) en.nextElement();
			if (logger.isLoggable(Level.INFO))
				logger.info(" > DELETE" + next);
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

	private Vector<WKFObject> _objectsToDelete;

	/**
	 * This method returns all the objects on which the delete method needs to be called. This should not be done by some other code than
	 * the one located in the doAction method. This method can be used in either initialiazer or finalizer of the action. Do not forget to
	 * consult the _deletionContext, because objectsToDelete returns the objects that will be deleted, not the ones that have been deleted.
	 * 
	 * @return All the objects to be deleted.
	 */
	public Vector getObjectsToDelete() {
		if (_objectsToDelete == null) {
			_objectsToDelete = new Vector<WKFObject>();
			if (!getGlobalSelection().contains(getFocusedObject())) {
				boolean includesIt = true;
				for (Enumeration en2 = getGlobalSelection().elements(); en2.hasMoreElements() && includesIt;) {
					FlexoModelObject next = (FlexoModelObject) en2.nextElement();
					if (next instanceof WKFObject) {
						if (((WKFObject) next).getAllEmbeddedDeleted().contains(getFocusedObject()))
							includesIt = false;
					}
				}
				if (includesIt) {
					_objectsToDelete.add(getFocusedObject());
				}
			}

			/**
			 * The purpose of the loop below is too find all objects on which the delete must be called, so that all the selected objects
			 * are removed from the model. The trick is to verify that an object will not already be deleted because another one has been
			 * previously removed (causing a double call to delete on that object, which will most likely fail).
			 */
			Enumeration<WKFObject> en = getGlobalSelection().elements();
			while (en.hasMoreElements()) {
				WKFObject object = en.nextElement();
				boolean includesIt = true;
				if (object instanceof FlexoPetriGraph)
					includesIt = false;
				for (Enumeration en2 = getGlobalSelection().elements(); en2.hasMoreElements() && includesIt;) {
					FlexoModelObject next = (FlexoModelObject) en2.nextElement();
					if ((next instanceof WKFObject) && (next != object)) {
						if (((WKFObject) next).getAllEmbeddedDeleted().contains(object))
							includesIt = false;
					}
				}
				if (includesIt) {
					_objectsToDelete.add(object);
				}
			}
		}
		return _objectsToDelete;
	}

	// ==========================================================
	// ============= Deletion contexts management ===============
	// ==========================================================

	private Hashtable<WKFObject, Object> _deletionContexts;

	private Vector<TOCEntry> entriesToDelete;

	public void setDeletionContextForObject(Object deletionContext, WKFObject object) {
		_deletionContexts.put(object, deletionContext);
	}

	// ==========================================================
	// ================== Deletion procedure ====================
	// ==========================================================

	private void deleteObject(WKFObject object) {
		if (entriesToDelete != null) {
			for (TOCEntry entry : entriesToDelete) {
				if (!entry.isDeleted())
					entry.delete();
			}
		}
		if (object instanceof FlexoProcess) {
			Boolean deleteProcess = (Boolean) _deletionContexts.get(object);
			if (deleteProcess != null && deleteProcess.booleanValue()) {
				FlexoProcess processToDelete = (FlexoProcess) object;
				// If process is a WS, delete the WS itself
				if (processToDelete.getProject().getFlexoWSLibrary().isDeclaredAsWS(processToDelete)) {
					(processToDelete.getProject().getFlexoWSLibrary().portTypeForProcess(processToDelete).getWSService()).delete();
				} else {
					processToDelete.delete();
				}
			}
		} else if (object instanceof DeletableObject) {
			object.delete();
		}

	}

	public boolean hasBeenDeleted(FlexoProcess p) {
		return (_objectsToDelete.contains(p)) && (_deletionContexts.get(p) != null && ((Boolean) _deletionContexts.get(p)).booleanValue());
	}

	public void setEntriesToDelete(Vector<TOCEntry> entriesToDelete) {
		this.entriesToDelete = entriesToDelete;
	}
}
