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
	List<IFlexoOntologyContainer> getSubContainers();

	/**
	 * Concepts defined by Ontology.
	 * 
	 * @return
	 */
	List<IFlexoOntologyConcept> getConcepts();

	/**
	 * DataTypes defined by Ontology.
	 * 
	 * @return
	 */
	List<IFlexoOntologyDataType> getDataTypes();

	/**
	 * Retrieve an ontology object from its URI, in the context of this container.<br>
	 * The current container defines the scope, in which to lookup returned object. This method does NOT try to lookup object from outer
	 * scope ontologies.
	 * 
	 * @param objectURI
	 * @return
	 */
	public abstract IFlexoOntologyConcept getOntologyObject(String objectURI);

	/**
	 * Retrieve an class from its URI, in the context of current ontology.<br>
	 * The current ontology defines the scope, in which to lookup returned object. This method does NOT try to lookup object from other
	 * ontologies. If you want to do this, try using method in OntologyLibrary.
	 * 
	 * @param objectURI
	 * @return
	 */
	public abstract IFlexoOntologyClass getClass(String classURI);

	/**
	 * Retrieve an individual from its URI, in the context of current ontology.<br>
	 * The current ontology defines the scope, in which to lookup returned object. This method does NOT try to lookup object from other
	 * ontologies.
	 * 
	 * @param objectURI
	 * @return
	 */
	public abstract IFlexoOntologyIndividual getIndividual(String individualURI);

	/**
	 * Retrieve an object property from its URI, in the context of current ontology.<br>
	 * The current ontology defines the scope, in which to lookup returned object. This method does NOT try to lookup object from other
	 * ontologies.
	 * 
	 * @param objectURI
	 * @return
	 */
	public abstract IFlexoOntologyObjectProperty getObjectProperty(String propertyURI);

	/**
	 * Retrieve an datatype property from its URI, in the context of current ontology.<br>
	 * The current ontology defines the scope, in which to lookup returned object. This method does NOT try to lookup object from other
	 * ontologies.
	 * 
	 * @param objectURI
	 * @return
	 */
	public abstract IFlexoOntologyDataProperty getDataProperty(String propertyURI);

	/**
	 * Retrieve a property from its URI, in the context of current ontology.<br>
	 * The current ontology defines the scope, in which to lookup returned object. This method does NOT try to lookup object from other
	 * ontologies.
	 * 
	 * @param objectURI
	 * @return
	 */
	public abstract IFlexoOntologyStructuralProperty getProperty(String objectURI);

	/**
	 * Return all classes explicitely defined in this container (strict mode)
	 * 
	 * @return
	 */
	public abstract List<? extends IFlexoOntologyClass> getClasses();

	/**
	 * Return all individuals explicitely defined in this container (strict mode)
	 * 
	 * @return
	 */
	public abstract List<? extends IFlexoOntologyIndividual> getIndividuals();

	/**
	 * Return all datatype properties explicitely defined in this container (strict mode)
	 * 
	 * @return
	 */
	public abstract List<? extends IFlexoOntologyDataProperty> getDataProperties();

	/**
	 * Return all object properties explicitely defined in this container (strict mode)
	 * 
	 * @return
	 */
	public abstract List<? extends IFlexoOntologyObjectProperty> getObjectProperties();

}
