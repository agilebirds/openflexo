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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;

import org.openflexo.action.ImportProjectInitializer;
import org.openflexo.action.ProjectExcelExportInitializer;
import org.openflexo.action.RemoveImportedProjectInitializer;
import org.openflexo.action.UploadPrjInitializer;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoProperty;
import org.openflexo.foundation.action.AddFlexoProperty;
import org.openflexo.foundation.action.DeleteFlexoProperty;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionEnableCondition;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoActionRedoFinalizer;
import org.openflexo.foundation.action.FlexoActionRedoInitializer;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoActionUndoFinalizer;
import org.openflexo.foundation.action.FlexoActionUndoInitializer;
import org.openflexo.foundation.action.FlexoActionVisibleCondition;
import org.openflexo.foundation.action.FlexoActionizer;
import org.openflexo.foundation.action.FlexoActionizer.EditorProvider;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.action.SortFlexoProperties;
import org.openflexo.module.FlexoModule;
import org.openflexo.view.controller.action.AddFlexoPropertyActionizer;
import org.openflexo.view.controller.action.DeleteFlexoPropertyActionizer;
import org.openflexo.view.controller.action.HelpActionizer;
import org.openflexo.view.controller.action.InspectActionizer;
import org.openflexo.view.controller.action.LoadAllImportedProjectInitializer;
import org.openflexo.view.controller.action.RefreshImportedProcessesActionInitializer;
import org.openflexo.view.controller.action.RefreshImportedRolesActionInitializer;
import org.openflexo.view.controller.action.SortFlexoPropertiesActionizer;
import org.openflexo.view.controller.action.SubmitDocumentationActionizer;

public class ControllerActionInitializer implements EditorProvider {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(ControllerActionInitializer.class
			.getPackage().getName());

	private final FlexoController _controller;

	private Map<FlexoActionType<?, ?, ?>, ActionInitializer<?, ?, ?>> initializers;
	private Map<Class<?>, ActionInitializer<?, ?, ?>> initializersByActionClass;

	protected ControllerActionInitializer(FlexoController controller) {
		super();
		initializers = new Hashtable<FlexoActionType<?, ?, ?>, ActionInitializer<?, ?, ?>>();
		initializersByActionClass = new Hashtable<Class<?>, ActionInitializer<?, ?, ?>>();
		_controller = controller;
		initializeActions();
	}

	public Map<FlexoActionType<?, ?, ?>, ActionInitializer<?, ?, ?>> getActionInitializers() {
		return initializers;
	}

