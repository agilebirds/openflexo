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
package org.openflexo.wkf.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.view.BrowserActionSource;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.param.InfoLabelParameter;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.foundation.wkf.DuplicateWKFObjectException;
import org.openflexo.foundation.wkf.action.AddServiceInterface;
import org.openflexo.foundation.wkf.ws.ServiceInterface;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class AddServiceInterfaceInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	AddServiceInterfaceInitializer(WKFControllerActionInitializer actionInitializer) {
		super(AddServiceInterface.actionType, actionInitializer);
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() {
		return (WKFControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<AddServiceInterface> getDefaultInitializer() {
		return new FlexoActionInitializer<AddServiceInterface>() {
			@Override
			public boolean run(ActionEvent e, AddServiceInterface action) {
				final ParameterDefinition[] parameters = new ParameterDefinition[2];

				parameters[0] = new TextFieldParameter("serviceInterfaceName", "service_interface_name", "Service");
				parameters[1] = new InfoLabelParameter("infoLabel", "description",
						FlexoLocalization.localizedForKey("add_service_interface_description"));
				AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
						FlexoLocalization.localizedForKey("add_service_interface"),
						FlexoLocalization.localizedForKey("add_service_interface"), parameters);
				if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
					String name = (String) dialog.parameterValueWithName("serviceInterfaceName");
					action.setNewInterfaceName(name);
					return true;
				}
				// CANCELLED
				return false;

				// return
				// (AskNewWebServiceDialog.displayDialog((CreateNewWebService)anAction,_wseController.getProject(),_wseController.getFlexoFrame())
				// == AskNewWebServiceDialog.VALIDATE);
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<AddServiceInterface> getDefaultFinalizer() {
		return new FlexoActionFinalizer<AddServiceInterface>() {
			@Override
			public boolean run(ActionEvent e, AddServiceInterface action) {
				ServiceInterface newInterface = action.getServiceInterface();
				if (e.getSource() instanceof BrowserActionSource) {
					ProjectBrowser browser = ((BrowserActionSource) e.getSource()).getBrowser();

					browser.focusOn(newInterface);

				}
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<AddServiceInterface> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<AddServiceInterface>() {
			@Override
			public boolean handleException(FlexoException exception, AddServiceInterface action) {
				if (exception instanceof DuplicateWKFObjectException) {

					FlexoController.showError(FlexoLocalization.localizedForKey("service_interface_already_exists"));
					return true;
				}
				return false;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return null;
	}

}
