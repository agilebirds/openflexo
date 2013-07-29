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

import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.view.diagram.viewpoint.ConnectorPatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.GraphicalElementPatternRole;
import org.openflexo.foundation.viewpoint.EditionPattern;
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

	/*VICNENTpublic static FlexoActionType<AbstractDeclareShapeInEditionPattern, FlexoObject, FlexoObject> actionType = new FlexoActionType<AbstractDeclareShapeInEditionPattern, FlexoObject, FlexoObject>(
			"declare_in_edition_pattern", FlexoActionType.editGroup, FlexoActionType.NORMAL_ACTION_TYPE) {


		@Override
		public AbstractDeclareShapeInEditionPattern makeNewAction(FlexoObject focusedObject, Vector<FlexoObject> globalSelection,
				FlexoEditor editor) {
			return new AbstractDeclareShapeInEditionPattern(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoObject shape, Vector<FlexoObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(FlexoObject shape, Vector<FlexoObject> globalSelection) {
			return shape != null && shape.getVirtualModel() != null;
		}

	};

	static {
		FlexoObject.addActionForClass(AbstractDeclareShapeInEditionPattern.actionType, FlexoObject.class);
	}
	*/
	public static enum NewEditionPatternChoices {
		MAP_SINGLE_INDIVIDUAL, BLANK_EDITION_PATTERN
	}

	public NewEditionPatternChoices patternChoice = NewEditionPatternChoices.MAP_SINGLE_INDIVIDUAL;

	private String editionPatternName;
	private IFlexoOntologyClass concept;
	private String individualPatternRoleName;

	public boolean isTopLevel = true;
	public EditionPattern containerEditionPattern;
	private String dropSchemeName;

	private EditionPattern newEditionPattern;
	private Hashtable<ExampleDrawingObjectEntry, GraphicalElementPatternRole> newGraphicalElementPatternRoles;

	// public Vector<PropertyEntry> propertyEntries = new Vector<PropertyEntry>();

	AbstractDeclareConnectorInEditionPattern(FlexoActionType actionType, T1 focusedObject, Vector<T2> globalSelection, FlexoEditor editor) {
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

	private ConnectorPatternRole patternRole;

	@Override
	public ConnectorPatternRole getPatternRole() {
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
		/*propertyEntries.clear();
		IFlexoOntology ownerOntology = concept.getOntology();
		for (IFlexoOntologyFeature p : concept.getPropertiesTakingMySelfAsDomain()) {
			if (p.getOntology() == ownerOntology && p instanceof IFlexoOntologyStructuralProperty) {
				PropertyEntry newEntry = new PropertyEntry((IFlexoOntologyStructuralProperty) p);
				propertyEntries.add(newEntry);
			}
		}*/
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

	/*public String getShapePatternRoleName() {
		if (StringUtils.isEmpty(shapePatternRoleName)) {
			return "shape";
		}
		return shapePatternRoleName;
	}

	public void setShapePatternRoleName(String shapePatternRoleName) {
		this.shapePatternRoleName = shapePatternRoleName;
	}*/

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
	@Override
	public EditionPattern getEditionPattern() {
		if (primaryChoice == DeclareInEditionPatternChoices.CREATES_EDITION_PATTERN) {
			return newEditionPattern;
		}
		return super.getEditionPattern();
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		// TODO Auto-generated method stub

	};

}
