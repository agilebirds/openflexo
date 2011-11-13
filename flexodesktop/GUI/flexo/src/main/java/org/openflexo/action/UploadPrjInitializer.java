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
package org.openflexo.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.ws.client.PPMWebService.CLProjectDescriptor;
import org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException;
import org.openflexo.ws.client.PPMWebService.PPMWebServiceClient;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.components.ProgressWindow;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.imported.action.UploadPrjAction;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.RadioButtonListParameter;
import org.openflexo.foundation.rm.SaveResourceException;

public class UploadPrjInitializer extends ActionInitializer {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	public UploadPrjInitializer(ControllerActionInitializer actionInitializer) {
		super(UploadPrjAction.actionType, actionInitializer);
	}

	@Override
	protected FlexoActionInitializer<UploadPrjAction> getDefaultInitializer() {
		return new FlexoActionInitializer<UploadPrjAction>() {
			@Override
			public boolean run(ActionEvent e, UploadPrjAction action) {
				boolean isFirst = true;
				PPMWebServiceClient client = null;
				CLProjectDescriptor[] targetProjects = null;
				while (targetProjects == null) {
					client = getController().getWSClient(!isFirst);
					isFirst = false;
					if (client == null)
						return false;// Cancelled
					try {
						targetProjects = client.getAvailableProjects();
					} catch (PPMWebServiceAuthentificationException e1) {
						getController().handleWSException(e1);
					} catch (RemoteException e1) {
						getController().handleWSException(e1);
					}
				}
				CLProjectDescriptor target = selectTarget(targetProjects);
				if (target != null) {
					action.setTargetProject(target);

					File zipFile = null;
					try {
						zipFile = File.createTempFile("tmp_" + System.currentTimeMillis(), "tmp_" + System.currentTimeMillis());
						zipFile.deleteOnExit();
					} catch (IOException e2) {
						e2.printStackTrace();
						FlexoController.showError(e2.getMessage());
						return false;
					}

					ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("saving"), 5);
					try {
						getProject().saveAsZipFile(zipFile, ProgressWindow.instance(), true, true);
					} catch (SaveResourceException e1) {
						FlexoController.showError(e1.getMessage());
						e1.printStackTrace();
						return false;
					}
					ProgressWindow.hideProgressWindow();
					action.setFile(zipFile);
					action.setClientWS(client);
					String s = FlexoController.askForString(FlexoLocalization.localizedForKey("please_provide_some_comments"));
					if (s == null) // Cancel
						return false;
					action.setComments(s);
					return true;
				}
				return false;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<UploadPrjAction> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<UploadPrjAction>() {

			@Override
			public boolean handleException(FlexoException exception, UploadPrjAction action) {
				Throwable e = exception;
				if (exception.getCause() != null) {
					e = exception.getCause();
				}
				if (e instanceof PPMWebServiceAuthentificationException) {
					getController().handleWSException((PPMWebServiceAuthentificationException) e);
				} else if (e instanceof RemoteException) {
					getController().handleWSException((RemoteException) e);
				} else {
					FlexoController.notify(FlexoLocalization.localizedForKey("upload_project_failed") + ": " + e.getMessage());
				}
				return false;
			}
		};
	}

	private CLProjectDescriptor selectTarget(CLProjectDescriptor[] targetProjects) {
		Arrays.sort(targetProjects, new Comparator<CLProjectDescriptor>() {
			@Override
			public int compare(CLProjectDescriptor o1, CLProjectDescriptor o2) {
				if (o1.getProjectName() == null) {
					if (o2.getProjectName() == null)
						return 0;
					else
						return -1;
				} else if (o2.getProjectName() == null) {
					return 1;
				} else
					return o1.getProjectName().compareTo(o2.getProjectName());
			}
		});
		if (targetProjects.length == 0) {
			FlexoController.showError(FlexoLocalization.localizedForKey("sorry_but_there_is_no_projects_where_this_prj_can_be_uploaded"));
			return null;
		}
		ParameterDefinition[] parameters = new ParameterDefinition[1];
		RadioButtonListParameter<CLProjectDescriptor> p = new RadioButtonListParameter<CLProjectDescriptor>("selectedProject",
				"choose_project", targetProjects[0], targetProjects);
		parameters[0] = p;
		AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(),
				FlexoLocalization.localizedForKey("import_processes"), FlexoLocalization.localizedForKey("select_processes_to_import"),
				parameters);
		if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
			return p.getValue();
		} else {
			return null;
		}
	}

	@Override
	protected FlexoActionFinalizer<UploadPrjAction> getDefaultFinalizer() {
		return new FlexoActionFinalizer<UploadPrjAction>() {
			@Override
			public boolean run(ActionEvent e, UploadPrjAction action) {
				if (action.getUploadReport() != null && action.getUploadReport().trim().length() > 0)
					FlexoController.notify(action.getUploadReport());
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return WKFIconLibrary.PROCESS_ICON;
	}

}
