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

import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.openflexo.foundation.FlexoModelObject;
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
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.module.FlexoModule;


public abstract class ActionInitializer<A extends FlexoAction<?,T1,T2>, T1 extends FlexoModelObject,T2 extends FlexoModelObject>
{
	private final ControllerActionInitializer _controllerActionInitializer;
	private final FlexoActionType _actionType;

	public ActionInitializer(FlexoActionType actionType, ControllerActionInitializer controllerActionInitializer)
	{
		super();
		_controllerActionInitializer = controllerActionInitializer;
		_actionType = actionType;
	}

	protected ControllerActionInitializer getControllerActionInitializer()
	{
		return _controllerActionInitializer;
	}

	public InteractiveFlexoEditor getEditor()
	{
		return getController().getEditor();
	}

	public FlexoController getController()
    {
        return _controllerActionInitializer.getController();
    }

	public FlexoModule getModule()
    {
        return getController().getModule();
    }

	public FlexoProject getProject()
    {
        return getController().getProject();
    }


	public void init()
	{
		initActionType(_actionType,
				getDefaultInitializer(),
				getDefaultFinalizer(),
				getDefaultUndoInitializer(),
				getDefaultUndoFinalizer(),
				getDefaultRedoInitializer(),
				getDefaultRedoFinalizer(),
				getDefaultExceptionHandler(),
				getEnableCondition(),
				getVisibleCondition(),
				getShortcut(),
				getEnabledIcon(),
				getDisabledIcon());
	}

	public void initForClass(Class<? extends FlexoActionType<?,?,?>> aClass)
	{
		initActionTypeForClass(aClass,
				getDefaultInitializer(),
				getDefaultFinalizer(),
				getDefaultUndoInitializer(),
				getDefaultUndoFinalizer(),
				getDefaultRedoInitializer(),
				getDefaultRedoFinalizer(),
				getDefaultExceptionHandler(),
				getEnableCondition(),
				getVisibleCondition(),
				getShortcut(),
				getEnabledIcon(),
				getDisabledIcon());
	}

	protected  void initActionType (
			FlexoActionType actionType,
			FlexoActionInitializer initializer,
			FlexoActionFinalizer finalizer,
			FlexoExceptionHandler exceptionHandler,
			FlexoActionEnableCondition enableCondition,
			FlexoActionVisibleCondition visibleCondition,
			KeyStroke shortcut,
			Icon enabledIcon,
			Icon disabledIcon)
	{
		if (initializer != null) {
			getEditor().registerInitializerFor(actionType, initializer, getModule());
		}
		if (finalizer != null) {
			getEditor().registerFinalizerFor(actionType, finalizer, getModule());
		}
		if (exceptionHandler != null) {
			getEditor().registerExceptionHandlerFor(actionType, exceptionHandler, getModule());
		}
		if (enableCondition != null) {
			getEditor().registerEnableConditionFor(actionType, enableCondition, getModule());
		}
		if (visibleCondition != null) {
			getEditor().registerVisibleConditionFor(actionType, visibleCondition, getModule());
		}

		getEditor().registerIcons(actionType, enabledIcon, disabledIcon);
		
		//if (enabledIcon != null) actionType.setSmallIcon(enabledIcon);
		//if (disabledIcon != null) actionType.setSmallDisabledIcon(disabledIcon);
		_controllerActionInitializer.registerAction(actionType,shortcut);

	}

	protected void initActionType (
			FlexoActionType actionType,
			FlexoActionInitializer initializer,
			FlexoActionFinalizer finalizer,
			FlexoActionUndoInitializer undoInitializer,
			FlexoActionUndoFinalizer undoFinalizer,
			FlexoActionRedoInitializer redoInitializer,
			FlexoActionRedoFinalizer redoFinalizer,
			FlexoExceptionHandler exceptionHandler,
			FlexoActionEnableCondition enableCondition,
			FlexoActionVisibleCondition visibleCondition,
			KeyStroke shortcut,
			Icon enabledIcon,
			Icon disabledIcon)
	{
		initActionType(actionType, initializer, finalizer, exceptionHandler, enableCondition, visibleCondition, shortcut, enabledIcon, disabledIcon);

		if (undoInitializer != null) {
			getEditor().registerUndoInitializerFor(actionType, undoInitializer, getModule());
		}
		if (undoFinalizer != null) {
			getEditor().registerUndoFinalizerFor(actionType, undoFinalizer, getModule());
		}
		if (redoInitializer != null) {
			getEditor().registerRedoInitializerFor(actionType, redoInitializer, getModule());
		}
		if (redoFinalizer != null) {
			getEditor().registerRedoFinalizerFor(actionType, redoFinalizer, getModule());
		}

	}

