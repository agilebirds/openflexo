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

import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.dm.view.DMEOEntityView;
import org.openflexo.dm.view.DMEOModelView;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.dm.action.CreateDMEORelationship;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.dm.eo.DMEOJoin;
import org.openflexo.foundation.dm.eo.EOAccessException;
import org.openflexo.foundation.dm.eo.FlattenRelationshipDefinition;
import org.openflexo.foundation.dm.eo.model.InvalidJoinException;
import org.openflexo.foundation.param.CheckboxParameter;
import org.openflexo.foundation.param.DMEOEntityParameter;
import org.openflexo.foundation.param.FlattenRelationshipDefinitionParameter;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.ParametersModel;
import org.openflexo.foundation.param.PropertyListParameter;
import org.openflexo.foundation.param.RadioButtonListParameter;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.inspector.model.PropertyListColumn;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class CreateDMEORelationshipInitializer extends ActionInitializer {

	static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	CreateDMEORelationshipInitializer(DMControllerActionInitializer actionInitializer) {
		super(CreateDMEORelationship.actionType, actionInitializer);
	}

	@Override
	protected DMControllerActionInitializer getControllerActionInitializer() {
		return (DMControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<CreateDMEORelationship> getDefaultInitializer() {
		return new FlexoActionInitializer<CreateDMEORelationship>() {
			@Override
			public boolean run(ActionEvent e, final CreateDMEORelationship action) {
				final String NORMAL_RELATIONSHIP = FlexoLocalization.localizedForKey("create_normal_relationship");
				final String FLATTEN_RELATIONSHIP = FlexoLocalization.localizedForKey("create_flatten_relationship");
				String[] relationshipTypeChoices = { NORMAL_RELATIONSHIP, FLATTEN_RELATIONSHIP };
				final RadioButtonListParameter<String> relationshipTypeChoiceParam = new RadioButtonListParameter<String>(
						"relationshipType", "please_select_a_choice", NORMAL_RELATIONSHIP, relationshipTypeChoices);

				final DMEOEntityParameter sourceEntityParameter = new DMEOEntityParameter("sourceEntity", "source_entity",
						action.getEntity());
				sourceEntityParameter.setDepends("relationshipType");
				sourceEntityParameter.setConditional("relationshipType=ALWAYS_HIDE");

				final TextFieldParameter newRelationshipNameParam = new TextFieldParameter("name", "new_relationship_name",
						action.getNewRelationshipName());
				newRelationshipNameParam.setDepends("destinationEntity,isMultiple,relationshipType");
				newRelationshipNameParam.setConditional("(relationshipType=" + '"' + NORMAL_RELATIONSHIP + '"'
						+ "and destinationEntity!=null) or relationshipType=" + '"' + FLATTEN_RELATIONSHIP + '"');

				final DMEOEntityParameter destinationEntityParameter = new DMEOEntityParameter("destinationEntity", "destination_entity",
						null);
				destinationEntityParameter.setDepends("relationshipType");
				destinationEntityParameter.setConditional("relationshipType=" + '"' + NORMAL_RELATIONSHIP + '"');
				destinationEntityParameter.addParameter("repository", "params.sourceEntity.value.repository");
				destinationEntityParameter.addValueListener(new ParameterDefinition.ValueListener<DMEOEntity>() {

					@Override
					public void newValueWasSet(ParameterDefinition param, DMEOEntity oldValue, DMEOEntity newValue) {
						if (destinationEntityParameter.getValue() != null) {
							newRelationshipNameParam.setValue(destinationEntityParameter.getValue().getNiceRelationshipNameToMe());
						} else {
							newRelationshipNameParam.setValue(action.getNewRelationshipName());
						}
					}

				});

				final CheckboxParameter isMultipleParameter = new CheckboxParameter("isMultiple", "is_to_many", false);
				isMultipleParameter.setDepends("relationshipType");
				isMultipleParameter.setConditional("relationshipType=" + '"' + NORMAL_RELATIONSHIP + '"');
				isMultipleParameter.addValueListener(new ParameterDefinition.ValueListener<Boolean>() {

					@Override
					public void newValueWasSet(ParameterDefinition param, Boolean oldValue, Boolean newValue) {
						if (newValue && !newRelationshipNameParam.getValue().endsWith("s")) {
							newRelationshipNameParam.setValue(newRelationshipNameParam.getValue() + "s");
						}
						if (!newValue && newRelationshipNameParam.getValue().endsWith("s")) {
							newRelationshipNameParam.setValue(newRelationshipNameParam.getValue().substring(0,
									newRelationshipNameParam.getValue().length() - 1));
						}
					}

				});

				final JoinsInfoParameter joinsParameters = new JoinsInfoParameter("joins", "joins_defining_relationship",
						action.getFocusedObject(), 20, 5);
				joinsParameters.setDepends("destinationEntity,relationshipType");
				joinsParameters.setConditional("relationshipType=" + '"' + NORMAL_RELATIONSHIP + '"');

				joinsParameters.addAddAction("add_join", "params.joins.addJoin", "params.joins.addJoinEnabled", null);
				joinsParameters.addDeleteAction("remove_join", "params.joins.removeJoin", "params.joins.removeJoinEnabled", null);

				joinsParameters.addIconColumn("isJoinValidIcon", "", 30, false);
				PropertyListColumn sourceAttributeColumn = joinsParameters.addCustomColumn("sourceAttribute", "source_attribute",
						"org.openflexo.components.widget.DMEOAttributeInspectorWidget", 180, true);
				sourceAttributeColumn.setValueForParameter("entity", "sourceEntity");
				sourceAttributeColumn.setValueForParameter("format", "name");
				PropertyListColumn destinationAttributeColumn = joinsParameters.addCustomColumn("destinationAttribute",
						"destination_attribute", "org.openflexo.components.widget.DMEOAttributeInspectorWidget", 180, true);
				destinationAttributeColumn.setValueForParameter("entity", "destinationEntity");
				destinationAttributeColumn.setValueForParameter("format", "name");

				destinationEntityParameter.addValueListener(new ParameterDefinition.ValueListener<DMEOEntity>() {
					@Override
					public void newValueWasSet(ParameterDefinition param, DMEOEntity oldValue, DMEOEntity newValue) {
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("New value :" + newValue);
						}
						joinsParameters.setDestinationEntity(newValue);
					}
				});

				final FlattenRelationshipDefinitionParameter flattenRelationshipDefinitionParameter = new FlattenRelationshipDefinitionParameter(
						"relationshipDefinition", "relationship_definition", null);
				flattenRelationshipDefinitionParameter.setDepends("relationshipType");
				flattenRelationshipDefinitionParameter.setConditional("relationshipType=" + '"' + FLATTEN_RELATIONSHIP + '"');
				flattenRelationshipDefinitionParameter.addParameter("source_entity", "params.sourceEntity.value");
				flattenRelationshipDefinitionParameter
						.addValueListener(new ParameterDefinition.ValueListener<FlattenRelationshipDefinition>() {

							@Override
							public void newValueWasSet(ParameterDefinition<FlattenRelationshipDefinition> param,
									FlattenRelationshipDefinition oldValue, FlattenRelationshipDefinition newValue) {
								if (newValue != null && newValue.getBindingPathLastElement() != null) {
									newRelationshipNameParam.setValue(newValue.getBindingPathLastElement().getName());
								} else {
									newRelationshipNameParam.setValue(action.getNewRelationshipName());
								}
							}
						});
				AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null, action.getLocalizedName(),
						FlexoLocalization.localizedForKey("what_would_you_like_to_do"), new AskParametersDialog.ValidationCondition() {
							@Override
							public boolean isValid(ParametersModel model) {
								if (relationshipTypeChoiceParam.getValue().equals(NORMAL_RELATIONSHIP)) {
									if (newRelationshipNameParam.getValue() != null && !newRelationshipNameParam.getValue().equals("")
											&& destinationEntityParameter.getValue() != null
									/*&& joinsParameters.getDMEOJoins().size() > 0*/) {
										/*for (DMEOJoin j : joinsParameters.getDMEOJoins()) {
											if (logger.isLoggable(Level.FINE))
										        logger.fine("join: "+j);
											if (!j.isJoinValid()) return false;
										}*/
										return true;
									}
									errorMessage = FlexoLocalization.localizedForKey("invalid_joins_definition");
									return false;
								} else if (relationshipTypeChoiceParam.getValue().equals(FLATTEN_RELATIONSHIP)) {
									if (newRelationshipNameParam.getValue() != null && !newRelationshipNameParam.getValue().equals("")
											&& flattenRelationshipDefinitionParameter.getValue() != null
											&& flattenRelationshipDefinitionParameter.getValue().isDefinitionValid()) {
										return true;
									}
									errorMessage = FlexoLocalization.localizedForKey("invalid_flatten_relationship_definition");
								}
								return false;
							}
						}, sourceEntityParameter, relationshipTypeChoiceParam, newRelationshipNameParam, destinationEntityParameter,
						isMultipleParameter, joinsParameters, flattenRelationshipDefinitionParameter);

				if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
					action.setNewRelationshipName(newRelationshipNameParam.getValue());
					if (relationshipTypeChoiceParam.getValue().equals(NORMAL_RELATIONSHIP)) {
						action.setFlattenRelationShip(false);
						action.setDestinationEntity(destinationEntityParameter.getValue());
						action.setMultipleRelation(isMultipleParameter.getValue());
						if (joinsParameters.getValue().size() > 0) {
							if (joinsParameters.getValue() instanceof Vector) {
								action.setJoins((Vector<DMEOJoin>) joinsParameters.getValue());
							} else {
								Vector<DMEOJoin> joins = new Vector<DMEOJoin>();
								joins.addAll(joinsParameters.getValue());
								action.setJoins(joins);
							}
						}
					} else {
						action.setFlattenRelationShip(true);
						action.setFlattenRelationshipDefinition(flattenRelationshipDefinitionParameter.getValue());
					}
					return true;
				}
				return false;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<CreateDMEORelationship> getDefaultFinalizer() {
		return new FlexoActionFinalizer<CreateDMEORelationship>() {
			@Override
			public boolean run(ActionEvent e, CreateDMEORelationship action) {
				if (getControllerActionInitializer().getDMController().getCurrentEditedObject() == action.getEntity().getDMEOModel()) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Finalizer for CreateDMEORelationship in DMEOModelView");
					}
					DMEOModelView dmEOModelView = (DMEOModelView) getControllerActionInitializer().getDMController()
							.getCurrentEditedObjectView();
					dmEOModelView.getEoEntityTable().selectObject(action.getEntity());
					dmEOModelView.getEoRelationshipTable().selectObject(action.getNewEORelationship());
				} else if (getControllerActionInitializer().getDMController().getCurrentEditedObject() == action.getEntity()) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Finalizer for CreateDMEORelationship in DMEOEntityView");
					}
					DMEOEntityView eoEntityView = (DMEOEntityView) getControllerActionInitializer().getDMController()
							.getCurrentEditedObjectView();
					eoEntityView.getEoRelationshipTable().selectObject(action.getNewEORelationship());
				}
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<CreateDMEORelationship> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<CreateDMEORelationship>() {
			@Override
			public boolean handleException(FlexoException exception, CreateDMEORelationship action) {
				if (exception instanceof EOAccessException) {
					FlexoController.showError(((EOAccessException) exception).getMessage());
					return true;
				}
				if (exception.getCause() instanceof InvalidJoinException) {
					FlexoController.showError(exception.getMessage());
					return true;
				}
				return false;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return DMEIconLibrary.DM_EORELATIONSHIP_ICON;
	}

	public class JoinsInfoParameter extends PropertyListParameter<DMEOJoin> {
		private DMEOEntity _sourceEntity = null;
		private DMEOEntity _destinationEntity = null;

		public JoinsInfoParameter(String name, String label, DMEOEntity sourceEntity, int rowHeight, int visibleRowCount) {
			super(name, label, new Vector<DMEOJoin>(), rowHeight, visibleRowCount);
			_sourceEntity = sourceEntity;
		}

		public DMEOJoin addJoin() {
			DMEOJoin newJoin = new DMEOJoin(_sourceEntity);
			newJoin.setDestinationEntity(_destinationEntity);

			addToDMEOJoins(newJoin);
			return newJoin;
		}

		public void removeJoin(DMEOJoin join) {
			removeFromDMEOJoins(join);
		}

		public boolean addJoinEnabled(DMEOJoin join) {
			return true;
		}

		public boolean removeJoinEnabled(DMEOJoin join) {
			return true;
		}

		public List<DMEOJoin> getDMEOJoins() {
			return getValue();
		}

		public void addToDMEOJoins(DMEOJoin join) {
			getDMEOJoins().add(join);
		}

		public void removeFromDMEOJoins(DMEOJoin join) {
			getDMEOJoins().remove(join);
		}

		private void resetJoins() {
			Vector<DMEOJoin> toDelete = new Vector<DMEOJoin>();
			toDelete.addAll(getDMEOJoins());
			for (Enumeration en = toDelete.elements(); en.hasMoreElements();) {
				DMEOJoin next = (DMEOJoin) en.nextElement();
				next.delete();
			}
			getDMEOJoins().clear();
		}

		public DMEOEntity getDestinationEntity() {
			return _destinationEntity;
		}

		public void setDestinationEntity(DMEOEntity destinationEntity) {
			DMEOEntity oldDestinationEntity = getDestinationEntity();
			if (destinationEntity != oldDestinationEntity) {
				_destinationEntity = destinationEntity;
				for (DMEOJoin j : getDMEOJoins()) {
					j.setDestinationEntity(destinationEntity);
				}
			}
		}

	}

}
