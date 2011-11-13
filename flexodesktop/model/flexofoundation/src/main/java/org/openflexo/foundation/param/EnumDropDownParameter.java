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

public class EnumDropDownParameter<E extends Enum<E>> extends ParameterDefinition<E> {

	private Vector<E> _availableValues;

	public EnumDropDownParameter(String name, String label, E value, E[] values) {
		super(name, label, value);
		addParameter("dynamiclist", "params." + name + ".availableValues");
		_availableValues = new Vector<E>();
		for (E e : values) {
			if (accept(e))
				_availableValues.add(e);
		}
	}

	@Override
	public String getWidgetName() {
		return DenaliWidget.DROPDOWN;
	}

	public void setShowReset(boolean showReset) {
		addParameter("showReset", "" + showReset);
	}

	// Override this if list not defined in constructor
	public Vector<E> getAvailableValues() {
		return _availableValues;
	}

	public boolean accept(E value) {
		return true;
	}
}
