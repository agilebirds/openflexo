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
package org.openflexo.dm.view.controller.action;

import java.util.EventObject;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.dm.view.controller.DMController;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMTranstyper;
import org.openflexo.foundation.dm.DMTranstyper.DMTranstyperEntry;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.action.CreateDMTranstyper;
import org.openflexo.foundation.param.DMTypeParameter;
import org.openflexo.foundation.param.LabelParameter;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.ParametersModel;
import org.openflexo.foundation.param.PropertyListParameter;
import org.openflexo.foundation.param.RadioButtonListParameter;
import org.openflexo.foundation.param.ReadOnlyTextFieldParameter;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.inspector.model.PropertyListColumn;
import org.openflexo.inspector.widget.LabelWidget;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class CreateDMTranstyperInitializer extends ActionInitializer {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	CreateDMTranstyperInitializer(DMControllerActionInitializer actionInitializer) {
		super(CreateDMTranstyper.actionType, actionInitializer);
	}

	@Override
	protected DMControllerActionInitializer getControllerActionInitializer() {
		return (DMControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<CreateDMTranstyper> getDefaultInitializer() {
		return new FlexoActionInitializer<CreateDMTranstyper>() {
			@Override
			public boolean run(EventObject e, final CreateDMTranstyper action) {
				ReadOnlyTextFieldParameter declaringEntityParam = new ReadOnlyTextFieldParameter("declaringEntity", "declaring_entity",
						action.getEntity().getName());
				TextFieldParameter nameParam = new TextFieldParameter("transtyperName", "transtyper_name", action.getEntity().getDMModel()
						.getNextDefautTranstyperName(action.getEntity()));
				DMTypeParameter typeParam = new DMTypeParameter("returnedType", "returned_type", DMType.makeResolvedDMType(action
						.getEntity()));

				final TranstyperEntryInfoParameter entriesParameters = new TranstyperEntryInfoParameter("entries",
						"required_entries_for_this_new_transtyper", action.getFocusedObject(), 20, 8);

				entriesParameters.addAddAction("add_entry", "params.entries.addEntry", "params.entries.addEntryEnabled", null);
				entriesParameters.addDeleteAction("remove_entry", "params.entries.removeEntry", "params.entries.removeEntryEnabled", null);

				entriesParameters.addIconColumn("icon", "", 30, false);
				entriesParameters.addTextFieldColumn("name", "entry_name", 150, true);
				PropertyListColumn typeColumn = entriesParameters.addCustomColumn("type", "entry_type",
						"org.openflexo.components.widget.DMTypeInspectorWidget", 180, true);
				typeColumn.setValueForParameter("format", "simplifiedStringRepresentation");

				final String ALLOWS_MAPPING = FlexoLocalization.localizedForKey("mapping_definition_is_allowed_as_HTML");
				final String REFUSE_MAPPING = FlexoLocalization.localizedForKey("mapping_definition_is_not_allowed_as_HTML");
				final LabelParameter allowsMappingDefinitionParam = new LabelParameter("allowsMappingDefinition", "", ALLOWS_MAPPING, false);
				allowsMappingDefinitionParam.setAlign(LabelWidget.CENTER);
				typeParam.addValueListener(new ParameterDefinition.ValueListener<DMType>() {
					@Override
					public void newValueWasSet(ParameterDefinition param, DMType oldValue, DMType newValue) {
						allowsMappingDefinitionParam.setValue(DMTranstyper.allowsMappingDefinitionForType(newValue) ? ALLOWS_MAPPING
								: REFUSE_MAPPING);
					}
				});

				final String DEFINE_MAPPING = FlexoLocalization.localizedForKey("define_data_mapping");
				final String CUSTOM_CODE = FlexoLocalization.localizedForKey("mapping_will_be_later_encoded");
				String[] relationshipTypeChoices = { DEFINE_MAPPING, CUSTOM_CODE };
				final RadioButtonListParameter<String> transtyperTypeChoiceParam = new RadioButtonListParameter<String>("transtyperType",
						"please_select_a_choice", DEFINE_MAPPING, relationshipTypeChoices);
				transtyperTypeChoiceParam.setDepends("allowsMappingDefinition");
				transtyperTypeChoiceParam.setConditional("allowsMappingDefinition=\"" + ALLOWS_MAPPING + "\"");

				AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(
						getProject(),
						null,
						action.getLocalizedName(),
						FlexoLocalization.localizedForKey("please_enter_parameters_for_new_transtyper"),
						new AskParametersDialog.ValidationCondition() {
							@Override
							public boolean isValid(ParametersModel model) {
								if (entriesParameters.getValue().size() == 0) {
									errorMessage = FlexoLocalization.localizedForKey("please_supply_at_least_one_entry");
									return false;
								} else {
									for (DMTranstyperEntry entry : entriesParameters.getValue()) {
										if (entry.getType() == null) {
											errorMessage = FlexoLocalization.localizedForKeyWithParams("entry_($0)_has_undefined_type",
													entry.getName());
											return false;
										}
									}
									return true;
								}
							}
						}, declaringEntityParam, nameParam, typeParam, entriesParameters, allowsMappingDefinitionParam,
						transtyperTypeChoiceParam);

				if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
					action.setNewTranstyperName(nameParam.getValue());
					action.setNewTranstyperType(typeParam.getValue());
					action.setEntries(entriesParameters.getValue());
					action.setIsMappingDefined(transtyperTypeChoiceParam.getValue().equals(DEFINE_MAPPING));
					return true;
				}

				// Cancelled
				return false;

			}
		};
	}

	@Override
	protected FlexoActionFinalizer<CreateDMTranstyper> getDefaultFinalizer() {
		return new FlexoActionFinalizer<CreateDMTranstyper>() {
			@Override
			public boolean run(EventObject e, CreateDMTranstyper action) {
				((DMController) getController()).getSelectionManager().setSelectedObject(action.getNewTranstyper());
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return DMEIconLibrary.DM_TRANSTYPER_ICON;
	}

	public class TranstyperEntryInfoParameter extends PropertyListParameter<DMTranstyperEntry> {
		private DMEntity _declaringEntity;

		public TranstyperEntryInfoParameter(String name, String label, DMEntity declaringEntity, int rowHeight, int visibleRowCount) {
			super(name, label, new Vector<DMTranstyperEntry>(), rowHeight, visibleRowCount);
			_declaringEntity = declaringEntity;
		}

		public DMTranstyperEntry addEntry() {
			DMTranstyperEntry newEntry = new DMTranstyperEntry(_declaringEntity.getDMModel(), null);
			newEntry.setName(FlexoLocalization.localizedForKey("entry") + (getValue().size() + 1));
			getValue().add(newEntry);
			return newEntry;
		}

		public void removeEntry(DMTranstyperEntry entry) {
			getValue().remove(entry);
		}

		public boolean addEntryEnabled(DMTranstyperEntry entry) {
			return true;
		}

		public boolean removeEntryEnabled(DMTranstyperEntry entry) {
			return true;
		}

	}

}
