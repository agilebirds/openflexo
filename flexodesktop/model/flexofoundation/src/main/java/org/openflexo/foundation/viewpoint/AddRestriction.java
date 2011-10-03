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
package org.openflexo.foundation.viewpoint;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.OntologicObjectPatternRole.OntologicObjectType;



public class AddRestriction extends AddProperty {

	private static final Logger logger = Logger.getLogger(AddRestriction.class.getPackage().getName());

	private String propertyURI;
	private String object;
	private String restrictionType;
	private String cardinality;
	
	public AddRestriction() {
	}
	
	@Override
	public EditionActionType getEditionActionType()
	{
		return EditionActionType.AddRestriction;
	}
	

	public String _getPropertyURI()
	{
		return propertyURI;
	}

	public void _setPropertyURI(String propertyURI) 
	{
		this.propertyURI = propertyURI;
	}

	public OntologyProperty getObjectProperty()
	{
		getCalc().loadWhenUnloaded();
		return getOntologyLibrary().getObjectProperty(_getPropertyURI());
	}
	
	public void setObjectProperty(OntologyProperty p)
	{
		_setPropertyURI(p != null ? p.getURI() : null);
	}

	public String _getObject()
	{
		return object;
	}
	
	public void _setObject(String anObject)
	{
		object = anObject;
	}
	
	private Vector<String> availableObjectValues = null;
	
	public Vector<String> getAvailableObjectValues()
	{
		if (availableObjectValues == null) {
			availableObjectValues = new Vector<String>();
			switch (getScheme().getEditionSchemeType()) {
			case DropScheme:
				availableObjectValues.add(EditionAction.CONTAINER);
				availableObjectValues.add(EditionAction.CONTAINER_OF_CONTAINER);
				break;
			case LinkScheme:
				availableObjectValues.add(EditionAction.FROM_TARGET);
				availableObjectValues.add(EditionAction.TO_TARGET);
				break;
			default:
				break;
			}
			for (PatternRole pr : getEditionPattern().getPatternRoles()) {
				availableObjectValues.add(pr.getPatternRoleName());
			}
			for (EditionPatternParameter p : getScheme().getParameters()) {
				availableObjectValues.add(p.getName());
			}
		}
		return availableObjectValues;
	}

	public OntologyObject getPropertyObject(EditionSchemeAction action)
	{
		return retrieveOntologyObject(_getObject(), action);
	}

	@Override
	public String getInspectorName() 
	{
		return Inspectors.CED.ADD_RESTRICTION_INSPECTOR;
	}

	public String _getRestrictionType() {
		return restrictionType;
	}

	public void _setRestrictionType(String restrictionType) {
		this.restrictionType = restrictionType;
	}
	
	public EditionPatternParameter getRestrictionTypeParameter()
	{
		return getScheme().getParameter(restrictionType);
	}
	
	public void setRestrictionTypeParameter(EditionPatternParameter param)
	{
		restrictionType = param.getName();
	}

	public String _getCardinality() {
		return cardinality;
	}

	public void _setCardinality(String cardinality) {
		this.cardinality = cardinality;
	}

	public EditionPatternParameter getCardinalityParameter()
	{
		return getScheme().getParameter(cardinality);
	}
	
	public void setCardinalityParameter(EditionPatternParameter param)
	{
		cardinality = param.getName();
	}

	@Override
	public OntologicObjectPatternRole getPatternRole() {
		try {
			return super.getPatternRole();
		} catch (ClassCastException e) {
			logger.warning("Unexpected pattern role type");
			setPatternRole(null);
			return null;
		}
	}
	
	@Override
	protected void updatePatternRoleType()
	{
		if (getPatternRole() == null) {
			return;
		}
		
		getPatternRole().setOntologicObjectType(OntologicObjectType.OntologyStatement);
	}
}
