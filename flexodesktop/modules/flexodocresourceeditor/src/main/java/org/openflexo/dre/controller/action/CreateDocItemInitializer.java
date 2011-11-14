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

import org.openflexo.components.AskParametersDialog;
import org.openflexo.drm.DocItem;
import org.openflexo.drm.DocItemFolder;
import org.openflexo.drm.action.CreateDocItem;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.TextAreaParameter;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class CreateDocItemInitializer extends ActionInitializer {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	CreateDocItemInitializer(DREControllerActionInitializer actionInitializer) {
		super(CreateDocItem.actionType, actionInitializer);
	}

	@Override
	protected DREControllerActionInitializer getControllerActionInitializer() {
		return (DREControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<CreateDocItem> getDefaultInitializer() {
		return new FlexoActionInitializer<CreateDocItem>() {
			@Override
			public boolean run(ActionEvent e, CreateDocItem action) {
				DocItemFolder docItemFolder = action.getDocItemFolder();
				ParameterDefinition[] parameters = new ParameterDefinition[2];
				parameters[0] = new TextFieldParameter("newItemIdentifier", "identifier", docItemFolder.getNextDefautItemName());
				parameters[1] = new TextAreaParameter("description", "description", "");
				parameters[1].addParameter("columns", "20");
				AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
						FlexoLocalization.localizedForKey("create_new_doc_item"),
						FlexoLocalization.localizedForKey("enter_parameters_for_the_new_doc_item"), parameters);
				if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
					String newItemIdentifier = (String) dialog.parameterValueWithName("newItemIdentifier");
					if (newItemIdentifier == null) {
						return false;
					}
					action.setNewItemIdentifier(newItemIdentifier);
					action.setNewItemDescription((String) dialog.parameterValueWithName("description"));
					return true;
				} else {
					return false;
				}
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<CreateDocItem> getDefaultFinalizer() {
		return new FlexoActionFinalizer<CreateDocItem>() {
			@Override
			public boolean run(ActionEvent e, CreateDocItem action) {
				DocItem newDocItem = action.getNewDocItem();
				getControllerActionInitializer().getDREController().getDREBrowser().focusOn(newDocItem);
				return true;
			}
		};
	}

}
