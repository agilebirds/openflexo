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
 * Common interface for concepts of Ontology.
 * 
 * @author gbesancon
 */
public interface IFlexoOntologyConcept extends IFlexoOntologyObject {
	/**
	 * Ontology of Concept.
	 * 
	 * @return
	 */
	IFlexoOntology getOntology();

	/**
	 * Annotation upon Concept.
	 * 
	 * @return
	 */
	List<IFlexoOntologyAnnotation> getAnnotations();

	/**
	 * Container of Concept.
	 * 
	 * @return
	 */
	IFlexoOntologyConceptContainer getContainer();

	/**
	 * Association with features for Concept.
	 * 
	 * @return
	 */
	List<IFlexoOntologyFeatureAssociation> getFeatureAssociations();

	/**
	 * 
	 * Is this a Super Concept of concept.
	 * 
	 * @return
	 */
	boolean isSuperConceptOf(IFlexoOntologyConcept concept);

	/**
	 * 
	 * Is this a equal to concept.
	 * 
	 * @return
	 */
	boolean isEqualToConcept(IFlexoOntologyConcept concept);

	/**
	 * 
	 * Is this a Sub Concept of concept.
	 * 
	 * @return
	 */
	boolean isSubConceptOf(IFlexoOntologyConcept concept);

	/**
	 * Visitor access.
	 * 
	 * @param visitor
	 * @return
	 * 
	 * @pattern visitor
	 */
	<T> T accept(IFlexoOntologyConceptVisitor<T> visitor);

	/**
	 * This equals has a particular semantics (differs from {@link #equals(Object)} method) in the way that it returns true only and only if
	 * compared objects are representing same concept regarding URI. This does not guarantee that both objects will respond the same way to
	 * some methods.<br>
	 * This method returns true if and only if objects are same, or if one of both object redefine the other one (with eventual many levels)
	 * 
	 * @param o
	 * @return
	 */
	public boolean equalsToConcept(IFlexoOntologyConcept o);

}
