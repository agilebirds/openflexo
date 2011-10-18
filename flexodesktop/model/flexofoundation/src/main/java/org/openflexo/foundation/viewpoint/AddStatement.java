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
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.inspector.InspectorDataBinding;



public abstract class AddStatement<R extends OntologicObjectPatternRole> extends EditionAction<R> {

	private static final Logger logger = Logger.getLogger(AddStatement.class.getPackage().getName());

	public AddStatement() {
	}
	
	public OntologyObject getPropertySubject(EditionSchemeAction action)
	{
		return (OntologyObject)getSubject().getBindingValue(action);
	}

	@Override
	public R getPatternRole() {
		try {
			return super.getPatternRole();
		} catch (ClassCastException e) {
			logger.warning("Unexpected pattern role type");
			setPatternRole(null);
			return null;
		}
	}
	
	// FIXME: if we remove this useless code, some FIB won't work (see EditionPatternView.fib, inspect an AddIndividual)
	// Need to be fixed in KeyValueProperty.java
	@Override
	public void setPatternRole(R patternRole) 
	{
		super.setPatternRole(patternRole);
	}
	
	private InspectorDataBinding subject;
	
	private BindingDefinition SUBJECT = new BindingDefinition("subject", OntologyObject.class, BindingDefinitionType.GET, false);
	
	public BindingDefinition getSubjectBindingDefinition()
	{
		return SUBJECT;
	}

	public InspectorDataBinding getSubject() 
	{
		if (subject == null) subject = new InspectorDataBinding(this,EditionActionBindingAttribute.subject,getSubjectBindingDefinition());
		return subject;
	}

	public void setSubject(InspectorDataBinding subject) 
	{
		subject.setOwner(this);
		subject.setBindingAttribute(EditionActionBindingAttribute.subject);
		subject.setBindingDefinition(getSubjectBindingDefinition());
		this.subject = subject;
	}


}
