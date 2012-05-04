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

import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.openflexo.components.ProgressWindow;
import org.openflexo.foundation.FlexoEditor;
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
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.rm.FlexoProject;
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

public abstract class InteractiveFlexoEditor implements FlexoEditor {

	private static final Logger logger = FlexoLogger.getLogger(InteractiveFlexoEditor.class.getPackage().getName());

	private static final boolean WARN_MODEL_MODIFICATIONS_OUTSIDE_FLEXO_ACTION_LAYER = false;

	protected class ModuleContext {
		private final Hashtable<Class<? extends FlexoActionType>, FlexoActionInitializer> _initializers;
		private final Hashtable<Class<? extends FlexoActionType>, FlexoActionFinalizer> _finalizers;
		private final Hashtable<Class<? extends FlexoActionType>, FlexoActionUndoInitializer> _undoInitializers;
		private final Hashtable<Class<? extends FlexoActionType>, FlexoActionUndoFinalizer> _undoFinalizers;
		private final Hashtable<Class<? extends FlexoActionType>, FlexoActionRedoInitializer> _redoInitializers;
		private final Hashtable<Class<? extends FlexoActionType>, FlexoActionRedoFinalizer> _redoFinalizers;
		private final Hashtable<Class<? extends FlexoActionType>, FlexoExceptionHandler> _exceptionHandlers;
		private final Hashtable<Class<? extends FlexoActionType>, FlexoActionEnableCondition> _enableConditions;
		private final Hashtable<Class<? extends FlexoActionType>, FlexoActionVisibleCondition> _visibleConditions;

		protected ModuleContext() {
			_initializers = new Hashtable<Class<? extends FlexoActionType>, FlexoActionInitializer>();
			_finalizers = new Hashtable<Class<? extends FlexoActionType>, FlexoActionFinalizer>();
			_undoInitializers = new Hashtable<Class<? extends FlexoActionType>, FlexoActionUndoInitializer>();
			_undoFinalizers = new Hashtable<Class<? extends FlexoActionType>, FlexoActionUndoFinalizer>();
			_redoInitializers = new Hashtable<Class<? extends FlexoActionType>, FlexoActionRedoInitializer>();
			_redoFinalizers = new Hashtable<Class<? extends FlexoActionType>, FlexoActionRedoFinalizer>();
			_exceptionHandlers = new Hashtable<Class<? extends FlexoActionType>, FlexoExceptionHandler>();
			_enableConditions = new Hashtable<Class<? extends FlexoActionType>, FlexoActionEnableCondition>();
			_visibleConditions = new Hashtable<Class<? extends FlexoActionType>, FlexoActionVisibleCondition>();

		}

		protected FlexoActionFinalizer getFinalizerFor(FlexoActionType actionType) {
			return _finalizers.get(actionType.getClass());
		}

		protected FlexoActionInitializer getInitializerFor(FlexoActionType actionType) {
			return _initializers.get(actionType.getClass());
		}

		protected FlexoActionUndoFinalizer getUndoFinalizerFor(FlexoActionType actionType) {
			return _undoFinalizers.get(actionType.getClass());
		}

		protected FlexoActionUndoInitializer getUndoInitializerFor(FlexoActionType actionType) {
			return _undoInitializers.get(actionType.getClass());
		}

		protected FlexoActionRedoFinalizer getRedoFinalizerFor(FlexoActionType actionType) {
			return _redoFinalizers.get(actionType.getClass());
		}

		protected FlexoActionRedoInitializer getRedoInitializerFor(FlexoActionType actionType) {
			return _redoInitializers.get(actionType.getClass());
		}

		protected FlexoActionEnableCondition getEnableConditionFor(FlexoActionType actionType) {
			return _enableConditions.get(actionType.getClass());
		}

		protected FlexoActionVisibleCondition getVisibleConditionFor(FlexoActionType actionType) {
			return _visibleConditions.get(actionType.getClass());
		}

		protected FlexoExceptionHandler getExceptionHandlerFor(FlexoActionType actionType) {
			return _exceptionHandlers.get(actionType.getClass());
		}

		protected void registerFinalizerFor(FlexoActionType actionType, FlexoActionFinalizer finalizer) {
			_finalizers.put(actionType.getClass(), finalizer);
		}

