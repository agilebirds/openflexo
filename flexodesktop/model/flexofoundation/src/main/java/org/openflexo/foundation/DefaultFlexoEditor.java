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
package org.openflexo.foundation;

import java.util.EventObject;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.foundation.action.UndoManager;
import org.openflexo.foundation.rm.DefaultFlexoResourceUpdateHandler;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.ResourceUpdateHandler;
import org.openflexo.foundation.utils.FlexoProgressFactory;

public class DefaultFlexoEditor implements FlexoEditor {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(DefaultFlexoEditor.class
			.getPackage().getName());

	private final FlexoProject _project;
	private DefaultFlexoResourceUpdateHandler resourceUpdateHandler;

	public DefaultFlexoEditor(FlexoProject project) {
		_project = project;
		if (_project != null) {
			_project.addToEditors(this);
		}
		resourceUpdateHandler = new DefaultFlexoResourceUpdateHandler();
	}

	@Override
	public final FlexoProject getProject() {
		return _project;
	}

	@Override
	public void notifyObjectCreated(FlexoModelObject object) {
	}

	@Override
	public void notifyObjectDeleted(FlexoModelObject object) {
	}

	@Override
	public void notifyObjectChanged(FlexoModelObject object) {
	}

	@Override
	public boolean performResourceScanning() {
		return true;
	}

	@Override
	public FlexoProgressFactory getFlexoProgressFactory() {
		// Only interactive editor have a progress window
		return null;
	}

	@Override
	public void focusOn(FlexoModelObject object) {
		// Only interactive editor handle this
	}

	@Override
	public boolean isInteractive() {
		return false;
	}

	@Override
	public ResourceUpdateHandler getResourceUpdateHandler() {
		return resourceUpdateHandler;
	}

	@Override
	public UndoManager getUndoManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> A performActionType(
			FlexoActionType<A, T1, T2> actionType, T1 focusedObject, Vector<T2> globalSelection, EventObject e) {
		A action = actionType.makeNewAction(focusedObject, globalSelection, this);
		return performAction(action, e);
	}

	@Override
	public <A extends FlexoUndoableAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> A performUndoActionType(
			FlexoActionType<A, T1, T2> actionType, T1 focusedObject, Vector<T2> globalSelection, EventObject e) {
		A action = actionType.makeNewAction(focusedObject, globalSelection, this);
		return performUndoAction(action, e);
	}

	@Override
	public <A extends FlexoUndoableAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> A performRedoActionType(
			FlexoActionType<A, T1, T2> actionType, T1 focusedObject, Vector<T2> globalSelection, EventObject e) {
		A action = actionType.makeNewAction(focusedObject, globalSelection, this);
		return performRedoAction(action, e);
	}

	@Override
	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> A performAction(A action,
			EventObject e) {
		if (!action.getActionType().isEnabled(action.getFocusedObject(), action.getGlobalSelection())) {
			return null;
		}
		if (action.getFocusedObject() != null && action.getFocusedObject().getProject() != getProject()) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Focused object project is not the same as my project. Refusing to edit and execute action "
						+ action.getLocalizedName());
			}
			return null;
		}
		try {
			return action.doActionInContext();
		} catch (FlexoException e1) {
			e1.printStackTrace();
			return null;
		}
	}

	@Override
	public <A extends FlexoUndoableAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> A performUndoAction(
			A action, EventObject e) {
		try {
			return action.undoActionInContext();
		} catch (FlexoException e1) {
			e1.printStackTrace();
			return null;
		}
	}

	@Override
	public <A extends FlexoUndoableAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> A performRedoAction(
			A action, EventObject e) {
		try {
			return action.redoActionInContext();
		} catch (FlexoException e1) {
			e1.printStackTrace();
			return null;
		}
	}

	@Override
	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> KeyStroke getKeyStrokeFor(
			FlexoActionType<A, T1, T2> action) {
		return null;
	}

	@Override
	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> boolean isActionEnabled(
			FlexoActionType<A, T1, T2> actionType, T1 focusedObject, Vector<T2> globalSelection) {
		return actionType.isEnabled(focusedObject, globalSelection);
	}

	@Override
	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> boolean isActionVisible(
			FlexoActionType<A, T1, T2> actionType, T1 focusedObject, Vector<T2> globalSelection) {
		return true;
	}

	@Override
	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> Icon getEnabledIconFor(
			FlexoActionType<A, T1, T2> action) {
		return null;
	}

	@Override
	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> Icon getDisabledIconFor(
			FlexoActionType<A, T1, T2> action) {
		return null;
	}

}
