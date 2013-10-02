/*
 * (c) Copyright 2013 Openflexo
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

import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.technologyadapter.TypeAwareModelSlot;
import org.openflexo.foundation.view.diagram.model.DiagramElement;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramEditionScheme;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPalette;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPaletteElement;
import org.openflexo.foundation.view.diagram.viewpoint.DropScheme;
import org.openflexo.foundation.view.diagram.viewpoint.GraphicalElementPatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.ShapePatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.editionaction.AddShape;
import org.openflexo.foundation.viewpoint.AddIndividual;
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

public abstract class AbstractDeclareShapeInEditionPattern<T1 extends FlexoObject & GRShapeTemplate, T2 extends FlexoObject, A extends AbstractDeclareShapeInEditionPattern<T1, T2, A>>
		extends DeclareInEditionPattern<A, T1, T2> {

	private static final Logger logger = Logger.getLogger(AbstractDeclareShapeInEditionPattern.class.getPackage().getName());

	public static enum NewEditionPatternChoices {
		MAP_SINGLE_INDIVIDUAL, MAP_SINGLE_EDITION_PATTERN, BLANK_EDITION_PATTERN
	}

	public NewEditionPatternChoices patternChoice = NewEditionPatternChoices.MAP_SINGLE_INDIVIDUAL;

	private String editionPatternName;
	private IFlexoOntologyClass concept;
	private String individualPatternRoleName;
	private String virtualModelPatternRoleName;
	private EditionPattern newEditionPattern;
	private LinkedHashMap<DrawingObjectEntry, GraphicalElementPatternRole> newGraphicalElementPatternRoles;
	public DiagramPalette palette;

	private boolean isTopLevel = true;
	public boolean isPushedToPalette = false;
	private EditionPattern containerEditionPattern;
	private EditionPattern virtualModelConcept;
	private String dropSchemeName;

	AbstractDeclareShapeInEditionPattern(FlexoActionType actionType, T1 focusedObject, Vector<T2> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
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
						&& StringUtils.isNotEmpty(getIndividualPatternRoleName()) && getSelectedEntriesCount() > 0
						&& (isTopLevel || containerEditionPattern != null) && StringUtils.isNotEmpty(getDropSchemeName());
			case MAP_SINGLE_EDITION_PATTERN:
				return StringUtils.isNotEmpty(getEditionPatternName()) && virtualModelConcept != null
						&& StringUtils.isNotEmpty(getVirtualModelPatternRoleName()) && getSelectedEntriesCount() > 0
						&& (isTopLevel || containerEditionPattern != null) && StringUtils.isNotEmpty(getDropSchemeName());
			case BLANK_EDITION_PATTERN:
				return StringUtils.isNotEmpty(getEditionPatternName()) && getSelectedEntriesCount() > 0
						&& (isTopLevel || containerEditionPattern != null) && StringUtils.isNotEmpty(getDropSchemeName());
			default:
				break;
			}
		default:
			return false;
		}
	}

	private ShapePatternRole patternRole;

	@Override
	public ShapePatternRole getPatternRole() {
		return patternRole;
	}

	public void setPatternRole(ShapePatternRole patternRole) {
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
		/*propertyEntries.clear();
		IFlexoOntology ownerOntology = concept.getOntology();
		for (IFlexoOntologyFeature p : concept.getPropertiesTakingMySelfAsDomain()) {
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

	public String getDropSchemeName() {
		if (StringUtils.isEmpty(dropSchemeName)) {
			return "drop" + (StringUtils.isEmpty(getEditionPatternName()) ? "" : getEditionPatternName())
					+ (isTopLevel ? "AtTopLevel" : containerEditionPattern != null ? "In" + containerEditionPattern.getName() : "");
		}
		return dropSchemeName;
	}

	public void setDropSchemeName(String dropSchemeName) {
		this.dropSchemeName = dropSchemeName;
	}

	@Override
	public EditionPattern getEditionPattern() {
		if (primaryChoice == DeclareInEditionPatternChoices.CREATES_EDITION_PATTERN) {
			return newEditionPattern;
		}
		return super.getEditionPattern();
	}

	public String getEditionPatternName() {
		if (isTypeAwareModelSlot()) {
			if (StringUtils.isEmpty(editionPatternName) && concept != null) {
				return concept.getName();
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

	// public Vector<PropertyEntry> propertyEntries = new Vector<PropertyEntry>();

	/*
	public void selectNoneProperties() {
		for (PropertyEntry e : propertyEntries) {
			e.selectEntry = false;
		}
	}
	 */

	/*public class PropertyEntry {

		public IFlexoOntologyStructuralProperty property;
		public String label;
		public boolean selectEntry = false;

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
	}*/

	/*public PropertyEntry createPropertyEntry() {
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

	 */

	public String getVirtualModelPatternRoleName() {
		if (StringUtils.isEmpty(virtualModelPatternRoleName) && virtualModelConcept != null) {
			return JavaUtils.getVariableName(virtualModelConcept.getName());
		}
		return virtualModelPatternRoleName;
	}

	public void setVirtualModelPatternRoleName(String virtualModelPatternRoleName) {
		this.virtualModelPatternRoleName = virtualModelPatternRoleName;
	}

	public boolean isTopLevel() {
		return isTopLevel;
	}

	public void setTopLevel(boolean isTopLevel) {
		this.isTopLevel = isTopLevel;
	}

	public EditionPattern getContainerEditionPattern() {
		return containerEditionPattern;
	}

	public void setContainerEditionPattern(EditionPattern containerEditionPattern) {
		this.containerEditionPattern = containerEditionPattern;
	}

	@Override
	protected void doAction(Object context) {
		logger.info("Declare shape in edition pattern");
		if (isValid()) {
			switch (primaryChoice) {
			case CHOOSE_EXISTING_EDITION_PATTERN:
				if (getPatternRole() != null) {
					getPatternRole().updateGraphicalRepresentation(getFocusedObject().getGraphicalRepresentation());
				}
				break;
			case CREATES_EDITION_PATTERN:
				// DiagramShape diagramShape = getFocusedObject();

				VirtualModel.VirtualModelBuilder builder = new VirtualModel.VirtualModelBuilder(getFocusedObject()
						.getDiagramSpecification().getViewPointLibrary(), getFocusedObject().getDiagramSpecification().getViewPoint(),
						getFocusedObject().getDiagramSpecification().getResource());
				switch (patternChoice) {
				case MAP_SINGLE_INDIVIDUAL:
				case MAP_SINGLE_EDITION_PATTERN:
				case BLANK_EDITION_PATTERN:

					// Create new edition pattern
					newEditionPattern = new EditionPattern(builder);
					newEditionPattern.setName(getEditionPatternName());

					// And add the newly created edition pattern
					getFocusedObject().getDiagramSpecification().addToEditionPatterns(newEditionPattern);

					// Find best URI base candidate
					// PropertyEntry mainPropertyDescriptor = selectBestEntryForURIBaseName();

					// Create pattern role, if it is an ontology then we create an individual, otherwise if it is a virtual model we create
					// an edition pattern instance
					IndividualPatternRole<?> individualPatternRole = null;
					EditionPatternInstancePatternRole editionPatternPatternRole = null;
					if (patternChoice == NewEditionPatternChoices.MAP_SINGLE_INDIVIDUAL) {
						if (isTypeAwareModelSlot()) {
							TypeAwareModelSlot<?, ?> flexoOntologyModelSlot = (TypeAwareModelSlot<?, ?>) getModelSlot();
							individualPatternRole = flexoOntologyModelSlot.makeIndividualPatternRole(getConcept());
							individualPatternRole.setPatternRoleName(getIndividualPatternRoleName());
							individualPatternRole.setOntologicType(getConcept());
							newEditionPattern.addToPatternRoles(individualPatternRole);
							newEditionPattern.setPrimaryConceptRole(individualPatternRole);
						}
					}
					if (patternChoice == NewEditionPatternChoices.MAP_SINGLE_EDITION_PATTERN) {
						if (isVirtualModelModelSlot()) {
							VirtualModelModelSlot<?, ?> virtualModelModelSlot = (VirtualModelModelSlot<?, ?>) getModelSlot();
							editionPatternPatternRole = virtualModelModelSlot
									.makeEditionPatternInstancePatternRole(getVirtualModelConcept());
							editionPatternPatternRole.setPatternRoleName(getVirtualModelPatternRoleName());
							newEditionPattern.addToPatternRoles(editionPatternPatternRole);
						}
					}

					// Create graphical elements pattern role

					newGraphicalElementPatternRoles = new LinkedHashMap<DrawingObjectEntry, GraphicalElementPatternRole>();

					GraphicalElementPatternRole primaryRepresentationRole = null;
					for (DrawingObjectEntry entry : drawingObjectEntries) {
						if (entry.getSelectThis()) {
							ShapePatternRole newShapePatternRole = new ShapePatternRole(builder);
							newShapePatternRole.setPatternRoleName(entry.patternRoleName);
							/*if (mainPropertyDescriptor != null && entry.isMainEntry()) {
								newShapePatternRole.setLabel(new DataBinding<String>(getIndividualPatternRoleName() + "."
										+ mainPropertyDescriptor.property.getName()));
							} else {*/
							newShapePatternRole.setReadOnlyLabel(true);
							if (StringUtils.isNotEmpty(entry.graphicalObject.getName())) {
								newShapePatternRole.setLabel(new DataBinding<String>("\"" + entry.graphicalObject.getName() + "\""));
							}
							// }
							newShapePatternRole.setExampleLabel(((ShapeGraphicalRepresentation) entry.graphicalObject
									.getGraphicalRepresentation()).getText());
							// We clone here the GR (fixed unfocusable GR bug)
							newShapePatternRole.setGraphicalRepresentation(((ShapeGraphicalRepresentation<?>) entry.graphicalObject
									.getGraphicalRepresentation()).clone());
							// Forces GR to be displayed in view
							((ShapeGraphicalRepresentation<?>) newShapePatternRole.getGraphicalRepresentation())
									.setAllowToLeaveBounds(false);
							newEditionPattern.addToPatternRoles(newShapePatternRole);
							if (entry.getParentEntry() != null) {
								newShapePatternRole.setParentShapePatternRole((ShapePatternRole) newGraphicalElementPatternRoles.get(entry
										.getParentEntry()));
							}
							if (entry.isMainEntry()) {
								primaryRepresentationRole = newShapePatternRole;
							}
							newGraphicalElementPatternRoles.put(entry, newShapePatternRole);
						}
					}
					newEditionPattern.setPrimaryRepresentationRole(primaryRepresentationRole);

					if (isPushedToPalette) {
						DiagramPaletteElement _newPaletteElement = palette.addPaletteElement(newEditionPattern.getName(),
								getFocusedObject().getGraphicalRepresentation());
						_newPaletteElement.setEditionPattern(newEditionPattern);
					}

					// Create other individual roles
					/*Vector<IndividualPatternRole> otherRoles = new Vector<IndividualPatternRole>();
					if (patternChoice == NewEditionPatternChoices.MAP_SINGLE_INDIVIDUAL) {
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

					// Create new drop scheme
					DropScheme newDropScheme = new DropScheme(builder);
					newDropScheme.setName(getDropSchemeName());

					// Add new drop scheme
					newEditionPattern.addToEditionSchemes(newDropScheme);

					newDropScheme.setTopTarget(isTopLevel);
					if (!isTopLevel) {
						newDropScheme.setTargetEditionPattern(containerEditionPattern);
					}

					// Parameters
					if (patternChoice == NewEditionPatternChoices.MAP_SINGLE_INDIVIDUAL) {
						if (isTypeAwareModelSlot()) {
							TypeAwareModelSlot<?, ?> flexoOntologyModelSlot = (TypeAwareModelSlot<?, ?>) getModelSlot();
							// Vector<PropertyEntry> candidates = new Vector<PropertyEntry>();
							/*for (PropertyEntry e : propertyEntries) {
								if (e != null && e.selectEntry) {
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
										newDropScheme.addToParameters(newParameter);
									}
								}
							}*/

							URIParameter uriParameter = new URIParameter(builder);
							uriParameter.setName("uri");
							uriParameter.setLabel("uri");
							/*if (mainPropertyDescriptor != null) {
								uriParameter.setBaseURI(new DataBinding<String>(mainPropertyDescriptor.property.getName()));
							}*/
							newDropScheme.addToParameters(uriParameter);

							// Declare pattern role
							/*for (IndividualPatternRole r : otherRoles) {
								DeclarePatternRole action = new DeclarePatternRole(builder);
								action.setAssignation(new DataBinding<Object>(r.getPatternRoleName()));
								action.setObject(new DataBinding<Object>("parameters." + r.getName()));
								newDropScheme.addToActions(action);
							}*/

							// Add individual action
							AddIndividual<?, ?> newAddIndividual = flexoOntologyModelSlot.makeAddIndividualAction(individualPatternRole,
									newDropScheme);

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
							}*/
							newDropScheme.addToActions(newAddIndividual);
						}
					}

					// Parameters for edition patterns creation action
					/*if (patternChoice == NewEditionPatternChoices.MAP_SINGLE_EDITION_PATTERN) {
						if (isVirtualModelModelSlot()) {
							VirtualModelModelSlot<?, ?> virtualModelModelSlot = (VirtualModelModelSlot<?, ?>) getModelSlot();

							// Add individual action
							EditionAction newAddEditionPattern = virtualModelModelSlot.makeAddEditionPatternInstanceEditionAction(
									editionPatternPatternRole, newDropScheme);

							newDropScheme.addToActions(newAddEditionPattern);
						}
					}*/

					// Add shape/connector actions
					boolean mainPatternRole = true;
					for (GraphicalElementPatternRole graphicalElementPatternRole : newGraphicalElementPatternRoles.values()) {
						if (graphicalElementPatternRole instanceof ShapePatternRole) {
							ShapePatternRole grPatternRole = (ShapePatternRole) graphicalElementPatternRole;
							// Add shape action
							AddShape newAddShape = new AddShape(builder);
							newDropScheme.addToActions(newAddShape);
							newAddShape.setAssignation(new DataBinding<Object>(graphicalElementPatternRole.getPatternRoleName()));
							if (mainPatternRole) {
								if (isTopLevel) {
									newAddShape.setContainer(new DataBinding<DiagramElement<?>>(DiagramEditionScheme.TOP_LEVEL));
								} else {
									newAddShape.setContainer(new DataBinding<DiagramElement<?>>(DiagramEditionScheme.TARGET + "."
											+ containerEditionPattern.getPrimaryRepresentationRole().getPatternRoleName()));
								}
							} else {
								newAddShape.setContainer(new DataBinding<DiagramElement<?>>(grPatternRole.getParentShapePatternRole()
										.getPatternRoleName()));
							}
							mainPatternRole = false;
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
					break;
				}
			default:
				logger.warning("Pattern not implemented");
			}
		} else {
			logger.warning("Focused role is null !");
		}
	}

	/*public class PropertyEntry {

		public IFlexoOntologyStructuralProperty property;
		public String label;
		public boolean selectEntry = false;

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
	}*/

	/*public PropertyEntry createPropertyEntry() {
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
	 */

}