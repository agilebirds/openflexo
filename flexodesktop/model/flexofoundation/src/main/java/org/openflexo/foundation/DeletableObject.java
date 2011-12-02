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

import java.util.Vector;

/**
 * Interface implemented by objects able to be deleted
 * 
 * @author sguerin
 */
public interface DeletableObject {

	/**
	 * Delete this object
	 */
	public void delete();

	/**
	 * Build and return a vector of all the objects that will be deleted during this deletion
	 * 
	 * @param aVector
	 *            of DeletableObject
	 */
	public Vector<? extends FlexoModelObject> getAllEmbeddedDeleted();

	/**
	 * Return a boolean indicating if this object has been deleted
	 * 
	 * @return
	 */
	public boolean isDeleted();

}
