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
package org.openflexo.view.controller.action;

import java.util.EventObject;

import javax.swing.Icon;

import org.openflexo.action.SubmitDocumentationAction;
import org.openflexo.dre.SubmitNewVersionPopup;
import org.openflexo.drm.DocItem;
import org.openflexo.drm.DocItemAction;
import org.openflexo.drm.DocItemVersion;
import org.openflexo.drm.Language;
import org.openflexo.drm.action.SubmitVersion;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.icon.IconLibrary;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class SubmitDocumentationActionizer extends ActionInitializer<SubmitDocumentationAction, FlexoObject, FlexoObject> {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(SubmitDocumentationActionizer.class
			.getPackage().getName());

	public SubmitDocumentationActionizer(ControllerActionInitializer actionInitializer) {
		super(SubmitDocumentationAction.actionType, actionInitializer);
	}

	@Override
	protected FlexoActionInitializer<SubmitDocumentationAction> getDefaultInitializer() {
		return new FlexoActionInitializer<SubmitDocumentationAction>() {
			@Override
			public boolean run(EventObject e, SubmitDocumentationAction anAction) {
				if (!(anAction.getFocusedObject() instanceof InspectableObject)) {
					return false;
				}
				DocItem docItem;
				if (anAction.getFocusedObject() instanceof DocItem) {
					docItem = (DocItem) anAction.getFocusedObject();
				} else {
					docItem = getController().getApplicationContext().getDocResourceManager()
							.getDocItemFor((InspectableObject) anAction.getFocusedObject());
				}
				if (docItem == null) {
					return false;
				}
				Language language = null;
				if (docItem.getDocResourceCenter().getLanguages().size() > 1) {
					logger.warning("Please reimplement this");
					// TODO: reimplement this
					/*ParameterDefinition[] langParams = new ParameterDefinition[1];
					langParams[0] = new DynamicDropDownParameter("language", "language", docItem.getDocResourceCenter().getLanguages(),
							docItem.getDocResourceCenter().getLanguages().firstElement());
					langParams[0].addParameter("format", "name");
					AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
							FlexoLocalization.localizedForKey("choose_language"),
							FlexoLocalization.localizedForKey("define_submission_language"), langParams);
					if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
						language = (Language) dialog.parameterValueWithName("language");
					} else {
						return false;
					}*/

				} else if (docItem.getDocResourceCenter().getLanguages().size() == 1) {
					language = docItem.getDocResourceCenter().getLanguages().firstElement();
				}
				if (language == null) {
					return false;
				}
				SubmitVersion action = SubmitVersion.actionType.makeNewAction(docItem, null, getController().getApplicationContext()
						.getDocResourceManager().getEditor());
				SubmitNewVersionPopup editVersionPopup = new SubmitNewVersionPopup(action.getDocItem(), language, getController()
						.getFlexoFrame(), getController().getApplicationContext().getDocResourceManager().getEditor());
				action.setVersion(editVersionPopup.getVersionToSubmit());
				if (action.getVersion() == null) {
					return false;
				}
				String title;
				DocItemAction lastAction = action.getDocItem().getLastActionForLanguage(action.getVersion().getLanguage());
				if (lastAction == null) {
					title = FlexoLocalization.localizedForKey("submit_documentation");
				} else {
					title = FlexoLocalization.localizedForKey("review_documentation");
					action.getVersion().setVersion(
							DocItemVersion.Version.versionByIncrementing(lastAction.getVersion().getVersion(), 0, 0, 1));
				}
				logger.warning("Please reimplement this");
				// TODO: reimplement this
				/*ParameterDefinition[] parameters = new ParameterDefinition[4];
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
					anAction.setContext(action);
					action.setContext(anAction);
					return true;
				} else {
					return false;
				}*/
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<SubmitDocumentationAction> getDefaultFinalizer() {
		return new FlexoActionFinalizer<SubmitDocumentationAction>() {
			@Override
			public boolean run(EventObject e, SubmitDocumentationAction action) {
				if (action.getContext() != null && action.getContext() instanceof SubmitVersion) {
					((SubmitVersion) action.getContext()).doAction();
					FlexoController.notify(FlexoLocalization.localizedForKey("submission_has_been_successfully_recorded"));
					return true;
				}
				return false;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return IconLibrary.HELP_ICON;
	}
}
