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

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.viewpoint.inspector.InspectorDataBinding;
import org.openflexo.logging.FlexoLogger;


public class AddIndividual extends AddConcept<IndividualPatternRole> {

	protected static final Logger logger = FlexoLogger.getLogger(AddIndividual.class.getPackage().getName());
	
	private Vector<DataPropertyAssertion> dataAssertions;
	private Vector<ObjectPropertyAssertion> objectAssertions;

	public AddIndividual() 
	{	
		super();
		dataAssertions = new Vector<DataPropertyAssertion>();
		objectAssertions = new Vector<ObjectPropertyAssertion>();
	}
	
	@Override
	public EditionActionType getEditionActionType()
	{
		return EditionActionType.AddIndividual;
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
	
	public Vector<DataPropertyAssertion> getDataAssertions() 
	{
		return dataAssertions;
	}

	public void setDataAssertions(Vector<DataPropertyAssertion> assertions) 
	{
		this.dataAssertions = assertions;
	}

	public void addToDataAssertions(DataPropertyAssertion assertion) 
	{
		assertion.setAction(this);
		dataAssertions.add(assertion);
	}

	public void removeFromDataAssertions(DataPropertyAssertion assertion)
	{
		assertion.setAction(null);
		dataAssertions.remove(assertion);
	}
	
	public DataPropertyAssertion createDataPropertyAssertion()
	{
		DataPropertyAssertion newDataPropertyAssertion = new DataPropertyAssertion();
		addToDataAssertions(newDataPropertyAssertion);
		return newDataPropertyAssertion;
	}
	
	public DataPropertyAssertion deleteDataPropertyAssertion(DataPropertyAssertion assertion)
	{
		removeFromDataAssertions(assertion);
		assertion.delete();
		return assertion;
	}
	
	
	public Vector<ObjectPropertyAssertion> getObjectAssertions() 
	{
		return objectAssertions;
	}

	public void setObjectAssertions(Vector<ObjectPropertyAssertion> assertions) 
	{
		this.objectAssertions = assertions;
	}

	public void addToObjectAssertions(ObjectPropertyAssertion assertion) 
	{
		assertion.setAction(this);
		objectAssertions.add(assertion);
	}

	public void removeFromObjectAssertions(ObjectPropertyAssertion assertion)
	{
		assertion.setAction(null);
		objectAssertions.remove(assertion);
	}
	
	public ObjectPropertyAssertion createObjectPropertyAssertion()
	{
		ObjectPropertyAssertion newObjectPropertyAssertion = new ObjectPropertyAssertion();
		addToObjectAssertions(newObjectPropertyAssertion);
		return newObjectPropertyAssertion;
	}
	
	public ObjectPropertyAssertion deleteObjectPropertyAssertion(ObjectPropertyAssertion assertion)
	{
		removeFromObjectAssertions(assertion);
		assertion.delete();
		return assertion;
	}
	

	@Override
	public String getInspectorName() 
	{
		return Inspectors.VPM.ADD_INDIVIDUAL_INSPECTOR;
	}

	/*@Override
	protected void updatePatternRoleType()
	{
		if (getPatternRole() == null) {
			return;
		}
	}*/
	
	private InspectorDataBinding individualName;
	
	private BindingDefinition INDIVIDUAL_NAME = new BindingDefinition("individualName", String.class, BindingDefinitionType.GET, false);
	
	public BindingDefinition getIndividualNameBindingDefinition()
	{
		return INDIVIDUAL_NAME;
	}

	public InspectorDataBinding getIndividualName() 
	{
		if (individualName == null) individualName = new InspectorDataBinding(this,EditionActionBindingAttribute.individualName,getIndividualNameBindingDefinition());
		return individualName;
	}

	public void setIndividualName(InspectorDataBinding individualName) 
	{
		individualName.setOwner(this);
		individualName.setBindingAttribute(EditionActionBindingAttribute.individualName);
		individualName.setBindingDefinition(getIndividualNameBindingDefinition());
		this.individualName = individualName;
	}


}
