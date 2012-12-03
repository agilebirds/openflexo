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
package org.openflexo.foundation.bindings;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.inspector.InspectableObject;

public class AdditionalBindingDefinition extends FlexoModelObject implements InspectableObject {

	/**
	 * Never use this constructor
	 */
	public AdditionalBindingDefinition() {
		super(null);
	}

	public AdditionalBindingDefinition(FlexoProject project) {
		super(project);
		// TODO Auto-generated constructor stub
	}

	private String _variableName;
	private String _value;
	private IEWidget _owner;

	public String getValue() {
		return _value;
	}

	public void setValue(String value) {
		this._value = value;
		if (_owner != null) {
			_owner.setChanged();
		}
	}

	public String getVariableName() {
		return _variableName;
	}

	public void setVariableName(String name) {
		_variableName = name;
		if (_owner != null) {
			_owner.setChanged();
		}
	}

	public void setOwner(IEWidget widget) {
		_owner = widget;
	}

	public IEWidget getOwner() {
		return _owner;
	}

	@Override
	public XMLStorageResourceData getXMLResourceData() {
		if (getOwner() != null) {
			return getOwner().getXMLResourceData();
		}
		return null;
	}

	@Override
	public String getInspectorName() {
		return null;
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "additional_binding";
	}

	/**
	 * Overrides getFullyQualifiedName
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getFullyQualifiedName()
	 */
	@Override
	public String getFullyQualifiedName() {
		return "ADDITIONAL_BINDING_" + getFlexoID();
	}

}
