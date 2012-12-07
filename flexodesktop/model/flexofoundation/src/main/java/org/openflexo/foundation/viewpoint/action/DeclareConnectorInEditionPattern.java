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
package org.openflexo.foundation.viewpoint.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ontology.BuiltInDataType;
import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.IFlexoOntologyDataProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyFeature;
import org.openflexo.foundation.ontology.IFlexoOntologyObjectProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.view.diagram.viewpoint.ConnectorPatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.LinkScheme;
import org.openflexo.foundation.view.diagram.viewpoint.editionaction.AddConnector;
import org.openflexo.foundation.viewpoint.CheckboxParameter;
import org.openflexo.foundation.viewpoint.DeclarePatternRole;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.EditionScheme;
import org.openflexo.foundation.viewpoint.EditionSchemeParameter;
import org.openflexo.foundation.viewpoint.ExampleDrawingConnector;
import org.openflexo.foundation.viewpoint.ExampleDrawingObject;
import org.openflexo.foundation.viewpoint.FloatParameter;
import org.openflexo.foundation.viewpoint.IndividualParameter;
import org.openflexo.foundation.viewpoint.IndividualPatternRole;
import org.openflexo.foundation.viewpoint.IntegerParameter;
import org.openflexo.foundation.viewpoint.TextFieldParameter;
import org.openflexo.foundation.viewpoint.URIParameter;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;
import org.openflexo.foundation.viewpoint.inspector.CheckboxInspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.EditionPatternInspector;
import org.openflexo.foundation.viewpoint.inspector.FloatInspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.InspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.IntegerInspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.TextFieldInspectorEntry;
import org.openflexo.toolbox.JavaUtils;
import org.openflexo.toolbox.StringUtils;

public class DeclareConnectorInEditionPattern extends DeclareInEditionPattern<DeclareConnectorInEditionPattern, ExampleDrawingConnector> {

	private static final Logger logger = Logger.getLogger(DeclareConnectorInEditionPattern.class.getPackage().getName());

