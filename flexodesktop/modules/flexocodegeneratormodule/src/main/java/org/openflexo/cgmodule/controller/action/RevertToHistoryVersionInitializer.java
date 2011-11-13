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
package org.openflexo.cgmodule.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.icon.GeneratorIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.cg.version.action.RevertToHistoryVersion;
import org.openflexo.foundation.param.CGFileVersionParameter;
import org.openflexo.foundation.param.CheckboxParameter;

public class RevertToHistoryVersionInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	RevertToHistoryVersionInitializer(GeneratorControllerActionInitializer actionInitializer) {
		super(RevertToHistoryVersion.actionType, actionInitializer);
	}

	@Override
	protected GeneratorControllerActionInitializer getControllerActionInitializer() {
		return (GeneratorControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<RevertToHistoryVersion> getDefaultInitializer() {
		return new FlexoActionInitializer<RevertToHistoryVersion>() {
			@Override
			public boolean run(ActionEvent e, RevertToHistoryVersion action) {
				if (action.getVersionId() == null) {
					CGFileVersionParameter versionParameter = new CGFileVersionParameter("version", "version", action.getCGFile(), null);
					CheckboxParameter doItNowParameter = new CheckboxParameter("doItNow", "do_it_immediately", false);
					AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
							action.getLocalizedName(), FlexoLocalization.localizedForKey("please_choose_version_to_override_with"),
							versionParameter, doItNowParameter);
					if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
						if (versionParameter.getValue() == null) {
							FlexoController.notify(FlexoLocalization.localizedForKey("please_select_valid_version"));
							return false;
						}
						action.setVersionId(versionParameter.getValue().getVersionId());
						action.setDoItNow(doItNowParameter.getValue());
						return true;
					}
					return false;
				}
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<RevertToHistoryVersion> getDefaultFinalizer() {
		return new FlexoActionFinalizer<RevertToHistoryVersion>() {
			@Override
			public boolean run(ActionEvent e, RevertToHistoryVersion action) {
				getControllerActionInitializer().getGeneratorController().switchToPerspective(
						getControllerActionInitializer().getGeneratorController().CODE_GENERATOR_PERSPECTIVE);
				getControllerActionInitializer().getGeneratorController().selectAndFocusObject(action.getCGFile());
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return GeneratorIconLibrary.REVERT_TO_HISTORY_VERSION_ICON;
	}

	@Override
	protected Icon getDisabledIcon() {
		return GeneratorIconLibrary.REVERT_TO_HISTORY_VERSION_DISABLED_ICON;
	}

}
