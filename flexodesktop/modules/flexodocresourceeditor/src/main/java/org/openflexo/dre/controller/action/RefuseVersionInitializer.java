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

import java.util.EventObject;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.dre.AbstractDocItemView;
import org.openflexo.drm.DocItemVersion;
import org.openflexo.drm.DocResourceManager;
import org.openflexo.drm.action.RefuseVersion;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.param.DynamicDropDownParameter;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.ReadOnlyTextFieldParameter;
import org.openflexo.foundation.param.TextAreaParameter;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class RefuseVersionInitializer extends ActionInitializer {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	RefuseVersionInitializer(DREControllerActionInitializer actionInitializer) {
		super(RefuseVersion.actionType, actionInitializer);
	}

	@Override
	protected DREControllerActionInitializer getControllerActionInitializer() {
		return (DREControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<RefuseVersion> getDefaultInitializer() {
		return new FlexoActionInitializer<RefuseVersion>() {
			@Override
			public boolean run(EventObject e, final RefuseVersion action) {
				Vector<DocItemVersion> availableVersions = action.getVersionsThatCanBeApproved();

				if (availableVersions.size() == 0) {
					return false;
				}

				if (action.getVersion() == null) {
					action.setVersion(availableVersions.firstElement());
				}

				final ParameterDefinition[] params = new ParameterDefinition[4];

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
				params[3] = new TextAreaParameter("note", "note", "", 25, 3);
				AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
						FlexoLocalization.localizedForKey("refuse_version"),
						FlexoLocalization.localizedForKey("define_refusal_parameters"), params);
				if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
					action.setAuthor(DocResourceManager.instance().getUser());
					action.setNote((String) dialog.parameterValueWithName("note"));
					return true;
				} else {
					return false;
				}
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<RefuseVersion> getDefaultFinalizer() {
		return new FlexoActionFinalizer<RefuseVersion>() {
			@Override
			public boolean run(EventObject e, RefuseVersion action) {
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
