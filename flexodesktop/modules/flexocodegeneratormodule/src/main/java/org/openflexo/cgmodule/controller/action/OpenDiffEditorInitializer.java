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

import javax.swing.Icon;

import org.openflexo.cgmodule.view.popups.CGFileDiffEditorPopup;
import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.param.CGFileVersionParameter;
import org.openflexo.foundation.param.EnumDropDownParameter;
import org.openflexo.foundation.rm.cg.ContentSource;
import org.openflexo.foundation.rm.cg.ContentSource.ContentSourceType;
import org.openflexo.generator.action.OpenDiffEditor;
import org.openflexo.generator.action.ShowFileVersion;
import org.openflexo.icon.GeneratorIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class OpenDiffEditorInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	OpenDiffEditorInitializer(GeneratorControllerActionInitializer actionInitializer) {
		super(OpenDiffEditor.actionType, actionInitializer);
	}

	@Override
	protected GeneratorControllerActionInitializer getControllerActionInitializer() {
		return (GeneratorControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<OpenDiffEditor> getDefaultInitializer() {
		return new FlexoActionInitializer<OpenDiffEditor>() {
			@Override
			public boolean run(EventObject e, final OpenDiffEditor action) {
				EnumDropDownParameter<ContentSourceType> leftSourceParam = new EnumDropDownParameter<ContentSourceType>("leftSource",
						"left_source", null, ContentSourceType.values()) {
					@Override
					public boolean accept(ContentSourceType value) {
						return ShowFileVersion.getActionTypeFor(value).isEnabled(action.getFocusedObject(), null,
								getController().getEditor());
					}
				};
				leftSourceParam.addParameter("showReset", "false");
				CGFileVersionParameter versionLeftParameter = new CGFileVersionParameter("versionLeft", "version",
						action.getFocusedObject(), null);
				versionLeftParameter.setDepends("leftSource");
				versionLeftParameter.setConditional("leftSource=" + '"' + ContentSourceType.HistoryVersion.getStringRepresentation() + '"');
				EnumDropDownParameter<ContentSourceType> rightSourceParam = new EnumDropDownParameter<ContentSourceType>("rightSource",
						"right_source", null, ContentSourceType.values()) {
					@Override
					public boolean accept(ContentSourceType value) {
						return ShowFileVersion.getActionTypeFor(value).isEnabled(action.getFocusedObject(), null,
								getController().getEditor());
					}
				};
				rightSourceParam.addParameter("showReset", "false");
				CGFileVersionParameter versionRightParameter = new CGFileVersionParameter("versionRight", "version",
						action.getFocusedObject(), null);
				versionRightParameter.setDepends("rightSource");
				versionRightParameter.setConditional("rightSource=" + '"' + ContentSourceType.HistoryVersion.getStringRepresentation()
						+ '"');
				AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
						FlexoLocalization.localizedForKey("open_in_diff_editor"),
						FlexoLocalization.localizedForKey("please_choose_sources_that_you_want_to_compare"), leftSourceParam,
						versionLeftParameter, rightSourceParam, versionRightParameter);
				if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
					if ((rightSourceParam.getValue() == null) || (leftSourceParam.getValue() == null)) {
						FlexoController.notify(FlexoLocalization.localizedForKey("please_select_valid_sources"));
						return false;
					}
					ContentSource rightSource = ContentSource.getContentSource(rightSourceParam.getValue(),
							(versionRightParameter.getValue() != null ? versionRightParameter.getValue().getVersionId() : null));
					ContentSource leftSource = ContentSource.getContentSource(leftSourceParam.getValue(),
							(versionLeftParameter.getValue() != null ? versionLeftParameter.getValue().getVersionId() : null));
					action.setRightSource(rightSource);
					action.setLeftSource(leftSource);
					return true;
				}
				return false;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<OpenDiffEditor> getDefaultFinalizer() {
		return new FlexoActionFinalizer<OpenDiffEditor>() {
			@Override
			public boolean run(EventObject e, OpenDiffEditor action) {
				CGFileDiffEditorPopup popup = new CGFileDiffEditorPopup(action.getFocusedObject(), action.getLeftSource(),
						action.getRightSource(), getControllerActionInitializer().getGeneratorController());
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
