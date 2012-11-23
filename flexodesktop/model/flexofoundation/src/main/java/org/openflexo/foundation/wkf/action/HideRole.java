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
package org.openflexo.foundation.wkf.action;

import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoGUIAction;
import org.openflexo.foundation.wkf.Role;

public class HideRole extends FlexoGUIAction<HideRole, Role, Role> {

	public static final FlexoActionType<HideRole, Role, Role> actionType = new FlexoActionType<HideRole, Role, Role>("hide_role",
			FlexoActionType.defaultGroup) {

		@Override
		public boolean isEnabledForSelection(Role object, Vector<Role> globalSelection) {
			return object != null;
		}

		@Override
		public boolean isVisibleForSelection(Role object, Vector<Role> globalSelection) {
			return true;
		}

		@Override
		public HideRole makeNewAction(Role focusedObject, Vector<Role> globalSelection, FlexoEditor editor) {
			return new HideRole(focusedObject, globalSelection, editor);
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, Role.class);
	}

	protected HideRole(Role focusedObject, Vector<Role> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	public Role getRole() {
		return getFocusedObject();
	}

}
