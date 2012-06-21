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

import java.util.EventObject;
import java.util.logging.Logger;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.param.CGFileVersionParameter;
import org.openflexo.generator.action.ShowFileVersion;
import org.openflexo.generator.action.ShowFileVersion.ShowFileVersionActionType;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class ShowFileVersionInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	ShowFileVersionInitializer(ShowFileVersionActionType actionType, GeneratorControllerActionInitializer actionInitializer) {
		super(actionType, actionInitializer);
	}

	@Override
	protected GeneratorControllerActionInitializer getControllerActionInitializer() {
		return (GeneratorControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<ShowFileVersion> getDefaultInitializer() {
		return new FlexoActionInitializer<ShowFileVersion>() {
			@Override
			public boolean run(EventObject e, ShowFileVersion action) {
				if (action.getActionType() == ShowFileVersion.showHistoryVersion) {
					CGFileVersionParameter versionParameter = new CGFileVersionParameter("version", "version", action.getFocusedObject(),
							null);
					AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
							FlexoLocalization.localizedForKey("show_history_version"),
							FlexoLocalization.localizedForKey("choose_version_to_show"), versionParameter);
					if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
						action.setVersionId(versionParameter.getValue().getVersionId());
						return true;
					}
					return false;
				}
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<ShowFileVersion> getDefaultFinalizer() {
		return new FlexoActionFinalizer<ShowFileVersion>() {
			@Override
			public boolean run(EventObject e, ShowFileVersion action) {
				getControllerActionInitializer().getGeneratorController()
						.getPopupShowingFileVersion(action.getFocusedObject(), action.getSource()).setVisible(true);
				return true;
			}
		};
	}

}
