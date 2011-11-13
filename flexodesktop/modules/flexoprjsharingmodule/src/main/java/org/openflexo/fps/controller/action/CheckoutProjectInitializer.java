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
package org.openflexo.fps.controller.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.logging.Logger;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.param.DirectoryParameter;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.ReadOnlyTextFieldParameter;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.fps.FPSPreferences;
import org.openflexo.fps.action.CheckoutProject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.prefs.FlexoPreferences;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class CheckoutProjectInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	private static File lastCheckoutDirectory = new File(System.getProperty("user.home"));

	CheckoutProjectInitializer(FPSControllerActionInitializer actionInitializer) {
		super(CheckoutProject.actionType, actionInitializer);
	}

	@Override
	protected FPSControllerActionInitializer getControllerActionInitializer() {
		return (FPSControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<CheckoutProject> getDefaultInitializer() {
		return new FlexoActionInitializer<CheckoutProject>() {
			@Override
			public boolean run(ActionEvent e, final CheckoutProject action) {
				final DirectoryParameter directoryParameter = new DirectoryParameter("localDirectory", "local_directory",
						(action.getLocalDirectory() != null ? action.getLocalDirectory() : lastCheckoutDirectory));
				final TextFieldParameter localNameParameter = new TextFieldParameter("localName", "local_name", action.getLocalName());
				final ReadOnlyTextFieldParameter checkoutDirectoryFileName = new ReadOnlyTextFieldParameter("checkoutDirectoryFileName",
						"project_will_be_checkouted_in",
						(new File(directoryParameter.getValue(), action.getFocusedObject().getModuleName())).getAbsolutePath(), 40);
				checkoutDirectoryFileName.setDepends("localDirectory,localName");
				directoryParameter.addValueListener(new ParameterDefinition.ValueListener<File>() {
					@Override
					public void newValueWasSet(ParameterDefinition param, File oldValue, File newValue) {
						checkoutDirectoryFileName.setValue((new File(directoryParameter.getValue(), localNameParameter.getValue()))
								.getAbsolutePath());
					}
				});
				localNameParameter.addValueListener(new ParameterDefinition.ValueListener<String>() {
					@Override
					public void newValueWasSet(ParameterDefinition param, String oldValue, String newValue) {
						checkoutDirectoryFileName.setValue((new File(directoryParameter.getValue(), localNameParameter.getValue()))
								.getAbsolutePath());
					}
				});

				AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null, action.getLocalizedName(),
						FlexoLocalization.localizedForKey("enter_location_where_to_checkout"), directoryParameter, localNameParameter,
						checkoutDirectoryFileName);
				System.setProperty("apple.awt.fileDialogForDirectories", "false");
				if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
					lastCheckoutDirectory = directoryParameter.getValue();
					action.setLocalDirectory(directoryParameter.getValue());
					action.setLocalName(localNameParameter.getValue());
					return true;
				} else {
					return false;
				}
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<CheckoutProject> getDefaultFinalizer() {
		return new FlexoActionFinalizer<CheckoutProject>() {
			@Override
			public boolean run(ActionEvent e, CheckoutProject action) {
				getControllerActionInitializer().getFPSController().setSharedProject(action.getCheckoutedProject());
				FPSPreferences.addToLastOpenedProjects(action.getCheckoutedProject().getModuleDirectory());
				FlexoPreferences.savePreferences(true);
				return true;
			}
		};
	}

}
