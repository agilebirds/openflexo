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

import java.io.File;
import java.util.EventObject;
import java.util.logging.Logger;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.components.OpenProjectComponent;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.fps.CVSRepository;
import org.openflexo.fps.FPSPreferences;
import org.openflexo.fps.SharedProject;
import org.openflexo.fps.action.AddCVSRepository;
import org.openflexo.fps.action.CVSAction;
import org.openflexo.fps.action.OpenSharedProject;
import org.openflexo.fps.view.component.CVSRepositoryParameter;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.prefs.FlexoPreferences;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class OpenSharedProjectInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	OpenSharedProjectInitializer(FPSControllerActionInitializer actionInitializer) {
		super(OpenSharedProject.actionType, actionInitializer);
	}

	@Override
	protected FPSControllerActionInitializer getControllerActionInitializer() {
		return (FPSControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<OpenSharedProject> getDefaultInitializer() {
		return new FlexoActionInitializer<OpenSharedProject>() {
			@Override
			public boolean run(EventObject e, OpenSharedProject action) {
				if (action.getProjectDirectory() == null) {
					File newProjectDirectory;
					newProjectDirectory = OpenProjectComponent.getProjectDirectory();
					if (newProjectDirectory == null) {
						return false;
					}
					action.setProjectDirectory(newProjectDirectory);
				}

				File cvsRepositoryLocation = new File(action.getProjectDirectory(), SharedProject.CVS_REPOSITORY_LOCATION_FILE);
				if (!cvsRepositoryLocation.exists()) {
					CVSRepository repository = null;
					while (repository == null) {
						int returned = FlexoController.selectOption(
								FlexoLocalization.localizedForKey("this_project_doesn_t_define_any_repository_location"),
								FlexoLocalization.localizedForKey("create_cvs_repository_location"),
								FlexoLocalization.localizedForKey("create_cvs_repository_location"),
								FlexoLocalization.localizedForKey("choose_cvs_repository_location"),
								FlexoLocalization.localizedForKey("abort"));
						if (returned == 0) {
							AddCVSRepository createNewRepository = AddCVSRepository.actionType.makeNewAction(
									CVSAction.getRepositoryList(action.getFocusedObject()), null, action.getEditor());
							createNewRepository.doAction();
							repository = createNewRepository.getNewCVSRepository();
						} else if (returned == 1) {
							CVSRepositoryParameter repositoryChoice = new CVSRepositoryParameter("CVSRepository", "cvs_repository", null);

							AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
									FlexoLocalization.localizedForKey("share_project"),
									FlexoLocalization.localizedForKey("please_chooose_a_cvs_repository_in_which_to_import_project"),
									repositoryChoice);
							if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
								repository = repositoryChoice.getValue();
							} else {
								return false;
							}
						} else if (returned == 2) {
							return false;
						}
					}
					action.setRepository(repository);
				}

				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<OpenSharedProject> getDefaultFinalizer() {
		return new FlexoActionFinalizer<OpenSharedProject>() {
			@Override
			public boolean run(EventObject e, OpenSharedProject action) {
				if (action.getNewProject() != null) {
					getControllerActionInitializer().getFPSController().setSharedProject(action.getNewProject());
					FPSPreferences.addToLastOpenedProjects(action.getNewProject().getModuleDirectory());
					FlexoPreferences.savePreferences(true);
					return true;
				}
				return false;
			}
		};
	}

}
