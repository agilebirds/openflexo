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

import java.text.Collator;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;

import org.openflexo.foundation.Inspectors;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;

public class OntologyIndividual extends OntologyObject implements Comparable<OntologyIndividual> {

	private final FlexoOntology _ontology;
	private final String uri;
	private String name;
	private final Individual individual;

	private final Vector<OntologyClass> superClasses; 

	protected OntologyIndividual(Individual anIndividual, FlexoOntology ontology)
	{
		super();
		_ontology = ontology;
		uri = anIndividual.getURI();
		individual = anIndividual;
		superClasses = new Vector<OntologyClass>();
		if (uri.indexOf("#") > -1) {
			name = uri.substring(uri.indexOf("#")+1);
		} else {
			name = uri;
		}
	}

	protected void init()
	{
		updateOntologyStatements();
		updateSuperClasses();
	}

	@Override
	public void delete()
	{		
		getFlexoOntology().removeIndividual(this);
		getOntResource().remove();
		getFlexoOntology().updateConceptsAndProperties();
		super.delete();
		deleteObservers();
	}
		
	@Override
	protected void update()
	{
		updateOntologyStatements();
		updateSuperClasses();
	}

	private void updateSuperClasses()
	{
		//superClasses.clear();
		Iterator it = individual.listOntClasses(true);
		while (it.hasNext()) {
			OntClass father = (OntClass)it.next();
			OntologyClass fatherClass = getOntologyLibrary().getClass(father.getURI());
			if (fatherClass != null) {
				if (!superClasses.contains(fatherClass)) {
					superClasses.add(fatherClass);
				}
				//System.out.println("Add "+fatherClass.getName()+" as a super class of "+getName());
				if (!fatherClass.individuals.contains(this)) {
					//System.out.println("Add "+getName()+" as an individual of "+fatherClass.getName());
					fatherClass.individuals.add(this);
				}
			}
		}
	}
	
	public Vector<OntologyClass> getSuperClasses()
	{
		return superClasses;
	}

	@Override
	public String getURI()
	{
		return uri;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public FlexoOntology getFlexoOntology()
	{
		return _ontology;
	}

	@Override
	public String getClassNameKey()
	{
		return "ontology_individual";
	}

	@Override
	public String getFullyQualifiedName()
	{
		return "OntologyIndividual:"+getURI();
	}

	public static final Comparator<OntologyIndividual> COMPARATOR = new Comparator<OntologyIndividual>() {
		@Override
		public int compare(OntologyIndividual o1, OntologyIndividual o2) {
			return Collator.getInstance().compare(o1.getName(), o2.getName());
		}
	};

	@Override
	public String getInspectorName()
	{
		if (getIsReadOnly()) {
			return Inspectors.VE.ONTOLOGY_INDIVIDUAL_READ_ONLY_INSPECTOR; // read-only
		} else {
			return Inspectors.VE.ONTOLOGY_INDIVIDUAL_INSPECTOR;
		}
	}

	@Override
	public int compareTo(OntologyIndividual o) 
	{
		return COMPARATOR.compare(this, o);
	}

	@Override
	public Individual getOntResource() 
	{
		return individual;
	}

	public Individual getIndividual() 
	{
		return getOntResource();
	}

	@Override
	public boolean isSuperConceptOf(OntologyObject concept)
	{
		return false;
	}
	
	// Return first property value matching supplied data property
	public Object getPropertyValue(OntologyProperty property)
	{
		PropertyStatement s = getPropertyStatement(property);
		if (s != null) {
			if (s.hasLitteralValue()) {
				return s.getLiteral().getValue();
			} else if (s instanceof ObjectPropertyStatement) {
				return ((ObjectPropertyStatement)s).getStatementObject();
			}
		}
		return null;
	}

	public void setPropertyValue(OntologyProperty property, Object newValue) 
	{
		PropertyStatement s = getPropertyStatement(property);
		if (s != null) {
			if (s.hasLitteralValue() && (newValue instanceof String)) {
				s.setStringValue((String)newValue);
				return;
			}
			else if ((s instanceof ObjectPropertyStatement) && (newValue instanceof OntologyObject)) {
				 ((ObjectPropertyStatement)s).setStatementObject((OntologyObject)newValue);
				 return;
			}
		}
		else {
			if (newValue instanceof String) {
				getOntResource().addProperty(property.getOntProperty(), (String)newValue);
				updateOntologyStatements();
			}
			else if (newValue instanceof OntologyObject) {
				getOntResource().addProperty(property.getOntProperty(), ((OntologyObject)newValue).getOntResource());
				updateOntologyStatements();
			}
		}
	}

	@Override
	public String getDisplayableDescription()
	{
		String extendsLabel = " extends ";
		boolean isFirst = true;
		for (OntologyClass s : superClasses) {
			extendsLabel+=(isFirst?"":",")+s.getName();
			isFirst = false;
		}
		return "Individual "+getName()+extendsLabel;
	}

	@Override
	public boolean isOntologyIndividual()
	{
		return true;
	}

	@Override
	protected void recursivelySearchRangeAndDomains()
	{
		super.recursivelySearchRangeAndDomains();
		Vector<OntologyClass> alreadyComputed = new Vector<OntologyClass>();
		for (OntologyClass aClass : getSuperClasses()) {
			_appendRangeAndDomains(aClass, alreadyComputed);
		}
	}
	
	private void _appendRangeAndDomains(OntologyClass superClass, Vector<OntologyClass> alreadyComputed)
	{
		if (alreadyComputed.contains(superClass)) return;
		alreadyComputed.add(superClass);
		for (OntologyProperty p : superClass.getDeclaredPropertiesTakingMySelfAsDomain()) {
			if (!propertiesTakingMySelfAsDomain.contains(p)) propertiesTakingMySelfAsDomain.add(p);
		}
		for (OntologyProperty p : superClass.getDeclaredPropertiesTakingMySelfAsRange()) {
			if (!propertiesTakingMySelfAsRange.contains(p)) propertiesTakingMySelfAsRange.add(p);
		}
		for (OntologyClass superSuperClass : superClass.getSuperClasses()) {
			_appendRangeAndDomains(superSuperClass, alreadyComputed);
		}
	}

}
