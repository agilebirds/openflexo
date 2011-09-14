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
import org.openflexo.foundation.param.PortParameter;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.foundation.wkf.DuplicateWKFObjectException;
import org.openflexo.foundation.wkf.action.AddServiceOperation;
import org.openflexo.foundation.wkf.ws.FlexoPort;
import org.openflexo.foundation.wkf.ws.ServiceOperation;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.wkf.controller.WKFController;


public class AddServiceOperationInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	AddServiceOperationInitializer(WKFControllerActionInitializer actionInitializer)
	{
		super(AddServiceOperation.actionType,actionInitializer);
	}
	
	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() 
	{
		return (WKFControllerActionInitializer)super.getControllerActionInitializer();
	}
	
	@Override
	protected FlexoActionInitializer<AddServiceOperation> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<AddServiceOperation>() {
            @Override
			public boolean run(ActionEvent e, AddServiceOperation action)
            {
                final ParameterDefinition[] parameters = new ParameterDefinition[3];

                parameters[0] = new TextFieldParameter("serviceOperationName", "service_operation_name", "Operation");
                parameters[1] = new PortParameter("relatedPort", "select_related_port", action.getProcess(), null);
                parameters[2] = new InfoLabelParameter("infoLabel", "description", FlexoLocalization
                        .localizedForKey("add_service_operation_description"));
                AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null, FlexoLocalization
								                .localizedForKey("add_service_operation"), FlexoLocalization.localizedForKey("add_service_operation"), parameters);
                if (dialog.getStatus() == AskParametersDialog.VALIDATE) {

                    String name = (String) dialog.parameterValueWithName("serviceOperationName");
                    action.setNewOperationName(name);
                    FlexoPort port = (FlexoPort) dialog.parameterValueWithName("relatedPort");
                    action.setRelatedPort(port);
                    return true;
                }
                // CANCELLED
                return false;
             }
        };
	}

     @Override
	protected FlexoActionFinalizer<AddServiceOperation> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<AddServiceOperation>() {
            @Override
			public boolean run(ActionEvent e, AddServiceOperation action)
            {
                ServiceOperation newOperation = action.getNewServiceOperation();
                if (e.getSource() instanceof BrowserActionSource) {
                    ProjectBrowser browser = ((BrowserActionSource) e.getSource()).getBrowser();

                    browser.focusOn(newOperation);

                }
                return true;
          }
        };
	}

     @Override
 	protected FlexoExceptionHandler<AddServiceOperation> getDefaultExceptionHandler() 
 	{
 		return new FlexoExceptionHandler<AddServiceOperation>() {
 			@Override
			public boolean handleException(FlexoException exception, AddServiceOperation action) {
                if (exception instanceof DuplicateWKFObjectException) {
                    FlexoController.showError(FlexoLocalization.localizedForKey("service_operation_already_exists"));
                    return true;
                }
                return false;
			}
        };
 	}


	@Override
	protected Icon getEnabledIcon() 
	{
		return null;
	}

}
