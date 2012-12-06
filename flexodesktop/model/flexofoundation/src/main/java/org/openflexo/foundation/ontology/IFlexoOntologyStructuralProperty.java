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
 * Concept of structural property.
 * 
 * @author gbesancon
 * 
 * 
 */
public interface IFlexoOntologyStructuralProperty extends IFlexoOntologyFeature {

	/**
	 * Range of property.
	 * 
	 * @return
	 */
	IFlexoOntologyConcept getDomain();

	/**
	 * Range of property.
	 * 
	 * @return
	 */
	IFlexoOntologyObject getRange();

	/**
	 * Return flag indicating if this property is an annotation property
	 * 
	 * @return
	 */
	public boolean isAnnotationProperty();

	/**
	 * Super properties of this property.
	 * 
	 * @return
	 */
	public List<? extends IFlexoOntologyStructuralProperty> getSuperProperties();

	/**
	 * Return a vector of properties, accessible from scope defined by supplied ontology, which are declared to be sub-properties of this
	 * property
	 * 
	 * @param context
	 * @return
	 */
	public List<? extends IFlexoOntologyStructuralProperty> getSubProperties(IFlexoOntology context);

}
