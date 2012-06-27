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

import java.util.EventObject;
import java.util.Hashtable;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.openflexo.ApplicationContext;
import org.openflexo.components.ProgressWindow;
import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoAction.ExecutionStatus;
import org.openflexo.foundation.action.FlexoActionEnableCondition;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoActionRedoFinalizer;
import org.openflexo.foundation.action.FlexoActionRedoInitializer;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoActionUndoFinalizer;
import org.openflexo.foundation.action.FlexoActionUndoInitializer;
import org.openflexo.foundation.action.FlexoActionVisibleCondition;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.foundation.action.UndoManager;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.rm.DefaultFlexoResourceUpdateHandler;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.ResourceUpdateHandler;
import org.openflexo.foundation.utils.FlexoDocFormat;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.FlexoProgressFactory;
import org.openflexo.foundation.view.ViewObject;
import org.openflexo.foundation.view.action.ActionSchemeActionType;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.ws.WSObject;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.module.FlexoModule;
import org.openflexo.module.Module;
import org.openflexo.module.ModuleLoader;
import org.openflexo.module.ModuleLoadingException;

public class InteractiveFlexoEditor extends DefaultFlexoEditor {

	private static final Logger logger = FlexoLogger.getLogger(InteractiveFlexoEditor.class.getPackage().getName());

	private static final boolean WARN_MODEL_MODIFICATIONS_OUTSIDE_FLEXO_ACTION_LAYER = false;

	private final UndoManager _undoManager;
	private ScenarioRecorder _scenarioRecorder;

	private final Hashtable<FlexoAction<?, ?, ?>, Vector<FlexoModelObject>> _createdAndNotNotifiedObjects;
	private final Hashtable<FlexoAction<?, ?, ?>, Vector<FlexoModelObject>> _deletedAndNotNotifiedObjects;

	private Stack<FlexoAction<?, ?, ?>> _currentlyPerformedActionStack = null;
	private Stack<FlexoAction<?, ?, ?>> _currentlyUndoneActionStack = null;
	private Stack<FlexoAction<?, ?, ?>> _currentlyRedoneActionStack = null;

	private final FlexoProgressFactory _progressFactory;

	private final ApplicationContext applicationContext;

	private Map<FlexoModule, ControllerActionInitializer> actionInitializers;

	public InteractiveFlexoEditor(ApplicationContext applicationContext, FlexoProject project) {
		super(project);
		this.applicationContext = applicationContext;
		actionInitializers = new Hashtable<FlexoModule, ControllerActionInitializer>();
		_undoManager = new UndoManager();
		if (ScenarioRecorder.ENABLE) {
			_scenarioRecorder = new ScenarioRecorder();
		}
		_createdAndNotNotifiedObjects = new Hashtable<FlexoAction<?, ?, ?>, Vector<FlexoModelObject>>();
		_deletedAndNotNotifiedObjects = new Hashtable<FlexoAction<?, ?, ?>, Vector<FlexoModelObject>>();
		_currentlyPerformedActionStack = new Stack<FlexoAction<?, ?, ?>>();
		_currentlyUndoneActionStack = new Stack<FlexoAction<?, ?, ?>>();
		_currentlyRedoneActionStack = new Stack<FlexoAction<?, ?, ?>>();
		_progressFactory = new FlexoProgressFactory() {
			@Override
			public FlexoProgress makeFlexoProgress(String title, int steps) {
				return ProgressWindow.makeProgressWindow(title, steps);
			}
		};

	}

	private ModuleLoader getModuleLoader() {
		return applicationContext.getModuleLoader();
	}

	@Override
	public ResourceUpdateHandler getResourceUpdateHandler() {
		return new DefaultFlexoResourceUpdateHandler();
	}

	@Override
	public boolean isInteractive() {
		return true;
	}