		protected void registerFinalizerFor(Class<? extends FlexoActionType<?, ?, ?>> actionTypeClass, FlexoActionFinalizer finalizer) {
			_finalizers.put(actionTypeClass, finalizer);
		}

		protected void registerInitializerFor(FlexoActionType actionType, FlexoActionInitializer initializer) {
			_initializers.put(actionType.getClass(), initializer);
		}

		protected void registerInitializerFor(Class<? extends FlexoActionType<?, ?, ?>> actionTypeClass, FlexoActionInitializer initializer) {
			_initializers.put(actionTypeClass, initializer);
		}

		protected void registerUndoFinalizerFor(FlexoActionType actionType, FlexoActionUndoFinalizer undoFinalizer) {
			_undoFinalizers.put(actionType.getClass(), undoFinalizer);
		}

		protected void registerUndoFinalizerFor(Class<? extends FlexoActionType<?, ?, ?>> actionTypeClass,
				FlexoActionUndoFinalizer undoFinalizer) {
			_undoFinalizers.put(actionTypeClass, undoFinalizer);
		}

		protected void registerUndoInitializerFor(FlexoActionType actionType, FlexoActionUndoInitializer undoInitializer) {
			_undoInitializers.put(actionType.getClass(), undoInitializer);
		}

		protected void registerUndoInitializerFor(Class<? extends FlexoActionType<?, ?, ?>> actionTypeClass,
				FlexoActionUndoInitializer undoInitializer) {
			_undoInitializers.put(actionTypeClass, undoInitializer);
		}

		protected void registerRedoFinalizerFor(FlexoActionType actionType, FlexoActionRedoFinalizer redoFinalizer) {
			_redoFinalizers.put(actionType.getClass(), redoFinalizer);
		}

		protected void registerRedoFinalizerFor(Class<? extends FlexoActionType<?, ?, ?>> actionTypeClass,
				FlexoActionRedoFinalizer redoFinalizer) {
			_redoFinalizers.put(actionTypeClass, redoFinalizer);
		}

		protected void registerRedoInitializerFor(FlexoActionType actionType, FlexoActionRedoInitializer redoInitializer) {
			_redoInitializers.put(actionType.getClass(), redoInitializer);
		}

		protected void registerRedoInitializerFor(Class<? extends FlexoActionType<?, ?, ?>> actionTypeClass,
				FlexoActionRedoInitializer redoInitializer) {
			_redoInitializers.put(actionTypeClass, redoInitializer);
		}

		protected void registerEnableConditionFor(FlexoActionType actionType, FlexoActionEnableCondition enableCondition) {
			_enableConditions.put(actionType.getClass(), enableCondition);
		}

		protected void registerEnableConditionFor(Class<? extends FlexoActionType<?, ?, ?>> actionTypeClass,
				FlexoActionEnableCondition enableCondition) {
			_enableConditions.put(actionTypeClass, enableCondition);
		}

		protected void registerVisibleConditionFor(FlexoActionType actionType, FlexoActionVisibleCondition enableCondition) {
			_visibleConditions.put(actionType.getClass(), enableCondition);
		}

		protected void registerVisibleConditionFor(Class<? extends FlexoActionType<?, ?, ?>> actionTypeClass,
				FlexoActionVisibleCondition enableCondition) {
			_visibleConditions.put(actionTypeClass, enableCondition);
		}

		protected void registerExceptionHandlerFor(FlexoActionType actionType, FlexoExceptionHandler exceptionHandler) {
			_exceptionHandlers.put(actionType.getClass(), exceptionHandler);
		}

		protected void registerExceptionHandlerFor(Class<? extends FlexoActionType<?, ?, ?>> actionTypeClass,
				FlexoExceptionHandler exceptionHandler) {
			_exceptionHandlers.put(actionTypeClass, exceptionHandler);
		}

	}

	protected Hashtable<FlexoModule, ModuleContext> _moduleContexts;

	private FlexoProject _project;
	private final UndoManager _undoManager;
	private ScenarioRecorder _scenarioRecorder;
	private final Vector<FlexoActionType> _registeredActions;
	private final Hashtable<FlexoActionType, Icon> _enabledIcons;
	private final Hashtable<FlexoActionType, Icon> _disabledIcons;

