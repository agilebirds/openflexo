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

import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.OntologyClass;


public class AddClass extends AddConcept<ClassPatternRole> {

	private static final Logger logger = Logger.getLogger(AddClass.class.getPackage().getName());

	private String newClassName;
	
	public AddClass() {
	}
	
	@Override
	public EditionActionType getEditionActionType()
	{
		return EditionActionType.AddClass;
	}
	
	public String _getNewClassName() 
	{
		return newClassName;
	}

	public void _setNewClassName(String aClassName) 
	{
		this.newClassName = aClassName;
	}
	
	public EditionPatternParameter getNewClassNameParameter()
	{
		return getScheme().getParameter(newClassName);
	}
	
	public void setNewClassNameParameter(EditionPatternParameter param)
	{
		newClassName = param.getName();
	}
	
	@Override
	public OntologyClass getOntologyClass()
	{
		if (getPatternRole() != null) 
			return getPatternRole().getOntologicType();
		return null;
	}
	
	@Override
	public void setOntologyClass(OntologyClass ontologyClass)
	{
		if (getPatternRole() != null) 
			getPatternRole().setOntologicType(ontologyClass);
	}


	@Override
	public String getInspectorName() 
	{
		return Inspectors.VPM.ADD_CLASS_INSPECTOR;
	}

	/*@Override
	protected void updatePatternRoleType()
	{
		if (getPatternRole() == null) {
			return;
		}
	}*/
}
