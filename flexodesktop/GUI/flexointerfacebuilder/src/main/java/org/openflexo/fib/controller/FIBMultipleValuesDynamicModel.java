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
package org.openflexo.fib.controller;

import org.openflexo.fib.model.FIBMultipleValues;

public class FIBMultipleValuesDynamicModel<T, O> extends FIBComponentDynamicModel<T> {
	private O selected;
	private int selectedIndex;

	public FIBMultipleValuesDynamicModel(T data, FIBMultipleValues component) {
		super(data, component);
	}

	public O getSelected() {
		return selected;
	}

	public void setSelected(O selected) {
		this.selected = selected;
		getPropertyChangeSupport().firePropertyChange("selected", null, selected);
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
		getPropertyChangeSupport().firePropertyChange("selectedIndex", null, selectedIndex);
	}
}