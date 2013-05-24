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

import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoActionRedoFinalizer;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.action.CreateNode;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class CreateNodeInitializer extends ActionInitializer<CreateNode, WKFObject, WKFObject> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());
	private final Icon icon;

	CreateNodeInitializer(FlexoActionType<CreateNode, WKFObject, WKFObject> actionType, Icon icon,
			WKFControllerActionInitializer actionInitializer) {
		super(actionType, actionInitializer);
		this.icon = icon;
	}

	@Override
	protected Icon getEnabledIcon() {
		return icon;
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() {
		return (WKFControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<CreateNode> getDefaultInitializer() {
		return new FlexoActionInitializer<CreateNode>() {
			@Override
			public boolean run(EventObject e, CreateNode action) {
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
			public boolean run(EventObject e, CreateNode action) {
				getControllerActionInitializer().getWKFController().getSelectionManager().setSelectedObject(action.getNewNode());
				return true;
			}
		};
	}

	@Override
	protected FlexoActionRedoFinalizer<CreateNode> getDefaultRedoFinalizer() {
		return new FlexoActionRedoFinalizer<CreateNode>() {
			@Override
			public boolean run(EventObject e, CreateNode action) {
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

}
