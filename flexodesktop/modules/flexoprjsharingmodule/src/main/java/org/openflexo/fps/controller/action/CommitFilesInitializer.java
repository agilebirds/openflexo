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
package org.openflexo.fps.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.icon.FPSIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.param.TextAreaParameter;
import org.openflexo.fps.action.CommitFiles;

public class CommitFilesInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	CommitFilesInitializer(FPSControllerActionInitializer actionInitializer) {
		super(CommitFiles.actionType, actionInitializer);
	}

	@Override
	protected FPSControllerActionInitializer getControllerActionInitializer() {
		return (FPSControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<CommitFiles> getDefaultInitializer() {
		return new FlexoActionInitializer<CommitFiles>() {
			@Override
			public boolean run(ActionEvent e, CommitFiles action) {
				if (action.getCommitMessage() == null) {
					TextAreaParameter commitMessage = new TextAreaParameter("commitMessage", "commit_message", action.getCommitMessage(),
							40, 15);

					AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
							action.getLocalizedName(), FlexoLocalization.localizedForKey("please_supply_commit_message"), commitMessage);
					if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
						action.setCommitMessage(commitMessage.getValue());
						return true;
					} else {
						return false;
					}
				} else {
					return true;
				}
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<CommitFiles> getDefaultFinalizer() {
		return new FlexoActionFinalizer<CommitFiles>() {
			@Override
			public boolean run(ActionEvent e, CommitFiles action) {
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return FPSIconLibrary.FPS_COMMIT_ICON;
	}

	@Override
	protected Icon getDisabledIcon() {
		return FPSIconLibrary.FPS_COMMIT_DISABLED_ICON;
	}

}
