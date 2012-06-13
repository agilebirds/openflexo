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
package org.openflexo.view.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JMenuItem;

import org.openflexo.AdvancedPrefs;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoGUIAction;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.HasPropertyChangeSupport;

public class UndoManager implements HasPropertyChangeSupport, PropertyChangeListener {

	private static final Logger logger = Logger.getLogger(UndoManager.class.getPackage().getName());

	private List<FlexoAction> _actionHistory;
	private int _lastDoneIndex = -1;

	private PropertyChangeSupport propertyChangeSupport;

	private boolean enabled;
	private int undoLevel;

	public UndoManager() {
		this.propertyChangeSupport = new PropertyChangeSupport(this);
		_actionHistory = new LinkedList<FlexoAction>();
		enabled = AdvancedPrefs.getEnableUndoManager();
		undoLevel = AdvancedPrefs.getUndoLevels();
		AdvancedPrefs.getPreferences().getPropertyChangeSupport().addPropertyChangeListener(AdvancedPrefs.ENABLE_UNDO_MANAGER, this);
		AdvancedPrefs.getPreferences().getPropertyChangeSupport().addPropertyChangeListener(AdvancedPrefs.UNDO_LEVELS, this);
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return propertyChangeSupport;
	}

	@Override
	public String getDeletedProperty() {
		return null;
	}

