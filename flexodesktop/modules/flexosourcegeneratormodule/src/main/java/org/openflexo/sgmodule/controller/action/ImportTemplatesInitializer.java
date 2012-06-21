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
package org.openflexo.sgmodule.controller.action;

import java.util.EventObject;
import java.io.File;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.templates.CGTemplates;
import org.openflexo.foundation.cg.templates.CustomCGTemplateRepository;
import org.openflexo.foundation.cg.templates.action.AddCustomTemplateRepository;
import org.openflexo.foundation.cg.templates.action.ImportTemplates;
import org.openflexo.foundation.cg.utils.TemplateRepositoryType;
import org.openflexo.foundation.param.DirectoryParameter;
import org.openflexo.foundation.param.DynamicDropDownParameter;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.RadioButtonListParameter;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.icon.GeneratorIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class ImportTemplatesInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	ImportTemplatesInitializer(SGControllerActionInitializer actionInitializer) {
		super(ImportTemplates.actionType, actionInitializer);
	}

	@Override
	protected SGControllerActionInitializer getControllerActionInitializer() {
		return (SGControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<ImportTemplates> getDefaultInitializer() {
		return new FlexoActionInitializer<ImportTemplates>() {
			@Override
			public boolean run(EventObject e, ImportTemplates action) {
				CGTemplates templates = action.getFocusedObject().getTemplates();
				boolean hasTemplateRepositories = templates.getCustomCodeRepositoriesVector().size() > 0;
				ParameterDefinition[] params;
				TextFieldParameter newRepositoryNameParam = new TextFieldParameter("name", "custom_template_repository_name",
						templates.getNextGeneratedCodeRepositoryName());
				DirectoryParameter paramDir = new DirectoryParameter("directory", "source_directory", new File(
						System.getProperty("user.home"), getProject().getProjectName()));
				String NEW = FlexoLocalization.localizedForKey("new_repository");
				String EXISTING = FlexoLocalization.localizedForKey("existing_repository");
				String[] repChoice = new String[] { NEW, EXISTING };
				RadioButtonListParameter<String> repChoiceParam = new RadioButtonListParameter<String>("repChoice", "select_a_choice", NEW,
						repChoice);
				DynamicDropDownParameter<CustomCGTemplateRepository> repositoryParam = new DynamicDropDownParameter<CustomCGTemplateRepository>(
						"repository", "template_repository", templates.getCustomCodeRepositoriesVector(), hasTemplateRepositories ? action
								.getFocusedObject().getProject().getGeneratedCode().getTemplates().getCustomCodeRepositoriesVector()
								.firstElement() : null);
				repositoryParam.addParameter("format", "name");
				if (hasTemplateRepositories) {
					newRepositoryNameParam.setDepends("repChoice");
					newRepositoryNameParam.setConditional("repChoice=\"" + NEW + "\"");
					repositoryParam.setDepends("repChoice");
					repositoryParam.setConditional("repChoice=\"" + EXISTING + "\"");
					repositoryParam.setShowReset(false);
					params = new ParameterDefinition[4];
					params[0] = repChoiceParam;
					params[1] = newRepositoryNameParam;
					params[2] = repositoryParam;
					params[3] = paramDir;
				} else {
					params = new ParameterDefinition[2];
					params[0] = newRepositoryNameParam;
					params[1] = paramDir;
				}
				AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
						FlexoLocalization.localizedForKey("import_templates"),
						FlexoLocalization.localizedForKey("enter_new_repository_name"), params);
				if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
					if (paramDir.getValue() == null || !paramDir.getValue().isDirectory()) {
						return false;
					}
					if (newRepositoryNameParam.getValue() == null || newRepositoryNameParam.getValue().length() == 0) {
						return false;
					}
					// CustomCGTemplateRepository repository = null;
					if (!hasTemplateRepositories || repChoiceParam.getValue().equals(NEW)) {
						if (hasTemplateRepositories) {
							if (templates.getCustomCGTemplateRepositoryForName(newRepositoryNameParam.getValue()) != null) {
								FlexoController.notify(FlexoLocalization.localizedForKey("template_repository") + " "
										+ newRepositoryNameParam.getValue() + " " + FlexoLocalization.localizedForKey("already_exists"));
								return false;
							}
						}
						AddCustomTemplateRepository addDirectory = AddCustomTemplateRepository.actionType.makeNewAction(templates, null,
								action.getEditor());
						addDirectory.setNewCustomTemplatesRepositoryName(newRepositoryNameParam.getValue());
						addDirectory.setRepositoryType(TemplateRepositoryType.Code);
						addDirectory.setNewCustomTemplatesRepositoryDirectory(new FlexoProjectFile(action.getFocusedObject().getProject(),
								newRepositoryNameParam.getValue()));
						addDirectory.doAction();
						if (!addDirectory.hasActionExecutionSucceeded()) {
							FlexoController.notify(FlexoLocalization.localizedForKey("could_not_create_repository") + " "
									+ newRepositoryNameParam.getValue());
							return false;
						}
						action.setRepository(addDirectory.getNewCustomTemplatesRepository());

					} else {
						if (repositoryParam.getValue() != null) {
							action.setRepository(repositoryParam.getValue());
						} else {
							FlexoController.notify(FlexoLocalization.localizedForKey("you_must_choose_a_repository"));
							return false;
						}
					}
					action.setExternalTemplateDirectory(paramDir.getValue());
					/*
					if (contextChoiceParam.getValue().equals(SPECIFIC_TARGET)) {
						action.setTarget(targetTypeParam.getValue());
					}
					*/
					// If context is CGFile, check if custom template repository is
					// to associate to current repository
					if (action.getContext() instanceof CGFile) {
						GenerationRepository enclosingRepository = ((CGFile) action.getContext()).getRepository();
						if (enclosingRepository.getPreferredTemplateRepository() != action.getRepository()) {
							if (FlexoController.confirm(FlexoLocalization
									.localizedForKey("would_you_like_to_associate_custom_template_repository_to_current_code_repository"))) {
								enclosingRepository.setPreferredTemplateRepository(action.getRepository());
							}
						}
					}

					return true;
				} else {
					return false;
				}

			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return GeneratorIconLibrary.EDIT_ICON;
	}

	@Override
	protected Icon getDisabledIcon() {
		return GeneratorIconLibrary.EDIT_DISABLED_ICON;
	}

}