	public static FlexoActionType<DeclareConnectorInEditionPattern, ExampleDrawingConnector, ExampleDrawingObject> actionType = new FlexoActionType<DeclareConnectorInEditionPattern, ExampleDrawingConnector, ExampleDrawingObject>(
			"declare_in_edition_pattern", FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DeclareConnectorInEditionPattern makeNewAction(ExampleDrawingConnector focusedObject,
				Vector<ExampleDrawingObject> globalSelection, FlexoEditor editor) {
			return new DeclareConnectorInEditionPattern(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(ExampleDrawingConnector connector, Vector<ExampleDrawingObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(ExampleDrawingConnector connector, Vector<ExampleDrawingObject> globalSelection) {
			return connector != null && connector.getViewPoint().getEditionPatterns().size() > 0;
		}

	};

	static {
		FlexoModelObject.addActionForClass(DeclareConnectorInEditionPattern.actionType, ExampleDrawingConnector.class);
	}

	public static enum NewEditionPatternChoices {
		MAP_SINGLE_INDIVIDUAL, MAP_OBJECT_PROPERTY, BLANK_EDITION_PATTERN
	}

	public NewEditionPatternChoices patternChoice = NewEditionPatternChoices.MAP_SINGLE_INDIVIDUAL;

	private String editionPatternName;
	private IFlexoOntologyClass concept;
	private IFlexoOntologyObjectProperty objectProperty;
	private String individualPatternRoleName;
	private String connectorPatternRoleName;
	private String objectPropertyStatementPatternRoleName;

	public EditionPattern fromEditionPattern;
	public EditionPattern toEditionPattern;

	private String linkSchemeName;

	private EditionPattern newEditionPattern;
	private ConnectorPatternRole newConnectorPatternRole;

	public Vector<PropertyEntry> propertyEntries = new Vector<PropertyEntry>();

	DeclareConnectorInEditionPattern(ExampleDrawingConnector focusedObject, Vector<ExampleDrawingObject> globalSelection, FlexoEditor editor) {
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

				ViewPointBuilder builder = new ViewPointBuilder(getFocusedObject().getViewPoint());

				// Create new edition pattern
				newEditionPattern = new EditionPattern(builder);
				newEditionPattern.setName(getEditionPatternName());

				// Find best URI base candidate
				PropertyEntry mainPropertyDescriptor = selectBestEntryForURIBaseName();

				// Create individual pattern role if required
				IndividualPatternRole individualPatternRole = null;
				if (patternChoice == NewEditionPatternChoices.MAP_SINGLE_INDIVIDUAL) {
					individualPatternRole = new IndividualPatternRole(builder);
					individualPatternRole.setPatternRoleName(getIndividualPatternRoleName());
					individualPatternRole.setOntologicType(getConcept());
					newEditionPattern.addToPatternRoles(individualPatternRole);
					newEditionPattern.setPrimaryConceptRole(individualPatternRole);
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
				if (mainPropertyDescriptor != null) {
					newConnectorPatternRole.setLabel(new ViewPointDataBinding(getIndividualPatternRoleName() + "."
							+ mainPropertyDescriptor.property.getName()));
				} else {
					newConnectorPatternRole.setReadOnlyLabel(true);
					newConnectorPatternRole.setLabel(new ViewPointDataBinding("\"label\""));
					newConnectorPatternRole.setExampleLabel(((ConnectorGraphicalRepresentation) getFocusedObject()
							.getGraphicalRepresentation()).getText());
				}
				// We clone here the GR (fixed unfocusable GR bug)
				newConnectorPatternRole.setGraphicalRepresentation(((ConnectorGraphicalRepresentation<?>) getFocusedObject()
						.getGraphicalRepresentation()).clone());
				newEditionPattern.addToPatternRoles(newConnectorPatternRole);
				newEditionPattern.setPrimaryRepresentationRole(newConnectorPatternRole);

				// Create other individual roles
				Vector<IndividualPatternRole> otherRoles = new Vector<IndividualPatternRole>();
				if (patternChoice == NewEditionPatternChoices.MAP_SINGLE_INDIVIDUAL) {
					for (PropertyEntry e : propertyEntries) {
						if (e.selectEntry) {
							if (e.property instanceof IFlexoOntologyObjectProperty) {
								IFlexoOntologyConcept range = ((IFlexoOntologyObjectProperty) e.property).getRange();
								if (range instanceof IFlexoOntologyClass) {
									IndividualPatternRole newPatternRole = new IndividualPatternRole(builder);
									newPatternRole.setPatternRoleName(e.property.getName());
									newPatternRole.setOntologicType((IFlexoOntologyClass) range);
									newEditionPattern.addToPatternRoles(newPatternRole);
									otherRoles.add(newPatternRole);
								}
							}
						}
					}
				}

				// Create new link scheme
				LinkScheme newLinkScheme = new LinkScheme(builder);
				newLinkScheme.setName(getLinkSchemeName());
				newLinkScheme.setFromTargetEditionPattern(fromEditionPattern);
				newLinkScheme.setToTargetEditionPattern(toEditionPattern);

				// Parameters
				if (patternChoice == NewEditionPatternChoices.MAP_SINGLE_INDIVIDUAL) {
					Vector<PropertyEntry> candidates = new Vector<PropertyEntry>();
					for (PropertyEntry e : propertyEntries) {
						if (e.selectEntry) {
							EditionSchemeParameter newParameter = null;
							if (e.property instanceof IFlexoOntologyDataProperty) {
								switch (((IFlexoOntologyDataProperty) e.property).getRange().getBuiltInDataType()) {
								case Boolean:
									newParameter = new CheckboxParameter(builder);
									newParameter.setName(e.property.getName());
									newParameter.setLabel(e.label);
									break;
								case Byte:
								case Integer:
								case Long:
								case Short:
									newParameter = new IntegerParameter(builder);
									newParameter.setName(e.property.getName());
									newParameter.setLabel(e.label);
									break;
								case Double:
								case Float:
									newParameter = new FloatParameter(builder);
									newParameter.setName(e.property.getName());
									newParameter.setLabel(e.label);
									break;
								case String:
									newParameter = new TextFieldParameter(builder);
									newParameter.setName(e.property.getName());
									newParameter.setLabel(e.label);
									break;
								default:
									break;
								}
							} else if (e.property instanceof IFlexoOntologyObjectProperty) {
								IFlexoOntologyConcept range = ((IFlexoOntologyObjectProperty) e.property).getRange();
								if (range instanceof IFlexoOntologyClass) {
									newParameter = new IndividualParameter(builder);
									newParameter.setName(e.property.getName());
									newParameter.setLabel(e.label);
									((IndividualParameter) newParameter).setConcept((IFlexoOntologyClass) range);
								}
							}
							if (newParameter != null) {
								newLinkScheme.addToParameters(newParameter);
							}
						}
					}

					URIParameter uriParameter = new URIParameter(builder);
					uriParameter.setName("uri");
					uriParameter.setLabel("uri");
					if (mainPropertyDescriptor != null) {
						uriParameter.setBaseURI(new ViewPointDataBinding(mainPropertyDescriptor.property.getName()));
					}
					newLinkScheme.addToParameters(uriParameter);

					// Declare pattern role
					for (IndividualPatternRole r : otherRoles) {
						DeclarePatternRole action = new DeclarePatternRole(builder);
						action.setAssignation(new ViewPointDataBinding(r.getPatternRoleName()));
						action.setObject(new ViewPointDataBinding("parameters." + r.getName()));
						newLinkScheme.addToActions(action);
					}

					// Add individual action
					/*AddIndividual newAddIndividual = new AddIndividual(builder);
					newAddIndividual.setAssignation(new ViewPointDataBinding(individualPatternRole.getPatternRoleName()));
					newAddIndividual.setIndividualName(new ViewPointDataBinding("parameters.uri"));
					for (PropertyEntry e : propertyEntries) {
						if (e.selectEntry) {
							if (e.property instanceof IFlexoOntologyObjectProperty) {
								IFlexoOntologyConcept range = ((IFlexoOntologyObjectProperty) e.property).getRange();
								if (range instanceof IFlexoOntologyClass) {
									ObjectPropertyAssertion propertyAssertion = new ObjectPropertyAssertion(builder);
									propertyAssertion.setOntologyProperty(e.property);
									propertyAssertion.setObject(new ViewPointDataBinding("parameters." + e.property.getName()));
									newAddIndividual.addToObjectAssertions(propertyAssertion);
								}
							} else if (e.property instanceof IFlexoOntologyDataProperty) {
								DataPropertyAssertion propertyAssertion = new DataPropertyAssertion(builder);
								propertyAssertion.setOntologyProperty(e.property);
								propertyAssertion.setValue(new ViewPointDataBinding("parameters." + e.property.getName()));
								newAddIndividual.addToDataAssertions(propertyAssertion);
							}
						}
					}
					newLinkScheme.addToActions(newAddIndividual);
					*/
				}

				// Add connector action
				AddConnector newAddConnector = new AddConnector(builder);
				newAddConnector.setAssignation(new ViewPointDataBinding(newConnectorPatternRole.getPatternRoleName()));
				newAddConnector.setFromShape(new ViewPointDataBinding(EditionScheme.FROM_TARGET + "."
						+ fromEditionPattern.getPrimaryRepresentationRole().getPatternRoleName()));
				newAddConnector.setToShape(new ViewPointDataBinding(EditionScheme.TO_TARGET + "."
						+ toEditionPattern.getPrimaryRepresentationRole().getPatternRoleName()));

				newLinkScheme.addToActions(newAddConnector);

				// Add new drop scheme
				newEditionPattern.addToEditionSchemes(newLinkScheme);

				// Add inspector
				EditionPatternInspector inspector = newEditionPattern.getInspector();
				inspector.setInspectorTitle(getEditionPatternName());
				if (patternChoice == NewEditionPatternChoices.MAP_SINGLE_INDIVIDUAL) {
					for (PropertyEntry e : propertyEntries) {
						if (e.selectEntry) {
							if (e.property instanceof IFlexoOntologyObjectProperty) {
								IFlexoOntologyConcept range = ((IFlexoOntologyObjectProperty) e.property).getRange();
								if (range instanceof IFlexoOntologyClass) {
									InspectorEntry newInspectorEntry = null;
									newInspectorEntry = new TextFieldInspectorEntry(builder);
									newInspectorEntry.setName(e.property.getName());
									newInspectorEntry.setLabel(e.label);
									newInspectorEntry.setIsReadOnly(true);
									newInspectorEntry.setData(new ViewPointDataBinding(e.property.getName() + ".uriName"));
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
									newInspectorEntry.setData(new ViewPointDataBinding(getIndividualPatternRoleName() + "."
											+ e.property.getName()));
									inspector.addToEntries(newInspectorEntry);
								}
							}
						}
					}
				}

				// And add the newly created edition pattern
				getFocusedObject().getViewPoint().addToEditionPatterns(newEditionPattern);

			default:
				logger.warning("Pattern not implemented");
			}
		} else {
			logger.warning("Focused role is null !");
		}
	}

	@Override
	public boolean isValid() {
		if (getFocusedObject() == null) {
			return false;
		}
		switch (primaryChoice) {
		case CHOOSE_EXISTING_EDITION_PATTERN:
			return getEditionPattern() != null && getPatternRole() != null;
		case CREATES_EDITION_PATTERN:
			switch (patternChoice) {
			case MAP_SINGLE_INDIVIDUAL:
				return StringUtils.isNotEmpty(getEditionPatternName()) && concept != null
						&& StringUtils.isNotEmpty(getIndividualPatternRoleName()) && StringUtils.isNotEmpty(getConnectorPatternRoleName())
						&& fromEditionPattern != null && toEditionPattern != null && StringUtils.isNotEmpty(getLinkSchemeName());
			case MAP_OBJECT_PROPERTY:
				return StringUtils.isNotEmpty(getEditionPatternName()) && objectProperty != null
						&& StringUtils.isNotEmpty(getObjectPropertyStatementPatternRoleName())
						&& StringUtils.isNotEmpty(getConnectorPatternRoleName()) && fromEditionPattern != null && toEditionPattern != null
						&& StringUtils.isNotEmpty(getLinkSchemeName());
			case BLANK_EDITION_PATTERN:
				return StringUtils.isNotEmpty(getEditionPatternName()) && StringUtils.isNotEmpty(getConnectorPatternRoleName())
						&& fromEditionPattern != null && toEditionPattern != null && StringUtils.isNotEmpty(getLinkSchemeName());
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
		propertyEntries.clear();
		IFlexoOntology ownerOntology = concept.getOntology();
		for (IFlexoOntologyFeature p : concept.getPropertiesTakingMySelfAsDomain()) {
			if (p.getOntology() == ownerOntology && p instanceof IFlexoOntologyStructuralProperty) {
				PropertyEntry newEntry = new PropertyEntry((IFlexoOntologyStructuralProperty) p);
				propertyEntries.add(newEntry);
			}
		}
	}

	public IFlexoOntologyObjectProperty getObjectProperty() {
		return objectProperty;
	}

	public void setObjectProperty(IFlexoOntologyObjectProperty property) {
		this.objectProperty = property;
	}

	public String getEditionPatternName() {
		if (StringUtils.isEmpty(editionPatternName) && concept != null) {
			return concept.getName();
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

	public String getLinkSchemeName() {
		if (StringUtils.isEmpty(linkSchemeName)) {
			return "link" + (fromEditionPattern != null ? fromEditionPattern.getName() : "") + "To"
					+ (toEditionPattern != null ? toEditionPattern.getName() : "");
		}
		return linkSchemeName;
	}

	public void setLinkSchemeName(String linkSchemeName) {
		this.linkSchemeName = linkSchemeName;
	}

	public class PropertyEntry {

		public IFlexoOntologyStructuralProperty property;
		public String label;
		public boolean selectEntry = true;

		public PropertyEntry(IFlexoOntologyStructuralProperty property) {
			this.property = property;
			if (StringUtils.isNotEmpty(property.getDescription())) {
				label = property.getDescription();
			} else {
				label = property.getName() + "_of_" + getIndividualPatternRoleName();
			}
		}

		public String getRange() {
			return property.getRange().getName();
		}
	}

	private PropertyEntry selectBestEntryForURIBaseName() {
		Vector<PropertyEntry> candidates = new Vector<PropertyEntry>();
		for (PropertyEntry e : propertyEntries) {
			if (e.selectEntry && e.property instanceof IFlexoOntologyDataProperty
					&& ((IFlexoOntologyDataProperty) e.property).getRange().getBuiltInDataType() == BuiltInDataType.String) {
				candidates.add(e);
			}
		}
		if (candidates.size() > 0) {
			return candidates.firstElement();
		}
		return null;
	}

	public PropertyEntry createPropertyEntry() {
		PropertyEntry newPropertyEntry = new PropertyEntry(null);
		propertyEntries.add(newPropertyEntry);
		return newPropertyEntry;
	}

	public PropertyEntry deletePropertyEntry(PropertyEntry aPropertyEntry) {
		propertyEntries.remove(aPropertyEntry);
		return aPropertyEntry;
	}

	public void selectAllProperties() {
		for (PropertyEntry e : propertyEntries) {
			e.selectEntry = true;
		}
	}

	public void selectNoneProperties() {
		for (PropertyEntry e : propertyEntries) {
			e.selectEntry = false;
		}
	}

	@Override
	public EditionPattern getEditionPattern() {
		if (primaryChoice == DeclareInEditionPatternChoices.CREATES_EDITION_PATTERN) {
			return newEditionPattern;
		}
		return super.getEditionPattern();
	};

}
