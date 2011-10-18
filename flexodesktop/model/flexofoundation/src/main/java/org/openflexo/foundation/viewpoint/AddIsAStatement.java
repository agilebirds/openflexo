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

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.inspector.InspectorDataBinding;



public class AddIsAStatement extends AddStatement<IsAStatementPatternRole> {

	private static final Logger logger = Logger.getLogger(AddIsAStatement.class.getPackage().getName());

	public AddIsAStatement() {
	}
	
	@Override
	public EditionActionType getEditionActionType()
	{
		return EditionActionType.AddIsAStatement;
	}
	
	public OntologyObject getPropertyFather(EditionSchemeAction action)
	{
		return (OntologyObject)getFather().getBindingValue(action);
	}
	
	@Override
	public String getInspectorName() 
	{
		return Inspectors.VPM.ADD_IS_A_PROPERTY_INSPECTOR;
	}

	private InspectorDataBinding father;
	
	private BindingDefinition FATHER = new BindingDefinition("father", OntologyObject.class, BindingDefinitionType.GET, false);
	
	public BindingDefinition getFatherBindingDefinition()
	{
		return FATHER;
	}

	public InspectorDataBinding getFather() 
	{
		if (father == null) father = new InspectorDataBinding(this,EditionActionBindingAttribute.father,getFatherBindingDefinition());
		return father;
	}

	public void setFather(InspectorDataBinding father) 
	{
		father.setOwner(this);
		father.setBindingAttribute(EditionActionBindingAttribute.father);
		father.setBindingDefinition(getFatherBindingDefinition());
		this.father = father;
	}

		
}
