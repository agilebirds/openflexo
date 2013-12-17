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
import org.openflexo.foundation.AgileBirdsObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoGUIAction;
import org.openflexo.foundation.wkf.FlexoProcess;

public class ShowRoles extends FlexoGUIAction<ShowRoles, FlexoProcess, FlexoProcess> {

	public static final FlexoActionType<ShowRoles, FlexoProcess, FlexoProcess> actionType = new FlexoActionType<ShowRoles, FlexoProcess, FlexoProcess>(
			"show_roles", FlexoActionType.defaultGroup) {

		@Override
		public boolean isEnabledForSelection(FlexoProcess object, Vector<FlexoProcess> globalSelection) {
			return object != null;
		}

		@Override
		public boolean isVisibleForSelection(FlexoProcess object, Vector<FlexoProcess> globalSelection) {
			return true;
		}

		@Override
		public ShowRoles makeNewAction(FlexoProcess focusedObject, Vector<FlexoProcess> globalSelection, FlexoEditor editor) {
			return new ShowRoles(focusedObject, globalSelection, editor);
		}

	};

	static {
		AgileBirdsObject.addActionForClass(actionType, FlexoProcess.class);
	}

	protected ShowRoles(FlexoProcess focusedObject, Vector<FlexoProcess> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	public FlexoProcess getProcess() {
		return getFocusedObject();
	}

}
