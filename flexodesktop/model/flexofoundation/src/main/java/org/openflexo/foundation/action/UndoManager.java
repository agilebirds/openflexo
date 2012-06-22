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

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.toolbox.HasPropertyChangeSupport;

public class UndoManager implements HasPropertyChangeSupport {

	private static final Logger logger = Logger.getLogger(UndoManager.class.getPackage().getName());

	public static final String ACTION_HISTORY = "actionHistory";
	public static final String ENABLED = "enabled";

	private List<FlexoUndoableAction<?, ?, ?>> _actionHistory;
	private int _lastDoneIndex = -1;

	private PropertyChangeSupport propertyChangeSupport;

	private boolean enabled;
	private int undoLevel;

	public UndoManager() {
		this.propertyChangeSupport = new PropertyChangeSupport(this);
		_actionHistory = new LinkedList<FlexoUndoableAction<?, ?, ?>>();
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return propertyChangeSupport;
	}

	@Override
	public String getDeletedProperty() {
		return null;
	}

	public void registerDoneAction(FlexoAction<?, ?, ?> action) {

		if (!enabled) {
			return;
		}

		if (action instanceof FlexoGUIAction) {
			// Ignore
		} else if (action instanceof FlexoUndoableAction) {
			_actionHistory.add((FlexoUndoableAction<?, ?, ?>) action);
			_lastDoneIndex = _actionHistory.size() - 1;
			getPropertyChangeSupport().firePropertyChange(ACTION_HISTORY, null, action);
		} else {
			// Cannot be undone
			// Reset action history
			resetActionHistory();
			getPropertyChangeSupport().firePropertyChange(ACTION_HISTORY, null, action);
		}

		if (_actionHistory.size() > undoLevel) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Removing " + _actionHistory.get(0));
			}
			_actionHistory.get(0).delete();
			_actionHistory.remove(0);
			_lastDoneIndex--;
		}

		if (logger.isLoggable(Level.FINE)) {
			debug();
		}
	}

	private void debug() {
		logger.info("History: ");
		int i = 0;
		for (FlexoAction<?, ?, ?> a : _actionHistory) {
			logger.info("" + i + " " + (_lastDoneIndex == i ? "*" : ">") + " : " + a);
			i++;
		}
	}

	private void resetActionHistory() {
		_actionHistory.clear();
		_lastDoneIndex = -1;
	}

	public void undo() {
		if (isUndoActive()) {
			FlexoUndoableAction<?, ?, ?> action = getNextUndoAction();
			action.undoAction();
			_lastDoneIndex--;
			getPropertyChangeSupport().firePropertyChange(ACTION_HISTORY, null, action);
			if (logger.isLoggable(Level.FINE)) {
				debug();
			}
		}
	}

	public void redo() {
		if (isRedoActive()) {
			FlexoUndoableAction<?, ?, ?> action = getNextRedoAction();
			action.redoAction();
			_lastDoneIndex++;
			getPropertyChangeSupport().firePropertyChange(ACTION_HISTORY, null, action);
			if (logger.isLoggable(Level.FINE)) {
				debug();
			}
		}
	}

	public boolean isUndoActive() {
		return enabled && _actionHistory.size() > 0 && _lastDoneIndex > -1 && _lastDoneIndex < _actionHistory.size();
	}

	public boolean isRedoActive() {
		return enabled && _actionHistory.size() > 0 && _lastDoneIndex < _actionHistory.size() - 1 && _lastDoneIndex >= -1;
	}

	public FlexoUndoableAction<?, ?, ?> getNextRedoAction() {
		if (isRedoActive()) {
			return _actionHistory.get(_lastDoneIndex + 1);
		} else {
			return null;
		}
	}

	public FlexoUndoableAction<?, ?, ?> getNextUndoAction() {
		if (isUndoActive()) {
			return _actionHistory.get(_lastDoneIndex);
		} else {
			return null;
		}
	}

	public void actionWillBePerformed(FlexoAction<?, ?, ?> action) {
		List<FlexoAction<?, ?, ?>> allActionsToTakeIntoAccount = new ArrayList<FlexoAction<?, ?, ?>>();
		allActionsToTakeIntoAccount.add(action);
		for (int i = 0; i < _actionHistory.size(); i++) {
			FlexoAction<?, ?, ?> actionToTakeIntoAccount = _actionHistory.get(i);
			allActionsToTakeIntoAccount.add(actionToTakeIntoAccount);
		}
		action.getExecutionContext().saveExecutionContext(allActionsToTakeIntoAccount);
	}

	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> void actionHasBeenPerformed(
			A action, boolean success) {
		if (success) {
			if (!action.isEmbedded()) {
				registerDoneAction(action);
			}
		}
	}

	public <A extends FlexoUndoableAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> void actionWillBeUndone(
			A action) {
	}

	public <A extends FlexoUndoableAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> void actionHasBeenUndone(
			A action, boolean success) {
		List<FlexoAction<?, ?, ?>> allActionsToNotify = new Vector<FlexoAction<?, ?, ?>>();
		allActionsToNotify.add(action);
		int indexOfActionCurrentlyUndone = _actionHistory.indexOf(action);
		for (int i = indexOfActionCurrentlyUndone + 1; i < _actionHistory.size(); i++) {
			FlexoAction<?, ?, ?> actionToNotify = _actionHistory.get(i);
			allActionsToNotify.add(actionToNotify);
		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("******* actionHasBeenUndone() for " + action + " notify actions: " + allActionsToNotify);
		}

		for (Map.Entry<String, FlexoModelObject> e : action.getExecutionContext().getObjectsCreatedWhileExecutingAction().entrySet()) {
			// Actions creating object are normally deleting those while undoing
			FlexoModelObject deletedObject = e.getValue();
			for (FlexoAction<?, ?, ?> a : allActionsToNotify) {
				a.getExecutionContext().notifyExternalObjectDeletedByAction(deletedObject, action, e.getKey(), false);
			}
		}

		for (Map.Entry<String, FlexoModelObject> e : action.getExecutionContext().getObjectsDeletedWhileExecutingAction().entrySet()) {
			// Actions deleting object are normally recreating those while undoing
			FlexoModelObject createdObject = e.getValue();
			for (FlexoAction<?, ?, ?> a : allActionsToNotify) {
				a.getExecutionContext().notifyExternalObjectCreatedByAction(createdObject, action, e.getKey(), false);
			}
		}
	}

	public <A extends FlexoUndoableAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> void actionWillBeRedone(
			A action) {
	}

	public <A extends FlexoUndoableAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> void actionHasBeenRedone(
			A action, boolean success) {
		List<FlexoAction<?, ?, ?>> allActionsToNotify = new Vector<FlexoAction<?, ?, ?>>();
		allActionsToNotify.add(action);
		int indexOfActionCurrentlyUndone = _actionHistory.indexOf(action);
		for (int i = indexOfActionCurrentlyUndone + 1; i < _actionHistory.size(); i++) {
			FlexoAction<?, ?, ?> actionToNotify = _actionHistory.get(i);
			allActionsToNotify.add(actionToNotify);
		}

		for (Map.Entry<String, FlexoModelObject> e : action.getExecutionContext().getObjectsCreatedWhileExecutingAction().entrySet()) {
			// Actions creating object are normally recreated those while redoing
			FlexoModelObject createdObject = e.getValue();
			for (FlexoAction<?, ?, ?> a : allActionsToNotify) {
				a.getExecutionContext().notifyExternalObjectCreatedByAction(createdObject, action, e.getKey(), true);
			}
		}

		for (Map.Entry<String, FlexoModelObject> e : action.getExecutionContext().getObjectsDeletedWhileExecutingAction().entrySet()) {
			// Actions deleting object are normally redeleted those while redoing
			FlexoModelObject deletedObject = e.getValue();
			for (FlexoAction<?, ?, ?> a : allActionsToNotify) {
				a.getExecutionContext().notifyExternalObjectDeletedByAction(deletedObject, action, e.getKey(), true);
			}
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		getPropertyChangeSupport().firePropertyChange(ENABLED, !enabled, enabled);
	}

	public int getUndoLevel() {
		return undoLevel;
	}

	public void setUndoLevel(int undoLevel) {
		this.undoLevel = undoLevel;
	}
}
