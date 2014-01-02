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

import java.util.Collections;
import java.util.Comparator;
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

	private Vector<IndividualPatternRole> otherRoles;
	private IndividualPatternRole individualPatternRole;

	// public Vector<PropertyEntry> propertyEntries = new Vector<PropertyEntry>();

	AbstractDeclareConnectorInEditionPattern(FlexoActionType actionType, T1 focusedObject, Vector<T2> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		EditionSchemeConfiguration linkEditionScheme = new EditionSchemeConfiguration(getLinkSchemeName(), EditionSchemeChoice.LINK);
		getEditionSchemes().add(linkEditionScheme);
		linkEditionScheme.setValid(true);
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
						createScheme(editionSchemeConf, newEditionPattern, builder);
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
						&& fromEditionPattern != null && toEditionPattern != null && editionSchemesNamedAreValid();
			case MAP_OBJECT_PROPERTY:
				return StringUtils.isNotEmpty(getEditionPatternName()) && objectProperty != null
						&& StringUtils.isNotEmpty(getObjectPropertyStatementPatternRoleName())
						&& StringUtils.isNotEmpty(getConnectorPatternRoleName()) && fromEditionPattern != null && toEditionPattern != null
						&& editionSchemesNamedAreValid();
			case MAP_SINGLE_EDITION_PATTERN:
				return StringUtils.isNotEmpty(getEditionPatternName()) && virtualModelConcept != null
						&& StringUtils.isNotEmpty(getVirtualModelPatternRoleName()) && getSelectedEntriesCount() > 0
						&& fromEditionPattern != null && toEditionPattern != null && editionSchemesNamedAreValid();
			case BLANK_EDITION_PATTERN:
				return StringUtils.isNotEmpty(getEditionPatternName()) && StringUtils.isNotEmpty(getConnectorPatternRoleName())
						&& fromEditionPattern != null && toEditionPattern != null && editionSchemesNamedAreValid();
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
				conf.setName("linkScheme" + fromEditionPattern.getName() + toEditionPattern.getName());
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
		updateEditionSchemesName(editionPatternName);
		return editionPatternName;
	}

	public void setEditionPatternName(String editionPatternName) {
		this.editionPatternName = editionPatternName;
		updateEditionSchemesName(editionPatternName);
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

	private EditionScheme createScheme(EditionSchemeConfiguration editionSchemeConfiguration, EditionPattern editionPattern,
			VirtualModel.VirtualModelBuilder builder) {
		EditionScheme editionScheme = null;

		// Create new link scheme
		if (editionSchemeConfiguration.getType() == EditionSchemeChoice.LINK) {
			editionScheme = new LinkScheme(builder);
			editionScheme.setName(editionSchemeConfiguration.getName());
			((LinkScheme) editionScheme).setFromTargetEditionPattern(fromEditionPattern);
			((LinkScheme) editionScheme).setToTargetEditionPattern(toEditionPattern);
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
					((LinkScheme) editionScheme).addToParameters(uriParameter);

					// Declare pattern role
					for (IndividualPatternRole r : otherRoles) {
						DeclarePatternRole action = new DeclarePatternRole(builder);
						action.setAssignation(new DataBinding<Object>(r.getPatternRoleName()));
						action.setObject(new DataBinding<Object>("parameters." + r.getName()));
						((LinkScheme) editionScheme).addToActions(action);
					}

					// Add individual action
					if (individualPatternRole != null) {
						AddIndividual newAddIndividual = typeAwareModelSlot.makeAddIndividualAction(individualPatternRole,
								((LinkScheme) editionScheme));
						((LinkScheme) editionScheme).addToActions(newAddIndividual);
					}
				}
			}

			// Add connector action
			AddConnector newAddConnector = new AddConnector(builder);
			newAddConnector.setAssignation(new DataBinding<Object>(newConnectorPatternRole.getPatternRoleName()));
			newAddConnector.setFromShape(new DataBinding<DiagramShape>(DiagramEditionScheme.FROM_TARGET + "."
					+ fromEditionPattern.getPrimaryRepresentationRole().getPatternRoleName()));
			newAddConnector.setToShape(new DataBinding<DiagramShape>(DiagramEditionScheme.TO_TARGET + "."
					+ toEditionPattern.getPrimaryRepresentationRole().getPatternRoleName()));

			((LinkScheme) editionScheme).addToActions(newAddConnector);

			// Add new drop scheme
			editionPattern.addToEditionSchemes(editionScheme);
		}

		// Delete shapes as well as model
		if (editionSchemeConfiguration.getType() == EditionSchemeChoice.DELETE_GR_AND_MODEL) {
			editionScheme = editionPattern.createDeletionScheme();
			editionScheme.setName(editionSchemeConfiguration.getName());
		}

		// Delete only shapes
		if (editionSchemeConfiguration.getType() == EditionSchemeChoice.DELETE_GR_ONLY) {
			editionScheme = new DeletionScheme(null);
			editionScheme.setName(editionSchemeConfiguration.getName());
			Vector<PatternRole> rolesToDelete = new Vector<PatternRole>();
			for (PatternRole pr : editionPattern.getPatternRoles()) {
				if (pr instanceof GraphicalElementPatternRole) {
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
			editionPattern.addToEditionSchemes(editionScheme);
		}

		return editionScheme;
	}

}
