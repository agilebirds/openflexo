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
package org.openflexo.technologyadapter.owl.viewpoint.editionaction;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;
import org.openflexo.technologyadapter.owl.model.OntologyRestrictionClass.RestrictionType;
import org.openflexo.technologyadapter.owl.model.SubClassStatement;

public class AddRestrictionStatement extends AddStatement {

	private static final Logger logger = Logger.getLogger(AddRestrictionStatement.class.getPackage().getName());

	private String propertyURI;

	public AddRestrictionStatement(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public EditionActionType getEditionActionType() {
		return EditionActionType.AddRestrictionStatement;
	}

	/*@Override
	public List<RestrictionStatementPatternRole> getAvailablePatternRoles() {
		return getEditionPattern().getPatternRoles(RestrictionStatementPatternRole.class);
	}*/

	public String _getPropertyURI() {
		return propertyURI;
	}

	public void _setPropertyURI(String propertyURI) {
		this.propertyURI = propertyURI;
	}

	public OntologyProperty getObjectProperty() {
		getViewPoint().loadWhenUnloaded();
		return getViewPoint().getViewpointOntology().getObjectProperty(_getPropertyURI());
	}

	public void setObjectProperty(OntologyProperty p) {
		_setPropertyURI(p != null ? p.getURI() : null);
	}

	public OntologyObject getPropertyObject(EditionSchemeAction action) {
		return (OntologyObject) getObject().getBindingValue(action);
	}

	@Override
	public String getInspectorName() {
		return Inspectors.VPM.ADD_RESTRICTION_INSPECTOR;
	}

	/*@Override
	protected void updatePatternRoleType()
	{
		if (getPatternRole() == null) {
			return;
		}
	}*/

	private ViewPointDataBinding object;

	private BindingDefinition OBJECT = new BindingDefinition("object", OntologyObject.class, BindingDefinitionType.GET, false);

	public BindingDefinition getObjectBindingDefinition() {
		return OBJECT;
	}

	public ViewPointDataBinding getObject() {
		if (object == null) {
			object = new ViewPointDataBinding(this, EditionActionBindingAttribute.object, getObjectBindingDefinition());
		}
		return object;
	}

	public void setObject(ViewPointDataBinding object) {
		object.setOwner(this);
		object.setBindingAttribute(EditionActionBindingAttribute.object);
		object.setBindingDefinition(getObjectBindingDefinition());
		this.object = object;
	}

	private ViewPointDataBinding restrictionType;

	private BindingDefinition RESTRICTION_TYPE = new BindingDefinition("restrictionType", RestrictionType.class, BindingDefinitionType.GET,
			false);

	public BindingDefinition getRestrictionTypeBindingDefinition() {
		return RESTRICTION_TYPE;
	}

	public ViewPointDataBinding getRestrictionType() {
		if (restrictionType == null) {
			restrictionType = new ViewPointDataBinding(this, EditionActionBindingAttribute.restrictionType,
					getRestrictionTypeBindingDefinition());
		}
		return restrictionType;
	}

	public void setRestrictionType(ViewPointDataBinding restrictionType) {
		restrictionType.setOwner(this);
		restrictionType.setBindingAttribute(EditionActionBindingAttribute.restrictionType);
		restrictionType.setBindingDefinition(getRestrictionTypeBindingDefinition());
		this.restrictionType = restrictionType;
	}

	public RestrictionType getRestrictionType(EditionSchemeAction action) {
		RestrictionType restrictionType = (RestrictionType) getRestrictionType().getBindingValue(action);
		if (restrictionType == null) {
			restrictionType = RestrictionType.Some;
		}
		return restrictionType;
	}

	private ViewPointDataBinding cardinality;

	private BindingDefinition CARDINALITY = new BindingDefinition("cardinality", Integer.class, BindingDefinitionType.GET, false);

	public BindingDefinition getCardinalityBindingDefinition() {
		return CARDINALITY;
	}

	public ViewPointDataBinding getCardinality() {
		if (cardinality == null) {
			cardinality = new ViewPointDataBinding(this, EditionActionBindingAttribute.cardinality, getCardinalityBindingDefinition());
		}
		return cardinality;
	}

	public void setCardinality(ViewPointDataBinding cardinality) {
		cardinality.setOwner(this);
		cardinality.setBindingAttribute(EditionActionBindingAttribute.cardinality);
		cardinality.setBindingDefinition(getCardinalityBindingDefinition());
		this.cardinality = cardinality;
	}

	public int getCardinality(EditionSchemeAction action) {
		return ((Number) getCardinality().getBindingValue(action)).intValue();
	}

	@Override
	public Type getAssignableType() {
		return SubClassStatement.class;
	}

}
