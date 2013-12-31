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
package org.openflexo.selection;

import java.util.Vector;

import org.openflexo.foundation.FlexoObject;

/**
 * Implemented by components fully-synchronized with a SelectionManager This means that this component, once registered in a
 * SelectionManager, reveive synchronization request, and can send it's own selection requests, so that selection and the representation
 * given by current component are synchronized.
 * 
 * @author sguerin
 */
public interface SelectionSynchronizedComponent extends SelectionListener {

	/**
	 * Returns the SelectionManager whose this component is connected to
	 * 
	 * @return the SelectionManager whose this component is connected to
	 */
	public SelectionManager getSelectionManager();

	/**
	 * Return the current selection, as a Vector of FlexoObject
	 * 
	 * @return a Vector of FlexoObject
	 */
	public Vector getSelection();

	/**
	 * Reset selection
	 */
	public void resetSelection();

	/**
	 * Add supplied object to current selection
	 * 
	 * @param object
	 *            : the object to add to selection
	 */
	public void addToSelected(FlexoObject object);

	/**
	 * Remove supplied object from current selection
	 * 
	 * @param object
	 *            : the object to remove from selection
	 */
	public void removeFromSelected(FlexoObject object);

	/**
	 * Add supplied objects to current selection
	 * 
	 * @param objects
	 *            : objects to add to selection, as a Vector of FlexoObject
	 */
	public void addToSelected(Vector<? extends FlexoObject> objects);

	/**
	 * Remove supplied objects from current selection
	 * 
	 * @param objects
	 *            : objects to remove from selection, as a Vector of FlexoObject
	 */
	public void removeFromSelected(Vector<? extends FlexoObject> objects);

	/**
	 * Sets supplied vector of FlexoObjects to be the current Selection
	 * 
	 * @param objects
	 *            : the object to set for current selection, as a Vector of FlexoObject
	 */
	public void setSelectedObjects(Vector<? extends FlexoObject> objects);

	/**
	 * Return currently focused object
	 */
	public FlexoObject getFocusedObject();

	/**
	 * Return boolean indicating if supplied object could be represented in current component
	 * 
	 * @param anObject
	 * @return a boolean
	 */
	public boolean mayRepresents(FlexoObject anObject);

}