	public void registerDoneAction(FlexoAction action) {

		if (!enabled) {
			return;
		}

		if (action instanceof FlexoGUIAction) {
			// Ignore
		} else if (action instanceof FlexoUndoableAction) {
			_actionHistory.add(action);
			_lastDoneIndex = _actionHistory.size() - 1;
			refreshControls();
		} else {
			// Cannot be undone
			// Reset action history
			resetActionHistory();
			refreshControls();
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
		for (FlexoAction a : _actionHistory) {
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
			try {
				((FlexoUndoableAction) _actionHistory.get(_lastDoneIndex)).undoAction();
				_lastDoneIndex--;
				refreshControls();
			} catch (FlexoException e) {
				// Warns about the exception
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				e.printStackTrace();
			}
			if (logger.isLoggable(Level.FINE)) {
				debug();
			}
		}
	}

	public void redo() {
		if (isRedoActive()) {
			try {
				((FlexoUndoableAction) _actionHistory.get(_lastDoneIndex + 1)).redoAction();
				_lastDoneIndex++;
				refreshControls();
			} catch (FlexoException e) {
				// Warns about the exception
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				e.printStackTrace();
			}
			if (logger.isLoggable(Level.FINE)) {
				debug();
			}
		}
	}

	private boolean isUndoActive() {
		return _actionHistory.size() > 0 && _lastDoneIndex > -1 && _lastDoneIndex < _actionHistory.size();
	}

	private boolean isRedoActive() {
		return _actionHistory.size() > 0 && _lastDoneIndex < _actionHistory.size() - 1 && _lastDoneIndex >= -1;
	}

	private void refreshControls() {
		for (Enumeration en = _undoControls.elements(); en.hasMoreElements();) {
			JMenuItem next = (JMenuItem) en.nextElement();
			next.setEnabled(isUndoActive());
			if (isUndoActive()) {
				next.setText(FlexoLocalization.localizedForKey("undo") + " (" + _actionHistory.get(_lastDoneIndex).getLocalizedName() + ")");
			} else {
				next.setText(FlexoLocalization.localizedForKey("undo"));
			}
		}
		for (Enumeration en = _redoControls.elements(); en.hasMoreElements();) {
			JMenuItem next = (JMenuItem) en.nextElement();
			next.setEnabled(isRedoActive());
			if (isRedoActive()) {
				next.setText(FlexoLocalization.localizedForKey("redo") + " (" + _actionHistory.get(_lastDoneIndex + 1).getLocalizedName()
						+ ")");
			} else {
				next.setText(FlexoLocalization.localizedForKey("redo"));
			}
		}
	}

	public void actionWillBePerformed(FlexoAction action) {
		List<FlexoAction> allActionsToTakeIntoAccount = new Vector<FlexoAction>();
		allActionsToTakeIntoAccount.add(action);
		for (int i = 0; i < _actionHistory.size(); i++) {
			FlexoAction actionToTakeIntoAccount = _actionHistory.get(i);
			allActionsToTakeIntoAccount.add(actionToTakeIntoAccount);
		}
		action.getExecutionContext().saveExecutionContext(allActionsToTakeIntoAccount);
	}

	public void actionHasBeenPerformed(FlexoAction action, boolean success) {
		if (success) {
			if (!action.isEmbedded()) {
				registerDoneAction(action);
			}
		}
	}

	public void actionWillBeUndone(FlexoAction action) {
	}

	public void actionHasBeenUndone(FlexoAction action, boolean success) {
		List<FlexoAction> allActionsToNotify = new Vector<FlexoAction>();
		allActionsToNotify.add(action);
		int indexOfActionCurrentlyUndone = _actionHistory.indexOf(action);
		for (int i = indexOfActionCurrentlyUndone + 1; i < _actionHistory.size(); i++) {
			FlexoAction actionToNotify = _actionHistory.get(i);
			allActionsToNotify.add(actionToNotify);
		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("******* actionHasBeenUndone() for " + action + " notify actions: " + allActionsToNotify);
		}

		for (String key : action.getExecutionContext().getObjectsCreatedWhileExecutingAction().keySet()) {
			// Actions creating object are normally deleting those while undoing
			FlexoModelObject deletedObject = action.getExecutionContext().getObjectsCreatedWhileExecutingAction().get(key);
			for (FlexoAction a : allActionsToNotify) {
				a.getExecutionContext().notifyExternalObjectDeletedByAction(deletedObject, action, key, false);
			}
		}

		for (String key : action.getExecutionContext().getObjectsDeletedWhileExecutingAction().keySet()) {
			// Actions deleting object are normally recreating those while undoing
			FlexoModelObject createdObject = action.getExecutionContext().getObjectsDeletedWhileExecutingAction().get(key);
			for (FlexoAction a : allActionsToNotify) {
				a.getExecutionContext().notifyExternalObjectCreatedByAction(createdObject, action, key, false);
			}
		}
	}

	public void actionWillBeRedone(FlexoAction action) {
	}

	public void actionHasBeenRedone(FlexoAction action, boolean success) {
		List<FlexoAction> allActionsToNotify = new Vector<FlexoAction>();
		allActionsToNotify.add(action);
		int indexOfActionCurrentlyUndone = _actionHistory.indexOf(action);
		for (int i = indexOfActionCurrentlyUndone + 1; i < _actionHistory.size(); i++) {
			FlexoAction actionToNotify = _actionHistory.get(i);
			allActionsToNotify.add(actionToNotify);
		}

		for (Map.Entry<String, FlexoModelObject> e : action.getExecutionContext().getObjectsCreatedWhileExecutingAction().entrySet()) {
			// Actions creating object are normally recreated those while redoing
			FlexoModelObject createdObject = e.getValue();
			for (FlexoAction a : allActionsToNotify) {
				a.getExecutionContext().notifyExternalObjectCreatedByAction(createdObject, action, e.getKey(), true);
			}
		}

		for (Map.Entry<String, FlexoModelObject> e : action.getExecutionContext().getObjectsDeletedWhileExecutingAction().entrySet()) {
			// Actions deleting object are normally redeleted those while redoing
			FlexoModelObject deletedObject = e.getValue();
			for (FlexoAction a : allActionsToNotify) {
				a.getExecutionContext().notifyExternalObjectDeletedByAction(deletedObject, action, e.getKey(), true);
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(AdvancedPrefs.ENABLE_UNDO_MANAGER)) {
			enabled = AdvancedPrefs.getEnableUndoManager();
		} else if (evt.getPropertyName().equals(AdvancedPrefs.UNDO_LEVELS)) {
			undoLevel = AdvancedPrefs.getUndoLevels();
		}
	}
}
