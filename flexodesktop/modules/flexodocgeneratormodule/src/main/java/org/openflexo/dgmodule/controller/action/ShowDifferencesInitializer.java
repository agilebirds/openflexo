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

import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.dgmodule.view.popups.DGFileDiffEditorPopup;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.cg.version.action.ShowDifferences;
import org.openflexo.foundation.param.CGFileVersionParameter;
import org.openflexo.foundation.rm.cg.ContentSource;
import org.openflexo.foundation.rm.cg.ContentSource.ContentSourceType;
import org.openflexo.icon.GeneratorIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class ShowDifferencesInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	ShowDifferencesInitializer(DGControllerActionInitializer actionInitializer) {
		super(ShowDifferences.actionType, actionInitializer);
	}

	@Override
	protected DGControllerActionInitializer getControllerActionInitializer() {
		return (DGControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<ShowDifferences> getDefaultInitializer() {
		return new FlexoActionInitializer<ShowDifferences>() {
			@Override
			public boolean run(EventObject e, ShowDifferences action) {
				CGFileVersionParameter versionLeftParameter = new CGFileVersionParameter("versionLeft", "left_version", action.getCGFile(),
						null);
				CGFileVersionParameter versionRightParameter = new CGFileVersionParameter("versionRight", "right_version",
						action.getCGFile(), null);
				AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
						FlexoLocalization.localizedForKey("open_in_diff_editor"),
						FlexoLocalization.localizedForKey("please_choose_versions_that_you_want_to_compare"), versionLeftParameter,
						versionRightParameter);
				if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
					if (versionLeftParameter.getValue() == null || versionRightParameter.getValue() == null) {
						FlexoController.notify(FlexoLocalization.localizedForKey("please_select_valid_versions"));
						return false;
					}
					ContentSource rightSource = ContentSource.getContentSource(ContentSourceType.HistoryVersion, versionRightParameter
							.getValue().getVersionId());
					ContentSource leftSource = ContentSource.getContentSource(ContentSourceType.HistoryVersion, versionLeftParameter
							.getValue().getVersionId());
					action.setRightSource(rightSource);
					action.setLeftSource(leftSource);
					return true;
				}
				return false;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<ShowDifferences> getDefaultFinalizer() {
		return new FlexoActionFinalizer<ShowDifferences>() {
			@Override
			public boolean run(EventObject e, ShowDifferences action) {
				DGFileDiffEditorPopup popup = new DGFileDiffEditorPopup(action.getCGFile(), action.getLeftSource(),
						action.getRightSource(), getControllerActionInitializer().getDGController());
				popup.setVisible(true);
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return GeneratorIconLibrary.COMPARE_ICON;
	}

	@Override
	protected Icon getDisabledIcon() {
		return GeneratorIconLibrary.COMPARE_DISABLED_ICON;
	}

}
