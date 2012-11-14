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
package org.openflexo.foundation.view.action;

import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoGUIAction;
import org.openflexo.foundation.view.ViewObject;

public class VESelectAll extends FlexoGUIAction<VESelectAll, ViewObject, ViewObject> {

	public static FlexoActionType<VESelectAll, ViewObject, ViewObject> actionType = new FlexoActionType<VESelectAll, ViewObject, ViewObject>(
			"select_all", FlexoActionType.editGroup) {

		/**
		 * Factory method
		 */
		@Override
		public VESelectAll makeNewAction(ViewObject focusedObject, Vector<ViewObject> globalSelection, FlexoEditor editor) {
			return new VESelectAll(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(ViewObject object, Vector<ViewObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(ViewObject object, Vector<ViewObject> globalSelection) {
			return true;
		}

	};

	static {
		FlexoModelObject.addActionForClass(VESelectAll.actionType, ViewObject.class);
	}

	VESelectAll(ViewObject focusedObject, Vector<ViewObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

}
