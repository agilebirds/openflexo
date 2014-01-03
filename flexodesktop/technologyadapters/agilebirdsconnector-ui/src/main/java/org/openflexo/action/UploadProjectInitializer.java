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

import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.rest.action.UploadProjectAction;
import org.openflexo.rest.client.ServerRestClientModel;
import org.openflexo.rest.client.ServerRestClientModel.NewProjectParameter;
import org.openflexo.rest.client.model.UserType;
import org.openflexo.toolbox.FlexoVersion;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.AgileBirdsFlexoController;

public class UploadProjectInitializer extends ActionInitializer<UploadProjectAction, FlexoProject, FlexoProject> {

	public static final String ROLE_ADMIN = "DNL";

	public static final String ROLE_ACCOUNT_ADMIN = "CLADMIN";

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
				FlexoProject project = action.getFocusedObject();
				FlexoVersion version = project.getVersion();
				String comment = AgileBirdsFlexoController.askForString(FlexoLocalization.localizedForKey("please_provide_some_comments"));
				if (comment == null) {
					return false;
				}
				ServerRestClientModel model = new ServerRestClientModel(getController(), project);
				model.performOperationsInSwingWorker(true, true, model.new UpdateUserOperation(), model.new UpdateServerProject(false));
				if (model.getServerProject() != null) {
					model.performOperationsInSwingWorker(true, true, model.new SendProjectToServer(comment));
				} else {
					if (model.getUser() != null) {
						if (model.getUser().getUserType() == UserType.ADMIN || model.getUser().getUserType() == UserType.CLIENT_ADMIN) {
							NewProjectParameter data = model.new NewProjectParameter();
							data.getProject().setName(project.getDisplayName());
							data.setComment(FlexoLocalization.localizedForKey("first_import_of_project"));
							FIBDialog<NewProjectParameter> dialog = FIBDialog.instanciateAndShowDialog(
									ServerRestClientModel.NEW_SERVER_PROJECT_FIB_FILE, data, getController().getFlexoFrame(), true,
									FlexoLocalization.getMainLocalizer());
							if (dialog.getController().getStatus() == FIBController.Status.VALIDATED) {
								model.performOperationsInSwingWorker(true, true, model.new CreateProjectAndUploadFirstVersion(data));
							}
						} else {
							AgileBirdsFlexoController.notify(FlexoLocalization.localizedForKey("your_project_is_not_handled_by_the_server"));
							return false;
						}
					} else {
						return false;
					}
				}
				return project.getVersion() != null && !project.getVersion().equals(version);
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
				return false;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<UploadProjectAction> getDefaultFinalizer() {
		return new FlexoActionFinalizer<UploadProjectAction>() {
			@Override
			public boolean run(EventObject e, UploadProjectAction action) {
				AgileBirdsFlexoController.notify(FlexoLocalization.localizedForKey("successfully_created_version") + " "
						+ action.getFocusedObject().getVersion());
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return WKFIconLibrary.PROCESS_ICON;
	}

}
