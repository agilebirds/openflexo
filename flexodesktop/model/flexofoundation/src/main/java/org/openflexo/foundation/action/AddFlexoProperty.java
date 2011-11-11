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
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoProperty;

public class AddFlexoProperty extends FlexoAction<AddFlexoProperty, FlexoModelObject, FlexoModelObject> {

	public static final FlexoActionType<AddFlexoProperty, FlexoModelObject, FlexoModelObject> actionType = new FlexoActionType<AddFlexoProperty, FlexoModelObject, FlexoModelObject>(
			"add_flexo_property") {

		@Override
		protected boolean isEnabledForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return object != null;
		}

		@Override
		protected boolean isVisibleForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return false;
		}

		@Override
		public AddFlexoProperty makeNewAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
			return new AddFlexoProperty(focusedObject, globalSelection, editor);
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, FlexoModelObject.class);
	}

	private String name;
	private String value;

	private boolean insertSorted = false;

	private FlexoProperty createdProperty;

	public AddFlexoProperty(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		if (getFocusedObject() != null) {
			createdProperty = new FlexoProperty(getFocusedObject().getProject(), getFocusedObject());
			if (getName() != null)
				createdProperty.setName(getName());
			else
				createdProperty.setName(getFocusedObject().getNextPropertyName());
			if (getValue() != null)
				createdProperty.setValue(getValue());
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
