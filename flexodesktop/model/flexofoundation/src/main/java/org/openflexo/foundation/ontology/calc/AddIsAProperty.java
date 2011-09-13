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
package org.openflexo.foundation.ontology.calc;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.action.EditionSchemeAction;
import org.openflexo.foundation.ontology.calc.OntologicObjectPatternRole.OntologicObjectType;



public class AddIsAProperty extends AddProperty {

	private static final Logger logger = Logger.getLogger(AddIsAProperty.class.getPackage().getName());

	private String father;
	
	public AddIsAProperty() {
	}
	
	@Override
	public EditionActionType getEditionActionType()
	{
		return EditionActionType.AddIsAProperty;
	}
	
	public String _getFather()
	{
		return father;
	}
	
	public void _setFather(String anObject)
	{
		father = anObject;
	}
	
	private Vector<String> availableFatherValues = null;
	
	public Vector<String> getAvailableFatherValues()
	{
		if (availableFatherValues == null) {
			availableFatherValues = new Vector<String>();
			switch (getScheme().getEditionSchemeType()) {
			case DropScheme:
				availableFatherValues.add(EditionAction.CONTAINER);
				availableFatherValues.add(EditionAction.CONTAINER_OF_CONTAINER);
				break;
			case LinkScheme:
				availableFatherValues.add(EditionAction.FROM_TARGET);
				availableFatherValues.add(EditionAction.TO_TARGET);
				break;
			default:
				break;
			}
			for (PatternRole pr : getEditionPattern().getPatternRoles()) {
				availableFatherValues.add(pr.getPatternRoleName());
			}
			for (EditionPatternParameter p : getScheme().getParameters()) {
				availableFatherValues.add(p.getName());
			}
		}
		return availableFatherValues;
	}
	
	public OntologyObject getPropertyFather(EditionSchemeAction action)
	{
		return retrieveOntologyObject(_getFather(), action);
	}
	
	@Override
	public String getInspectorName() 
	{
		return Inspectors.CED.ADD_IS_A_PROPERTY_INSPECTOR;
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
