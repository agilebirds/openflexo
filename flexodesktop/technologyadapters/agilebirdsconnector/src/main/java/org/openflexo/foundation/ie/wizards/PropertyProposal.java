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
package org.openflexo.foundation.ie.wizards;

import org.openflexo.foundation.bindings.BindingValue.BindingPathElement;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.ie.widget.IEWidget;

public abstract class PropertyProposal {
	private String name;
	private IEWidget widget;
	private BindingPathElement _implementedProperty;

	public PropertyProposal(IEWidget widget, String name) {
		super();
		this.name = name;
		this.widget = widget;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public IEWidget getWidget() {
		return widget;
	}

	public BindingPathElement getImplementedProperty() {
		return _implementedProperty;
	}

	public void setImplementedProperty(DMProperty p) {
		_implementedProperty = p;
	}

}