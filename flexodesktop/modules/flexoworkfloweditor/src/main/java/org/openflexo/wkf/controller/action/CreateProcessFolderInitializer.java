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

import javax.swing.Icon;

import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.ParametersModel;
import org.openflexo.foundation.param.TextAreaParameter;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.foundation.wkf.action.CreateProcessFolder;

public class CreateProcessFolderInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	CreateProcessFolderInitializer(WKFControllerActionInitializer actionInitializer) {
		super(CreateProcessFolder.actionType, actionInitializer);
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() {
		return (WKFControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<CreateProcessFolder> getDefaultInitializer() {
		return new FlexoActionInitializer<CreateProcessFolder>() {
			@Override
			public boolean run(ActionEvent e, CreateProcessFolder action) {
				ParameterDefinition[] parameters = new ParameterDefinition[2];
				final TextFieldParameter name = new TextFieldParameter("name", "name", action.getName());
				parameters[0] = name;
				final TextAreaParameter desc = new TextAreaParameter("description", "description", "");
				parameters[1] = desc;
				parameters[1].addParameter("columns", "20");
				AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
						FlexoLocalization.localizedForKey("create_new_process_folder"),
						FlexoLocalization.localizedForKey("enter_parameters_for_the_new_folder"),
						new AskParametersDialog.ValidationCondition() {

							@Override
							public boolean isValid(ParametersModel model) {
								return name.getValue() != null && name.getValue().trim().length() > 0;
							}

							@Override
							public String getErrorMessage() {
								if (!(name.getValue() != null && name.getValue().trim().length() > 0)) {
									return FlexoLocalization.localizedForKey("name_cannot_be_empty");
								}
								return null;
							}
						}, parameters);
				if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
					action.setName(name.getValue());
					action.setDescription(desc.getValue());
					return true;
				} else {
					return false;
				}
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<CreateProcessFolder> getDefaultFinalizer() {
		return new FlexoActionFinalizer<CreateProcessFolder>() {
			@Override
			public boolean run(ActionEvent e, CreateProcessFolder action) {
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<CreateProcessFolder> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<CreateProcessFolder>() {
			@Override
			public boolean handleException(FlexoException exception, CreateProcessFolder action) {
				return false;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return WKFIconLibrary.PROCESS_FOLDER_ICON;
	}

}
