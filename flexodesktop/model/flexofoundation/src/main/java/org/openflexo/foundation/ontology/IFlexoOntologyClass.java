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
	List<IFlexoOntologyClass> getSuperClasses();

	/**
	 * Sub Classes of Class.
	 * 
	 * @return
	 */
	List<IFlexoOntologyClass> getSubClasses();

	/**
	 * Is this a Super Class of aClass.
	 * 
	 * 
	 * @return
	 */
	boolean isSuperClassOf(IFlexoOntologyClass aClass);

	/**
	 * Is this a Sub Class of Class.
	 * 
	 * @return
	 */
	boolean isSubClassOf(IFlexoOntologyClass aClass);

}
