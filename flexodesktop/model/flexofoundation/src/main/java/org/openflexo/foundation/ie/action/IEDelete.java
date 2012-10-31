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
package org.openflexo.foundation.ie.action;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.foundation.dkv.DKVObject;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.FlexoComponentFolder;
import org.openflexo.foundation.ie.widget.IESequenceTopComponent;
import org.openflexo.foundation.ie.widget.IESequenceWidget;
import org.openflexo.foundation.ie.widget.IETDWidget;
import org.openflexo.foundation.ie.widget.IETRWidget;
import org.openflexo.foundation.toc.TOCEntry;
import org.openflexo.foundation.wkf.node.ActionNode;

public class IEDelete extends FlexoUndoableAction<IEDelete, IEObject, IEObject> {

	private static final Logger logger = Logger.getLogger(IEDelete.class.getPackage().getName());

	public static FlexoActionType<IEDelete, IEObject, IEObject> actionType = new FlexoActionType<IEDelete, IEObject, IEObject>("delete",
			FlexoActionType.editGroup, FlexoActionType.DELETE_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public IEDelete makeNewAction(IEObject focusedObject, Vector<IEObject> globalSelection, FlexoEditor editor) {
			return new IEDelete(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(IEObject object, Vector<IEObject> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(IEObject object, Vector<IEObject> globalSelection) {
			return (object != null && !(object instanceof IESequenceWidget) && !(object instanceof IETDWidget)
					&& !(object instanceof IETRWidget) || globalSelection != null && globalSelection.size() > 0
					&& !(globalSelection.get(0) instanceof IESequenceWidget) && !(globalSelection.get(0) instanceof IETRWidget)
					&& !(globalSelection.get(0) instanceof IETDWidget))
					&& !(object instanceof IESequenceTopComponent && !((IESequenceTopComponent) object).isSubsequence());
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, IEObject.class);
	}

	private Vector<TOCEntry> entriesToDelete;
	private Vector<ActionNode> actionsToDelete;

	IEDelete(IEObject focusedObject, Vector<IEObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
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
		if (actionsToDelete != null) {
			for (ActionNode node : actionsToDelete) {
				if (!node.isDeleted()) {
					node.delete();
				}
			}
		}
		logger.info("DELETE on IE");
		Vector<ComponentDefinition> components = new Vector<ComponentDefinition>();
		Vector<FlexoComponentFolder> folders = new Vector<FlexoComponentFolder>();
		Vector<FlexoModelObject> v = getGlobalSelectionAndFocusedObject();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("IE Delete with " + v);
		}

		for (FlexoModelObject object : v) {
			if (object instanceof ComponentDefinition) {
				components.add((ComponentDefinition) object);
			} else if (object instanceof FlexoComponentFolder) {
				folders.add((FlexoComponentFolder) object);
			}
		}
		for (FlexoComponentFolder f : folders) {
			if (f.containsComponents()) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Cannot delete non-empty folders");
				}
				return;
			} else if (f.isRootFolder()) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Cannot delete root folder");
				}
				return;
			}
		}
		for (FlexoModelObject object : v) {
			if (object.isDeleted()) {
				continue;
			}
			if (object instanceof DKVObject) {
				if (((DKVObject) object).isDeleteAble()) {
					object.delete();
				} else {
					if (logger.isLoggable(Level.INFO)) {
						logger.info("DKV-Object " + object + " is not deleteable");
					}
				}
			} else if (object instanceof IEWOComponent) {
				((IEWOComponent) object).getComponentDefinition().delete();
			} else {
				object.delete();
			}
		}
	}

	@Override
	protected void undoAction(Object context) {
		logger.warning("UNDO DELETE on IE not implemented yet !");
	}

	@Override
	protected void redoAction(Object context) {
		logger.warning("REDO DELETE on IE not implemented yet !");
	}

	public void setEntriesToDelete(Vector<TOCEntry> entriesToDelete) {
		this.entriesToDelete = entriesToDelete;
	}

	public void setActionNodesToDelete(Vector<ActionNode> actionsToDelete) {
		this.actionsToDelete = actionsToDelete;
	}

}
