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
package org.openflexo.wkf.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoActionRedoFinalizer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.foundation.wkf.action.CreateNode;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class CreateNodeInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	CreateNodeInitializer(WKFControllerActionInitializer actionInitializer) {
		super(null, actionInitializer);
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() {
		return (WKFControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<CreateNode> getDefaultInitializer() {
		return new FlexoActionInitializer<CreateNode>() {
			@Override
			public boolean run(ActionEvent e, CreateNode action) {
				if (!action.isNewNodeNameInitialized()) {
					TextFieldParameter newNodeNameParam = new TextFieldParameter("newBeginNodeName", "new_node_name",
							action.getNewNodeName());
					AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
							action.getLocalizedName(), FlexoLocalization.localizedForKey("please_enter_name_for_newly_created_node"),
							newNodeNameParam);
					if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
						action.setNewNodeName(newNodeNameParam.getValue());
						return true;
					} else {
						return false;
					}
				}
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<CreateNode> getDefaultFinalizer() {
		return new FlexoActionFinalizer<CreateNode>() {
			@Override
			public boolean run(ActionEvent e, CreateNode action) {
				getControllerActionInitializer().getWKFController().getSelectionManager().setSelectedObject(action.getNewNode());
				return true;
			}
		};
	}

	@Override
	protected FlexoActionRedoFinalizer<CreateNode> getDefaultRedoFinalizer() {
		return new FlexoActionRedoFinalizer<CreateNode>() {
			@Override
			public boolean run(ActionEvent e, CreateNode action) {
				getControllerActionInitializer().getWKFController().getSelectionManager().setSelectedObject(action.getNewNode());
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<CreateNode> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<CreateNode>() {
			@Override
			public boolean handleException(FlexoException exception, CreateNode action) {
				return false;
			}
		};
	}

	@Override
	public void init() {
		initActionType(CreateNode.createActivityBeginNode, getDefaultInitializer(), getDefaultFinalizer(), getDefaultUndoInitializer(),
				getDefaultUndoFinalizer(), getDefaultRedoInitializer(), getDefaultRedoFinalizer(), getDefaultExceptionHandler(),
				getEnableCondition(), getVisibleCondition(), getShortcut(), WKFIconLibrary.BEGIN_ACTIVITY_ICON, getDisabledIcon());
		initActionType(CreateNode.createActivityEndNode, getDefaultInitializer(), getDefaultFinalizer(), getDefaultUndoInitializer(),
				getDefaultUndoFinalizer(), getDefaultRedoInitializer(), getDefaultRedoFinalizer(), getDefaultExceptionHandler(),
				getEnableCondition(), getVisibleCondition(), getShortcut(), WKFIconLibrary.END_ACTIVITY_ICON, getDisabledIcon());
		initActionType(CreateNode.createActivityNormalNode, getDefaultInitializer(), getDefaultFinalizer(), getDefaultUndoInitializer(),
				getDefaultUndoFinalizer(), getDefaultRedoInitializer(), getDefaultRedoFinalizer(), getDefaultExceptionHandler(),
				getEnableCondition(), getVisibleCondition(), getShortcut(), WKFIconLibrary.ACTIVITY_NODE_ICON, getDisabledIcon());

		initActionType(CreateNode.createOperationBeginNode, getDefaultInitializer(), getDefaultFinalizer(), getDefaultUndoInitializer(),
				getDefaultUndoFinalizer(), getDefaultRedoInitializer(), getDefaultRedoFinalizer(), getDefaultExceptionHandler(),
				getEnableCondition(), getVisibleCondition(), getShortcut(), WKFIconLibrary.BEGIN_OPERATION_ICON, getDisabledIcon());
		initActionType(CreateNode.createOperationEndNode, getDefaultInitializer(), getDefaultFinalizer(), getDefaultUndoInitializer(),
				getDefaultUndoFinalizer(), getDefaultRedoInitializer(), getDefaultRedoFinalizer(), getDefaultExceptionHandler(),
				getEnableCondition(), getVisibleCondition(), getShortcut(), WKFIconLibrary.END_OPERATION_ICON, getDisabledIcon());
		initActionType(CreateNode.createOperationNormalNode, getDefaultInitializer(), getDefaultFinalizer(), getDefaultUndoInitializer(),
				getDefaultUndoFinalizer(), getDefaultRedoInitializer(), getDefaultRedoFinalizer(), getDefaultExceptionHandler(),
				getEnableCondition(), getVisibleCondition(), getShortcut(), WKFIconLibrary.OPERATION_NODE_ICON, getDisabledIcon());

		initActionType(CreateNode.createActionBeginNode, getDefaultInitializer(), getDefaultFinalizer(), getDefaultUndoInitializer(),
				getDefaultUndoFinalizer(), getDefaultRedoInitializer(), getDefaultRedoFinalizer(), getDefaultExceptionHandler(),
				getEnableCondition(), getVisibleCondition(), getShortcut(), WKFIconLibrary.BEGIN_ACTION_ICON, getDisabledIcon());
		initActionType(CreateNode.createActionEndNode, getDefaultInitializer(), getDefaultFinalizer(), getDefaultUndoInitializer(),
				getDefaultUndoFinalizer(), getDefaultRedoInitializer(), getDefaultRedoFinalizer(), getDefaultExceptionHandler(),
				getEnableCondition(), getVisibleCondition(), getShortcut(), WKFIconLibrary.END_ACTION_ICON, getDisabledIcon());
		initActionType(CreateNode.createActionNormalNode, getDefaultInitializer(), getDefaultFinalizer(), getDefaultUndoInitializer(),
				getDefaultUndoFinalizer(), getDefaultRedoInitializer(), getDefaultRedoFinalizer(), getDefaultExceptionHandler(),
				getEnableCondition(), getVisibleCondition(), getShortcut(), WKFIconLibrary.ACTION_NODE_ICON, getDisabledIcon());
	}

}
