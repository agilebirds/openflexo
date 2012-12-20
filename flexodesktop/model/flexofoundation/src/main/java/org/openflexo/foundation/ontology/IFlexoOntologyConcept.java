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
import java.util.Set;

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
	public IFlexoOntology getOntology();

	/**
	 * Annotation upon Concept.
	 * 
	 * @return
	 */
	public List<? extends IFlexoOntologyAnnotation> getAnnotations();

	/**
	 * Container of Concept.
	 * 
	 * @return
	 */
	public IFlexoOntologyConceptContainer getContainer();

	/**
	 * Association with features for Concept.
	 * 
	 * @return
	 */
	public List<? extends IFlexoOntologyFeatureAssociation> getFeatureAssociations();

	/**
	 * 
	 * Is this a Super Concept of concept.
	 * 
	 * @return
	 */
	public boolean isSuperConceptOf(IFlexoOntologyConcept concept);

	/**
	 * 
	 * Is this a Sub Concept of concept.
	 * 
	 * @return
	 */
	public boolean isSubConceptOf(IFlexoOntologyConcept concept);

	/**
	 * Visitor access.
	 * 
	 * @param visitor
	 * @return
	 * 
	 * @pattern visitor
	 */
	public <T> T accept(IFlexoOntologyConceptVisitor<T> visitor);

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

	/**
	 * Return all properties accessible in the scope of this ontology object, where declared range is this object
	 * 
	 * @return
	 */
	@Deprecated
	public Set<? extends IFlexoOntologyStructuralProperty> getPropertiesTakingMySelfAsRange();

	/**
	 * Return all properties accessible in the scope of this ontology object, where declared domain is this object
	 * 
	 * @return
	 */
	@Deprecated
	public Set<? extends IFlexoOntologyFeature> getPropertiesTakingMySelfAsDomain();

	// NB: implemented in FlexoModelObject
	// @Deprecated
	// public void registerEditionPatternReference(EditionPatternInstance editionPatternInstance, PatternRole<?> patternRole);

	// NB: implemented in FlexoModelObject
	// @Deprecated
	// public void unregisterEditionPatternReference(EditionPatternInstance editionPatternInstance, PatternRole<?> patternRole);

}
