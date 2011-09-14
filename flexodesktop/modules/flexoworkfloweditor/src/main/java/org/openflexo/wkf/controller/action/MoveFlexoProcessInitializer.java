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

import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;


import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.param.LabelParameter;
import org.openflexo.foundation.param.ParametersModel;
import org.openflexo.foundation.param.ProcessParameter;
import org.openflexo.foundation.param.RadioButtonListParameter;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.action.MoveFlexoProcess;

public class MoveFlexoProcessInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	public MoveFlexoProcessInitializer(ControllerActionInitializer actionInitializer)
	{
		super(MoveFlexoProcess.actionType,actionInitializer);
	}

	@Override
	protected FlexoActionInitializer<MoveFlexoProcess> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<MoveFlexoProcess>() {
			@Override
			public boolean run(ActionEvent e, final MoveFlexoProcess action)
			{
				if (action.getFocusedObject() == null) 
					return false;
				if (action.isDoImmediately()) 
					return true;
				LabelParameter infoLabel = new LabelParameter("info","info",action.getLocalizedDescription(), false);
				infoLabel.setWidth(300);
				infoLabel.setHeight(150);
				
				final String CHOOSE_PARENT_PROCESS = FlexoLocalization.localizedForKey("move_process_under_another_process");
				final String MAKE_CONTEXT_FREE = FlexoLocalization.localizedForKey("make_context_free");
				String[] modes = { CHOOSE_PARENT_PROCESS, MAKE_CONTEXT_FREE };
				final RadioButtonListParameter<String> modeSelector = new RadioButtonListParameter<String>("mode", "what_would_you_like_to_do", CHOOSE_PARENT_PROCESS, modes);

				final ProcessParameter parentProcessParameter = new ProcessParameter("parentProcess", "parent_process", action.getFocusedObject().getParentProcess());
				// This widget is visible if and only if mode is NEW_PROCESS
				parentProcessParameter.setDepends("mode");
				parentProcessParameter.setConditional("mode=" + '"' + CHOOSE_PARENT_PROCESS + '"');
				//parentProcessParameter.addParameter("isSelectable", "params.parentProcess.isAcceptableProcess");
				parentProcessParameter.setProcessSelectingConditional(new ProcessParameter.ProcessSelectingConditional() {
					@Override
					public boolean isSelectable(FlexoProcess aProcess)
					{
						return action.getFocusedObject().isAcceptableAsParentProcess(aProcess);
						//return aProcess.isAncestorOf(action.getFocusedObject()) && (aProcess != action.getFocusedObject());
					}
				});

		     	AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(
		     			getProject(), 
						null, 
						action.getLocalizedName(),
		     			"<html><center>"+WKFIconLibrary.PROCESS_ICON.getHTMLImg()+"<b>&nbsp;"+FlexoLocalization.localizedForKeyWithParams("move_process_($0)",action.getFocusedObject().getName())+"</b></center></html>",						
						new AskParametersDialog.ValidationCondition() {
							@Override
							public boolean isValid(ParametersModel model) {
								if(modeSelector.getValue().equals(CHOOSE_PARENT_PROCESS) && (parentProcessParameter.getValue()!=null)){
									return true;
								}
								if(modeSelector.getValue().equals(MAKE_CONTEXT_FREE)) {
									return true;
								}
								errorMessage = FlexoLocalization.localizedForKey("please_choose_a_valid_parent_process");
								return false;
							}     				
		     			},
						infoLabel,
						modeSelector, parentProcessParameter);
		       

				if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
					action.setNewParentProcess(parentProcessParameter.getValue());
					return true;
				}

				return false;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<MoveFlexoProcess> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<MoveFlexoProcess>() {
			@Override
			public boolean run(ActionEvent e, MoveFlexoProcess action)
			{
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<MoveFlexoProcess> getDefaultExceptionHandler() 
	{
		return new FlexoExceptionHandler<MoveFlexoProcess>() {
			@Override
			public boolean handleException(FlexoException exception, MoveFlexoProcess action) 
			{
				exception.printStackTrace();
				FlexoController.showError(exception.getLocalizedMessage());
				return true;
			}
		};
	}

}