	private final Hashtable<FlexoAction, Vector<FlexoModelObject>> _createdAndNotNotifiedObjects;
	private final Hashtable<FlexoAction, Vector<FlexoModelObject>> _deletedAndNotNotifiedObjects;

	private Stack<FlexoAction> _currentlyPerformedActionStack = null;
	private Stack<FlexoAction> _currentlyUndoneActionStack = null;
	private Stack<FlexoAction> _currentlyRedoneActionStack = null;

	private final FlexoProgressFactory _progressFactory;

	InteractiveFlexoEditor() {
		_moduleContexts = new Hashtable<FlexoModule, ModuleContext>();
		_enabledIcons = new Hashtable<FlexoActionType, Icon>();
		_disabledIcons = new Hashtable<FlexoActionType, Icon>();

		_undoManager = new UndoManager();
		if (ScenarioRecorder.ENABLE) {
			_scenarioRecorder = new ScenarioRecorder();
		}
		_registeredActions = new Vector<FlexoActionType>();
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

	}

	public InteractiveFlexoEditor(FlexoProject project) {
		this();
		_project = project;
		_project.addToEditors(this);
	}

	@Override
	public <A extends org.openflexo.foundation.action.FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> void executeAction(
			final A action) throws org.openflexo.foundation.FlexoException {
		if (action.isLongRunningAction() && SwingUtilities.isEventDispatchThread()) {
			ProgressWindow.showProgressWindow(action.getLocalizedName(), 100);
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					action.execute();
					return null;
				}

				@Override
				protected void done() {
					super.done();
				}
			};
			worker.execute();
		} else {
			action.execute();
		}
	}

	public abstract boolean isAutoSaveEnabledByDefault();

	@Override
	public FlexoProject getProject() {
		return _project;
	}

	public UndoManager getUndoManager() {
		return _undoManager;
	}

	private static final int NUMBER_OF_ACTIONS_BETWEEN_NOTIFICATIONS = 4;

	@Override
	public void actionWillBePerformed(FlexoAction action) {
		_undoManager.actionWillBePerformed(action);
		_currentlyPerformedActionStack.push(action);
		_createdAndNotNotifiedObjects.put(action, new Vector<FlexoModelObject>());
		_deletedAndNotNotifiedObjects.put(action, new Vector<FlexoModelObject>());
	}

	@Override
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

	@Override
	public void actionWillBeUndone(FlexoAction action) {
		_undoManager.actionWillBeUndone(action);
		_currentlyUndoneActionStack.push(action);
	}

	@Override
	public void actionHasBeenUndone(FlexoAction action, boolean success) {
		_undoManager.actionHasBeenUndone(action, success);
		FlexoAction popAction = _currentlyUndoneActionStack.pop();
		if (popAction != action) {
			logger.warning("Expected to pop " + action + " but found " + popAction);
		}
	}

	@Override
	public void actionWillBeRedone(FlexoAction action) {
		_undoManager.actionWillBeRedone(action);
		_currentlyRedoneActionStack.push(action);
	}

	@Override
	public void actionHasBeenRedone(FlexoAction action, boolean success) {
		_undoManager.actionHasBeenRedone(action, success);
		FlexoAction popAction = _currentlyRedoneActionStack.pop();
		if (popAction != action) {
			logger.warning("Expected to pop " + action + " but found " + popAction);
		}
	}

	public void registerAction(FlexoActionType actionType) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Register action " + actionType);
		}
		_registeredActions.add(actionType);
	}

	public void registerIcons(FlexoActionType actionType, Icon enabledIcon, Icon disabledIcon) {
		if (enabledIcon != null) {
			_enabledIcons.put(actionType, enabledIcon);
		}
		if (disabledIcon != null) {
			_disabledIcons.put(actionType, disabledIcon);
		}
	}

	// Only explicitely registered actions are enabled
	@Override
	public boolean isActionEnabled(FlexoActionType actionType) {
		if (actionType instanceof ActionSchemeActionType) {
			return true;
		}
		return _registeredActions.contains(actionType);
	}

	// Only explicitely registered actions are visible
	@Override
	public boolean isActionVisible(FlexoActionType actionType) {
		return _registeredActions.contains(actionType);
	}

	public static final InteractiveFlexoEditorFactory FACTORY = new InteractiveFlexoEditorFactory();

	protected static class InteractiveFlexoEditorFactory implements FlexoEditorFactory {
		@Override
		public InteractiveFlexoEditor makeFlexoEditor(FlexoProject project) {
			return new InteractiveFlexoEditor(project) {

				@Override
				public boolean isAutoSaveEnabledByDefault() {
					return true;
				}

			};
		}
	}

	public static final InteractiveFlexoEditor makeInteractiveEditorWithoutProject() {
		return new InteractiveFlexoEditor() {

			@Override
			public boolean isAutoSaveEnabledByDefault() {
				return true;
			}

		};
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

	// private FlexoAction getCurrentlyPerformedAction()
	// {
	// if (!_currentlyPerformedActionStack.isEmpty()) return _currentlyPerformedActionStack.peek();
	// if (!_currentlyUndoneActionStack.isEmpty()) return _currentlyUndoneActionStack.peek();
	// if (!_currentlyRedoneActionStack.isEmpty()) return _currentlyRedoneActionStack.peek();
	// return null;
	// }

	public FlexoModule getActiveModule() {
		return FlexoModule.getActiveModule();
	}

	@Override
	public FlexoActionFinalizer getFinalizerFor(FlexoActionType actionType) {
		if (moduleContextForModule(getActiveModule()) != null) {
			return moduleContextForModule(getActiveModule()).getFinalizerFor(actionType);
		}
		return null;
	}

	@Override
	public FlexoActionInitializer getInitializerFor(FlexoActionType actionType) {
		if (moduleContextForModule(getActiveModule()) != null) {
			return moduleContextForModule(getActiveModule()).getInitializerFor(actionType);
		}
		return null;
	}

	@Override
	public FlexoActionUndoFinalizer getUndoFinalizerFor(FlexoActionType actionType) {
		if (moduleContextForModule(getActiveModule()) != null) {
			return moduleContextForModule(getActiveModule()).getUndoFinalizerFor(actionType);
		}
		return null;
	}

	@Override
	public FlexoActionUndoInitializer getUndoInitializerFor(FlexoActionType actionType) {
		if (moduleContextForModule(getActiveModule()) != null) {
			return moduleContextForModule(getActiveModule()).getUndoInitializerFor(actionType);
		}
		return null;
	}

	@Override
	public FlexoActionRedoFinalizer getRedoFinalizerFor(FlexoActionType actionType) {
		if (moduleContextForModule(getActiveModule()) != null) {
			return moduleContextForModule(getActiveModule()).getRedoFinalizerFor(actionType);
		}
		return null;
	}

	@Override
	public FlexoActionRedoInitializer getRedoInitializerFor(FlexoActionType actionType) {
		if (moduleContextForModule(getActiveModule()) != null) {
			return moduleContextForModule(getActiveModule()).getRedoInitializerFor(actionType);
		}
		return null;
	}

	@Override
	public FlexoActionEnableCondition getEnableConditionFor(FlexoActionType actionType) {
		if (moduleContextForModule(getActiveModule()) != null) {
			return moduleContextForModule(getActiveModule()).getEnableConditionFor(actionType);
		}
		return null;
	}

	@Override
	public FlexoActionVisibleCondition getVisibleConditionFor(FlexoActionType actionType) {
		if (moduleContextForModule(getActiveModule()) != null) {
			return moduleContextForModule(getActiveModule()).getVisibleConditionFor(actionType);
		}
		return null;
	}

	@Override
	public FlexoExceptionHandler getExceptionHandlerFor(FlexoActionType actionType) {
		if (moduleContextForModule(getActiveModule()) != null) {
			return moduleContextForModule(getActiveModule()).getExceptionHandlerFor(actionType);
		}
		return null;
	}

	private ModuleContext moduleContextForModule(FlexoModule module) {
		if (module == null) {
			return null;
		}
		if (_moduleContexts.get(module) == null) {
			_moduleContexts.put(module, new ModuleContext());
		}
		return _moduleContexts.get(module);
	}

	public void registerFinalizerFor(FlexoActionType actionType, FlexoActionFinalizer finalizer, FlexoModule module) {
		if (moduleContextForModule(module) != null) {
			moduleContextForModule(module).registerFinalizerFor(actionType, finalizer);
		} else {
			logger.warning("Null module !");
		}
	}

	public void registerFinalizerFor(Class<? extends FlexoActionType<?, ?, ?>> aClass, FlexoActionFinalizer finalizer, FlexoModule module) {
		if (moduleContextForModule(module) != null) {
			moduleContextForModule(module).registerFinalizerFor(aClass, finalizer);
		} else {
			logger.warning("Null module !");
		}
	}

	public void registerInitializerFor(FlexoActionType actionType, FlexoActionInitializer initializer, FlexoModule module) {
		if (moduleContextForModule(module) != null) {
			moduleContextForModule(module).registerInitializerFor(actionType, initializer);
		} else {
			logger.warning("Null module !");
		}
	}

	public void registerInitializerFor(Class<? extends FlexoActionType<?, ?, ?>> aClass, FlexoActionInitializer initializer,
			FlexoModule module) {
		if (moduleContextForModule(module) != null) {
			moduleContextForModule(module).registerInitializerFor(aClass, initializer);
		} else {
			logger.warning("Null module !");
		}
	}

	public void registerUndoFinalizerFor(FlexoActionType actionType, FlexoActionUndoFinalizer undoFinalizer, FlexoModule module) {
		if (moduleContextForModule(module) != null) {
			moduleContextForModule(module).registerUndoFinalizerFor(actionType, undoFinalizer);
		} else {
			logger.warning("Null module !");
		}
	}

	public void registerUndoFinalizerFor(Class<? extends FlexoActionType<?, ?, ?>> aClass, FlexoActionUndoFinalizer undoFinalizer,
			FlexoModule module) {
		if (moduleContextForModule(module) != null) {
			moduleContextForModule(module).registerUndoFinalizerFor(aClass, undoFinalizer);
		} else {
			logger.warning("Null module !");
		}
	}

	public void registerUndoInitializerFor(FlexoActionType actionType, FlexoActionUndoInitializer undoInitializer, FlexoModule module) {
		if (moduleContextForModule(module) != null) {
			moduleContextForModule(module).registerUndoInitializerFor(actionType, undoInitializer);
		} else {
			logger.warning("Null module !");
		}
	}

	public void registerUndoInitializerFor(Class<? extends FlexoActionType<?, ?, ?>> aClass, FlexoActionUndoInitializer undoInitializer,
			FlexoModule module) {
		if (moduleContextForModule(module) != null) {
			moduleContextForModule(module).registerUndoInitializerFor(aClass, undoInitializer);
		} else {
			logger.warning("Null module !");
		}
	}

	public void registerRedoFinalizerFor(FlexoActionType actionType, FlexoActionRedoFinalizer redoFinalizer, FlexoModule module) {
		if (moduleContextForModule(module) != null) {
			moduleContextForModule(module).registerRedoFinalizerFor(actionType, redoFinalizer);
		} else {
			logger.warning("Null module !");
		}
	}

	public void registerRedoFinalizerFor(Class<? extends FlexoActionType<?, ?, ?>> aClass, FlexoActionRedoFinalizer redoFinalizer,
			FlexoModule module) {
		if (moduleContextForModule(module) != null) {
			moduleContextForModule(module).registerRedoFinalizerFor(aClass, redoFinalizer);
		} else {
			logger.warning("Null module !");
		}
	}

	public void registerRedoInitializerFor(FlexoActionType actionType, FlexoActionRedoInitializer redoInitializer, FlexoModule module) {
		if (moduleContextForModule(module) != null) {
			moduleContextForModule(module).registerRedoInitializerFor(actionType, redoInitializer);
		} else {
			logger.warning("Null module !");
		}
	}

	public void registerRedoInitializerFor(Class<? extends FlexoActionType<?, ?, ?>> aClass, FlexoActionRedoInitializer redoInitializer,
			FlexoModule module) {
		if (moduleContextForModule(module) != null) {
			moduleContextForModule(module).registerRedoInitializerFor(aClass, redoInitializer);
		} else {
			logger.warning("Null module !");
		}
	}

	public void registerEnableConditionFor(FlexoActionType actionType, FlexoActionEnableCondition enableCondition, FlexoModule module) {
		if (moduleContextForModule(module) != null) {
			moduleContextForModule(module).registerEnableConditionFor(actionType, enableCondition);
		} else {
			logger.warning("Null module !");
		}
	}

	public void registerEnableConditionFor(Class<? extends FlexoActionType<?, ?, ?>> aClass, FlexoActionEnableCondition enableCondition,
			FlexoModule module) {
		if (moduleContextForModule(module) != null) {
			moduleContextForModule(module).registerEnableConditionFor(aClass, enableCondition);
		} else {
			logger.warning("Null module !");
		}
	}

	public void registerVisibleConditionFor(FlexoActionType actionType, FlexoActionVisibleCondition visibleCondition, FlexoModule module) {
		if (moduleContextForModule(module) != null) {
			moduleContextForModule(module).registerVisibleConditionFor(actionType, visibleCondition);
		} else {
			logger.warning("Null module !");
		}
	}

	public void registerVisibleConditionFor(Class<? extends FlexoActionType<?, ?, ?>> aClass, FlexoActionVisibleCondition visibleCondition,
			FlexoModule module) {
		if (moduleContextForModule(module) != null) {
			moduleContextForModule(module).registerVisibleConditionFor(aClass, visibleCondition);
		} else {
			logger.warning("Null module !");
		}
	}

	public void registerExceptionHandlerFor(FlexoActionType actionType, FlexoExceptionHandler exceptionHandler, FlexoModule module) {
		if (moduleContextForModule(module) != null) {
			moduleContextForModule(module).registerExceptionHandlerFor(actionType, exceptionHandler);
		} else {
			logger.warning("Null module !");
		}
	}

	public void registerExceptionHandlerFor(Class<? extends FlexoActionType<?, ?, ?>> aClass, FlexoExceptionHandler exceptionHandler,
			FlexoModule module) {
		if (moduleContextForModule(module) != null) {
			moduleContextForModule(module).registerExceptionHandlerFor(aClass, exceptionHandler);
		} else {
			logger.warning("Null module !");
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
				if (ModuleLoader.instance().getActiveModule() != Module.WKF_MODULE) {
					ModuleLoader.instance().switchToModule(Module.WKF_MODULE, getProject());
				}
			} else if (object instanceof IEObject) {
				if (ModuleLoader.instance().getActiveModule() != Module.IE_MODULE) {
					ModuleLoader.instance().switchToModule(Module.IE_MODULE, getProject());
				}
			} else if (object instanceof DMObject) {
				if (ModuleLoader.instance().getActiveModule() != Module.DM_MODULE) {
					ModuleLoader.instance().switchToModule(Module.DM_MODULE, getProject());
				}
			} else if (object instanceof WSObject) {
				if (ModuleLoader.instance().getActiveModule() != Module.WSE_MODULE) {
					ModuleLoader.instance().switchToModule(Module.WSE_MODULE, getProject());
				}
			} else if (object instanceof ViewObject) {
				if (ModuleLoader.instance().getActiveModule() != Module.VE_MODULE) {
					ModuleLoader.instance().switchToModule(Module.VE_MODULE, getProject());
				}
			}
		} catch (ModuleLoadingException e) {
			logger.warning("Cannot load module " + e.getModule());
			e.printStackTrace();
		}

		// Only interactive editor handle this
		getActiveModule().getFlexoController().setCurrentEditedObjectAsModuleView(object);
		if (getActiveModule().getFlexoController() instanceof SelectionManagingController) {
			((SelectionManagingController) getActiveModule().getFlexoController()).getSelectionManager().setSelectedObject(object);
		}
	}

	@Override
	public Icon getEnabledIconFor(FlexoActionType actionType) {
		return _enabledIcons.get(actionType);
	}

	@Override
	public Icon getDisabledIconFor(FlexoActionType actionType) {
		return _disabledIcons.get(actionType);
	}

}
