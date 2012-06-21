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
package org.openflexo.foundation.ws.action;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.foundation.wkf.ws.ServiceInterface;
import org.openflexo.foundation.ws.ExternalWSService;
import org.openflexo.foundation.ws.InternalWSService;
import org.openflexo.foundation.ws.WSObject;
import org.openflexo.foundation.ws.WSPortType;
import org.openflexo.foundation.ws.WSService;

/**
 * Action Delete for WSGroups. Deletion of an ExternalWSService should remove all the WSService and its children (process, data linked to it
 * included) Deletion of an InternalWSService should remove only the WSObjects (not the process and data of the subprocess)
 * 
 * @author dvanvyve
 * 
 */
public class WSDelete extends FlexoUndoableAction {

	private static final Logger logger = Logger.getLogger(WSDelete.class.getPackage().getName());

	public static FlexoActionType actionType = new FlexoActionType("delete", FlexoActionType.editGroup, FlexoActionType.DELETE_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public FlexoAction makeNewAction(FlexoModelObject focusedObject, Vector globalSelection, FlexoEditor editor) {
			return new WSDelete(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoModelObject object, Vector globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(FlexoModelObject object, Vector globalSelection) {
			if (globalSelection == null) {
				return false;
			}
			for (Enumeration en = globalSelection.elements(); en.hasMoreElements();) {
				FlexoModelObject next = (FlexoModelObject) en.nextElement();
				// Only ExternalWSService and InternalWSService
				if ((next instanceof ExternalWSService) && (((ExternalWSService) next).isDeletable())) {
					return true;
				} else if ((next instanceof InternalWSService) && (((InternalWSService) next).isDeletable())) {
					return true;
				} else if ((next instanceof ServiceInterface)) {
					WSService ws = next.getProject().getFlexoWSLibrary().getParentOfServiceInterface(((ServiceInterface) next));
					if (ws != null && ws instanceof InternalWSService) {
						return true;
					}
				}
			}
			return false;
		}

	};

	WSDelete(FlexoModelObject focusedObject, Vector globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		_deletionContexts = new Hashtable();
	}

	@Override
	protected void doAction(Object context) {
		logger.info("DELETE on DM");
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("selection is: " + getGlobalSelection());
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("selection to delete is: " + getObjectsToDelete());
		}
		for (Enumeration en = getObjectsToDelete().elements(); en.hasMoreElements();) {
			FlexoModelObject next = (FlexoModelObject) en.nextElement();
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

	private Vector<FlexoModelObject> _objectsToDelete;

	/**
	 * simplified form DMDelete. We can only delete ExternalWSGroups (and with this all children)
	 * 
	 * @return
	 */
	public Vector<FlexoModelObject> getObjectsToDelete() {
		if (_objectsToDelete == null) {
			_objectsToDelete = new Vector<FlexoModelObject>();
			Enumeration en = getGlobalSelection().elements();
			while (en.hasMoreElements()) {
				FlexoModelObject object = (FlexoModelObject) en.nextElement();
				// only externalWSGroup and InternalWSService
				if ((object instanceof ExternalWSService || object instanceof InternalWSService) && (((WSObject) object).isDeletable())) {

					_objectsToDelete.add(object);

				}
				// WARNING: assumption that THERE IS ONLY ONE PortType for a ServiceInterface !
				else if ((object instanceof ServiceInterface)) {
					WSService ws = object.getProject().getFlexoWSLibrary().getParentOfServiceInterface(((ServiceInterface) object));
					if (ws != null && ws instanceof InternalWSService) {
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

	private Hashtable _deletionContexts;

	public void setDeletionContextForObject(Object deletionContext, WSObject object) {
		_deletionContexts.put(object, deletionContext);
	}

	// ==========================================================
	// ================== Deletion procedure ====================
	// ==========================================================

	private void deleteObject(FlexoModelObject object) {
		if (object instanceof ExternalWSService) {
			((ExternalWSService) object).delete();
		} else if (object instanceof InternalWSService) {
			((InternalWSService) object).delete();
		} else if (object instanceof ServiceInterface) {
			ServiceInterface si = ((ServiceInterface) object);
			WSPortType pt = si.getProject().getFlexoWSLibrary().getWSPortTypeNamed(si.getName());
			if (pt != null && (pt.getWSService() instanceof InternalWSService)) {
				pt.delete();
			}

		}

	}
}
