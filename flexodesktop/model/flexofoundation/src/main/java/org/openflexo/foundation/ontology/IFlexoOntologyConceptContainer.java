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
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or 
 * combining it with eclipse EMF (or a modified version of that library), 
 * containing parts covered by the terms of EPL 1.0, the licensors of this 
 * Program grant you additional permission to convey the resulting work.
 *
 * Contributors :
 *
 */
package org.openflexo.foundation.ontology;

import java.util.List;

/**
 * Concept Container.
 * 
 * @author gbesancon
 */
public interface IFlexoOntologyConceptContainer {

	/**
	 * Sub container of container.
	 * 
	 * @return
	 */
	public List<? extends IFlexoOntologyContainer> getSubContainers();

	/**
	 * Concepts defined by Ontology.
	 * 
	 * @return
	 */
	public List<? extends IFlexoOntologyConcept> getConcepts();

	/**
	 * DataTypes defined by Ontology.
	 * 
	 * @return
	 */
	public List<? extends IFlexoOntologyDataType> getDataTypes();

	/**
	 * Retrieve an ontology object from its URI, in the context of this container (if this container is an ontology, will lookup in ontology
	 * and recursively on imported ontologies)<br>
	 * The current container defines the scope, in which to lookup returned object. This method does NOT try to lookup object from outer
	 * scope ontologies.
	 * 
	 * @param objectURI
	 * @return
	 */
	public IFlexoOntologyConcept getOntologyObject(String objectURI);

	/**
	 * Retrieve an class from its URI, in the context of this container (if this container is an ontology, will lookup in ontology and
	 * recursively on imported ontologies)<br>
	 * The current container defines the scope, in which to lookup returned object. This method does NOT try to lookup object from outer
	 * scope ontologies.
	 * 
	 * @param objectURI
	 * @return
	 */
	public IFlexoOntologyClass getClass(String classURI);

	/**
	 * Retrieve an individual from its URI, in the context of this container (if this container is an ontology, will lookup in ontology and
	 * recursively on imported ontologies)<br>
	 * The current container defines the scope, in which to lookup returned object. This method does NOT try to lookup object from outer
	 * scope ontologies.
	 * 
	 * 
	 * @param objectURI
	 * @return
	 */
	public IFlexoOntologyIndividual getIndividual(String individualURI);

	/**
	 * Retrieve an object property from its URI, in the context of this container (if this container is an ontology, will lookup in ontology
	 * and recursively on imported ontologies)<br>
	 * The current container defines the scope, in which to lookup returned object. This method does NOT try to lookup object from outer
	 * scope ontologies.
	 * 
	 * @param objectURI
	 * @return
	 */
	public IFlexoOntologyObjectProperty getObjectProperty(String propertyURI);

	/**
	 * Retrieve an datatype property from its URI, in the context of this container (if this container is an ontology, will lookup in
	 * ontology and recursively on imported ontologies)<br>
	 * The current container defines the scope, in which to lookup returned object. This method does NOT try to lookup object from outer
	 * scope ontologies.
	 * 
	 * 
	 * @param objectURI
	 * @return
	 */
	public IFlexoOntologyDataProperty getDataProperty(String propertyURI);

	/**
	 * Retrieve a property from its URI, in the context of this container (if this container is an ontology, will lookup in ontology and
	 * recursively on imported ontologies)<br>
	 * The current container defines the scope, in which to lookup returned object. This method does NOT try to lookup object from outer
	 * scope ontologies.
	 * 
	 * 
	 * @param objectURI
	 * @return
	 */
	public IFlexoOntologyStructuralProperty getProperty(String objectURI);

	/**
	 * Return all classes explicitely defined in this container (strict mode)
	 * 
	 * @return
	 */
	public List<? extends IFlexoOntologyClass> getClasses();

	/**
	 * Return all individuals explicitely defined in this container (strict mode)
	 * 
	 * @return
	 */
	public List<? extends IFlexoOntologyIndividual> getIndividuals();

	/**
	 * Return all datatype properties explicitely defined in this container (strict mode)
	 * 
	 * @return
	 */
	public List<? extends IFlexoOntologyDataProperty> getDataProperties();

	/**
	 * Return all object properties explicitely defined in this container (strict mode)
	 * 
	 * @return
	 */
	public List<? extends IFlexoOntologyObjectProperty> getObjectProperties();

}