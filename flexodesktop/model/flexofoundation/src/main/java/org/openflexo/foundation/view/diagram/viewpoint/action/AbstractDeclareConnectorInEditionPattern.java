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
package org.openflexo.foundation.view.diagram.viewpoint.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyObjectProperty;
import org.openflexo.foundation.technologyadapter.TypeAwareModelSlot;
import org.openflexo.foundation.view.diagram.model.DiagramShape;
import org.openflexo.foundation.view.diagram.viewpoint.ConnectorPatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramEditionScheme;
import org.openflexo.foundation.view.diagram.viewpoint.GraphicalElementPatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.LinkScheme;
import org.openflexo.foundation.view.diagram.viewpoint.ShapePatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.editionaction.AddConnector;
import org.openflexo.foundation.viewpoint.AddEditionPatternInstance;
import org.openflexo.foundation.viewpoint.AddIndividual;
import org.openflexo.foundation.viewpoint.DeclarePatternRole;
import org.openflexo.foundation.viewpoint.DeleteAction;
import org.openflexo.foundation.viewpoint.DeletionScheme;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.EditionPatternInstancePatternRole;
import org.openflexo.foundation.viewpoint.EditionScheme;
import org.openflexo.foundation.viewpoint.IndividualPatternRole;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.URIParameter;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.VirtualModelModelSlot;
import org.openflexo.foundation.viewpoint.inspector.EditionPatternInspector;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.JavaUtils;
import org.openflexo.toolbox.StringUtils;

/**
 * This class represents an abstraction for a declare shape in edition pattern action among several kind of shapes.</br>
 * 
 * 
 * @author Vincent
 * 
 * @param <T1>
 */
