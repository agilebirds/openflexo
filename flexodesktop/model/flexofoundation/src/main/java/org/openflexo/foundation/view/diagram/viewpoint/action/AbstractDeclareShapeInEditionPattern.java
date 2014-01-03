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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
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
import org.openflexo.foundation.view.diagram.viewpoint.ConnectorPatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramEditionScheme;
import org.openflexo.foundation.view.diagram.viewpoint.DropScheme;
import org.openflexo.foundation.view.diagram.viewpoint.GraphicalElementPatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.ShapePatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.editionaction.AddShape;
import org.openflexo.foundation.viewpoint.AddEditionPatternInstance;
import org.openflexo.foundation.viewpoint.AddIndividual;
import org.openflexo.foundation.viewpoint.DeclarePatternRole;
import org.openflexo.foundation.viewpoint.DeleteAction;
import org.openflexo.foundation.viewpoint.DeletionScheme;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.EditionPatternInstanceParameter;
import org.openflexo.foundation.viewpoint.EditionPatternInstancePatternRole;
import org.openflexo.foundation.viewpoint.EditionScheme;
import org.openflexo.foundation.viewpoint.IndividualParameter;
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
	private EditionPatternInstancePatternRole editionPatternPatternRole;
	private EditionPattern newEditionPattern;
	private LinkedHashMap<DrawingObjectEntry, GraphicalElementPatternRole> newGraphicalElementPatternRoles;
	private EditionPattern virtualModelConcept;
	private DropScheme selectedDropScheme;

	private static final String PATTERN_ROLE_IS_NULL = FlexoLocalization.localizedForKey("pattern_role_is_null");
	private static final String EDITION_PATTERN_IS_NULL = FlexoLocalization.localizedForKey("edition_pattern_is_null");
	private static final String EDITION_PATTERN_NAME_IS_NULL = FlexoLocalization.localizedForKey("edition_pattern_name_is_null");
	private static final String FOCUSED_OBJECT_IS_NULL = FlexoLocalization.localizedForKey("focused_object_is_null");
	private static final String INDIVIDUAL_PATTERN_ROLE_NAME_IS_NULL = FlexoLocalization
			.localizedForKey("individual_pattern_role_name_is_null");
	private static final String CONCEPT_IS_NULL = FlexoLocalization.localizedForKey("concept_is_null");
	private static final String NO_SELECTED_ENTRY = FlexoLocalization.localizedForKey("no_selected_entry");
	private static final String A_SCHEME_NAME_IS_NOT_VALID = FlexoLocalization.localizedForKey("a_scheme_name_is_not_valid");
	private static final String VIRTUAL_MODEL_PATTERN_ROLE_NAME_IS_NULL = FlexoLocalization
			.localizedForKey("virtual_model_pattern_role_name_is_null");
	private static final String VIRTUAL_MODEL_CONCEPT_IS_NULL = FlexoLocalization.localizedForKey("virtual_model_concept_is_null");

	AbstractDeclareShapeInEditionPattern(FlexoActionType actionType, T1 focusedObject, Vector<T2> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	private String errorMessage;

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
		case CHOOSE_EXISTING_EDITION_PATTERN: {
			if (getEditionPattern() == null) {
				errorMessage = PATTERN_ROLE_IS_NULL;
			}
			if (getPatternRole() == null) {
				errorMessage = EDITION_PATTERN_IS_NULL;
			}
			return getEditionPattern() != null && getPatternRole() != null;
		}
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
				if (getSelectedEntriesCount() == 0) {
					errorMessage = NO_SELECTED_ENTRY;
				}
				if (!editionSchemesNamedAreValid()) {
					errorMessage = A_SCHEME_NAME_IS_NOT_VALID;
				}
				return StringUtils.isNotEmpty(getEditionPatternName()) && concept != null
						&& StringUtils.isNotEmpty(getIndividualPatternRoleName()) && getSelectedEntriesCount() > 0
						&& editionSchemesNamedAreValid();

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
				if (getSelectedEntriesCount() == 0) {
					errorMessage = NO_SELECTED_ENTRY;
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
				if (getSelectedEntriesCount() == 0) {
					errorMessage = NO_SELECTED_ENTRY;
				}
				if (!editionSchemesNamedAreValid()) {
					errorMessage = A_SCHEME_NAME_IS_NOT_VALID;
				}
				return StringUtils.isNotEmpty(getEditionPatternName()) && getSelectedEntriesCount() > 0 && editionSchemesNamedAreValid();
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

	/*public String getDropSchemeName() {
		if (StringUtils.isEmpty(dropSchemeName)) {
			return "drop" + (StringUtils.isEmpty(getEditionPatternName()) ? "" : getEditionPatternName())
					+ (isTopLevel ? "AtTopLevel" : containerEditionPattern != null ? "In" + containerEditionPattern.getName() : "");
		}
		return dropSchemeName;
	}

	public void setDropSchemeName(String dropSchemeName) {
		this.dropSchemeName = dropSchemeName;
	}*/

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

	@Override
	public void updateSpecialSchemeNames() {
		for (EditionSchemeConfiguration conf : getEditionSchemes()) {
			if (conf.getType() == EditionSchemeChoice.DROP_AND_CREATE) {
				DropScheme dropScheme = (DropScheme) conf.getEditionScheme();
				if (dropScheme.isTopTarget()) {
					dropScheme.setName("dropSchemeAtTopLevel");
				} else {
					dropScheme.setName("dropSchemeAt");
				}
			}
		}
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

	private IndividualPatternRole<?> individualPatternRole;

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
							if (entry.graphicalObject instanceof GRShapeTemplate) {
								GRShapeTemplate grShape = (GRShapeTemplate) entry.graphicalObject;
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
								newShapePatternRole.setExampleLabel((grShape.getGraphicalRepresentation()).getText());
								// We clone here the GR (fixed unfocusable GR bug)
								newShapePatternRole.setGraphicalRepresentation((grShape.getGraphicalRepresentation()).clone());
								// Forces GR to be displayed in view
								((ShapeGraphicalRepresentation<?>) newShapePatternRole.getGraphicalRepresentation())
										.setAllowToLeaveBounds(false);
								newEditionPattern.addToPatternRoles(newShapePatternRole);
								if (entry.getParentEntry() != null) {
									newShapePatternRole.setParentShapePatternRole((ShapePatternRole) newGraphicalElementPatternRoles
											.get(entry.getParentEntry()));
								}
								if (entry.isMainEntry()) {
									primaryRepresentationRole = newShapePatternRole;
								}
								newGraphicalElementPatternRoles.put(entry, newShapePatternRole);
							}
							if (entry.graphicalObject instanceof GRConnectorTemplate) {
								GRConnectorTemplate grConnector = (GRConnectorTemplate) entry.graphicalObject;
								ConnectorPatternRole newConnectorPatternRole = new ConnectorPatternRole(builder);
								newConnectorPatternRole.setPatternRoleName(entry.patternRoleName);
								newConnectorPatternRole.setReadOnlyLabel(true);
								if (StringUtils.isNotEmpty(entry.graphicalObject.getName())) {
									newConnectorPatternRole
											.setLabel(new DataBinding<String>("\"" + entry.graphicalObject.getName() + "\""));
								}
								newConnectorPatternRole.setExampleLabel((grConnector.getGraphicalRepresentation()).getText());
								newConnectorPatternRole.setGraphicalRepresentation((grConnector.getGraphicalRepresentation()).clone());
								newEditionPattern.addToPatternRoles(newConnectorPatternRole);
								// Set the source/target
								// newConnectorPatternRole.setEndShapePatternRole(endShapePatternRole);
								if (entry.isMainEntry()) {
									primaryRepresentationRole = newConnectorPatternRole;
								}
								newGraphicalElementPatternRoles.put(entry, newConnectorPatternRole);
							}

						}
					}
					newEditionPattern.setPrimaryRepresentationRole(primaryRepresentationRole);

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

	private void createSchemeActions(EditionSchemeConfiguration editionSchemeConfiguration, VirtualModel.VirtualModelBuilder builder) {
		EditionScheme editionScheme = null;

		// Delete shapes as well as model
		if (editionSchemeConfiguration.getType() == EditionSchemeChoice.DELETE_GR_AND_MODEL) {
			editionScheme = createDeleteEditionSchemeActions(editionSchemeConfiguration, null, false);
		}

		// Delete only shapes
		if (editionSchemeConfiguration.getType() == EditionSchemeChoice.DELETE_GR_ONLY) {
			editionScheme = createDeleteEditionSchemeActions(editionSchemeConfiguration, null, true);
		}

		// Drop
		if (editionSchemeConfiguration.getType() == EditionSchemeChoice.DROP_AND_SELECT
				|| editionSchemeConfiguration.getType() == EditionSchemeChoice.DROP_AND_CREATE) {
			editionScheme = createDropEditionSchemeActions(editionSchemeConfiguration, null);
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

	private EditionScheme createDropEditionSchemeActions(EditionSchemeConfiguration editionSchemeConfiguration,
			VirtualModel.VirtualModelBuilder builder) {
		// Create new drop scheme
		DropScheme editionScheme = (DropScheme) editionSchemeConfiguration.getEditionScheme();

		// Parameters
		if (patternChoice == NewEditionPatternChoices.MAP_SINGLE_INDIVIDUAL) {
			if (isTypeAwareModelSlot()) {
				TypeAwareModelSlot<?, ?> flexoOntologyModelSlot = (TypeAwareModelSlot<?, ?>) getModelSlot();

				if (editionSchemeConfiguration.getType() == EditionSchemeChoice.DROP_AND_SELECT) {
					IndividualParameter individualParameter = new IndividualParameter(builder);
					individualParameter.setName(individualPatternRole.getName());
					individualParameter.setLabel(individualPatternRole.getName());
					individualParameter.setModelSlot(individualPatternRole.getModelSlot());
					individualParameter.setConcept(individualPatternRole.getOntologicType());
					editionScheme.addToParameters(individualParameter);
					// Add individual action
					DeclarePatternRole declarePatternRole = new DeclarePatternRole(builder);
					declarePatternRole.setAssignation(new DataBinding<Object>(individualPatternRole.getName()));
					declarePatternRole.setObject(new DataBinding<Object>("parameters." + individualParameter.getName()));
					editionScheme.addToActions(declarePatternRole);
				}
				if (editionSchemeConfiguration.getType() == EditionSchemeChoice.DROP_AND_CREATE) {
					URIParameter uriParameter = new URIParameter(builder);
					uriParameter.setName("uri");
					uriParameter.setLabel("uri");
					editionScheme.addToParameters(uriParameter);
					// Add individual action
					AddIndividual<?, ?> newAddIndividual = flexoOntologyModelSlot.makeAddIndividualAction(individualPatternRole,
							editionScheme);
					editionScheme.addToActions(newAddIndividual);
				}
			}
		}

		if (patternChoice == NewEditionPatternChoices.MAP_SINGLE_EDITION_PATTERN) {
			if (isVirtualModelModelSlot()) {
				VirtualModelModelSlot<?, ?> virtualModelModelSlot = (VirtualModelModelSlot<?, ?>) getModelSlot();

				if (editionSchemeConfiguration.getType() == EditionSchemeChoice.DROP_AND_SELECT) {
					EditionPatternInstanceParameter editionPatternInstanceParameter = new EditionPatternInstanceParameter(builder);
					editionPatternInstanceParameter.setName(editionPatternPatternRole.getName());
					editionPatternInstanceParameter.setLabel(editionPatternPatternRole.getName());
					editionPatternInstanceParameter.setModelSlot(editionPatternPatternRole.getModelSlot());
					// editionPatternInstanceParameter.setEditionPatternType(editionPatternPatternRole.getEditionPatternType());
					editionScheme.addToParameters(editionPatternInstanceParameter);
					// Add individual action
					DeclarePatternRole declarePatternRole = new DeclarePatternRole(builder);
					declarePatternRole.setObject(new DataBinding<Object>("parameters." + editionPatternInstanceParameter.getName()));
					declarePatternRole.setAssignation(new DataBinding<Object>(editionPatternPatternRole.getName()));
					editionScheme.addToActions(declarePatternRole);
				}
				if (editionSchemeConfiguration.getType() == EditionSchemeChoice.DROP_AND_CREATE) {
					// Add individual action
					AddEditionPatternInstance newAddEditionPatternInstance = virtualModelModelSlot
							.makeEditionAction(AddEditionPatternInstance.class);
					newAddEditionPatternInstance.setAssignation(new DataBinding<Object>(editionPatternPatternRole.getName()));
					newAddEditionPatternInstance.setEditionPatternType(editionPatternPatternRole.getEditionPatternType());
					editionScheme.addToActions(newAddEditionPatternInstance);
				}
			}
		}

		// Add shape/connector actions
		boolean mainPatternRole = true;
		for (GraphicalElementPatternRole graphicalElementPatternRole : newGraphicalElementPatternRoles.values()) {
			if (graphicalElementPatternRole instanceof ShapePatternRole) {
				ShapePatternRole grPatternRole = (ShapePatternRole) graphicalElementPatternRole;
				// Add shape action
				AddShape newAddShape = new AddShape(builder);
				editionScheme.addToActions(newAddShape);
				newAddShape.setAssignation(new DataBinding<Object>(graphicalElementPatternRole.getPatternRoleName()));
				if (mainPatternRole) {
					if (editionScheme.isTopTarget()) {
						newAddShape.setContainer(new DataBinding<DiagramElement<?>>(DiagramEditionScheme.TOP_LEVEL));
					} else {

						newAddShape.setContainer(new DataBinding<DiagramElement<?>>(DiagramEditionScheme.TARGET
								+ "."
								+ getFocusedObject().getDiagramSpecification().getEditionPattern(editionScheme._getTarget())
										.getPrimaryRepresentationRole().getPatternRoleName()));
					}
				} else {
					newAddShape.setContainer(new DataBinding<DiagramElement<?>>(grPatternRole.getParentShapePatternRole()
							.getPatternRoleName()));
				}
				mainPatternRole = false;
			}
		}
		return editionScheme;
	}

	public DropScheme getDropScheme(EditionSchemeConfiguration conf) {
		if (conf != null) {
			if (conf.getEditionScheme() instanceof DropScheme) {
				selectedDropScheme = (DropScheme) conf.getEditionScheme();
				return selectedDropScheme;
			}
		}
		return null;
	}

	public void addEditionSchemeConfigurationDropAndCreate() {
		EditionSchemeConfiguration editionSchemeConfiguration = new EditionSchemeConfiguration(EditionSchemeChoice.DROP_AND_CREATE);
		getEditionSchemes().add(editionSchemeConfiguration);
	}

	public void addEditionSchemeConfigurationDropAndSelect() {
		EditionSchemeConfiguration editionSchemeConfiguration = new EditionSchemeConfiguration(EditionSchemeChoice.DROP_AND_SELECT);
		getEditionSchemes().add(editionSchemeConfiguration);
	}

	@Override
	public void initializeSchemes() {
		EditionSchemeConfiguration dropEditionScheme = new EditionSchemeConfiguration(EditionSchemeChoice.DROP_AND_CREATE);
		dropEditionScheme.getEditionScheme().setName("drop");
		getEditionSchemes().add(dropEditionScheme);
	}

	private List<EditionPattern> editionPatternsDropList;

	public List<EditionPattern> getEditionPatternsDrop() {
		if (editionPatternsDropList == null) {
			editionPatternsDropList = new ArrayList<EditionPattern>();
			editionPatternsDropList.addAll(getDiagramSpecification().getEditionPatterns());
		}
		if (selectedDropScheme != null
				&& getFocusedObject().getDiagramSpecification().getEditionPattern(selectedDropScheme._getTarget()) != null) {
			EditionPattern currentFromEp = getFocusedObject().getDiagramSpecification().getEditionPattern(selectedDropScheme._getTarget());
			EditionPattern firstEp = editionPatternsDropList.get(0);
			if (!currentFromEp.equals(firstEp)) {
				int lastIndex = editionPatternsDropList.indexOf(currentFromEp);
				;
				editionPatternsDropList.set(0, currentFromEp);
				editionPatternsDropList.set(lastIndex, firstEp);
			}
		}
		return editionPatternsDropList;
	}

}
