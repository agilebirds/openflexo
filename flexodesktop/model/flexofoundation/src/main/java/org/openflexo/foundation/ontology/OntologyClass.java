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

public class OntologyClass extends OntologyObject implements Comparable<OntologyClass> {

	private final FlexoOntology _ontology;
	private String uri;
	private String name;
	private final OntClass ontClass;

	private final Vector<OntologyClass> superClasses; 
	private final Vector<OntologyClass> subClasses; 
	protected Vector<OntologyIndividual> individuals; 
	
	protected OntologyClass(OntClass anOntClass, FlexoOntology ontology)
	{
		super();
		_ontology = ontology;
		superClasses = new Vector<OntologyClass>();
		subClasses = new Vector<OntologyClass>();
		individuals = new Vector<OntologyIndividual>();
		if (anOntClass != null) {
			uri = anOntClass.getURI();
		}
		ontClass = anOntClass;
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
		updateSubClasses();
	}

	@Override
	public void delete()
	{		
		getFlexoOntology().removeClass(this);
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
		updateSubClasses();
	}

	private void updateSuperClasses()
	{
		//superClasses.clear();
		Iterator it = ontClass.listSuperClasses(true);
		while (it.hasNext()) {
			OntClass father = (OntClass)it.next();
			OntologyClass fatherClass = getOntologyLibrary().getClass(father.getURI());
			if (fatherClass != null) {
				if (!superClasses.contains(fatherClass)) {
					superClasses.add(fatherClass);
				}
				//System.out.println("Add "+fatherClass.getName()+" as a super class of "+getName());
				if (!fatherClass.subClasses.contains(this)) {
					//System.out.println("Add "+getName()+" as a sub class of "+fatherClass.getName());
					fatherClass.subClasses.add(this);
				}
			}
		}
		if ((superClasses.size() > 0) && (getOntologyLibrary().THING != null)) {
			if (superClasses.contains(getOntologyLibrary().THING) && (superClasses.size() > 1)) {
				superClasses.remove(getOntologyLibrary().THING);
				getOntologyLibrary().THING.subClasses.remove(this);
			}
			/*if (superClasses.size() > 0 && getOntologyLibrary().THING != null) {
				getOntologyLibrary().THING.subClasses.remove(this);
			}*/
		}
		if ((superClasses.size() == 0) && (getOntologyLibrary() != null) && (getOntologyLibrary().THING != null)) {
			superClasses.add(getOntologyLibrary().THING);
			if (!getOntologyLibrary().THING.subClasses.contains(this)) {
				getOntologyLibrary().THING.subClasses.add(this);
			}
		}
	}
	
	private void updateSubClasses()
	{
		//subClasses.clear();
		Iterator it = ontClass.listSubClasses(true);
		while (it.hasNext()) {
			OntClass child = (OntClass)it.next();
			OntologyClass childClass = getOntologyLibrary().getClass(child.getURI());
			if (childClass != null) {
				if (!subClasses.contains(childClass)) {
					subClasses.add(childClass);
				}
				if (!childClass.superClasses.contains(this)) {
					childClass.superClasses.add(this);
				}
			}
		}
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
		return "ontology_concept";
	}

	@Override
	public String getFullyQualifiedName()
	{
		return "OntologyConcept:"+getURI();
	}

	public static final Comparator<OntologyClass> COMPARATOR = new Comparator<OntologyClass>() {
		@Override
		public int compare(OntologyClass o1, OntologyClass o2) {
			return Collator.getInstance().compare(o1.getName(), o2.getName());
		}
	};

	@Override
	public String getInspectorName()
	{
		if (getIsReadOnly()) {
			return Inspectors.VE.ONTOLOGY_CLASS_READ_ONLY_INSPECTOR; // read-only
		} else {
			return Inspectors.VE.ONTOLOGY_CLASS_INSPECTOR;
		}
	}

	@Override
	public int compareTo(OntologyClass o) 
	{
		return COMPARATOR.compare(this, o);
	}

	@Override
	public OntClass getOntResource() 
	{
		return ontClass;
	}

	private static boolean isSuperClassOf (OntClass parentClass, OntClass subClass)
	{
		if (parentClass == null) {
			return false;
		}
		if (subClass == null) {
			return false;
		}
		if (parentClass.equals(subClass)) {
			return true;
		}
		Iterator it = subClass.listSuperClasses();
		while(it.hasNext()) {
			OntClass p = (OntClass)it.next();
			if (p.equals(parentClass)) {
				return true;
			}
			if (isSuperClassOf(parentClass, p)) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean isSuperClassOf (OntClass parentClass, Individual individual)
	{
		if (parentClass == null) {
			return false;
		}
		if (individual == null) {
			return false;
		}
		Iterator it = individual.listOntClasses(false);
		while(it.hasNext()) {
			OntClass p = (OntClass)it.next();
			if (p.equals(parentClass)) {
				return true;
			}
			if (isSuperClassOf(parentClass, p)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean isSuperConceptOf(OntologyObject concept)
	{
		if (concept instanceof OntologyIndividual) {
			OntologyIndividual ontologyIndividual = (OntologyIndividual)concept;
			// Doesn't work, i dont know why
			// return ontologyIndividual.getIndividual().hasOntClass(ontClass);
			return isSuperClassOf(ontClass, ontologyIndividual.getIndividual());
		}
		if (concept instanceof OntologyClass) {
			OntologyClass ontologyClass = (OntologyClass)concept;
			// Doesn't work, i dont know why
			// return ontologyClass.getOntResource().hasSuperClass(ontClass);
			return isSuperClassOf(ontClass, ontologyClass.getOntResource());
		}
		return false;
	}

	public Vector<OntologyClass> getSuperClasses()
	{
		return superClasses;
	}

	public Vector<OntologyClass> getSubClasses() 
	{
		return subClasses;
	}

	public Vector<OntologyIndividual> getIndividuals() 
	{
		return individuals;
	}

	/**
	 * Return a vector of Ontology class, as a subset of getSubClasses(),
	 * which correspond to all classes necessary to see all classes
	 * belonging to supplied context, which is an ontology
	 * 
	 * @param context
	 * @return
	 */
	public Vector<OntologyClass> getSubClasses(FlexoOntology context) 
	{
		Vector<OntologyClass> returned = new Vector<OntologyClass>();
		for (OntologyClass aClass : getSubClasses()) {
			if (isRequired(aClass,context)) {
				returned.add(aClass);
			}
		}
		return returned;
	}

	private boolean isRequired(OntologyClass aClass, FlexoOntology context)
	{
		if (aClass.getFlexoOntology() == context) {
			return true;
		}
		for (OntologyClass aSubClass : aClass.getSubClasses()) {
			if (isRequired(aSubClass,context)) {
				return true;
			}
		}
		for (OntologyIndividual anIndividual : aClass.getIndividuals()) {
			if (anIndividual.getFlexoOntology() == context) {
				return true;
			}
		}
		return false;
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
		return "Class "+getName()+extendsLabel;
	}


}
