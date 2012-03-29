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

import org.openflexo.inspector.InspectableModification;

/*
 * DataModification.java
 * Project WorkflowEditor
 * 
 * Created by benoit on Mar 5, 2004
 */

/**
 * A DataModification encapsulates a modification that has been done on the datastructure. This object is sent to the datastructure
 * observers.
 * 
 * @author benoit
 */
public class DataModification implements InspectableModification {

	private String _propertyName;

	private Object _newValue;

	private Object _oldValue;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * @param modifcationType
	 *            : one of the static int declared in this class.
	 * @param oldValue
	 * @param newValue
	 */
	public DataModification(Object oldValue, Object newValue) {
		super();
		_oldValue = oldValue;
		_newValue = newValue;
	}

	public DataModification(String propertyName, Object oldValue, Object newValue) {
		super();
		_oldValue = oldValue;
		_newValue = newValue;
		_propertyName = propertyName;
	}

	public Object oldValue() {
		return _oldValue;
	}

	@Override
	public Object newValue() {
		return _newValue;
	}

	@Override
	public String propertyName() {
		return _propertyName;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "/" + _propertyName + "\nold Value: " + (_oldValue != null ? _oldValue : "null")
				+ "\nnew Value: " + (_newValue != null ? _newValue : "null");
	}

	private boolean _isReentrant = false;

	@Override
	public boolean isReentrant() {
		return _isReentrant;
	}

	public void setReentrant(boolean isReentrant) {
		_isReentrant = isReentrant;
	}
}
