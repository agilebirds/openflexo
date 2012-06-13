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
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoAction.ExecutionStatus;
import org.openflexo.foundation.action.FlexoActionType;
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

public class InteractiveFlexoEditor implements FlexoEditor {

	private static final Logger logger = FlexoLogger.getLogger(InteractiveFlexoEditor.class.getPackage().getName());

	private static final boolean WARN_MODEL_MODIFICATIONS_OUTSIDE_FLEXO_ACTION_LAYER = false;

	private FlexoProject _project;
	private final UndoManager _undoManager;
	private ScenarioRecorder _scenarioRecorder;

	private final Hashtable<FlexoAction, Vector<FlexoModelObject>> _createdAndNotNotifiedObjects;
	private final Hashtable<FlexoAction, Vector<FlexoModelObject>> _deletedAndNotNotifiedObjects;

	private Stack<FlexoAction> _currentlyPerformedActionStack = null;
	private Stack<FlexoAction> _currentlyUndoneActionStack = null;
	private Stack<FlexoAction> _currentlyRedoneActionStack = null;

	private final FlexoProgressFactory _progressFactory;

	private final ApplicationContext applicationContext;

	private Map<FlexoModule, ControllerActionInitializer> actionInitializers;

	public InteractiveFlexoEditor(ApplicationContext applicationContext, FlexoProject project) {
		this.applicationContext = applicationContext;
		actionInitializers = new Hashtable<FlexoModule, ControllerActionInitializer>();
		_undoManager = new UndoManager();
		if (ScenarioRecorder.ENABLE) {
			_scenarioRecorder = new ScenarioRecorder();
		}
		_createdAndNotNotifiedObjects = new Hashtable<FlexoAction, Vector<FlexoModelObject>>();
		_deletedAndNotNotifiedObjects = new Hashtable<FlexoAction, Vector<FlexoModelObject>>();
		_currentlyPerformedActionStack = new Stack<FlexoAction>();
		_currentlyUndoneActionStack = new Stack<FlexoAction>();
		_currentlyRedoneActionStack = new Stack<FlexoAction>();
		_progressFactory = new FlexoProgressFactory() {
			@Override
			public FlexoProgress makeFlexoProgress(String title, int steps) {
				return ProgressWindow.makeProgressWindow(title, steps);
			}
		};
		_project = project;
		_project.addToEditors(this);
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
			final A action, EventObject e) throws org.openflexo.foundation.FlexoException {
		if (action.isLongRunningAction() && SwingUtilities.isEventDispatchThread()) {
			ProgressWindow.showProgressWindow(action.getLocalizedName(), 100);
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					try {
						executeAction(action);
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
			executeAction(action);
			return action;
		}
	}

	private <A extends org.openflexo.foundation.action.FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> void executeAction(
			A action) {
		boolean confirmDoAction = true;
		if (getInitializer(action) != null) {
			confirmDoAction = getInitializer(action).run(originalActionEvent, action);
		}

		if (confirmDoAction) {
			actionWillBePerformed(action);
			try {
				getProject().clearRecentlyCreatedObjects();
				action.doAction();
				getProject().notifyRecentlyCreatedObjects();
				actionHasBeenPerformed(action, true); // Action succeeded
			} catch (FlexoException exception) {
				actionHasBeenPerformed(action, false); // Action failed
				ProgressWindow.hideProgressWindow();
				if (getExceptionHandler(action) != null) {
					if (getExceptionHandler(action).handleException(exception, action)) {
						// The exception has been handled, we may still have to execute finalizer, if any
					} else {
						throw exception;
					}
				} else {
					throw exception;
				}
			}

			if (getFinalizer(action) != null) {
				getFinalizer().run(originalActionEvent, action);
			}
		}
		ProgressWindow.hideProgressWindow();
	}

	@Override
	public FlexoProject getProject() {
		return _project;
	}

	public UndoManager getUndoManager() {
		return _undoManager;
	}

	public void actionWillBePerformed(FlexoAction action) {
		_undoManager.actionWillBePerformed(action);
		_currentlyPerformedActionStack.push(action);
		_createdAndNotNotifiedObjects.put(action, new Vector<FlexoModelObject>());
		_deletedAndNotNotifiedObjects.put(action, new Vector<FlexoModelObject>());
	}

	public void actionHasBeenPerformed(FlexoAction action, boolean success) {
		_undoManager.actionHasBeenPerformed(action, success);
		if (success) {
			if (_scenarioRecorder != null) {
				if (!action.isEmbedded() || action.getOwnerAction().getExecutionStatus() != ExecutionStatus.EXECUTING_CORE) {
					_scenarioRecorder.registerDoneAction(action);
				}
			}
		}
		FlexoAction popAction = _currentlyPerformedActionStack.pop();
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

	public void actionWillBeUndone(FlexoAction action) {
		_undoManager.actionWillBeUndone(action);
		_currentlyUndoneActionStack.push(action);
	}

	public void actionHasBeenUndone(FlexoAction action, boolean success) {
		_undoManager.actionHasBeenUndone(action, success);
		FlexoAction popAction = _currentlyUndoneActionStack.pop();
		if (popAction != action) {
			logger.warning("Expected to pop " + action + " but found " + popAction);
		}
	}

	public void actionWillBeRedone(FlexoAction action) {
		_undoManager.actionWillBeRedone(action);
		_currentlyRedoneActionStack.push(action);
	}

	public void actionHasBeenRedone(FlexoAction action, boolean success) {
		_undoManager.actionHasBeenRedone(action, success);
		FlexoAction popAction = _currentlyRedoneActionStack.pop();
		if (popAction != action) {
			logger.warning("Expected to pop " + action + " but found " + popAction);
		}
	}

	// Only explicitely registered actions are enabled
	@Override
	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> boolean isActionEnabled(
			FlexoActionType<A, T1, T2> actionType) {
		if (actionType instanceof ActionSchemeActionType) {
			return true;
		}
		return _registeredActions.contains(actionType);
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
		if (getModuleLoader().getActiveModule().getFlexoController() instanceof SelectionManagingController) {
			((SelectionManagingController) getModuleLoader().getActiveModule().getFlexoController()).getSelectionManager()
					.setSelectedObject(object);
		}
	}

	public void registerControllerActionInitializer(ControllerActionInitializer controllerActionInitializer) {
		actionInitializers.put(controllerActionInitializer.getModule(), controllerActionInitializer);
	}

	private ControllerActionInitializer getCurrentControllerActionInitializer() {
		return actionInitializers.get(getModuleLoader().getActiveModule());
	}

	private <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> ActionInitializer<A, T1, T2> getActionInitializer(
			FlexoActionType<A, T1, T2> actionType) {
		ControllerActionInitializer currentControllerActionInitializer = getCurrentControllerActionInitializer();
		if (currentControllerActionInitializer != null) {
			return currentControllerActionInitializer.getInitializer(actionType);
		}
		return null;
	}

	@Override
	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> A performActionType(
			FlexoActionType<A, T1, T2> actionType, T1 focusedObject, Vector<T2> globalSelection, EventObject e) {
		return null;
	}

	@Override
	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> A performUndoActionType(
			FlexoActionType<A, T1, T2> actionType, T1 focusedObject, Vector<T2> globalSelection, EventObject e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> A performRedoActionType(
			FlexoActionType<A, T1, T2> actionType, T1 focusedObject, Vector<T2> globalSelection, EventObject e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> A performUndoAction(A action) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> A performRedoAction(A action) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> boolean isActionEnabled(
			FlexoActionType<A, T1, T2> actionType, T1 focusedObject, Vector<T2> globalSelection) {
		if (actionType.isEnabled(object, globalSelection, this))
		ActionInitializer<A, T1, T2> actionInitializer = getActionInitializer(actionType);
		if (actionInitializer != null) {
			return actionInitializer.getEnableCondition()
		}
		return null;		return false;
	}

	@Override
	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> boolean isActionVisible(
			FlexoActionType<A, T1, T2> actionType, T1 focusedObject, Vector<T2> globalSelection) {
		// TODO Auto-generated method stub
		return false;
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
