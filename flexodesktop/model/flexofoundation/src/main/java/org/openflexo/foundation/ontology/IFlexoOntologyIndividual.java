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
 * Concept of Individual.
 * 
 * @author gbesancon
 * 
 */
public interface IFlexoOntologyIndividual extends IFlexoOntologyConcept {
	/**
	 * Return types of Individual.
	 * 
	 * @return
	 */
	public List<? extends IFlexoOntologyClass> getTypes();

	/**
	 * Add supplied type to the list of types implemented by this individual
	 * 
	 * @param aType
	 */
	public void addToTypes(IFlexoOntologyClass aType);

	/**
	 * Remove supplied type from the list of types implemented by this individual
	 * 
	 * @param aType
	 */
	public void removeFromTypes(IFlexoOntologyClass aType);

	/**
	 * Is this an Individual of aClass.
	 * 
	 * @param aClass
	 * @return
	 */
	public boolean isIndividualOf(IFlexoOntologyClass aClass);

	/**
	 * Property Values.
	 * 
	 * @return
	 */
	public List<? extends IFlexoOntologyPropertyValue> getPropertyValues();

	/**
	 * Return the {@link IFlexoOntologyPropertyValue} matching supplied property and defined for this individual<br>
	 * If no values were defined for supplied property, return null
	 * 
	 * @param property
	 * @return
	 */
	public IFlexoOntologyPropertyValue getPropertyValue(IFlexoOntologyStructuralProperty property);

	/**
	 * Add newValue as a value for supplied property<br>
	 * Return the {@link IFlexoOntologyPropertyValue} matching supplied property and defined for this individual<br>
	 * 
	 * @param property
	 * @param newValue
	 * @return
	 */
	public IFlexoOntologyPropertyValue addToPropertyValue(IFlexoOntologyStructuralProperty property, Object newValue);

	/**
	 * Remove valueToRemove from list of values for supplied property<br>
	 * Return the {@link IFlexoOntologyPropertyValue} matching supplied property and defined for this individual<br>
	 * If the supplied valueToRemove parameter was the only value defined for supplied property for this individual, return null
	 * 
	 * @param property
	 * @param valueToRemove
	 * @return
	 */
	public IFlexoOntologyPropertyValue removeFromPropertyValue(IFlexoOntologyStructuralProperty property, Object valueToRemove);
}
