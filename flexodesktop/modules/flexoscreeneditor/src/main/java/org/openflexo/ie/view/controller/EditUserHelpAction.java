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
package org.openflexo.ie.view.controller;

import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoGUIAction;
import org.openflexo.foundation.ie.IEOperationComponent;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.cl.OperationComponentDefinition;
import org.openflexo.localization.FlexoLocalization;

public class EditUserHelpAction extends FlexoGUIAction<EditUserHelpAction, FlexoModelObject, FlexoModelObject> {

	public static FlexoActionType<EditUserHelpAction, FlexoModelObject, FlexoModelObject> actionType = new FlexoActionType<EditUserHelpAction, FlexoModelObject, FlexoModelObject>(
			"help", FlexoActionType.helpGroup) {

		/**
		 * Factory method
		 */
		@Override
		public EditUserHelpAction makeNewAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
			return new EditUserHelpAction(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return object != null && object instanceof OperationComponentDefinition || object instanceof IEOperationComponent;
		}

		@Override
		protected boolean isEnabledForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return object != null && object instanceof OperationComponentDefinition || object instanceof IEOperationComponent;
		}

	};

	static {
		FlexoModelObject.addActionForClass(EditUserHelpAction.actionType, OperationComponentDefinition.class);
		FlexoModelObject.addActionForClass(EditUserHelpAction.actionType, IEOperationComponent.class);
	}

	EditUserHelpAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	public String getLocalizedName() {
		if (getFocusedObject() != null && getFocusedObject() instanceof IEOperationComponent) {
			return FlexoLocalization.localizedForKey("write_user_help_for") + " " + ((IEWOComponent) getFocusedObject()).getName();
		} else if (getFocusedObject() instanceof OperationComponentDefinition) {
			return FlexoLocalization.localizedForKey("write_user_help_for") + " "
					+ ((OperationComponentDefinition) getFocusedObject()).getName();
		}
		return null;
	}
}
