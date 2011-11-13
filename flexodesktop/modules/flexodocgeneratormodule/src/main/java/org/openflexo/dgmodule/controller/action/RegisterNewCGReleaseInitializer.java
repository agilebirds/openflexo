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
package org.openflexo.dgmodule.controller.action;

import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.icon.GeneratorIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.cg.version.CGVersionIdentifier;
import org.openflexo.foundation.cg.version.action.RegisterNewCGRelease;
import org.openflexo.foundation.param.RadioButtonListParameter;
import org.openflexo.foundation.param.ReadOnlyTextFieldParameter;
import org.openflexo.foundation.param.TextAreaParameter;
import org.openflexo.foundation.param.TextFieldParameter;

public class RegisterNewCGReleaseInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	RegisterNewCGReleaseInitializer(DGControllerActionInitializer actionInitializer) {
		super(RegisterNewCGRelease.actionType, actionInitializer);
	}

	@Override
	protected DGControllerActionInitializer getControllerActionInitializer() {
		return (DGControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<RegisterNewCGRelease> getDefaultInitializer() {
		return new FlexoActionInitializer<RegisterNewCGRelease>() {
			@Override
			public boolean run(ActionEvent e, final RegisterNewCGRelease action) {
				Date registeringDate = new Date();
				TextFieldParameter nameParam = new TextFieldParameter("name", "name", "");
				ReadOnlyTextFieldParameter dateParam = new ReadOnlyTextFieldParameter("date", "date", (new SimpleDateFormat(
						"dd/MM HH:mm:ss")).format(registeringDate));
				ReadOnlyTextFieldParameter userParam = new ReadOnlyTextFieldParameter("userId", "user_id",
						FlexoModelObject.getCurrentUserIdentifier());
				final RadioButtonListParameter<RegisterNewCGRelease.IncrementType> incTypeParam = new RadioButtonListParameter<RegisterNewCGRelease.IncrementType>(
						"incType", "inc_type_choice", RegisterNewCGRelease.IncrementType.IncrementMinor,
						RegisterNewCGRelease.IncrementType.values());
				ReadOnlyTextFieldParameter versionIdParam = new ReadOnlyTextFieldParameter("newVersionId", "version_identifier", "???") {
					@Override
					public String getValue() {
						if (incTypeParam.getValue() == RegisterNewCGRelease.IncrementType.IncrementMajor) {
							return action.getFocusedObject().getLastReleaseVersionIdentifier().newVersionByIncrementingMajor().toString();
						} else if (incTypeParam.getValue() == RegisterNewCGRelease.IncrementType.IncrementMinor) {
							return action.getFocusedObject().getLastReleaseVersionIdentifier().newVersionByIncrementingMinor().toString();
						}
						return null;
					}
				};
				versionIdParam.setDepends("incType");
				TextAreaParameter descriptionParam = new TextAreaParameter("description", "description", "", 40, 5);

				AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null, action.getLocalizedName(),
						FlexoLocalization.localizedForKey("enter_parameters_for_the_new_release"), nameParam, dateParam, userParam,
						versionIdParam, incTypeParam, descriptionParam);
				if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
					if (nameParam.getValue() == null || nameParam.getValue().trim().equals("")) {
						FlexoController.showError(FlexoLocalization.localizedForKey("please_supply_a_valid_name"));
						return false;
					}
					CGVersionIdentifier newVersionId = null;
					if (incTypeParam.getValue() == RegisterNewCGRelease.IncrementType.IncrementMajor) {
						newVersionId = action.getFocusedObject().getLastReleaseVersionIdentifier().newVersionByIncrementingMajor();
					} else if (incTypeParam.getValue() == RegisterNewCGRelease.IncrementType.IncrementMinor) {
						newVersionId = action.getFocusedObject().getLastReleaseVersionIdentifier().newVersionByIncrementingMinor();
					}
					action.setName(nameParam.getValue());
					action.setDescription(descriptionParam.getValue());
					action.setDate(registeringDate);
					action.setUserId(FlexoModelObject.getCurrentUserIdentifier());
					action.setVersionIdentifier(newVersionId);
					return true;
				} else {
					return false;
				}
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<RegisterNewCGRelease> getDefaultFinalizer() {
		return new FlexoActionFinalizer<RegisterNewCGRelease>() {
			@Override
			public boolean run(ActionEvent e, RegisterNewCGRelease action) {
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return GeneratorIconLibrary.REGISTER_NEW_CG_RELEASE_ICON;
	}

	@Override
	protected Icon getDisabledIcon() {
		return GeneratorIconLibrary.REGISTER_NEW_CG_RELEASE_DISABLED_ICON;
	}

}
