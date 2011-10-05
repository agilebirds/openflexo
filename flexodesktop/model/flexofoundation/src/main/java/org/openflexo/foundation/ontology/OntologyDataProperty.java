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
package org.openflexo.foundation.ontology;

import org.openflexo.foundation.Inspectors;

import com.hp.hpl.jena.ontology.DatatypeProperty;

public class OntologyDataProperty extends OntologyProperty implements Comparable<OntologyDataProperty> {

	//public OntologyClass domain;
	//public OntologyClass range;
	
	
	public enum PropertyDataType
	{
		String,
		Integer
	}
	
	private PropertyDataType dataType;

	protected OntologyDataProperty(DatatypeProperty aDataProperty, FlexoOntology ontology)
	{
		super(aDataProperty,ontology);
	}
	
	@Override
	public void delete()
	{		
		getFlexoOntology().removeDataProperty(this);
		getOntResource().remove();
		getFlexoOntology().updateConceptsAndProperties();
		super.delete();
		deleteObservers();
	}
		
	@Override
	public String getClassNameKey() 
	{
		return "ontology_data_property";
	}

	@Override
	public String getFullyQualifiedName() 
	{
		return "OntologyDataProperty:"+getURI();
	}

	@Override
	public String getInspectorName()
	{
		if (getIsReadOnly()) {
			return Inspectors.VE.ONTOLOGY_DATA_PROPERTY_READ_ONLY_INSPECTOR; // read-only
		} else {
			return Inspectors.VE.ONTOLOGY_DATA_PROPERTY_INSPECTOR;
		}
	}

	@Override
	public DatatypeProperty getOntProperty() 
	{
		return (DatatypeProperty)super.getOntProperty();
	}

	@Override
	public int compareTo(OntologyDataProperty o) 
	{
		return COMPARATOR.compare(this, o);
	}

	@Override
	public boolean isSuperConceptOf(OntologyObject concept)
	{
		if (concept instanceof OntologyDataProperty) {
			OntologyDataProperty ontologyDataProperty = (OntologyDataProperty)concept;
			return ontologyDataProperty.getOntProperty().hasSuperProperty(getOntProperty(), false);
		}
		return false;
	}

	public PropertyDataType getDataType()
	{
		return dataType;
	}

	public void setDataType(PropertyDataType dataType)
	{
		this.dataType = dataType;
	}

	@Override
	public String getDisplayableDescription()
	{
		// TODO display domain and range
		return "Datatype property "+getName();
	}

	public boolean isOntologyDataProperty()
	{
		return true;
	}

}
