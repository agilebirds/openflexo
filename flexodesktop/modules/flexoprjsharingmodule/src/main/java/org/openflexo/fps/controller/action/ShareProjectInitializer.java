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
import org.openflexo.components.OpenProjectComponent;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.param.CheckboxParameter;
import org.openflexo.foundation.param.InfoLabelParameter;
import org.openflexo.foundation.param.TextAreaParameter;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.fps.CVSRepository;
import org.openflexo.fps.FPSPreferences;
import org.openflexo.fps.action.AddCVSRepository;
import org.openflexo.fps.action.CVSAction;
import org.openflexo.fps.action.ShareProject;
import org.openflexo.fps.view.component.CVSRepositoryParameter;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.prefs.FlexoPreferences;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class ShareProjectInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	ShareProjectInitializer(FPSControllerActionInitializer actionInitializer) {
		super(ShareProject.actionType, actionInitializer);
	}

	@Override
	protected FPSControllerActionInitializer getControllerActionInitializer() {
		return (FPSControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<ShareProject> getDefaultInitializer() {
		return new FlexoActionInitializer<ShareProject>() {
			@Override
			public boolean run(ActionEvent e, ShareProject action) {
				if (action.getProjectDirectory() == null) {
					File newProjectDirectory;
					try {
						newProjectDirectory = OpenProjectComponent.getProjectDirectory();
					} catch (ProjectLoadingCancelledException e1) {
						return false;
					}
					if (newProjectDirectory == null)
						return false;
					action.setProjectDirectory(newProjectDirectory);
				}

				CVSRepository repository = null;
				String moduleName = action.getProjectDirectory().getName();
				while (repository == null) {
					int returned = FlexoController
							.selectOption(FlexoLocalization.localizedForKey("you_must_now_define_a_cvs_repository_location"),
									FlexoLocalization.localizedForKey("create_cvs_repository_location"),
									FlexoLocalization.localizedForKey("create_cvs_repository_location"),
									FlexoLocalization.localizedForKey("choose_cvs_repository_location"),
									FlexoLocalization.localizedForKey("abort"));
					if (returned == 0) { // Create new repository
						AddCVSRepository createNewRepository = AddCVSRepository.actionType.makeNewAction(
								CVSAction.getRepositoryList(action.getFocusedObject()), null, action.getEditor());
						createNewRepository.doAction();
						repository = createNewRepository.getNewCVSRepository();
						returned = 1;
					}
					if (returned == 1) {
						InfoLabelParameter infoLabel = new InfoLabelParameter("info", "info",
								FlexoLocalization.localizedForKey("share_project_description"), false, 5, 40);
						CVSRepositoryParameter repositoryChoice = new CVSRepositoryParameter("CVSRepository", "cvs_repository", repository);
						final TextFieldParameter moduleNameChoice = new TextFieldParameter("moduleName",
								"project_will_be_imported_as_module", action.getModuleName(), 40);

						AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
								FlexoLocalization.localizedForKey("share_project"),
								FlexoLocalization.localizedForKey("please_supply_import_parameters"), infoLabel, repositoryChoice,
								moduleNameChoice);
						if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
							repository = repositoryChoice.getValue();
							moduleName = moduleNameChoice.getValue();
						} else {
							return false;
						}
					} else if (returned == 2)
						return false;
				}
				action.setRepository(repository);
				action.setModuleName(moduleName);

				File mainCVSIgnoreFile = new File(action.getProjectDirectory(), ".cvsignore");
				if (!mainCVSIgnoreFile.exists()) {
					action.setCvsIgnorize(FlexoController.confirm(FlexoLocalization
							.localizedForKey("would_you_like_to_instrumentalize_your_project_with_required_cvs_ignore_files")));
				}

				if (FlexoProject.searchCVSDirs(action.getProjectDirectory())) {
					action.setRemoveExistingCVSDirectories(FlexoController.confirm(FlexoLocalization
							.localizedForKey("your_project_seems_to_be_already_under_cvs_remove_all_cvs_informations")));
				}

				TextFieldParameter vendorTag = new TextFieldParameter("vendorTag", "vendor_tag", action.getVendorTag());
				TextFieldParameter releaseTag = new TextFieldParameter("releaseTag", "release_tag", action.getReleaseTag());
				TextAreaParameter commitMessage = new TextAreaParameter("logMessage", "log_message", action.getLogMessage(), 40, 10);
				CheckboxParameter cvsIgnorise = new CheckboxParameter("cvsIgnorise", "create_cvs_ignore_files", action.getCvsIgnorize());

				AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null, action.getLocalizedName(),
						FlexoLocalization.localizedForKey("please_supply_import_parameters"), vendorTag, releaseTag, commitMessage,
						cvsIgnorise);
				if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
					action.setVendorTag(vendorTag.getValue());
					action.setReleaseTag(releaseTag.getValue());
					action.setLogMessage(commitMessage.getValue());
					action.setCvsIgnorize(cvsIgnorise.getValue());
					return true;
				} else {
					return false;
				}
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<ShareProject> getDefaultFinalizer() {
		return new FlexoActionFinalizer<ShareProject>() {
			@Override
			public boolean run(ActionEvent e, ShareProject action) {
				if (action.getProject() != null) {
					getControllerActionInitializer().getFPSController().setSharedProject(action.getProject());
					FPSPreferences.addToLastOpenedProjects(action.getProject().getModuleDirectory());
					FlexoPreferences.savePreferences(true);
					return true;
				}
				return false;
			}
		};
	}

}
