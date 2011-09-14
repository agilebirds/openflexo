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
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.ontology.action.EditionSchemeAction;
import org.openflexo.foundation.ontology.calc.OntologicObjectPatternRole.OntologicObjectType;



public class AddObjectProperty extends AddProperty {

	private static final Logger logger = Logger.getLogger(AddObjectProperty.class.getPackage().getName());

	private String objectPropertyURI;
	private String object;
	
	public AddObjectProperty() {
	}
	
	@Override
	public EditionActionType getEditionActionType()
	{
		return EditionActionType.AddObjectProperty;
	}
	
	public String _getObjectPropertyURI()
	{
		return objectPropertyURI;
	}

	public void _setObjectPropertyURI(String objectPropertyURI) 
	{
		this.objectPropertyURI = objectPropertyURI;
	}

	public OntologyProperty getObjectProperty()
	{
		getCalc().loadWhenUnloaded();
		return getOntologyLibrary().getObjectProperty(_getObjectPropertyURI());
	}
	
	public void setObjectProperty(OntologyProperty p)
	{
		_setObjectPropertyURI(p != null ? p.getURI() : null);
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
		return Inspectors.CED.ADD_OBJECT_PROPERTY_INSPECTOR;
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
