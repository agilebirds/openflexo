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

public class DeleteFlexoProperty extends FlexoAction<DeleteFlexoProperty, FlexoProperty, FlexoProperty> {

	public static final FlexoActionType<DeleteFlexoProperty, FlexoProperty, FlexoProperty> actionType = new FlexoActionType<DeleteFlexoProperty, FlexoProperty, FlexoProperty>(
			"delete_flexo_property") {

		@Override
		public boolean isEnabledForSelection(FlexoProperty object, Vector<FlexoProperty> globalSelection) {
			return object != null;
		}

		@Override
		public boolean isVisibleForSelection(FlexoProperty object, Vector<FlexoProperty> globalSelection) {
			return false;
		}

		@Override
		public DeleteFlexoProperty makeNewAction(FlexoProperty focusedObject, Vector<FlexoProperty> globalSelection, FlexoEditor editor) {
			return new DeleteFlexoProperty(focusedObject, globalSelection, editor);
		}

	};

	static {
		FlexoObject.addActionForClass(actionType, FlexoProperty.class);
	}

	public DeleteFlexoProperty(FlexoProperty focusedObject, Vector<FlexoProperty> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		Vector<FlexoObject> properties = getGlobalSelectionAndFocusedObject();
		for (FlexoObject object : properties) {
			if (object instanceof FlexoProperty) {
				((FlexoProperty) object).delete();
			}
		}
	}

}
