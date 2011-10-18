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

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.OntologyProperty;


public class DataPropertyAssertion extends AbstractAssertion {

	private String dataPropertyURI;
	private String value;

	public String _getDataPropertyURI() 
	{
		return dataPropertyURI;
	}

	public void _setDataPropertyURI(String dataPropertyURI) 
	{
		this.dataPropertyURI = dataPropertyURI;
	}

	@Override
	public String getInspectorName() 
	{
		return Inspectors.VPM.DATA_PROPERTY_ASSERTION_INSPECTOR;
	}

	public OntologyProperty getOntologyProperty()
	{
		return getOntologyLibrary().getProperty(_getDataPropertyURI());
	}

	public void setOntologyProperty(OntologyProperty p)
	{
		_setDataPropertyURI(p != null ? p.getURI() : null);
	}
	
	public String _getValue()
	{
		return value;
	}
	
	public void _setValue(String aValue)
	{
		value = aValue;
	}

	public EditionSchemeParameter getValueParameter()
	{
		return getScheme().getParameter(value);
	}
	
	public void setValueParameter(EditionSchemeParameter param)
	{
		if (param != null) {
			value = param.getName();
		}
	}
	

}
