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

import org.openflexo.foundation.technologyadapter.TechnologyAdapter;

/**
 * Flexo Ontology Object.
 * 
 * @author gbesancon
 */
public interface IFlexoOntologyObject {

	/**
	 * Name of Object.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Sets name of object
	 * 
	 * @param name
	 * @throws Exception
	 */
	public void setName(String name) throws Exception;

	/**
	 * Uri of Object.
	 * 
	 * @return
	 */
	public String getURI();

	/**
	 * Description of Object.
	 * 
	 * @return
	 */
	public String getDescription();

	/**
	 * Return the {@link TechnologyAdapter} of technical space where related FlexoOntology exists
	 * 
	 * @return
	 */
	public TechnologyAdapter<?, ?> getTechnologyAdapter();

}
