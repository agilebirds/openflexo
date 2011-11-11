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
package org.openflexo.dre.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import org.openflexo.action.SubmitDocumentationAction;
import org.openflexo.components.AskParametersDialog;
import org.openflexo.dre.AbstractDocItemView;
import org.openflexo.dre.SubmitNewVersionPopup;
import org.openflexo.drm.DocItemAction;
import org.openflexo.drm.DocItemVersion;
import org.openflexo.drm.DocResourceManager;
import org.openflexo.drm.Language;
import org.openflexo.drm.action.SubmitVersion;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.param.DynamicDropDownParameter;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.ReadOnlyTextFieldParameter;
import org.openflexo.foundation.param.TextAreaParameter;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class SubmitVersionInitializer extends ActionInitializer {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	SubmitVersionInitializer(DREControllerActionInitializer actionInitializer) {
		super(SubmitVersion.actionType, actionInitializer);
	}

	@Override
	protected DREControllerActionInitializer getControllerActionInitializer() {
		return (DREControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<SubmitVersion> getDefaultInitializer() {
		return new FlexoActionInitializer<SubmitVersion>() {
			@Override
			public boolean run(ActionEvent e, SubmitVersion action) {
				if ((action.getContext() != null) && (action.getContext() instanceof SubmitDocumentationAction)) {
					// In this case, action is a consequency of a SubmitDocumentationAction
					// launched from anywhere, no need to perform initializer here
					return true;
				}

				if (action.getDocItem() == null)
					return false;
				if (action.getVersion() == null) {
					Language language = null;
					if (action.getDocItem().getDocResourceCenter().getLanguages().size() > 1) {
						ParameterDefinition[] langParams = new ParameterDefinition[1];
						langParams[0] = new DynamicDropDownParameter("language", "language", action.getDocItem().getDocResourceCenter()
								.getLanguages(), action.getDocItem().getDocResourceCenter().getLanguages().firstElement());
						langParams[0].addParameter("format", "name");
						AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
								FlexoLocalization.localizedForKey("choose_language"),
								FlexoLocalization.localizedForKey("define_submission_language"), langParams);
						if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
							language = (Language) dialog.parameterValueWithName("language");
						} else {
							return false;
						}
					} else if (action.getDocItem().getDocResourceCenter().getLanguages().size() == 1) {
						language = action.getDocItem().getDocResourceCenter().getLanguages().firstElement();
					}
					if (language == null)
						return false;
					SubmitNewVersionPopup editVersionPopup = new SubmitNewVersionPopup(action.getDocItem(), language,
							getControllerActionInitializer().getDREController().getFlexoFrame(), getControllerActionInitializer()
									.getDREController().getEditor());
					action.setVersion(editVersionPopup.getVersionToSubmit());
				}
				if (action.getVersion() == null) {
					return false;
				}
				String title;
				DocItemAction lastAction = action.getDocItem().getLastActionForLanguage(action.getVersion().getLanguage());
				if (lastAction == null) {
					title = FlexoLocalization.localizedForKey("submit_version");
				} else {
					title = FlexoLocalization.localizedForKey("review_version");
					action.getVersion().setVersion(
							DocItemVersion.Version.versionByIncrementing(lastAction.getVersion().getVersion(), 0, 0, 1));
				}
				ParameterDefinition[] parameters = new ParameterDefinition[4];
				parameters[0] = new ReadOnlyTextFieldParameter("user", "username", DocResourceManager.instance().getUser().getIdentifier());
				parameters[1] = new ReadOnlyTextFieldParameter("language", "language", action.getVersion().getLanguageId());
				parameters[2] = new TextFieldParameter("version", "version", action.getVersion().getVersion().toString());
				parameters[3] = new TextAreaParameter("note", "note", "", 25, 3);
				AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null, title,
						FlexoLocalization.localizedForKey("define_submission_parameters"), parameters);
				if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
					action.setAuthor(DocResourceManager.instance().getUser());
					String versionId = (String) dialog.parameterValueWithName("version");
					action.getVersion().setVersion(new DocItemVersion.Version(versionId));
					action.setNote((String) dialog.parameterValueWithName("note"));
					return true;
				} else {
					return false;
				}
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<SubmitVersion> getDefaultFinalizer() {
		return new FlexoActionFinalizer<SubmitVersion>() {
			@Override
			public boolean run(ActionEvent e, SubmitVersion action) {
				if (getControllerActionInitializer().getDREController().getCurrentDisplayedObjectAsModuleView() == action.getDocItem()) {
					AbstractDocItemView docItemView = (AbstractDocItemView) getControllerActionInitializer().getDREController()
							.getCurrentModuleView();
					docItemView.updateViewFromModel();
					docItemView.setCurrentAction(action.getNewAction());
				}
				return true;
			}
		};
	}

}
