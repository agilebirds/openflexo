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

public class RadioButtonListParameter<T> extends ParameterDefinition<T> {

	private Vector<T> _availableValues;

	public RadioButtonListParameter(String name, String label, T defaultValue, T... availableValues) {
		super(name, label, defaultValue);
		_availableValues = new Vector<T>();
		for (int i = 0; i < availableValues.length; i++) {
			_availableValues.add(availableValues[i]);
		}
		addParameter("dynamiclist", "params." + name + ".availableValues");
		/* allValues = "";
		boolean isFirst = true;
		for (int i=0; i<availableValues.length; i++) {
		    allValues += (isFirst?availableValues[i]:","+availableValues[i]);
		    isFirst = false;
		}
		addParameter("staticlist",allValues);*/
	}

	public RadioButtonListParameter(String name, String label, T defaultValue, Vector<T> availableValues) {
		super(name, label, defaultValue);
		_availableValues = availableValues;
		addParameter("dynamiclist", "params." + name + ".availableValues");
	}

	@Override
	public String getWidgetName() {
		return DenaliWidget.RADIOBUTTON_LIST;
	}

	public Vector<T> getAvailableValues() {
		return _availableValues;
	}

}
