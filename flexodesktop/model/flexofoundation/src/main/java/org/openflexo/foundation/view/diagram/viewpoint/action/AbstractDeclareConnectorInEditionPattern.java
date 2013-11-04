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
import org.openflexo.foundation.view.diagram.viewpoint.LinkScheme;
import org.openflexo.foundation.view.diagram.viewpoint.editionaction.AddConnector;
import org.openflexo.foundation.viewpoint.AddIndividual;
import org.openflexo.foundation.viewpoint.DeclarePatternRole;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.EditionPatternInstancePatternRole;
import org.openflexo.foundation.viewpoint.IndividualPatternRole;
import org.openflexo.foundation.viewpoint.URIParameter;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.VirtualModelModelSlot;
import org.openflexo.foundation.viewpoint.inspector.EditionPatternInspector;
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

	private String editionPatternName;
	private IFlexoOntologyClass concept;
	private IFlexoOntologyObjectProperty objectProperty;
	private String individualPatternRoleName;
	private String connectorPatternRoleName;
	private String objectPropertyStatementPatternRoleName;
	private String virtualModelPatternRoleName;

	public EditionPattern fromEditionPattern;
	public EditionPattern toEditionPattern;

	private String linkSchemeName;

	private EditionPattern newEditionPattern;
	private EditionPattern virtualModelConcept;
	private ConnectorPatternRole newConnectorPatternRole;

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
				IndividualPatternRole individualPatternRole = null;
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
				EditionPatternInstancePatternRole editionPatternPatternRole = null;
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
				newConnectorPatternRole
						.setGraphicalRepresentation((ConnectorGraphicalRepresentation) ((ConnectorGraphicalRepresentation) getFocusedObject()
								.getGraphicalRepresentation()).clone());
				newEditionPattern.addToPatternRoles(newConnectorPatternRole);
				newEditionPattern.setPrimaryRepresentationRole(newConnectorPatternRole);

				// Create other individual roles
				Vector<IndividualPatternRole> otherRoles = new Vector<IndividualPatternRole>();
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

				// Create new link scheme
				LinkScheme newLinkScheme = new LinkScheme(builder);
				newLinkScheme.setName(getLinkSchemeName());
				newLinkScheme.setFromTargetEditionPattern(fromEditionPattern);
				newLinkScheme.setToTargetEditionPattern(toEditionPattern);

				// Parameters
				if (patternChoice == NewEditionPatternChoices.MAP_SINGLE_INDIVIDUAL) {
					if (isTypeAwareModelSlot()) {
						TypeAwareModelSlot<?, ?> typeAwareModelSlot = (TypeAwareModelSlot<?, ?>) getModelSlot();
						/*Vector<PropertyEntry> candidates = new Vector<PropertyEntry>();
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
						}*/

						URIParameter uriParameter = new URIParameter(builder);
						uriParameter.setName("uri");
						uriParameter.setLabel("uri");
						/*if (mainPropertyDescriptor != null) {
							uriParameter.setBaseURI(new DataBinding<String>(mainPropertyDescriptor.property.getName()));
						}*/
						newLinkScheme.addToParameters(uriParameter);

						// Declare pattern role
						for (IndividualPatternRole r : otherRoles) {
							DeclarePatternRole action = new DeclarePatternRole(builder);
							action.setAssignation(new DataBinding<Object>(r.getPatternRoleName()));
							action.setObject(new DataBinding<Object>("parameters." + r.getName()));
							newLinkScheme.addToActions(action);
						}

						// Add individual action
						if (individualPatternRole != null) {
							AddIndividual newAddIndividual = typeAwareModelSlot.makeAddIndividualAction(individualPatternRole,
									newLinkScheme);
							newLinkScheme.addToActions(newAddIndividual);
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
				}

				// Add connector action
				AddConnector newAddConnector = new AddConnector(builder);
				newAddConnector.setAssignation(new DataBinding<Object>(newConnectorPatternRole.getPatternRoleName()));
				newAddConnector.setFromShape(new DataBinding<DiagramShape>(DiagramEditionScheme.FROM_TARGET + "."
						+ fromEditionPattern.getPrimaryRepresentationRole().getPatternRoleName()));
				newAddConnector.setToShape(new DataBinding<DiagramShape>(DiagramEditionScheme.TO_TARGET + "."
						+ toEditionPattern.getPrimaryRepresentationRole().getPatternRoleName()));

				newLinkScheme.addToActions(newAddConnector);

				// Add new drop scheme
				newEditionPattern.addToEditionSchemes(newLinkScheme);

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
			case MAP_SINGLE_EDITION_PATTERN:
				return StringUtils.isNotEmpty(getEditionPatternName()) && virtualModelConcept != null
						&& StringUtils.isNotEmpty(getVirtualModelPatternRoleName()) && getSelectedEntriesCount() > 0
						&& fromEditionPattern != null && toEditionPattern != null && StringUtils.isNotEmpty(getLinkSchemeName());
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

	public String getEditionPatternName() {
		if (isTypeAwareModelSlot()) {
			if (StringUtils.isEmpty(editionPatternName) && concept != null) {
				return concept.getName();
			}
			if (StringUtils.isEmpty(editionPatternName) && objectProperty != null) {
				return objectProperty.getName();
			}
		}
		if (isVirtualModelModelSlot()) {
			if (StringUtils.isEmpty(editionPatternName) && virtualModelConcept != null) {
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

	@Override
	public EditionPattern getEditionPattern() {
		if (primaryChoice == DeclareInEditionPatternChoices.CREATES_EDITION_PATTERN) {
			return newEditionPattern;
		}
		return super.getEditionPattern();
	};

	/*public class PropertyEntry {

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
	}*/

	/*private PropertyEntry selectBestEntryForURIBaseName() {
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
	}*/

}
