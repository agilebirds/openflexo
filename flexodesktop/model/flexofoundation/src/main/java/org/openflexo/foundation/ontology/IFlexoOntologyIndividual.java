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
 * Concept of Individual.
 * 
 * @author gbesancon
 * 
 */
public interface IFlexoOntologyIndividual extends IFlexoOntologyConcept {
	/**
	 * Types of Individual.
	 * 
	 * @return
	 */
	List<IFlexoOntologyClass> getTypes();

	/**
	 * Is this an Individual of aClass.
	 * 
	 * @param aClass
	 * @return
	 */
	boolean isIndividualOf(IFlexoOntologyClass aClass);

	/**
	 * Property Values.
	 * 
	 * @return
	 */
	List<IFlexoOntologyPropertyValue> getPropertyValues();
}
