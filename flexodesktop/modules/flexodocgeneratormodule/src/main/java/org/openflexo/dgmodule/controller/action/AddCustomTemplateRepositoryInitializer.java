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
package org.openflexo.dgmodule.controller.action;

import java.util.EventObject;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.templates.CGTemplates;
import org.openflexo.foundation.cg.templates.action.AddCustomTemplateRepository;
import org.openflexo.foundation.cg.utils.TemplateRepositoryType;
import org.openflexo.foundation.param.CheckboxListParameter;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class AddCustomTemplateRepositoryInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	AddCustomTemplateRepositoryInitializer(DGControllerActionInitializer actionInitializer) {
		super(AddCustomTemplateRepository.actionType, actionInitializer);
	}

	@Override
	protected DGControllerActionInitializer getControllerActionInitializer() {
		return (DGControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<AddCustomTemplateRepository> getDefaultInitializer() {
		return new FlexoActionInitializer<AddCustomTemplateRepository>() {
			@Override
			public boolean run(EventObject e, AddCustomTemplateRepository action) {
				action.setRepositoryType(TemplateRepositoryType.Documentation);
				if (action.getNewCustomTemplatesRepositoryName() == null) {
					CGTemplates templates = action.getFocusedObject().getTemplates();
					if (action.getNewCustomTemplatesRepositoryName() == null) {
						action.setNewCustomTemplatesRepositoryName(templates.getNextGeneratedCodeRepositoryName());
					}
					TextFieldParameter paramName = new TextFieldParameter("name", "custom_template_repository_name",
							action.getNewCustomTemplatesRepositoryName());
					AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
							FlexoLocalization.localizedForKey("create_new_custom_template_repository"),
							FlexoLocalization.localizedForKey("enter_parameters_for_the_new_custom_template_repository"), paramName);
					if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
						action.setNewCustomTemplatesRepositoryName(paramName.getValue());
						action.setNewCustomTemplatesRepositoryDirectory(new FlexoProjectFile(action.getFocusedObject().getProject(),
								paramName.getValue()));
						return true;
					} else {
						return false;
					}
				}

				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<AddCustomTemplateRepository> getDefaultFinalizer() {
		return new FlexoActionFinalizer<AddCustomTemplateRepository>() {
			@Override
			public boolean run(EventObject e, AddCustomTemplateRepository action) {
				if (action.isAssociateTemplateRepository() && action.getNewCustomTemplatesRepository() != null
						&& getProject().getGeneratedDoc().getGeneratedRepositories().size() > 0) {
					Vector<DGRepository> repositories = new Vector<DGRepository>();
					for (GenerationRepository r : getProject().getGeneratedDoc().getGeneratedRepositories()) {
						if (r.getPreferredTemplateRepository() == null) {
							repositories.add((DGRepository) r);
						}
					}
					Vector<DGRepository> selected = (Vector<DGRepository>) repositories.clone();
					Iterator<DGRepository> i = selected.iterator();
					while (i.hasNext()) {
						DGRepository r = i.next();
						if (r.getPreferredTemplateRepository() != null) {
							i.remove();
						}
					}
					CheckboxListParameter<DGRepository> repositoriesParameter = new CheckboxListParameter<DGRepository>("repositories",
							FlexoLocalization.localizedForKey("select_repositories"), repositories, selected);
					repositoriesParameter.setFormatter("name");
					AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
							FlexoLocalization.localizedForKey("associate_custom_template_repository_with_repository"),
							FlexoLocalization.localizedForKey("select_repository_that_must_use_this_new_template_repository"),
							repositoriesParameter);
					if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
						for (DGRepository r : repositoriesParameter.getValue()) {
							r.setPreferredTemplateRepository(action.getNewCustomTemplatesRepository());
						}
					}
				}
				getControllerActionInitializer().getDGController().getSelectionManager()
						.setSelectedObject(action.getNewCustomTemplatesRepository());
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<AddCustomTemplateRepository> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<AddCustomTemplateRepository>() {
			@Override
			public boolean handleException(FlexoException exception, AddCustomTemplateRepository action) {
				if (exception instanceof InvalidFileNameException) {
					FlexoController.showError(FlexoLocalization.localizedForKey("this_template_name_is_already_used_please_choose_another")
							+ ":\n" + exception.getLocalizedMessage());
					return true;
				} else if (exception instanceof DuplicateResourceException) {
					FlexoController.showError(FlexoLocalization.localizedForKey("this_template_name_is_already_used_please_choose_another")
							+ ":\n" + exception.getLocalizedMessage());
					return true;
				}
				exception.printStackTrace();
				return true;
			}
		};
	}

}
