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
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.dre.AbstractDocItemView;
import org.openflexo.drm.DocItemVersion;
import org.openflexo.drm.DocResourceManager;
import org.openflexo.drm.action.ApproveVersion;
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

public class ApproveVersionInitializer extends ActionInitializer {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	ApproveVersionInitializer(DREControllerActionInitializer actionInitializer) {
		super(ApproveVersion.actionType, actionInitializer);
	}

	@Override
	protected DREControllerActionInitializer getControllerActionInitializer() {
		return (DREControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<ApproveVersion> getDefaultInitializer() {
		return new FlexoActionInitializer<ApproveVersion>() {
			@Override
			public boolean run(ActionEvent e, final ApproveVersion action) {

				Vector<DocItemVersion> availableVersions = action.getVersionsThatCanBeApproved();

				if (availableVersions.size() == 0) {
					return false;
				}

				if (action.getVersion() == null) {
					action.setVersion(availableVersions.firstElement());
				}

				final ParameterDefinition[] params = new ParameterDefinition[5];

				params[0] = new ReadOnlyTextFieldParameter("user", "username", DocResourceManager.instance().getUser().getIdentifier());
				params[1] = new ReadOnlyTextFieldParameter("language", "language", action.getVersion().getLanguageId()) {
					@Override
					public String getValue() {
						return action.getVersion().getLanguageId();
					}
				};
				params[1].setDepends("version");
				params[2] = new DynamicDropDownParameter<DocItemVersion>("version", "version", availableVersions, action.getVersion()) {
					@Override
					public void setValue(DocItemVersion value) {
						super.setValue(value);
						// logger.info("On met l'action a "+value);
						action.setVersion(value);
						params[3].setValue(value.getVersion().toString());
					}
				};
				params[2].addParameter("format", "localizedName");
				params[3] = new TextFieldParameter("new_version", "approve_as_version", action.getVersion().getVersion().toString());
				params[3].setDepends("version");
				params[4] = new TextAreaParameter("note", "note", "", 25, 3);
				AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
						FlexoLocalization.localizedForKey("approve_version"),
						FlexoLocalization.localizedForKey("define_approval_parameters"), params);
				if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
					action.setAuthor(DocResourceManager.instance().getUser());
					action.setNote((String) dialog.parameterValueWithName("note"));
					String versionId = (String) dialog.parameterValueWithName("new_version");
					action.setNewVersion(new DocItemVersion.Version(versionId));
					return true;
				} else {
					return false;
				}
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<ApproveVersion> getDefaultFinalizer() {
		return new FlexoActionFinalizer<ApproveVersion>() {
			@Override
			public boolean run(ActionEvent e, ApproveVersion action) {
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
