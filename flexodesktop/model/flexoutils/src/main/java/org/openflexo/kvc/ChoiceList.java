/*
 * (c) Copyright 2010-2011 AgileBirds
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
 */
package org.openflexo.kvc;

import java.util.List;

/**
 * Interface implemented by classes taking their values in a set of possible values
 * 
 * NOTE: All classes inplementing this interface MUST define a static method called public static Vector availableValues();
 * 
 * @author sguerin
 * 
 */
public interface ChoiceList {

	/**
	 * Return a Vector of possible values (which must be of the same type as the one declared as class implemented this interface)
	 * 
	 * @return a Vector of ChoiceList
	 */
	public List<?> getAvailableValues();

}