	protected  void initActionTypeForClass (
			Class<? extends FlexoActionType<?,?,?>> aClass,
			FlexoActionInitializer initializer,
			FlexoActionFinalizer finalizer,
			FlexoExceptionHandler exceptionHandler,
			FlexoActionEnableCondition enableCondition,
			FlexoActionVisibleCondition visibleCondition,
			KeyStroke shortcut,
			Icon enabledIcon,
			Icon disabledIcon)
	{
		if (initializer != null) {
			getEditor().registerInitializerFor(aClass, initializer, getModule());
		}
		if (finalizer != null) {
			getEditor().registerFinalizerFor(aClass, finalizer, getModule());
		}
		if (exceptionHandler != null) {
			getEditor().registerExceptionHandlerFor(aClass, exceptionHandler, getModule());
		}
		if (enableCondition != null) {
			getEditor().registerEnableConditionFor(aClass, enableCondition, getModule());
		}
		if (visibleCondition != null) {
			getEditor().registerVisibleConditionFor(aClass, visibleCondition, getModule());
		}

		//if (enabledIcon != null) actionType.setSmallIcon(enabledIcon);
		//if (disabledIcon != null) actionType.setSmallDisabledIcon(disabledIcon);
		//_controllerActionInitializer.registerAction(actionType,shortcut);

	}

	protected void initActionTypeForClass (
			Class<? extends FlexoActionType<?,?,?>> aClass,
			FlexoActionInitializer initializer,
			FlexoActionFinalizer finalizer,
			FlexoActionUndoInitializer undoInitializer,
			FlexoActionUndoFinalizer undoFinalizer,
			FlexoActionRedoInitializer redoInitializer,
			FlexoActionRedoFinalizer redoFinalizer,
			FlexoExceptionHandler exceptionHandler,
			FlexoActionEnableCondition enableCondition,
			FlexoActionVisibleCondition visibleCondition,
			KeyStroke shortcut,
			Icon enabledIcon,
			Icon disabledIcon)
	{
		initActionTypeForClass(aClass, initializer, finalizer, exceptionHandler, enableCondition, visibleCondition, shortcut, enabledIcon, disabledIcon);

		if (undoInitializer != null) {
			getEditor().registerUndoInitializerFor(aClass, undoInitializer, getModule());
		}
		if (undoFinalizer != null) {
			getEditor().registerUndoFinalizerFor(aClass, undoFinalizer, getModule());
		}
		if (redoInitializer != null) {
			getEditor().registerRedoInitializerFor(aClass, redoInitializer, getModule());
		}
		if (redoFinalizer != null) {
			getEditor().registerRedoFinalizerFor(aClass, redoFinalizer, getModule());
		}

	}

	/**
	 * Please override if required
	 * Default implementation return null
	 *
	 * @return null
	 */
	protected FlexoActionInitializer<? super A> getDefaultInitializer()
	{
		return null;
	}

	/**
	 * Please override if required
	 * Default implementation return null
	 *
	 * @return null
	 */
	protected FlexoActionFinalizer<? super A> getDefaultFinalizer()
	{
		return null;
	}

	/**
	 * Please override if required
	 * Default implementation return null
	 *
	 * @return null
	 */
	protected FlexoExceptionHandler<? super A> getDefaultExceptionHandler()
	{
		return null;
	}

	/**
	 * Please override if required
	 * Default implementation return null
	 *
	 * @return null
	 */
	protected FlexoActionEnableCondition getEnableCondition()
	{
		return null;
	}

	/**
	 * Please override if required
	 * Default implementation return null
	 *
	 * @return null
	 */
	protected FlexoActionVisibleCondition getVisibleCondition()
	{
		return null;
	}

	/**
	 * Please override if required
	 * Default implementation return null
	 *
	 * @return null
	 */
	protected KeyStroke getShortcut()
	{
		return null;
	}

	/**
	 * Please override if required
	 * Default implementation return null
	 *
	 * @return null
	 */
	protected Icon getEnabledIcon()
	{
		return null;
	}

	/**
	 * Please override if required
	 * Default implementation return null
	 *
	 * @return null
	 */
	protected Icon getDisabledIcon()
	{
		return null;
	}

	/**
	 * Please override if required
	 * Default implementation return null
	 *
	 * @return null
	 */
	protected FlexoActionUndoInitializer<? super A> getDefaultUndoInitializer()
	{
		return null;
	}

	/**
	 * Please override if required
	 * Default implementation return null
	 *
	 * @return null
	 */
	protected FlexoActionUndoFinalizer<? super A> getDefaultUndoFinalizer()
	{
		return null;
	}

	/**
	 * Please override if required
	 * Default implementation return null
	 *
	 * @return null
	 */
	protected FlexoActionRedoInitializer<? super A> getDefaultRedoInitializer()
	{
		return null;
	}

	/**
	 * Please override if required
	 * Default implementation return null
	 *
	 * @return null
	 */
	protected FlexoActionRedoFinalizer<? super A> getDefaultRedoFinalizer()
	{
		return null;
	}



}