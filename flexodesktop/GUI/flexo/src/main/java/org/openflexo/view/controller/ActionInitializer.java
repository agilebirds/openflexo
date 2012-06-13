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

public abstract class ActionInitializer<A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> {
	private final ControllerActionInitializer _controllerActionInitializer;
	private final FlexoActionType<A, T1, T2> _actionType;

	public ActionInitializer(FlexoActionType<A, T1, T2> actionType, ControllerActionInitializer controllerActionInitializer) {
		super();
		_controllerActionInitializer = controllerActionInitializer;
		_actionType = actionType;
		_controllerActionInitializer.registerInitializer(_actionType, this);
	}

	protected ControllerActionInitializer getControllerActionInitializer() {
		return _controllerActionInitializer;
	}

	public InteractiveFlexoEditor getEditor() {
		return getControllerActionInitializer().getEditor();
	}

	public FlexoController getController() {
		return _controllerActionInitializer.getController();
	}

	public FlexoModule getModule() {
		return getController().getModule();
	}

	public FlexoProject getProject() {
		return getEditor().getProject();
	}

	/**
	 * Please override if required Default implementation return null
	 * 
	 * @return null
	 */
	protected FlexoActionInitializer<A> getDefaultInitializer() {
		return null;
	}

	/**
	 * Please override if required Default implementation return null
	 * 
	 * @return null
	 */
	protected FlexoActionFinalizer<A> getDefaultFinalizer() {
		return null;
	}

	/**
	 * Please override if required Default implementation return null
	 * 
	 * @return null
	 */
	protected FlexoExceptionHandler<A> getDefaultExceptionHandler() {
		return null;
	}

	/**
	 * Please override if required Default implementation return null
	 * 
	 * @return null
	 */
	protected FlexoActionEnableCondition<A, T1, T2> getEnableCondition() {
		return null;
	}

	/**
	 * Please override if required Default implementation return null
	 * 
	 * @return null
	 */
	protected FlexoActionVisibleCondition<A, T1, T2> getVisibleCondition() {
		return null;
	}

	/**
	 * Please override if required Default implementation return null
	 * 
	 * @return null
	 */
	protected KeyStroke getShortcut() {
		return null;
	}

	/**
	 * Please override if required Default implementation return null
	 * 
	 * @return null
	 */
	protected Icon getEnabledIcon() {
		return null;
	}

	/**
	 * Please override if required Default implementation return null
	 * 
	 * @return null
	 */
	protected Icon getDisabledIcon() {
		return null;
	}

	/**
	 * Please override if required Default implementation return null
	 * 
	 * @return null
	 */
	protected FlexoActionUndoInitializer<A> getDefaultUndoInitializer() {
		return null;
	}

	/**
	 * Please override if required Default implementation return null
	 * 
	 * @return null
	 */
	protected FlexoActionUndoFinalizer<A> getDefaultUndoFinalizer() {
		return null;
	}

	/**
	 * Please override if required Default implementation return null
	 * 
	 * @return null
	 */
	protected FlexoActionRedoInitializer<A> getDefaultRedoInitializer() {
		return null;
	}

	/**
	 * Please override if required Default implementation return null
	 * 
	 * @return null
	 */
	protected FlexoActionRedoFinalizer<A> getDefaultRedoFinalizer() {
		return null;
	}

}