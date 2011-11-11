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
package org.openflexo.foundation.param;

import java.util.Vector;

import org.openflexo.inspector.widget.DenaliWidget;

public class DynamicDropDownParameter<T> extends ParameterDefinition<T> {

	private Vector<T> _availableValues;

	public DynamicDropDownParameter(String name, String label, Vector<T> availableValues, T defaultValue) {
		super(name, label, defaultValue);
		addParameter("dynamiclist", "params." + name + ".availableValues");
		_availableValues = availableValues;
	}

	public DynamicDropDownParameter(String name, String label, T defaultValue) {
		this(name, label, null, defaultValue);
	}

	public void setShowReset(boolean showReset) {
		addParameter("showReset", "" + showReset);
	}

	@Override
	public String getWidgetName() {
		return DenaliWidget.DROPDOWN;
	}

	// Override this if list not defined in constructor
	public Vector<T> getAvailableValues() {
		return _availableValues;
	}

	public void setAvailableValues(Vector<T> availableValues) {
		_availableValues = availableValues;
	}

	public void setStringFormatter(String formatter) {
		addParameter("format", formatter);
	}

}
