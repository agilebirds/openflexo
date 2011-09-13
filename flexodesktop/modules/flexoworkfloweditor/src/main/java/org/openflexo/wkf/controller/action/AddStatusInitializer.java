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

import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.wkf.controller.WKFController;


import org.openflexo.components.AskParametersDialog;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.components.browser.view.BrowserActionSource;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.TextAreaParameter;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.foundation.wkf.DuplicateStatusException;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.Status;
import org.openflexo.foundation.wkf.action.AddStatus;


public class AddStatusInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	AddStatusInitializer(WKFControllerActionInitializer actionInitializer)
	{
		super(AddStatus.actionType,actionInitializer);
	}
	
	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() 
	{
		return (WKFControllerActionInitializer)super.getControllerActionInitializer();
	}
	
	@Override
	protected FlexoActionInitializer<AddStatus> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<AddStatus>() {
            @Override
			public boolean run(ActionEvent e, AddStatus action)
            {
                if (action.getContext() instanceof DuplicateStatusException)
                    return true;
                FlexoProcess process = action.getProcess();
                if (process.getStatusList()==null)
                	return false;
                ParameterDefinition[] parameters = new ParameterDefinition[2];
                parameters[0] = new TextFieldParameter("newStatusName", "name", process.getStatusList().getNextNewStatusName());
                parameters[1] = new TextAreaParameter("description", "description", "");
                parameters[1].addParameter("columns", "20");
                AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(),
                        null, FlexoLocalization.localizedForKey("create_new_status"), FlexoLocalization.localizedForKey("enter_parameters_for_the_new_status"), parameters);
                if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
                    String newStatusName = (String) dialog.parameterValueWithName("newStatusName");
                    if (newStatusName == null)
                        return false;
                    action.setNewStatusName(newStatusName);
                    action.setNewDescription((String) dialog.parameterValueWithName("description"));
                    return true;
                } else {
                    return false;
                }
            }
        };
	}

     @Override
	protected FlexoActionFinalizer<AddStatus> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<AddStatus>() {
            @Override
			public boolean run(ActionEvent e, AddStatus action)
            {
                Status newStatus = action.getNewStatus();
                if (e!=null && e.getSource() instanceof BrowserActionSource) {
                    ProjectBrowser browser = ((BrowserActionSource) e.getSource()).getBrowser();
                    if (!browser.activateBrowsingFor(newStatus)) {
                        if (FlexoController.confirm(FlexoLocalization.localizedForKey("would_you_like_to_desactivate_status_filtering"))) {
                            browser.getFilterForObject(newStatus).setStatus(BrowserFilterStatus.SHOW);
                            browser.update();
                        }
                    }
                }
                if (e!=null) //If it wasn't created through the process inspector
                	getControllerActionInitializer().getWKFSelectionManager().setSelectedObject(newStatus);
                //getControllerActionInitializer().getWKFController().getWorkflowBrowser().focusOn(newStatus);
                return true;
          }
        };
	}

     @Override
 	protected FlexoExceptionHandler<AddStatus> getDefaultExceptionHandler() 
 	{
 		return new FlexoExceptionHandler<AddStatus>() {
 			@Override
			public boolean handleException(FlexoException exception, AddStatus action) {
                if (exception instanceof DuplicateStatusException) {
                    String newStatusName = FlexoController.askForString(FlexoLocalization
                            .localizedForKey("sorry_status_already_exists_please_choose_an_other_name"));
                    if (newStatusName!=null) {
                        action.setNewStatusName(newStatusName);
                        action.setContext(exception);
                        action.doAction();
                    }
                    return true;
                }
                return false;
			}
        };
 	}


	@Override
	protected Icon getEnabledIcon() 
	{
		return WKFIconLibrary.STATUS_ICON;
	}


}
