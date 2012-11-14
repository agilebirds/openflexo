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
package org.openflexo.ie.view.controller.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.dm.ProjectDatabaseRepository;
import org.openflexo.foundation.ie.action.GenerateEntityFromSelection;
import org.openflexo.foundation.ie.wizards.EntityFromWidgets;
import org.openflexo.foundation.ie.wizards.PropertyProposal;
import org.openflexo.foundation.param.DMEOModelParameter;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.ParametersModel;
import org.openflexo.foundation.param.RadioButtonListParameter;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class GenerateEntityFromSelectionInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	GenerateEntityFromSelectionInitializer(IEControllerActionInitializer actionInitializer) {
		super(GenerateEntityFromSelection.actionType, actionInitializer);
	}

	@Override
	protected IEControllerActionInitializer getControllerActionInitializer() {
		return (IEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<GenerateEntityFromSelection> getDefaultInitializer() {
		return new FlexoActionInitializer<GenerateEntityFromSelection>() {
			@Override
			public boolean run(ActionEvent e, GenerateEntityFromSelection action) {
				return askWidgetMapping(action);
			}
		};
	}

	// RadioButtonListParameter dbRepositoryMode;
	// PersistantRepositoryParameter dbrepositoryChooser;
	// TextFieldParameter newDBRepositoryName;

	RadioButtonListParameter eoModelMode;
	TextFieldParameter newEOModelName;
	DMEOModelParameter eoModelSelector;

	TextFieldParameter newEntityName;

	EntityFromWidgets entityFromWidgets;

	public boolean askWidgetMapping(GenerateEntityFromSelection action) {
		ParameterDefinition[] parameters = new ParameterDefinition[4];

		// ======================= Database repository selection =========================//
		// final String USE_EXISTING_DB_REPOSITORY = FlexoLocalization.localizedForKey("use_existing_db_repository");
		// final String CREATE_DB_REPOSITORY = FlexoLocalization.localizedForKey("create_db_repository");
		// String[] db_repository_modes = { USE_EXISTING_DB_REPOSITORY, CREATE_DB_REPOSITORY };
		// dbRepositoryMode = new RadioButtonListParameter("dbRepositoryMode", "select_a_choice",
		// atLeastOneDBRepositoryExist()?USE_EXISTING_DB_REPOSITORY:CREATE_DB_REPOSITORY, db_repository_modes);
		// parameters[0] = dbRepositoryMode;
		//
		// dbrepositoryChooser = new PersistantRepositoryParameter("selectedDBRepository", "create_entity_in_repository", null);
		// parameters[1] = dbrepositoryChooser;
		// parameters[1].setDepends("dbRepositoryMode");
		// parameters[1].setConditional("dbRepositoryMode=" + '"' + USE_EXISTING_DB_REPOSITORY + '"');
		//
		// String baseName = FlexoLocalization.localizedForKey("new_dbrepository_name");
		// newDBRepositoryName = new TextFieldParameter("newProcessName", "name_of_the_new_dbrepository", getProject().getFlexoWorkflow()
		// .findNextDefaultProcessName(baseName));
		// parameters[2] = newDBRepositoryName;
		// parameters[2].setDepends("dbRepositoryMode");
		// parameters[2].setConditional("dbRepositoryMode=" + '"' + CREATE_DB_REPOSITORY + '"');

		// ======================= Database eomodel selection =========================//
		final String USE_EXISTING_EOMODEL = FlexoLocalization.localizedForKey("use_existing_eo_model");
		final String CREATE_EOMODEL = FlexoLocalization.localizedForKey("create_new_eomodel");
		String[] eomodel_modes = { USE_EXISTING_EOMODEL, CREATE_EOMODEL };
		eoModelMode = new RadioButtonListParameter("eoModelMode", "select_a_choice", USE_EXISTING_EOMODEL, eomodel_modes);
		parameters[0] = eoModelMode;
		eoModelSelector = new DMEOModelParameter("eomodel", "eomodel_file", null);
		parameters[1] = eoModelSelector;
		parameters[1].setDepends("eoModelMode");
		parameters[1].setConditional("eoModelMode=" + '"' + USE_EXISTING_EOMODEL + '"');

		newEOModelName = new TextFieldParameter("newEOModelName", FlexoLocalization.localizedForKey("new_eomodel_name"), getProject()
				.getDataModel().findNextDefaultEOModelName());
		parameters[2] = newEOModelName;
		parameters[2].setDepends("eoModelMode");
		parameters[2].setConditional("eoModelMode=" + '"' + CREATE_EOMODEL + '"');

		newEntityName = new TextFieldParameter("newEntityName", FlexoLocalization.localizedForKey("new_entity_name"), null);
		parameters[3] = newEntityName;

		entityFromWidgets = new EntityFromWidgets(getProject(), action.getSelection(), true);

		AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
				FlexoLocalization.localizedForKey("create_new_sub_process_node"),
				FlexoLocalization.localizedForKey("what_would_you_like_to_do"), new AskParametersDialog.ValidationCondition() {
					@Override
					public boolean isValid(ParametersModel model) {
						// if(dbRepositoryMode.getValue().equals(USE_EXISTING_DB_REPOSITORY)&&(dbrepositoryChooser.getValue()==null)){
						// errorMessage = FlexoLocalization.localizedForKey("please_select_database_repository");
						// return false;
						// }
						// if(dbRepositoryMode.getValue().equals(CREATE_DB_REPOSITORY)&&(newDBRepositoryName.getValue()==null)){
						// errorMessage = FlexoLocalization.localizedForKey("please_specify_a_bdrepositoryname");
						// return false;
						// }
						if (eoModelMode.getValue().equals(USE_EXISTING_EOMODEL) && eoModelSelector.getValue() == null) {
							errorMessage = FlexoLocalization.localizedForKey("please_select_an_eomodelfile");
							return false;
						}
						if (eoModelMode.getValue().equals(USE_EXISTING_EOMODEL) && eoModelSelector.getValue() != null
								&& eoModelSelector.getValue().getRepository().isReadOnly()) {
							errorMessage = FlexoLocalization
									.localizedForKey("the_selected_eomodel_cannot_be_modified_please_choose_another_one_or_create_a_new_one");
							return false;
						}
						if (eoModelMode.getValue().equals(CREATE_EOMODEL) && newEOModelName.getValue() == null) {
							errorMessage = FlexoLocalization.localizedForKey("please_specify_an_eomodelfilename");
							return false;
						}
						if (newEntityName.getValue() == null) {
							errorMessage = FlexoLocalization.localizedForKey("please_specify_an_entityname");
							return false;
						}
						return true;
					}
				}, parameters[0], parameters[1], parameters[2], parameters[3]);

		if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
			if (entityFromWidgets != null) {
				if (getProject().getDataModel().getProjectDatabaseRepositories().size() == 0) {
					ProjectDatabaseRepository.createNewProjectDatabaseRepository(getProject().getDataModel(), getProject().getName()
							+ "Databases");
				}
				String dbRepName = getProject().getDataModel().getProjectDatabaseRepositories().get(0).getName();
				String eomName = eoModelMode.getValue().equals(USE_EXISTING_EOMODEL) ? eoModelSelector.getValue().getName()
						: newEOModelName.getValue();
				String eoentityName = newEntityName.getValue();
				ArrayList selectedProps = new ArrayList<PropertyProposal>();
				try {
					entityFromWidgets.justDoIt(dbRepName, eomName, eoentityName, selectedProps, getEditor());
				} catch (InvalidNameException e) {
					e.printStackTrace();
					FlexoController.showError(e.getMessage());
					return false;
				}
				return true;
			}
			return false;
		}
		// CANCELLED
		return false;
	}

	private boolean atLeastOneDBRepositoryExist() {
		return getProject().getDataModel().getProjectDatabaseRepositories() != null
				&& getProject().getDataModel().getProjectDatabaseRepositories().size() > 0;
	}

}