	public FlexoModule getFlexoModule() {
		return getController().getModule();
	}

	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> void registerInitializer(
			FlexoActionType<A, T1, T2> actionType, ActionInitializer<A, T1, T2> initializer) {
		if (actionType != null) {
			initializers.put(actionType, initializer);
		} else {
			Type superClass = initializer.getClass().getGenericSuperclass();
			if (superClass instanceof ParameterizedType) {
				Class<?> actionClass = TypeUtils.getBaseClass(((ParameterizedType) superClass).getActualTypeArguments()[0]);
				initializersByActionClass.put(actionClass, initializer);
			} else {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("You registered an action initializer without providing an action type and this method does not know how to retrieve the action Action class.");
				}
			}
		}
	}

	@Override
	public FlexoEditor getEditor() {
		return getController().getEditor();
	}

	public FlexoController getController() {
		return _controller;
	}

	public FlexoModule getModule() {
		return getController().getModule();
	}

	public void initializeActions() {
		new InspectActionizer(this);
		new ImportProjectInitializer(this);
		new RemoveImportedProjectInitializer(this);
		new HelpActionizer(this);
		new SubmitDocumentationActionizer(this);
		new RefreshImportedRolesActionInitializer(this);
		new RefreshImportedProcessesActionInitializer(this);
		new UploadPrjInitializer(this);
		new ProjectExcelExportInitializer(this);
		new LoadAllImportedProjectInitializer(this);

		new AddFlexoPropertyActionizer(this);
		new DeleteFlexoPropertyActionizer(this);
		new SortFlexoPropertiesActionizer(this);
		if (FlexoModelObject.addFlexoPropertyActionizer == null) {
			FlexoModelObject.addFlexoPropertyActionizer = new FlexoActionizer<AddFlexoProperty, FlexoModelObject, FlexoModelObject>(
					AddFlexoProperty.actionType, this);
		}
		if (FlexoModelObject.sortFlexoPropertiesActionizer == null) {
			FlexoModelObject.sortFlexoPropertiesActionizer = new FlexoActionizer<SortFlexoProperties, FlexoModelObject, FlexoModelObject>(
					SortFlexoProperties.actionType, this);
		}
		if (FlexoModelObject.deleteFlexoPropertyActionizer == null) {
			FlexoModelObject.deleteFlexoPropertyActionizer = new FlexoActionizer<DeleteFlexoProperty, FlexoProperty, FlexoProperty>(
					DeleteFlexoProperty.actionType, this);
		}
	}

	@SuppressWarnings("unchecked")
	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> ActionInitializer<A, T1, T2> getActionInitializer(
			FlexoActionType<A, T1, T2> actionType) {
		ActionInitializer<A, T1, T2> actionInitializer = (ActionInitializer<A, T1, T2>) initializers.get(actionType);
		if (actionInitializer == null) {
			Type superClass = actionType.getClass().getGenericSuperclass();
			if (superClass instanceof ParameterizedType) {
				Class<?> actionClass = TypeUtils.getBaseClass(((ParameterizedType) superClass).getActualTypeArguments()[0]);
				actionInitializer = (ActionInitializer<A, T1, T2>) initializersByActionClass.get(actionClass);
			}
		}
		return actionInitializer;
	}

	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> FlexoActionFinalizer<A> getFinalizerFor(
			FlexoActionType<A, T1, T2> actionType) {
		ActionInitializer<A, T1, T2> initializer = getActionInitializer(actionType);
		if (initializer != null) {
			return initializer.getDefaultFinalizer();
		}
		return null;
	}

	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> FlexoActionInitializer<A> getInitializerFor(
			FlexoActionType<A, T1, T2> actionType) {
		ActionInitializer<A, T1, T2> initializer = getActionInitializer(actionType);
		if (initializer != null) {
			return initializer.getDefaultInitializer();
		}
		return null;
	}

	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> FlexoActionUndoFinalizer<A> getUndoFinalizerFor(
			FlexoActionType<A, T1, T2> actionType) {
		ActionInitializer<A, T1, T2> initializer = getActionInitializer(actionType);
		if (initializer != null) {
			return initializer.getDefaultUndoFinalizer();
		}
		return null;
	}

	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> FlexoActionUndoInitializer<A> getUndoInitializerFor(
			FlexoActionType<A, T1, T2> actionType) {
		ActionInitializer<A, T1, T2> initializer = getActionInitializer(actionType);
		if (initializer != null) {
			return initializer.getDefaultUndoInitializer();
		}
		return null;
	}

	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> FlexoActionRedoFinalizer<A> getRedoFinalizerFor(
			FlexoActionType<A, T1, T2> actionType) {
		ActionInitializer<A, T1, T2> initializer = getActionInitializer(actionType);
		if (initializer != null) {
			return initializer.getDefaultRedoFinalizer();
		}
		return null;
	}

	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> FlexoActionRedoInitializer<A> getRedoInitializerFor(
			FlexoActionType<A, T1, T2> actionType) {
		ActionInitializer<A, T1, T2> initializer = getActionInitializer(actionType);
		if (initializer != null) {
			return initializer.getDefaultRedoInitializer();
		}
		return null;
	}

	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> FlexoActionEnableCondition<A, T1, T2> getEnableConditionFor(
			FlexoActionType<A, T1, T2> actionType) {
		ActionInitializer<A, T1, T2> initializer = getActionInitializer(actionType);
		if (initializer != null) {
			return initializer.getEnableCondition();
		}
		return null;
	}

	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> FlexoActionVisibleCondition<A, T1, T2> getVisibleConditionFor(
			FlexoActionType<A, T1, T2> actionType) {
		ActionInitializer<A, T1, T2> initializer = getActionInitializer(actionType);
		if (initializer != null) {
			return initializer.getVisibleCondition();
		}
		return null;
	}

	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> FlexoExceptionHandler<A> getExceptionHandlerFor(
			FlexoActionType<A, T1, T2> actionType) {
		ActionInitializer<A, T1, T2> initializer = getActionInitializer(actionType);
		if (initializer != null) {
			return initializer.getDefaultExceptionHandler();
		}
		return null;
	}

}