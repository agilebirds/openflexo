/** Copyright (c) 2012, THALES SYSTEMES AEROPORTES - All Rights Reserved
 * Author : Gilles Besançon
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
 * Contributors :
 *
 */
package org.openflexo.foundation.ontology;

import java.util.List;

/**
 * Flexo Ontology.
 * 
 * Reified interface for handling multi-technological implementation of ontology-like modelling technologies
 * 
 * @author gbesancon
 */
public interface IFlexoOntology extends IFlexoOntologyObject, IFlexoOntologyConceptContainer {

	/**
	 * Version of Ontology.
	 * 
	 * @return
	 */
	public String getVersion();

	/**
	 * Ontologies imported by Ontology.
	 * 
	 * @return
	 */
	public List<? extends IFlexoOntology> getImportedOntologies();

	/**
	 * Annotations upon Ontology.
	 * 
	 * @return
	 */
	public List<? extends IFlexoOntologyAnnotation> getAnnotations();

	/**
	 * Return all classes accessible in the context of this ontology.<br>
	 * This means that classes are also retrieved from imported ontologies (non-strict mode)
	 * 
	 * @return
	 */
	public List<? extends IFlexoOntologyClass> getAccessibleClasses();

	/**
	 * Return all individuals accessible in the context of this ontology.<br>
	 * This means that individuals are also retrieved from imported ontologies (non-strict mode)
	 * 
	 * @return
	 */
	public List<? extends IFlexoOntologyIndividual> getAccessibleIndividuals();

	/**
	 * Return all object properties accessible in the context of this ontology.<br>
	 * This means that properties are also retrieved from imported ontologies (non-strict mode)
	 * 
	 * @return
	 */
	public List<? extends IFlexoOntologyObjectProperty> getAccessibleObjectProperties();

	/**
	 * Return all data properties accessible in the context of this ontology.<br>
	 * This means that properties are also retrieved from imported ontologies (non-strict mode)
	 * 
	 * @return
	 */
	public List<? extends IFlexoOntologyDataProperty> getAccessibleDataProperties();

	/**
	 * Retrieve an ontology object from its URI, in the strict context of this ontology. That means that only objects declared in this
	 * ontology are subject to look up. If searched object is declared in an imported ontology for example, this method will not find it and
	 * will return null. Use {@link #getOntologyObject(String)} instead.
	 * 
	 * @param objectURI
	 * @return
	 */
	public abstract IFlexoOntologyConcept getDeclaredOntologyObject(String objectURI);

	/**
	 * Retrieve an class from its URI, in the strict context of this ontology. That means that only objects declared in this ontology are
	 * subject to look up. If searched object is declared in an imported ontology for example, this method will not find it and will return
	 * null. Use {@link #getClass()} instead.
	 * 
	 * 
	 * @param objectURI
	 * @return
	 */
	public abstract IFlexoOntologyClass getDeclaredClass(String classURI);

	/**
	 * Retrieve an individual from its URI, in the strict context of this ontology. That means that only objects declared in this ontology
	 * are subject to look up. If searched object is declared in an imported ontology for example, this method will not find it and will
	 * return null. Use {@link #getIndividual()} instead.
	 * 
	 * 
	 * 
	 * @param objectURI
	 * @return
	 */
	public abstract IFlexoOntologyIndividual getDeclaredIndividual(String individualURI);

	/**
	 * Retrieve an object property from its URI, in the strict context of this ontology. That means that only objects declared in this
	 * ontology are subject to look up. If searched object is declared in an imported ontology for example, this method will not find it and
	 * will return null. Use {@link #getObjectProperty()} instead.
	 * 
	 * 
	 * @param objectURI
	 * @return
	 */
	public abstract IFlexoOntologyObjectProperty getDeclaredObjectProperty(String propertyURI);

	/**
	 * Retrieve an datatype property from its URI, in the strict context of this ontology. That means that only objects declared in this
	 * ontology are subject to look up. If searched object is declared in an imported ontology for example, this method will not find it and
	 * will return null. Use {@link #getDataProperty()} instead.
	 * 
	 * @param objectURI
	 * @return
	 */
	public abstract IFlexoOntologyDataProperty getDeclaredDataProperty(String propertyURI);

	/**
	 * Retrieve a property from its URI, in the strict context of this ontology. That means that only objects declared in this ontology are
	 * subject to look up. If searched object is declared in an imported ontology for example, this method will not find it and will return
	 * null. Use {@link #getProperty()} instead.
	 * 
	 * @param objectURI
	 * @return
	 */
	public abstract IFlexoOntologyStructuralProperty getDeclaredProperty(String objectURI);

	/**
	 * Return the root concept accessible from the scope defined by this ontology (for example in OWL technology this is the owl:Thing
	 * concept, in Java this is java.lang.object, etc...)
	 * 
	 * @return
	 */
	public IFlexoOntologyClass getRootConcept();
}
