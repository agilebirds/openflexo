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
package org.openflexo.foundation;

import java.io.InputStream;
import java.util.List;

/**
 * @author Nicolas Daniels
 *
 */
public interface InspectorGroup {

	/**
	 * Return the name of the inspector group. <br>
	 * TODO: Why is this needed ?
	 * 
	 * @return the name of the inspector group
	 */
	public String getName();

	/**
	 * Return true if this inspector group contains an inspector for the specified name
	 * 
	 * @param inspectorName
	 * @return true if this inspector group contains an inspector for the specified name
	 */
	public boolean containsInspector(String inspectorName);

	/**
	 * Retrieve all inspector names defined in this inspector group.
	 * 
	 * @return the retrieved inspector names.
	 */
	public List<String> getAllInspectorNames();

	/**
	 * Return the input stream for the specified inspector if it exists, null otherwise.
	 * 
	 * @return the input stream for the specified inspector if it exists, null otherwise.
	 */
	public InputStream getInspectorStream(String inspectorName);
}
