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

import java.awt.HeadlessException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.EventObject;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang3.StringUtils;
import org.openflexo.components.ProgressWindow;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.rest.action.UploadProjectAction;
import org.openflexo.rest.client.Project;
import org.openflexo.rest.client.ProjectVersion;
import org.openflexo.rest.client.ServerRestClient;
import org.openflexo.rest.client.User;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.StreamDataBodyPart;

public class UploadProjectInitializer extends ActionInitializer<UploadProjectAction, FlexoProject, FlexoProject> {

	public static final String ROLE_ADMIN = "DNL";

	public static final String ROLE_ACCOUNT_ADMIN = "CLADMIN";

	private class ChangeServerException extends Exception {

	}

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	public UploadProjectInitializer(ControllerActionInitializer actionInitializer) {
		super(UploadProjectAction.actionType, actionInitializer);
	}

	@Override
	protected FlexoActionInitializer<UploadProjectAction> getDefaultInitializer() {
		return new FlexoActionInitializer<UploadProjectAction>() {
			@Override
			public boolean run(EventObject event, UploadProjectAction action) {
				boolean isFirst = true;
				ServerRestClient client = null;
				Integer projectId = null;
				ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("connecting_to_server"), -1);
				User user = null;
				try {
					while (projectId == null) {
						client = getController().getWSClient(!isFirst);
						isFirst = false;
						if (client == null) {
							return false;// Cancelled
						}
						try {
							user = client.users().id(client.getUserName()).getAsUserXml();
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}
						List<Project> projects = client.projects(
								client.createClient(),
								UriBuilder.fromUri(client.getBASE_URI())
										.queryParam("projectUri", action.getFocusedObject().getProjectURI()).build()).getAsXml(
								new GenericType<List<Project>>() {
								});
						if (projects.size() > 0) {
							projectId = projects.get(0).getProjectId();
						} /*else if (Arrays.asList(ROLE_ADMIN, ROLE_ACCOUNT_ADMIN).contains(user.getUsertype())) {
							
							}*/else {
							FlexoController.notify(FlexoLocalization.localizedForKey("your_project_is_not_handled_by_the_server"));
							return false;
						}

					}
				} finally {
					ProgressWindow.hideProgressWindow();
				}

				ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("saving"), 5);
				File zipFile = null;
				try {
					zipFile = File.createTempFile(StringUtils.rightPad(action.getFocusedObject().getProjectName(), 3, '_'), ".zip");
					zipFile.deleteOnExit();
				} catch (IOException e2) {
					e2.printStackTrace();
					FlexoController.showError(e2.getMessage());
					return false;
				}

				try {
					getProject().saveAsZipFile(zipFile, ProgressWindow.instance(), true, true);
				} catch (SaveResourceException e1) {
					FlexoController.showError(e1.getMessage());
					e1.printStackTrace();
					return false;
				} finally {
					ProgressWindow.hideProgressWindow();
				}
				final long length = zipFile.length();
				final int numberOfStates = 1000; // We will display tenth of percent, hence 1000 states
				ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("sending_project"), numberOfStates);
				try {
					FileInputStream inputStream = new FileInputStream(zipFile) {

						long progress = 0;
						long lastUpdate = 0;
						int stepSize = (int) (length / numberOfStates);

						@Override
						public int read() throws IOException {
							progress++;
							updateProgress();
							return super.read();
						}

						private void updateProgress() {
							double percent = progress * 100.0d / length;
							if (lastUpdate == 0 || progress - lastUpdate > stepSize || progress == length) {
								ProgressWindow.instance().setProgress(
										String.format("%1$.2f%1$%"/*+" (%2$d/%3$d)"*/, percent, progress, length));
								lastUpdate = progress;
							}
						}
					};
					ProjectVersion version = new ProjectVersion();
					version.setProject(projectId);
					version.setCreator(user.getLogin());
					String s = FlexoController.askForString(FlexoLocalization.localizedForKey("please_provide_some_comments"));
					if (s == null) {
						return false;
					}
					version.setComment(s);
					FormDataMultiPart mp = new FormDataMultiPart();
					mp.field("version", version, MediaType.APPLICATION_XML_TYPE);
					mp.bodyPart(new StreamDataBodyPart("file", inputStream, zipFile.getName()));
					client.projectsProjectIDVersions(projectId).postMultipartFormDataAsXml(mp, ProjectVersion.class);
					return true;
				} catch (HeadlessException e) {
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				return false;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<UploadProjectAction> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<UploadProjectAction>() {

			@Override
			public boolean handleException(FlexoException exception, UploadProjectAction action) {
				Throwable e = exception;
				if (exception.getCause() != null) {
					e = exception.getCause();
				}
				/*
				if (e instanceof PPMWebServiceAuthentificationException) {
					getController().handleWSException((PPMWebServiceAuthentificationException) e);
				} else if (e instanceof RemoteException) {
					getController().handleWSException((RemoteException) e);
				} else {
					FlexoController.notify(FlexoLocalization.localizedForKey("upload_project_failed") + ": " + e.getMessage());
				}*/
				return false;
			}
		};
	}

	/*
	private CLProjectDescriptor selectTarget(CLProjectDescriptor[] targetProjects) throws ChangeServerException {
		Arrays.sort(targetProjects, new Comparator<CLProjectDescriptor>() {
			@Override
			public int compare(CLProjectDescriptor o1, CLProjectDescriptor o2) {
				if (o1.getProjectName() == null) {
					if (o2.getProjectName() == null) {
						return 0;
					} else {
						return -1;
					}
				} else if (o2.getProjectName() == null) {
					return 1;
				} else {
					return o1.getProjectName().compareTo(o2.getProjectName());
				}
			}
		});
		if (targetProjects.length == 0) {
			FlexoController.showError(FlexoLocalization.localizedForKey("sorry_but_there_is_no_projects_where_this_prj_can_be_uploaded"));
			return null;
		}
		ParameterDefinition[] parameters = new ParameterDefinition[3];
		LabelParameter info = new LabelParameter("info", "", "<html>"
				+ FlexoLocalization.localizedForKey("selected_server")
				+ " <b>"
				+ FlexoServerInstanceManager.getInstance().getAddressBook().getInstanceWithID(AdvancedPrefs.getWebServiceInstance())
						.getName() + "</b></html>", false);
		info.setAlign("center");
		RadioButtonListParameter<CLProjectDescriptor> p = new RadioButtonListParameter<CLProjectDescriptor>("selectedProject",
				"choose_project", targetProjects[0], targetProjects);
		p.setDepends("change");
		p.setConditional("change=false");
		CheckboxParameter changeServer = new CheckboxParameter("change", "change_server", false);
		parameters[0] = info;
		parameters[1] = p;
		parameters[2] = changeServer;
		AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(),
				FlexoLocalization.localizedForKey("import_processes"), FlexoLocalization.localizedForKey("select_project"), parameters);
		if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
			if (changeServer.getValue()) {
				throw new ChangeServerException();
			}
			return p.getValue();
		} else {
			return null;
		}
	}
	*/
	@Override
	protected FlexoActionFinalizer<UploadProjectAction> getDefaultFinalizer() {
		return new FlexoActionFinalizer<UploadProjectAction>() {
			@Override
			public boolean run(EventObject e, UploadProjectAction action) {
				/*if (action.getUploadReport() != null && action.getUploadReport().trim().length() > 0) {
					FlexoController.notify(action.getUploadReport());
				}*/
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return WKFIconLibrary.PROCESS_ICON;
	}

}
