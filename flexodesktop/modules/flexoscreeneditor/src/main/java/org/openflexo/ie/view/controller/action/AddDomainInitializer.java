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
package org.openflexo.ie.view.controller.action;

import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.dkv.DKVModel;
import org.openflexo.foundation.dkv.DuplicateDKVObjectException;
import org.openflexo.foundation.dkv.EmptyStringException;
import org.openflexo.foundation.dkv.action.AddDomainAction;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.TextAreaParameter;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.icon.SEIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class AddDomainInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	AddDomainInitializer(IEControllerActionInitializer actionInitializer) {
		super(AddDomainAction.actionType, actionInitializer);
	}

	@Override
	protected IEControllerActionInitializer getControllerActionInitializer() {
		return (IEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<AddDomainAction> getDefaultInitializer() {
		return new FlexoActionInitializer<AddDomainAction>() {
			@Override
			public boolean run(EventObject e, AddDomainAction action) {
				DKVModel model = action.getFocusedObject().getDkvModel();
				boolean ok = false;
				while (!ok) {

					ParameterDefinition[] parameters = new ParameterDefinition[2];
					parameters[0] = new TextFieldParameter("newDomainName", "name", model.getNextDomainName());
					parameters[1] = new TextAreaParameter("description", "description", "");
					parameters[1].addParameter("columns", "20");
					AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
							FlexoLocalization.localizedForKey("create_new_domain"),
							FlexoLocalization.localizedForKey("enter_the_name_of_the_new_domain"), parameters);
					if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
						String newDomainName = (String) dialog.parameterValueWithName("newDomainName");
						if (newDomainName == null) {
							return false;
						}

						if (newDomainName.trim().length() == 0) {
							FlexoController.notify(FlexoLocalization.localizedForKey("name_of_domain_cannot_be_empty"));
							continue;
						}
						newDomainName = newDomainName.trim();
						if (newDomainName.length() > 50) {
							FlexoController.notify(FlexoLocalization.localizedForKey("name_of_domain_cannot_be_longer_than_50_chars"));
							continue;
						}

						try {
							if (model.isDomainNameLegal(newDomainName)) {
								(action).setDkvModel(model);
								(action).setNewDomainName(newDomainName);
								(action).setNewDomainDescription((String) dialog.parameterValueWithName("description"));
							}
							ok = true;
						} catch (DuplicateDKVObjectException e2) {
							FlexoController.notify(FlexoLocalization.localizedForKey("name_of_domain_already_exists"));
						} catch (EmptyStringException e2) {
						}

						return true;
					} else {
						return false;
					}

				}
				return ok;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<AddDomainAction> getDefaultFinalizer() {
		return new FlexoActionFinalizer<AddDomainAction>() {
			@Override
			public boolean run(EventObject e, AddDomainAction action) {
				getControllerActionInitializer().getIEController().getIESelectionManager().setSelectedObject((action).getNewDomain());
				// getController().setCurrentEditedObjectAsModuleView(((AddDomainAction)
				// action).getNewDomain());
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return SEIconLibrary.DOMAIN_ICON;
	}

}