public abstract class AbstractDeclareConnectorInEditionPattern<T1 extends FlexoObject & GRConnectorTemplate, T2 extends FlexoObject, A extends AbstractDeclareConnectorInEditionPattern<T1, T2, A>>
		extends DeclareInEditionPattern<A, T1, T2> {

	private static final Logger logger = Logger.getLogger(AbstractDeclareConnectorInEditionPattern.class.getPackage().getName());

	public static enum NewEditionPatternChoices {
		MAP_SINGLE_INDIVIDUAL, MAP_OBJECT_PROPERTY, MAP_SINGLE_EDITION_PATTERN, BLANK_EDITION_PATTERN
	}

	public NewEditionPatternChoices patternChoice = NewEditionPatternChoices.MAP_SINGLE_INDIVIDUAL;

	private String errorMessage;

	private String editionPatternName;
	private IFlexoOntologyClass concept;
	private IFlexoOntologyObjectProperty objectProperty;
	private String individualPatternRoleName;
	private String connectorPatternRoleName;
	private String objectPropertyStatementPatternRoleName;
	private String virtualModelPatternRoleName;

	private LinkScheme selectedLinkScheme;

	private EditionPattern newEditionPattern;
	private EditionPattern virtualModelConcept;
	private ConnectorPatternRole newConnectorPatternRole;

	private Vector<IndividualPatternRole> otherRoles;
	private IndividualPatternRole individualPatternRole;
	private EditionPatternInstancePatternRole editionPatternPatternRole;

	private static final String PATTERN_ROLE_IS_NULL = FlexoLocalization.localizedForKey("pattern_role_is_null");
	private static final String EDITION_PATTERN_IS_NULL = FlexoLocalization.localizedForKey("edition_pattern_is_null");
	private static final String EDITION_PATTERN_NAME_IS_NULL = FlexoLocalization.localizedForKey("edition_pattern_name_is_null");
	private static final String FOCUSED_OBJECT_IS_NULL = FlexoLocalization.localizedForKey("focused_object_is_null");
	private static final String INDIVIDUAL_PATTERN_ROLE_NAME_IS_NULL = FlexoLocalization
			.localizedForKey("individual_pattern_role_name_is_null");
	private static final String CONCEPT_IS_NULL = FlexoLocalization.localizedForKey("concept_is_null");
	private static final String CONNECTOR_PATTERN_ROLE_NAME_IS_NULL = FlexoLocalization
			.localizedForKey("connector_pattern_role_name_is_null");
	private static final String A_SCHEME_NAME_IS_NOT_VALID = FlexoLocalization.localizedForKey("a_scheme_name_is_not_valid");
	private static final String VIRTUAL_MODEL_PATTERN_ROLE_NAME_IS_NULL = FlexoLocalization
			.localizedForKey("virtual_model_pattern_role_name_is_null");
	private static final String VIRTUAL_MODEL_CONCEPT_IS_NULL = FlexoLocalization.localizedForKey("virtual_model_concept_is_null");

	// public Vector<PropertyEntry> propertyEntries = new Vector<PropertyEntry>();

	AbstractDeclareConnectorInEditionPattern(FlexoActionType actionType, T1 focusedObject, Vector<T2> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		logger.info("Declare connector in edition pattern");
		if (isValid()) {
			switch (primaryChoice) {
			case CHOOSE_EXISTING_EDITION_PATTERN:
				if (getPatternRole() != null) {
					System.out.println("Connector representation updated");
					// getPatternRole().setGraphicalRepresentation(getFocusedObject().getGraphicalRepresentation());
					getPatternRole().updateGraphicalRepresentation(getFocusedObject().getGraphicalRepresentation());
				}
				break;
			case CREATES_EDITION_PATTERN:

				VirtualModel.VirtualModelBuilder builder = new VirtualModel.VirtualModelBuilder(getFocusedObject()
						.getDiagramSpecification().getViewPointLibrary(), getFocusedObject().getDiagramSpecification().getViewPoint(),
						getFocusedObject().getDiagramSpecification().getResource());

				// Create new edition pattern
				newEditionPattern = new EditionPattern(builder);
				newEditionPattern.setName(getEditionPatternName());

				// And add the newly created edition pattern
				getFocusedObject().getDiagramSpecification().addToEditionPatterns(newEditionPattern);

				// Find best URI base candidate
				// PropertyEntry mainPropertyDescriptor = selectBestEntryForURIBaseName();

				// Create individual pattern role if required
				if (patternChoice == NewEditionPatternChoices.MAP_SINGLE_INDIVIDUAL) {
					if (isTypeAwareModelSlot()) {
						TypeAwareModelSlot ontologyModelSlot = (TypeAwareModelSlot) getModelSlot();
						individualPatternRole = ontologyModelSlot.makeIndividualPatternRole(getConcept());
						individualPatternRole.setPatternRoleName(getIndividualPatternRoleName());
						individualPatternRole.setOntologicType(getConcept());
						newEditionPattern.addToPatternRoles(individualPatternRole);
						newEditionPattern.setPrimaryConceptRole(individualPatternRole);
					}
				}

				// Create an edition pattern pattern role if required

				if (patternChoice == NewEditionPatternChoices.MAP_SINGLE_EDITION_PATTERN) {
					if (isVirtualModelModelSlot()) {
						VirtualModelModelSlot<?, ?> virtualModelModelSlot = (VirtualModelModelSlot<?, ?>) getModelSlot();
						editionPatternPatternRole = virtualModelModelSlot.makeEditionPatternInstancePatternRole(getVirtualModelConcept());
						editionPatternPatternRole.setPatternRoleName(getVirtualModelPatternRoleName());
						newEditionPattern.addToPatternRoles(editionPatternPatternRole);
					}
				}

				// Create individual pattern role if required
				/*ObjectPropertyStatementPatternRole objectPropertyStatementPatternRole = null;
				if (patternChoice == NewEditionPatternChoices.MAP_OBJECT_PROPERTY) {
					objectPropertyStatementPatternRole = new ObjectPropertyStatementPatternRole(builder);
					objectPropertyStatementPatternRole.setPatternRoleName(getObjectPropertyStatementPatternRoleName());
					objectPropertyStatementPatternRole.setObjectProperty(getObjectProperty());
					newEditionPattern.addToPatternRoles(objectPropertyStatementPatternRole);
					newEditionPattern.setPrimaryConceptRole(objectPropertyStatementPatternRole);
				}*/

				// Create connector pattern role
				newConnectorPatternRole = new ConnectorPatternRole(builder);
				newConnectorPatternRole.setPatternRoleName(getConnectorPatternRoleName());
				/*if (mainPropertyDescriptor != null) {
					newConnectorPatternRole.setLabel(new DataBinding<String>(getIndividualPatternRoleName() + "."
							+ mainPropertyDescriptor.property.getName()));
				} else {*/
				newConnectorPatternRole.setReadOnlyLabel(true);
				newConnectorPatternRole.setLabel(new DataBinding<String>("\"label\""));
				newConnectorPatternRole
						.setExampleLabel(((ConnectorGraphicalRepresentation) getFocusedObject().getGraphicalRepresentation()).getText());
				// }
				// We clone here the GR (fixed unfocusable GR bug)
				newConnectorPatternRole.setGraphicalRepresentation(((ConnectorGraphicalRepresentation<?>) getFocusedObject()
						.getGraphicalRepresentation()).clone());
				newEditionPattern.addToPatternRoles(newConnectorPatternRole);
				newEditionPattern.setPrimaryRepresentationRole(newConnectorPatternRole);

				// Create other individual roles
				otherRoles = new Vector<IndividualPatternRole>();
				/*if (patternChoice == NewEditionPatternChoices.MAP_SINGLE_INDIVIDUAL) {
					for (PropertyEntry e : propertyEntries) {
						if (e.selectEntry) {
							if (e.property instanceof IFlexoOntologyObjectProperty) {
								IFlexoOntologyConcept range = ((IFlexoOntologyObjectProperty) e.property).getRange();
								if (range instanceof IFlexoOntologyClass) {
									IndividualPatternRole newPatternRole = null; // new IndividualPatternRole(builder);
									newPatternRole.setPatternRoleName(e.property.getName());
									newPatternRole.setOntologicType((IFlexoOntologyClass) range);
									newEditionPattern.addToPatternRoles(newPatternRole);
									otherRoles.add(newPatternRole);
								}
							}
						}
					}
				}*/

				for (EditionSchemeConfiguration editionSchemeConf : getEditionSchemes()) {
					if (editionSchemeConf.isValid()) {
						createSchemeActions(editionSchemeConf, builder);
					}
				}

				// Add inspector
				EditionPatternInspector inspector = newEditionPattern.getInspector();
				inspector.setInspectorTitle(getEditionPatternName());
				if (patternChoice == NewEditionPatternChoices.MAP_SINGLE_INDIVIDUAL) {
					/*for (PropertyEntry e : propertyEntries) {
						if (e.selectEntry) {
							if (e.property instanceof IFlexoOntologyObjectProperty) {
								IFlexoOntologyConcept range = ((IFlexoOntologyObjectProperty) e.property).getRange();
								if (range instanceof IFlexoOntologyClass) {
									InspectorEntry newInspectorEntry = null;
									newInspectorEntry = new TextFieldInspectorEntry(builder);
									newInspectorEntry.setName(e.property.getName());
									newInspectorEntry.setLabel(e.label);
									newInspectorEntry.setIsReadOnly(true);
									newInspectorEntry.setData(new DataBinding<Object>(e.property.getName() + ".uriName"));
									inspector.addToEntries(newInspectorEntry);
								}
							} else if (e.property instanceof IFlexoOntologyDataProperty) {
								InspectorEntry newInspectorEntry = null;
								switch (((IFlexoOntologyDataProperty) e.property).getRange().getBuiltInDataType()) {
								case Boolean:
									newInspectorEntry = new CheckboxInspectorEntry(builder);
									break;
								case Byte:
								case Integer:
								case Long:
								case Short:
									newInspectorEntry = new IntegerInspectorEntry(builder);
									break;
								case Double:
								case Float:
									newInspectorEntry = new FloatInspectorEntry(builder);
									break;
								case String:
									newInspectorEntry = new TextFieldInspectorEntry(builder);
									break;
								default:
									logger.warning("Not handled: " + ((IFlexoOntologyDataProperty) e.property).getRange());
								}
								if (newInspectorEntry != null) {
									newInspectorEntry.setName(e.property.getName());
									newInspectorEntry.setLabel(e.label);
									newInspectorEntry.setData(new DataBinding<Object>(getIndividualPatternRoleName() + "."
											+ e.property.getName()));
									inspector.addToEntries(newInspectorEntry);
								}
							}
						}
					}*/
				}

			default:
				logger.warning("Pattern not implemented");
			}
		} else {
			logger.warning("Focused role is null !");
		}
	}

	public String getErrorMessage() {
		isValid();
		return errorMessage;
	}

	@Override
	public boolean isValid() {
		if (getFocusedObject() == null) {
			errorMessage = FOCUSED_OBJECT_IS_NULL;
			return false;
		}
		switch (primaryChoice) {
		case CHOOSE_EXISTING_EDITION_PATTERN:
			if (getEditionPattern() == null) {
				errorMessage = PATTERN_ROLE_IS_NULL;
			}
			if (getPatternRole() == null) {
				errorMessage = EDITION_PATTERN_IS_NULL;
			}
			return getEditionPattern() != null && getPatternRole() != null;
		case CREATES_EDITION_PATTERN:
			switch (patternChoice) {
			case MAP_SINGLE_INDIVIDUAL:
				if (StringUtils.isEmpty(getEditionPatternName())) {
					errorMessage = EDITION_PATTERN_NAME_IS_NULL;
				}
				if (StringUtils.isEmpty(getIndividualPatternRoleName())) {
					errorMessage = INDIVIDUAL_PATTERN_ROLE_NAME_IS_NULL;
				}
				if (concept == null) {
					errorMessage = CONCEPT_IS_NULL;
				}
				if (StringUtils.isEmpty(getConnectorPatternRoleName())) {
					errorMessage = CONNECTOR_PATTERN_ROLE_NAME_IS_NULL;
				}
				if (!editionSchemesNamedAreValid()) {
					errorMessage = A_SCHEME_NAME_IS_NOT_VALID;
				}
				return StringUtils.isNotEmpty(getEditionPatternName()) && concept != null
						&& StringUtils.isNotEmpty(getIndividualPatternRoleName()) && StringUtils.isNotEmpty(getConnectorPatternRoleName())
						&& editionSchemesNamedAreValid();
			case MAP_OBJECT_PROPERTY:
				return StringUtils.isNotEmpty(getEditionPatternName()) && objectProperty != null
						&& StringUtils.isNotEmpty(getObjectPropertyStatementPatternRoleName())
						&& StringUtils.isNotEmpty(getConnectorPatternRoleName()) && editionSchemesNamedAreValid();
			case MAP_SINGLE_EDITION_PATTERN:
				if (StringUtils.isEmpty(getEditionPatternName())) {
					errorMessage = EDITION_PATTERN_NAME_IS_NULL;
				}
				if (StringUtils.isEmpty(getVirtualModelPatternRoleName())) {
					errorMessage = VIRTUAL_MODEL_PATTERN_ROLE_NAME_IS_NULL;
				}
				if (virtualModelConcept == null) {
					errorMessage = VIRTUAL_MODEL_CONCEPT_IS_NULL;
				}
				if (StringUtils.isEmpty(getConnectorPatternRoleName())) {
					errorMessage = CONNECTOR_PATTERN_ROLE_NAME_IS_NULL;
				}
				if (!editionSchemesNamedAreValid()) {
					errorMessage = A_SCHEME_NAME_IS_NOT_VALID;
				}
				return StringUtils.isNotEmpty(getEditionPatternName()) && virtualModelConcept != null
						&& StringUtils.isNotEmpty(getVirtualModelPatternRoleName()) && getSelectedEntriesCount() > 0
						&& editionSchemesNamedAreValid();
			case BLANK_EDITION_PATTERN:
				if (StringUtils.isEmpty(getEditionPatternName())) {
					errorMessage = EDITION_PATTERN_NAME_IS_NULL;
				}
				if (StringUtils.isEmpty(getConnectorPatternRoleName())) {
					errorMessage = CONNECTOR_PATTERN_ROLE_NAME_IS_NULL;
				}
				if (!editionSchemesNamedAreValid()) {
					errorMessage = A_SCHEME_NAME_IS_NOT_VALID;
				}
				return StringUtils.isNotEmpty(getEditionPatternName()) && StringUtils.isNotEmpty(getConnectorPatternRoleName())
						&& editionSchemesNamedAreValid();
			default:
				break;
			}
		default:
			return false;
		}
	}

	private ConnectorPatternRole patternRole;

	@Override
	public ConnectorPatternRole getPatternRole() {
		if (primaryChoice == DeclareInEditionPatternChoices.CREATES_EDITION_PATTERN) {
			return newConnectorPatternRole;
		}
		return patternRole;
	}

	public void setPatternRole(ConnectorPatternRole patternRole) {
		this.patternRole = patternRole;
	}

	@Override
	public void resetPatternRole() {
		this.patternRole = null;
	}

	public IFlexoOntologyClass getConcept() {
		return concept;
	}

	public void setConcept(IFlexoOntologyClass concept) {
		this.concept = concept;
		// propertyEntries.clear();
		// IFlexoOntology ownerOntology = concept.getOntology();
		/*for (IFlexoOntologyFeature p : concept.getPropertiesTakingMySelfAsDomain()) {
			if (p.getOntology() == ownerOntology && p instanceof IFlexoOntologyStructuralProperty) {
				PropertyEntry newEntry = new PropertyEntry((IFlexoOntologyStructuralProperty) p);
				propertyEntries.add(newEntry);
			}
		}*/
	}

	public EditionPattern getVirtualModelConcept() {
		return virtualModelConcept;
	}

	public void setVirtualModelConcept(EditionPattern virtualModelConcept) {
		this.virtualModelConcept = virtualModelConcept;
	}

	public IFlexoOntologyObjectProperty getObjectProperty() {
		return objectProperty;
	}

	public void setObjectProperty(IFlexoOntologyObjectProperty property) {
		this.objectProperty = property;
	}

	@Override
	public void updateSpecialSchemeNames() {
		for (EditionSchemeConfiguration conf : getEditionSchemes()) {
			if (conf.getType() == EditionSchemeChoice.LINK) {
				if (((LinkScheme) conf.getEditionScheme()).getFromTargetEditionPattern() != null
						&& ((LinkScheme) conf.getEditionScheme()).getToTargetEditionPattern() != null) {
					conf.getEditionScheme().setName(
							"linkScheme" + ((LinkScheme) conf.getEditionScheme()).getFromTargetEditionPattern().getName()
									+ ((LinkScheme) conf.getEditionScheme()).getToTargetEditionPattern().getName());
				} else {
					conf.getEditionScheme().setName("linkScheme");
				}
			}
		}
	}

	public String getEditionPatternName() {
		if (isTypeAwareModelSlot()) {
			if (StringUtils.isEmpty(editionPatternName) && concept != null) {
				updateEditionSchemesName(concept.getName());
				return concept.getName();
			}
			if (StringUtils.isEmpty(editionPatternName) && objectProperty != null) {
				updateEditionSchemesName(objectProperty.getName());
				return objectProperty.getName();
			}
		}
		if (isVirtualModelModelSlot()) {
			if (StringUtils.isEmpty(editionPatternName) && virtualModelConcept != null) {
				updateEditionSchemesName(virtualModelConcept.getName());
				return virtualModelConcept.getName();
			}
		}
		return editionPatternName;
	}

	public void setEditionPatternName(String editionPatternName) {
		this.editionPatternName = editionPatternName;
	}

	public String getIndividualPatternRoleName() {
		if (StringUtils.isEmpty(individualPatternRoleName) && concept != null) {
			return JavaUtils.getVariableName(concept.getName());
		}
		return individualPatternRoleName;
	}

	public void setIndividualPatternRoleName(String individualPatternRoleName) {
		this.individualPatternRoleName = individualPatternRoleName;
	}

	public String getVirtualModelPatternRoleName() {
		if (StringUtils.isEmpty(virtualModelPatternRoleName) && virtualModelConcept != null) {
			return JavaUtils.getVariableName(virtualModelConcept.getName());
		}
		return virtualModelPatternRoleName;
	}

	public void setVirtualModelPatternRoleName(String virtualModelPatternRoleName) {
		this.virtualModelPatternRoleName = virtualModelPatternRoleName;
	}

	public String getObjectPropertyStatementPatternRoleName() {
		if (StringUtils.isEmpty(objectPropertyStatementPatternRoleName) && objectProperty != null) {
			return JavaUtils.getVariableName(objectProperty.getName()) + "Statement";
		}
		return objectPropertyStatementPatternRoleName;
	}

	public void setObjectPropertyStatementPatternRoleName(String objectPropertyStatementPatternRoleName) {
		this.objectPropertyStatementPatternRoleName = objectPropertyStatementPatternRoleName;
	}

	public String getConnectorPatternRoleName() {
		if (StringUtils.isEmpty(connectorPatternRoleName)) {
			return "connector";
		}
		return connectorPatternRoleName;
	}

	public void setConnectorPatternRoleName(String connectorPatternRoleName) {
		this.connectorPatternRoleName = connectorPatternRoleName;
	}

	@Override
	public EditionPattern getEditionPattern() {
		if (primaryChoice == DeclareInEditionPatternChoices.CREATES_EDITION_PATTERN) {
			return newEditionPattern;
		}
		return super.getEditionPattern();
	};

	private void createSchemeActions(EditionSchemeConfiguration editionSchemeConfiguration, VirtualModel.VirtualModelBuilder builder) {
		EditionScheme editionScheme = null;

		// Create new link scheme
		if (editionSchemeConfiguration.getType() == EditionSchemeChoice.LINK) {
			editionScheme = createLinkSchemeEditionActions(editionSchemeConfiguration, null);
		}

		// Delete shapes as well as model
		if (editionSchemeConfiguration.getType() == EditionSchemeChoice.DELETE_GR_AND_MODEL) {
			editionScheme = createDeleteEditionSchemeActions(editionSchemeConfiguration, null, false);
		}

		// Delete only shapes
		if (editionSchemeConfiguration.getType() == EditionSchemeChoice.DELETE_GR_ONLY) {
			editionScheme = createDeleteEditionSchemeActions(editionSchemeConfiguration, null, true);
		}
		newEditionPattern.addToEditionSchemes(editionScheme);
	}

	private EditionScheme createDeleteEditionSchemeActions(EditionSchemeConfiguration editionSchemeConfiguration,
			VirtualModel.VirtualModelBuilder builder, boolean shapeOnly) {

		DeletionScheme editionScheme = (DeletionScheme) editionSchemeConfiguration.getEditionScheme();

		Vector<PatternRole> rolesToDelete = new Vector<PatternRole>();
		if (shapeOnly) {
			for (PatternRole pr : newEditionPattern.getPatternRoles()) {
				if (pr instanceof GraphicalElementPatternRole) {
					rolesToDelete.add(pr);
				}
			}
		} else {
			for (PatternRole pr : newEditionPattern.getPatternRoles()) {
				rolesToDelete.add(pr);
			}
		}

		Collections.sort(rolesToDelete, new Comparator<PatternRole>() {
			@Override
			public int compare(PatternRole o1, PatternRole o2) {
				if (o1 instanceof ShapePatternRole && o2 instanceof ConnectorPatternRole) {
					return 1;
				} else if (o1 instanceof ConnectorPatternRole && o2 instanceof ShapePatternRole) {
					return -1;
				}

				if (o1 instanceof ShapePatternRole) {
					if (o2 instanceof ShapePatternRole) {
						if (((ShapePatternRole) o1).isEmbeddedIn((ShapePatternRole) o2)) {
							return -1;
						}
						if (((ShapePatternRole) o2).isEmbeddedIn((ShapePatternRole) o1)) {
							return 1;
						}
						return 0;
					}
				}
				return 0;
			}

		});
		for (PatternRole pr : rolesToDelete) {
			DeleteAction a = new DeleteAction(null);
			a.setObject(new DataBinding<Object>(pr.getPatternRoleName()));
			editionScheme.addToActions(a);
		}
		return editionScheme;
	}

	private EditionScheme createLinkSchemeEditionActions(EditionSchemeConfiguration editionSchemeConfiguration,
			VirtualModel.VirtualModelBuilder builder) {
		LinkScheme editionScheme = (LinkScheme) editionSchemeConfiguration.getEditionScheme();

		// Parameters
		if (patternChoice == NewEditionPatternChoices.MAP_SINGLE_INDIVIDUAL) {
			if (isTypeAwareModelSlot()) {
				TypeAwareModelSlot<?, ?> typeAwareModelSlot = (TypeAwareModelSlot<?, ?>) getModelSlot();

				URIParameter uriParameter = new URIParameter(builder);
				uriParameter.setName("uri");
				uriParameter.setLabel("uri");
				/*if (mainPropertyDescriptor != null) {
					uriParameter.setBaseURI(new DataBinding<String>(mainPropertyDescriptor.property.getName()));
				}*/
				editionScheme.addToParameters(uriParameter);

				// Declare pattern role
				for (IndividualPatternRole r : otherRoles) {
					DeclarePatternRole action = new DeclarePatternRole(builder);
					action.setAssignation(new DataBinding<Object>(r.getPatternRoleName()));
					action.setObject(new DataBinding<Object>("parameters." + r.getName()));
					editionScheme.addToActions(action);
				}

				// Add individual action
				if (individualPatternRole != null) {
					AddIndividual newAddIndividual = typeAwareModelSlot.makeAddIndividualAction(individualPatternRole,
							((LinkScheme) editionScheme));
					editionScheme.addToActions(newAddIndividual);
				}
			}
		}

		if (patternChoice == NewEditionPatternChoices.MAP_SINGLE_EDITION_PATTERN) {
			if (isVirtualModelModelSlot()) {
				VirtualModelModelSlot<?, ?> virtualModelModelSlot = (VirtualModelModelSlot<?, ?>) getModelSlot();

				// Add individual action
				AddEditionPatternInstance newAddEditionPatternInstance = virtualModelModelSlot
						.makeEditionAction(AddEditionPatternInstance.class);
				newAddEditionPatternInstance.setAssignation(new DataBinding<Object>(editionPatternPatternRole.getName()));
				newAddEditionPatternInstance.setEditionPatternType(editionPatternPatternRole.getEditionPatternType());
				editionScheme.addToActions(newAddEditionPatternInstance);
			}
		}

		// Add connector action
		AddConnector newAddConnector = new AddConnector(builder);
		newAddConnector.setAssignation(new DataBinding<Object>(newConnectorPatternRole.getPatternRoleName()));
		newAddConnector.setFromShape(new DataBinding<DiagramShape>(DiagramEditionScheme.FROM_TARGET
				+ "."
				+ getFocusedObject().getDiagramSpecification().getEditionPattern(editionScheme._getFromTarget())
						.getPrimaryRepresentationRole().getPatternRoleName()));
		newAddConnector.setToShape(new DataBinding<DiagramShape>(DiagramEditionScheme.TO_TARGET
				+ "."
				+ getFocusedObject().getDiagramSpecification().getEditionPattern(editionScheme._getToTarget())
						.getPrimaryRepresentationRole().getPatternRoleName()));

		editionScheme.addToActions(newAddConnector);
		return editionScheme;
	}

	public LinkScheme getLinkScheme(EditionSchemeConfiguration conf) {
		if (conf != null) {
			if (conf.getEditionScheme() instanceof LinkScheme) {
				selectedLinkScheme = (LinkScheme) conf.getEditionScheme();
				return selectedLinkScheme;
			}
		}
		return null;
	}

	public void addEditionSchemeConfigurationLink() {
		EditionSchemeConfiguration editionSchemeConfiguration = new EditionSchemeConfiguration(EditionSchemeChoice.LINK);
		getEditionSchemes().add(editionSchemeConfiguration);
	}

	public void initializeSchemes() {
		if (!getFocusedObject().getDiagramSpecification().getEditionPatterns().isEmpty()) {
			EditionSchemeConfiguration linkEditionScheme = new EditionSchemeConfiguration(EditionSchemeChoice.LINK);
			getEditionSchemes().add(linkEditionScheme);
			((LinkScheme) linkEditionScheme.getEditionScheme()).setToTargetEditionPattern(getFocusedObject().getDiagramSpecification()
					.getEditionPatterns().get(0));
			((LinkScheme) linkEditionScheme.getEditionScheme()).setToTargetEditionPattern(getFocusedObject().getDiagramSpecification()
					.getEditionPatterns().get(0));
		}
	}

	// Hack to keep the right edition patterns in link from/target drop downs
	// This should be removed.
	private List<EditionPattern> editionPatternsFromList;

	private List<EditionPattern> editionPatternsToList;

	public List<EditionPattern> getEditionPatternsFrom() {
		if (editionPatternsFromList == null) {
			editionPatternsFromList = new ArrayList<EditionPattern>();
			editionPatternsFromList.addAll(getDiagramSpecification().getEditionPatterns());
		}
		if (selectedLinkScheme != null
				&& getFocusedObject().getDiagramSpecification().getEditionPattern(selectedLinkScheme._getFromTarget()) != null) {
			EditionPattern currentFromEp = getFocusedObject().getDiagramSpecification().getEditionPattern(
					selectedLinkScheme._getFromTarget());
			EditionPattern firstEp = editionPatternsFromList.get(0);
			if (!currentFromEp.equals(firstEp)) {
				int lastIndex = editionPatternsFromList.indexOf(currentFromEp);
				;
				editionPatternsFromList.set(0, currentFromEp);
				editionPatternsFromList.set(lastIndex, firstEp);
			}
		}
		return editionPatternsFromList;
	}

	public List<EditionPattern> getEditionPatternsTo() {
		if (editionPatternsToList == null) {
			editionPatternsToList = new ArrayList<EditionPattern>();
			editionPatternsToList.addAll(getDiagramSpecification().getEditionPatterns());
		}
		if (selectedLinkScheme != null
				&& getFocusedObject().getDiagramSpecification().getEditionPattern(selectedLinkScheme._getToTarget()) != null) {
			EditionPattern currentToEp = getFocusedObject().getDiagramSpecification().getEditionPattern(selectedLinkScheme._getToTarget());
			EditionPattern firstEp = editionPatternsToList.get(0);
			if (!currentToEp.equals(firstEp)) {
				int lastIndex = editionPatternsToList.indexOf(currentToEp);
				editionPatternsToList.set(0, currentToEp);
				editionPatternsToList.set(lastIndex, firstEp);
			}
		}
		return editionPatternsToList;
	}

}
