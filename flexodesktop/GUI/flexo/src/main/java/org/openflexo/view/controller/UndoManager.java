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

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.JMenuItem;

import org.openflexo.AdvancedPrefs;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoGUIAction;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.localization.FlexoLocalization;

public class UndoManager {

	private static final Logger logger = Logger.getLogger(UndoManager.class.getPackage().getName());

	private Vector<FlexoAction> _actionHistory;
	private int _lastDoneIndex = -1;
	private Vector<AbstractButton> _undoControls;
	private Vector<AbstractButton> _redoControls;

	public UndoManager() {
		_actionHistory = new Vector<FlexoAction>();
		_undoControls = new Vector<AbstractButton>();
		_redoControls = new Vector<AbstractButton>();
		// logger.setLevel(Level.FINE);
	}

	public void registerDoneAction(FlexoAction action) {

		if (!ENABLED) {
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

		if (_actionHistory.size() > UNDO_LEVELS) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Removing " + _actionHistory.firstElement());
			}
			(_actionHistory.firstElement()).delete();
			_actionHistory.removeElementAt(0);
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
				((FlexoUndoableAction) (_actionHistory.get(_lastDoneIndex))).undoAction();
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
				((FlexoUndoableAction) (_actionHistory.get(_lastDoneIndex + 1))).redoAction();
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
		return ((_actionHistory.size() > 0) && (_lastDoneIndex > -1) && (_lastDoneIndex < _actionHistory.size()));
	}

	private boolean isRedoActive() {
		return ((_actionHistory.size() > 0) && (_lastDoneIndex < _actionHistory.size() - 1) && (_lastDoneIndex >= -1));
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

	public void registerUndoControl(AbstractButton item) {
		_undoControls.add(item);
		refreshControls();
	}

	public void registerRedoControl(AbstractButton item) {
		_redoControls.add(item);
		refreshControls();
	}

	private static void buildActionsToNotify(Vector<FlexoAction> allActionsToNotify, FlexoAction<?, ?, ?> action) {
		allActionsToNotify.add(action);
		for (FlexoAction a : action.getEmbbededActionsExecutedDuringInitializer()) {
			buildActionsToNotify(allActionsToNotify, a);
		}
		for (FlexoAction a : action.getEmbbededActionsExecutedDuringCore()) {
			buildActionsToNotify(allActionsToNotify, a);
		}
		for (FlexoAction a : action.getEmbbededActionsExecutedDuringFinalizer()) {
			buildActionsToNotify(allActionsToNotify, a);
		}
	}

	public void actionWillBePerformed(FlexoAction action) {
		Vector<FlexoAction> allActionsToTakeUnderAccount = new Vector<FlexoAction>();
		buildActionsToNotify(allActionsToTakeUnderAccount, action);
		for (int i = 0; i < _actionHistory.size(); i++) {
			FlexoAction actionToTakeUnderAccount = _actionHistory.get(i);
			buildActionsToNotify(allActionsToTakeUnderAccount, actionToTakeUnderAccount);
		}
		action.getExecutionContext().saveExecutionContext(allActionsToTakeUnderAccount);
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
		Vector<FlexoAction> allActionsToNotify = new Vector<FlexoAction>();
		buildActionsToNotify(allActionsToNotify, action);
		int indexOfActionCurrentlyUndone = _actionHistory.indexOf(action);
		for (int i = indexOfActionCurrentlyUndone + 1; i < _actionHistory.size(); i++) {
			FlexoAction actionToNotify = _actionHistory.get(i);
			buildActionsToNotify(allActionsToNotify, actionToNotify);
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
		Vector<FlexoAction> allActionsToNotify = new Vector<FlexoAction>();
		buildActionsToNotify(allActionsToNotify, action);
		int indexOfActionCurrentlyUndone = _actionHistory.indexOf(action);
		for (int i = indexOfActionCurrentlyUndone + 1; i < _actionHistory.size(); i++) {
			FlexoAction actionToNotify = _actionHistory.get(i);
			buildActionsToNotify(allActionsToNotify, actionToNotify);
		}

		for (String key : action.getExecutionContext().getObjectsCreatedWhileExecutingAction().keySet()) {
			// Actions creating object are normally recreated those while redoing
			FlexoModelObject createdObject = action.getExecutionContext().getObjectsCreatedWhileExecutingAction().get(key);
			for (FlexoAction a : allActionsToNotify) {
				a.getExecutionContext().notifyExternalObjectCreatedByAction(createdObject, action, key, true);
			}
		}

		for (String key : action.getExecutionContext().getObjectsDeletedWhileExecutingAction().keySet()) {
			// Actions deleting object are normally redeleted those while redoing
			FlexoModelObject deletedObject = action.getExecutionContext().getObjectsDeletedWhileExecutingAction().get(key);
			for (FlexoAction a : allActionsToNotify) {
				a.getExecutionContext().notifyExternalObjectDeletedByAction(deletedObject, action, key, true);
			}
		}
	}

	private static int UNDO_LEVELS = AdvancedPrefs.getUndoLevels();

	private static boolean ENABLED = AdvancedPrefs.getEnableUndoManager();

	public static void setUndoLevels(int undoLevels) {
		UNDO_LEVELS = undoLevels;
	}

	public static void setEnable(boolean enableUndoManager) {
		ENABLED = enableUndoManager;
	}

}