	@Override
	public <A extends org.openflexo.foundation.action.FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> A performAction(
			final A action, final EventObject e) {
		if (action.isLongRunningAction() && SwingUtilities.isEventDispatchThread()) {
			ProgressWindow.showProgressWindow(action.getLocalizedName(), 100);
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					try {
						executeAction(action, e);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return null;
				}

				@Override
				protected void done() {
					super.done();
					if (!action.isEmbedded()) {
						if (ProgressWindow.hasInstance()) {
							ProgressWindow.hideProgressWindow();
						}
					}
				}
			};
			worker.execute();
			return action;
		} else {
			executeAction(action, e);
			return action;
		}
	}

	private <A extends org.openflexo.foundation.action.FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> A executeAction(
			A action, EventObject event) {
		boolean confirmDoAction = true;
		ActionInitializer<A, T1, T2> actionInitializer = getActionInitializer(action.getActionType());
		FlexoActionInitializer<A> initializer = null;
		if (actionInitializer != null) {
			initializer = actionInitializer.getDefaultInitializer();
			if (initializer != null) {
				confirmDoAction = initializer.run(event, action);
			}
		}

		if (confirmDoAction) {
			actionWillBePerformed(action);
			try {
				if (getProject() != null) {
					getProject().clearRecentlyCreatedObjects();
				}
				action.doActionInContext();
				if (getProject() != null) {
					getProject().notifyRecentlyCreatedObjects();
				}
				actionHasBeenPerformed(action, true); // Action succeeded
			} catch (FlexoException exception) {
				actionHasBeenPerformed(action, false); // Action failed
				ProgressWindow.hideProgressWindow();
				FlexoExceptionHandler<A> exceptionHandler = null;
				if (actionInitializer != null) {
					exceptionHandler = actionInitializer.getDefaultExceptionHandler();
				}
				if (exceptionHandler != null) {
					if (exceptionHandler.handleException(exception, action)) {
						// The exception has been handled, we may still have to execute finalizer, if any
					} else {
						return action;
					}

				} else {
					return action;
				}
			}

			FlexoActionFinalizer<A> finalizer = null;
			if (actionInitializer != null) {
				finalizer = actionInitializer.getDefaultFinalizer();
				if (finalizer != null) {
					confirmDoAction = finalizer.run(event, action);
				}
			}
		}
		ProgressWindow.hideProgressWindow();
		return action;
	}

	@Override
	public <A extends FlexoUndoableAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> A performUndoAction(
			final A action, final EventObject event) {
		boolean confirmUndoAction = true;
		ActionInitializer<A, T1, T2> actionInitializer = getActionInitializer(action.getActionType());
		FlexoActionUndoInitializer<A> initializer = null;
		if (actionInitializer != null) {
			initializer = actionInitializer.getDefaultUndoInitializer();
			if (initializer != null) {
				confirmUndoAction = initializer.run(event, action);
			}
		}

		if (confirmUndoAction) {
			actionWillBeUndone(action);
			try {
				if (getProject() != null) {
					getProject().clearRecentlyCreatedObjects();
				}
				action.doActionInContext();
				if (getProject() != null) {
					getProject().notifyRecentlyCreatedObjects();
				}
				actionHasBeenUndone(action, true); // Action succeeded
			} catch (FlexoException exception) {
				actionHasBeenUndone(action, false); // Action failed
				ProgressWindow.hideProgressWindow();
				FlexoExceptionHandler<A> exceptionHandler = null;
				if (actionInitializer != null) {
					exceptionHandler = actionInitializer.getDefaultExceptionHandler();
				}
				if (exceptionHandler != null) {
					if (exceptionHandler.handleException(exception, action)) {
						// The exception has been handled, we may still have to execute finalizer, if any
					} else {
						return action;
					}
				} else {
					return action;
				}
			}

			FlexoActionUndoFinalizer<A> finalizer = null;
			if (actionInitializer != null) {
				finalizer = actionInitializer.getDefaultUndoFinalizer();
				if (finalizer != null) {
					confirmUndoAction = finalizer.run(event, action);
				}
			}
		}
		ProgressWindow.hideProgressWindow();
		return action;
	}

	@Override
	public <A extends FlexoUndoableAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> A performRedoAction(
			A action, EventObject event) {
		boolean confirmRedoAction = true;
		ActionInitializer<A, T1, T2> actionInitializer = getActionInitializer(action.getActionType());
		FlexoActionRedoInitializer<A> initializer = null;
		if (actionInitializer != null) {
			initializer = actionInitializer.getDefaultRedoInitializer();
			if (initializer != null) {
				confirmRedoAction = initializer.run(event, action);
			}
		}

		if (confirmRedoAction) {
			actionWillBeRedone(action);
			try {
				if (getProject() != null) {
					getProject().clearRecentlyCreatedObjects();
				}
				action.redoActionInContext();
				if (getProject() != null) {
					getProject().notifyRecentlyCreatedObjects();
				}
				actionHasBeenRedone(action, true); // Action succeeded
			} catch (FlexoException exception) {
				actionHasBeenUndone(action, false); // Action failed
				ProgressWindow.hideProgressWindow();
				FlexoExceptionHandler<A> exceptionHandler = null;
				if (actionInitializer != null) {
					exceptionHandler = actionInitializer.getDefaultExceptionHandler();
				}
				if (exceptionHandler != null) {
					if (exceptionHandler.handleException(exception, action)) {
						// The exception has been handled, we may still have to execute finalizer, if any
					} else {
						return action;
					}
				} else {
					return action;
				}
			}

			FlexoActionRedoFinalizer<A> finalizer = null;
			if (actionInitializer != null) {
				finalizer = actionInitializer.getDefaultRedoFinalizer();
				if (finalizer != null) {
					confirmRedoAction = finalizer.run(event, action);
				}
			}
		}
		ProgressWindow.hideProgressWindow();
		return action;
	}

	@Override
	public UndoManager getUndoManager() {
		return _undoManager;
	}

	private <A extends org.openflexo.foundation.action.FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> void actionWillBePerformed(
			A action) {
		_undoManager.actionWillBePerformed(action);
		_currentlyPerformedActionStack.push(action);
		_createdAndNotNotifiedObjects.put(action, new Vector<FlexoModelObject>());
		_deletedAndNotNotifiedObjects.put(action, new Vector<FlexoModelObject>());
	}

	private <A extends org.openflexo.foundation.action.FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> void actionHasBeenPerformed(
			A action, boolean success) {
		_undoManager.actionHasBeenPerformed(action, success);
		if (success) {
			if (_scenarioRecorder != null) {
				if (!action.isEmbedded() || action.getOwnerAction().getExecutionStatus() != ExecutionStatus.EXECUTING_CORE) {
					_scenarioRecorder.registerDoneAction(action);
				}
			}
		}
		FlexoAction<?, ?, ?> popAction = _currentlyPerformedActionStack.pop();
		if (popAction != action) {
			logger.warning("Expected to pop " + action + " but found " + popAction);
		}
		for (FlexoModelObject o : action.getExecutionContext().getObjectsCreatedWhileExecutingAction().values()) {
			_createdAndNotNotifiedObjects.get(action).remove(o);
		}
		for (FlexoModelObject o : action.getExecutionContext().getObjectsDeletedWhileExecutingAction().values()) {
			_deletedAndNotNotifiedObjects.get(action).remove(o);
		}
		if (WARN_MODEL_MODIFICATIONS_OUTSIDE_FLEXO_ACTION_LAYER) {
			for (FlexoModelObject o : _createdAndNotNotifiedObjects.get(action)) {
				logger.warning("FlexoModelObject " + o + " created during action " + action
						+ " but was not notified (see objectCreated(String,FlexoModelObject))");
			}
			for (FlexoModelObject o : _deletedAndNotNotifiedObjects.get(action)) {
				logger.warning("FlexoModelObject " + o + " deleted during action " + action
						+ " but was not notified (see objectDeleted(String,FlexoModelObject))");
			}
		}
		_createdAndNotNotifiedObjects.remove(action);
		_deletedAndNotNotifiedObjects.remove(action);
	}

	private <A extends FlexoUndoableAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> void actionWillBeUndone(
			A action) {
		_undoManager.actionWillBeUndone(action);
		_currentlyUndoneActionStack.push(action);
	}

	private <A extends FlexoUndoableAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> void actionHasBeenUndone(
			A action, boolean success) {
		_undoManager.actionHasBeenUndone(action, success);
		FlexoAction<?, ?, ?> popAction = _currentlyUndoneActionStack.pop();
		if (popAction != action) {
			logger.warning("Expected to pop " + action + " but found " + popAction);
		}
	}

	private <A extends FlexoUndoableAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> void actionWillBeRedone(
			A action) {
		_undoManager.actionWillBeRedone(action);
		_currentlyRedoneActionStack.push(action);
	}

	private <A extends FlexoUndoableAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> void actionHasBeenRedone(
			A action, boolean success) {
		_undoManager.actionHasBeenRedone(action, success);
		FlexoAction<?, ?, ?> popAction = _currentlyRedoneActionStack.pop();
		if (popAction != action) {
			logger.warning("Expected to pop " + action + " but found " + popAction);
		}
	}

	@Override
	public void notifyObjectCreated(FlexoModelObject object) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("notifyObjectCreated: " + object);
		}
		if (_currentlyPerformedActionStack.isEmpty() && _currentlyUndoneActionStack.isEmpty() && _currentlyRedoneActionStack.isEmpty()
				&& WARN_MODEL_MODIFICATIONS_OUTSIDE_FLEXO_ACTION_LAYER) {
			logger.warning("FlexoModelObject " + object + " created outside of FlexoAction context !!!");
		} else if (!_currentlyPerformedActionStack.isEmpty()) {
			_createdAndNotNotifiedObjects.get(_currentlyPerformedActionStack.peek()).add(object);
		}
		object.setDocFormat(FlexoDocFormat.HTML, false);
	}

	@Override
	public void notifyObjectDeleted(FlexoModelObject object) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("notifyObjectDeleted: " + object);
		}
		if (_currentlyPerformedActionStack.isEmpty() && _currentlyUndoneActionStack.isEmpty() && _currentlyRedoneActionStack.isEmpty()
				&& WARN_MODEL_MODIFICATIONS_OUTSIDE_FLEXO_ACTION_LAYER) {
			logger.warning("FlexoModelObject " + object + " deleted outside of FlexoAction context !!!");
		} else if (!_currentlyPerformedActionStack.isEmpty()) {
			_deletedAndNotNotifiedObjects.get(_currentlyPerformedActionStack.peek()).add(object);
		}
	}

	@Override
	public void notifyObjectChanged(FlexoModelObject object) {
		if (_currentlyPerformedActionStack.isEmpty() && _currentlyUndoneActionStack.isEmpty() && _currentlyRedoneActionStack.isEmpty()
				&& WARN_MODEL_MODIFICATIONS_OUTSIDE_FLEXO_ACTION_LAYER) {
			logger.warning("setChanged() called for " + object + " outside of FlexoAction context !!!");
		}
	}

	@Override
	public boolean performResourceScanning() {
		return true;
	}

	@Override
	public FlexoProgressFactory getFlexoProgressFactory() {
		return _progressFactory;
	}

	public boolean isTestEditor() {
		return false;
	}

	@Override
	public void focusOn(FlexoModelObject object) {

		try {
			if (object instanceof WKFObject) {
				getModuleLoader().switchToModule(Module.WKF_MODULE);
			} else if (object instanceof IEObject) {
				getModuleLoader().switchToModule(Module.IE_MODULE);
			} else if (object instanceof DMObject) {
				getModuleLoader().switchToModule(Module.DM_MODULE);
			} else if (object instanceof WSObject) {
				getModuleLoader().switchToModule(Module.WSE_MODULE);
			} else if (object instanceof ViewObject) {
				getModuleLoader().switchToModule(Module.VE_MODULE);
			}
		} catch (ModuleLoadingException e) {
			logger.warning("Cannot load module " + e.getModule());
			e.printStackTrace();
		}

		// Only interactive editor handle this
		getModuleLoader().getActiveModule().getFlexoController().setCurrentEditedObjectAsModuleView(object);
		getModuleLoader().getActiveModule().getFlexoController().getSelectionManager().setSelectedObject(object);
	}

	public void registerControllerActionInitializer(ControllerActionInitializer controllerActionInitializer) {
		actionInitializers.put(controllerActionInitializer.getModule(), controllerActionInitializer);
	}

	public void unregisterControllerActionInitializer(ControllerActionInitializer controllerActionInitializer) {
		actionInitializers.remove(controllerActionInitializer.getModule());
	}

	private ControllerActionInitializer getCurrentControllerActionInitializer() {
		return actionInitializers.get(getModuleLoader().getActiveModule());
	}

	private <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> ActionInitializer<A, T1, T2> getActionInitializer(
			FlexoActionType<A, T1, T2> actionType) {
		ControllerActionInitializer currentControllerActionInitializer = getCurrentControllerActionInitializer();
		if (currentControllerActionInitializer != null) {
			return currentControllerActionInitializer.getActionInitializer(actionType);
		}
		return null;
	}

	@Override
	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> boolean isActionEnabled(
			FlexoActionType<A, T1, T2> actionType, T1 focusedObject, Vector<T2> globalSelection) {
		if (actionType instanceof ActionSchemeActionType) {
			return true;
		}
		if (actionType.isEnabledForSelection(focusedObject, globalSelection)) {
			ActionInitializer<A, T1, T2> actionInitializer = getActionInitializer(actionType);
			if (actionInitializer != null) {
				FlexoActionEnableCondition<A, T1, T2> condition = actionInitializer.getEnableCondition();
				if (condition != null) {
					return condition.isEnabled(actionType, focusedObject, globalSelection, this);
				}
			} else {
				return false;
			}
		}
		return true;
	}

	@Override
	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> boolean isActionVisible(
			FlexoActionType<A, T1, T2> actionType, T1 focusedObject, Vector<T2> globalSelection) {
		if (actionType.isVisibleForSelection(focusedObject, globalSelection)) {
			ActionInitializer<A, T1, T2> actionInitializer = getActionInitializer(actionType);
			if (actionInitializer != null) {
				FlexoActionVisibleCondition<A, T1, T2> condition = actionInitializer.getVisibleCondition();
				if (condition != null) {
					return condition.isVisible(actionType, focusedObject, globalSelection, this);
				}
			} else {
				return false;
			}
		}
		return true;
	}

	@Override
	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> Icon getEnabledIconFor(
			FlexoActionType<A, T1, T2> actionType) {
		ActionInitializer<A, T1, T2> actionInitializer = getActionInitializer(actionType);
		if (actionInitializer != null) {
			return actionInitializer.getEnabledIcon();
		}
		return null;
	}

	@Override
	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> Icon getDisabledIconFor(
			FlexoActionType<A, T1, T2> actionType) {
		ActionInitializer<A, T1, T2> actionInitializer = getActionInitializer(actionType);
		if (actionInitializer != null) {
			return actionInitializer.getDisabledIcon();
		}
		return null;
	}

	@Override
	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> KeyStroke getKeyStrokeFor(
			FlexoActionType<A, T1, T2> actionType) {
		ActionInitializer<A, T1, T2> actionInitializer = getActionInitializer(actionType);
		if (actionInitializer != null) {
			return actionInitializer.getShortcut();
		}
		return null;
	}

}
