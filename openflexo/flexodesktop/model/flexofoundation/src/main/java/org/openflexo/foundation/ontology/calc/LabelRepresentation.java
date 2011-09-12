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

import org.openflexo.foundation.ontology.AbstractOntologyObject;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyIndividual;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.ontology.PropertyStatement;
import org.openflexo.foundation.ontology.RestrictionStatement;


public class LabelRepresentation extends CalcObject {

	public enum LabelRepresentationType
	{
		UseOntologicProperty,
		UseOntologicObjectDescriptor,
		StaticValue
	}
	
	private String propertyURI;
	private String text;
	private LabelRepresentationType type = LabelRepresentationType.UseOntologicProperty;
	
	
	//private boolean boundToClassName;
	//private boolean boundToRestriction;

	private PatternRole _patternRole;
	
	public LabelRepresentation()
	{
	}
	
	public String _getPropertyURI() 
	{
		return propertyURI;
	}

	public void _setPropertyURI(String dataPropertyURI) 
	{
		this.propertyURI = dataPropertyURI;
	}

	public OntologyProperty getOntologyProperty()
	{
		getCalc().loadWhenUnloaded();
		return getOntologyLibrary().getProperty(_getPropertyURI());
	}
	
	public void setOntologyProperty(OntologyProperty p)
	{
		_setPropertyURI(p != null ? p.getURI() : null);
	}
	

	public PatternRole getPatternRole()
	{
		return _patternRole;
	}

	public void setPatternRole(PatternRole aPatternRole) 
	{
		_patternRole = aPatternRole;
	}
	
	@Override
	public OntologyCalc getCalc() 
	{
		return getPatternRole().getCalc();
	}

	@Override
	public String getInspectorName() 
	{
		return null;
	}

	public String getDynamicValue(AbstractOntologyObject object)
	{
		switch (getType()) {
		case StaticValue:
			return getText();
		case UseOntologicObjectDescriptor:
			if (object instanceof OntologyClass) {
				return ((OntologyClass)object).getName();
			}
			if (object instanceof OntologyIndividual) {
				return ((OntologyIndividual)object).getName();
			}
			if (object instanceof RestrictionStatement) {
				return ((RestrictionStatement)object).getName();
			}
			return "???";
		case UseOntologicProperty:
			OntologyProperty property = getOntologyProperty();
			if ((getOntologyProperty() != null) && (object instanceof OntologyObject)) {
				PropertyStatement statement = ((OntologyObject)object).getPropertyStatement(property);
				if (statement != null) {
					return statement.getStringValue();
				}
			}
		default:
			break;
		}
		return null;
	}

	public void setDynamicValue(AbstractOntologyObject object, String value)
	{
		switch (getType()) {
		case StaticValue:
			return; // Cannot modify
		case UseOntologicObjectDescriptor:
			if (object instanceof OntologyClass) {
				try {
					((OntologyClass)object).setName(value);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			}
			if (object instanceof OntologyIndividual) {
				try {
					((OntologyIndividual)object).setName(value);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			}
			if (object instanceof RestrictionStatement) {
				// Cannot modify
			}
			return;
		case UseOntologicProperty:
			OntologyProperty property = getOntologyProperty();
			if ((property != null) && (object instanceof OntologyObject)) {
				PropertyStatement statement = ((OntologyObject)object).getPropertyStatement(property);
				if (statement != null) {
					statement.setStringValue(value);
				}
			}
		default:
			break;
		}
	}
	
	public String getText() 
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public LabelRepresentationType getType()
	{
		return type;
	}

	public void setType(LabelRepresentationType type)
	{
		this.type = type;
	}


}
