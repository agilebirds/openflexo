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
package org.openflexo.foundation.action;

import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoProperty;

public class AddFlexoProperty extends FlexoAction<AddFlexoProperty, FlexoObject, FlexoObject> {

	public static final FlexoActionType<AddFlexoProperty, FlexoObject, FlexoObject> actionType = new FlexoActionType<AddFlexoProperty, FlexoObject, FlexoObject>(
			"add_flexo_property") {

		@Override
		public boolean isEnabledForSelection(FlexoObject object, Vector<FlexoObject> globalSelection) {
			return object != null;
		}

		@Override
		public boolean isVisibleForSelection(FlexoObject object, Vector<FlexoObject> globalSelection) {
			return false;
		}

		@Override
		public AddFlexoProperty makeNewAction(FlexoObject focusedObject, Vector<FlexoObject> globalSelection, FlexoEditor editor) {
			return new AddFlexoProperty(focusedObject, globalSelection, editor);
		}

	};

	static {
		FlexoObject.addActionForClass(actionType, FlexoObject.class);
	}

	private String name;
	private String value;

	private boolean insertSorted = false;

	private FlexoProperty createdProperty;

	public AddFlexoProperty(FlexoObject focusedObject, Vector<FlexoObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		if (getFocusedObject() != null) {
			createdProperty = new FlexoProperty(getFocusedObject());
			if (getName() != null) {
				createdProperty.setName(getName());
			} else {
				createdProperty.setName(getFocusedObject().getNextPropertyName());
			}
			if (getValue() != null) {
				createdProperty.setValue(getValue());
			}
			getFocusedObject().addToCustomProperties(createdProperty, isInsertSorted());
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public FlexoProperty getCreatedProperty() {
		return createdProperty;
	}

	public void setCreatedProperty(FlexoProperty createdProperty) {
		this.createdProperty = createdProperty;
	}

	public boolean isInsertSorted() {
		return insertSorted;
	}

	public void setInsertSorted(boolean insertSorted) {
		this.insertSorted = insertSorted;
	}

}
