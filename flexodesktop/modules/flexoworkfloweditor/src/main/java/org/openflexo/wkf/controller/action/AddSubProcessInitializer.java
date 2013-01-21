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

import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.components.AskParametersDialog.ValidationCondition;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.ParametersModel;
import org.openflexo.foundation.param.ProcessParameter;
import org.openflexo.foundation.param.RadioButtonListParameter;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.ProcessFolder;
import org.openflexo.foundation.wkf.action.AddSubProcess;
import org.openflexo.foundation.wkf.action.AddToProcessFolder;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class AddSubProcessInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	AddSubProcessInitializer(WKFControllerActionInitializer actionInitializer) {
		super(AddSubProcess.actionType, actionInitializer);
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() {
		return (WKFControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<AddSubProcess> getDefaultInitializer() {
		return new FlexoActionInitializer<AddSubProcess>() {
			@Override
			public boolean run(EventObject e, AddSubProcess action) {
				FlexoProcess process = null;
				if (action.getFocusedObject() instanceof FlexoProcess) {
					process = (FlexoProcess) action.getFocusedObject();
				} else if (action.getFocusedObject() instanceof ProcessFolder) {
					process = ((ProcessFolder) action.getFocusedObject()).getProcessNode().getProcess();
				}
				ParameterDefinition[] parameters = new ParameterDefinition[3];
				String baseName = FlexoLocalization.localizedForKey("new_process_name");
				parameters[0] = new TextFieldParameter("newProcessName", "name_of_process", getProject().getFlexoWorkflow()
						.findNextDefaultProcessName(baseName));
				String UNDER_PROCESS = FlexoLocalization.localizedForKey("under_process");
				String NO_CONTEXT = FlexoLocalization.localizedForKey("without_context");
				String[] contexts = { UNDER_PROCESS, NO_CONTEXT };
				parameters[1] = new RadioButtonListParameter<String>("context", "process_context", process == null ? NO_CONTEXT
						: UNDER_PROCESS, contexts);
				parameters[2] = new ProcessParameter("parentProcess", "parent_process", process);
				parameters[2].setDepends("context");
				parameters[2].setConditional("context=" + '"' + UNDER_PROCESS + '"');
				AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
						FlexoLocalization.localizedForKey("create_new_sub_process"),
						FlexoLocalization.localizedForKey("enter_parameters_for_the_new_sub_process"), new ValidationCondition() {
							@Override
							public boolean isValid(ParametersModel model) {
								errorMessage = FlexoLocalization.localizedForKey("you_must_choose_a_context");
								return model.parameterForKey("context").getValue() != null;
							}

							@Override
							public String getErrorMessage() {
								return super.getErrorMessage();
							}
						}, parameters);
				if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
					String newProcessName = (String) dialog.parameterValueWithName("newProcessName");
					FlexoProcess parentProcess;
					if (dialog.parameterValueWithName("context").equals(UNDER_PROCESS)) {
						parentProcess = (FlexoProcess) dialog.parameterValueWithName("parentProcess");
					} else {
						parentProcess = null;
					}
					action.setParentProcess(parentProcess);
					action.setNewProcessName(newProcessName);
					return true;
				} else {
					return false;
				}

			}
		};
	}

	@Override
	protected FlexoActionFinalizer<AddSubProcess> getDefaultFinalizer() {
		return new FlexoActionFinalizer<AddSubProcess>() {
			@Override
			public boolean run(EventObject e, AddSubProcess action) {
				boolean res = true;
				if (action.getNewProcess() != null
						&& action.getFocusedObject() instanceof ProcessFolder
						&& action.getNewProcess().getProcessNode().getFatherProcessNode() == ((ProcessFolder) action.getFocusedObject())
								.getProcessNode()) {
					AddToProcessFolder add = AddToProcessFolder.actionType.makeNewAction(action.getNewProcess(), null, getController()
							.getEditor());
					add.setDestination((ProcessFolder) action.getFocusedObject());
					res &= add.doAction().hasActionExecutionSucceeded();
				}
				getControllerActionInitializer().getWKFController().setCurrentFlexoProcess(action.getNewProcess());
				return res;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<AddSubProcess> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<AddSubProcess>() {
			@Override
			public boolean handleException(FlexoException exception, AddSubProcess action) {
				if (exception instanceof DuplicateResourceException) {
					FlexoController.notify("Process named " + action.getNewProcessName() + " already exists !");
					return false;
				}
				if (exception instanceof InvalidFileNameException) {
					FlexoController.notify("Process named " + action.getNewProcessName() + " is not valid");
					return false;
				}
				return false;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return WKFIconLibrary.PROCESS_ICON;
	}

}
