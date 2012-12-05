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
 * Concept of Class.
 * 
 * @author gbesancon
 * 
 */
public interface IFlexoOntologyClass extends IFlexoOntologyConcept {
	/**
	 * Super Classes of Class.
	 * 
	 * @return
	 */
	public List<IFlexoOntologyClass> getSuperClasses();

	/**
	 * Return a list of classes, accessible from scope defined by supplied ontology, which are declared to be sub-classes of this property
	 * 
	 * @return
	 */
	public List<IFlexoOntologyClass> getSubClasses(IFlexoOntology context);

	/**
	 * Is this a Super Class of aClass.
	 * 
	 * 
	 * @return
	 */
	boolean isSuperClassOf(IFlexoOntologyClass aClass);

	/**
	 * Is this a Super Concept of anObject.
	 * 
	 * 
	 * @return
	 */
	@Override
	boolean isSuperConceptOf(IFlexoOntologyConcept aConcept);

	/**
	 * Return flag indicating if this class is a named class (that may happen in some technologies)
	 * 
	 * @return
	 */
	public boolean isNamedClass();

	/**
	 * Return flag indicating if this class is the root concept
	 * 
	 * @return
	 */
	public boolean isRootConcept();
}
