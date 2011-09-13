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
package org.openflexo.foundation.imported.action;

import java.util.Vector;

import org.openflexo.foundation.ConvertedIntoLocalObject;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.Role;


public class ConvertIntoLocalRole extends FlexoAction<ConvertIntoLocalRole, Role, Role> {

	public static final FlexoActionType<ConvertIntoLocalRole, Role, Role> actionType = new FlexoActionType<ConvertIntoLocalRole, Role, Role>(
			"convert_into_local_role") {

		@Override
		protected boolean isEnabledForSelection(Role object, Vector<Role> globalSelection) {
			return isVisibleForSelection(object, globalSelection) && object.getWorkflow().getRoleList().roleWithName(object.getName())==null;
		}

		@Override
		protected boolean isVisibleForSelection(Role object, Vector<Role> globalSelection) {
			return object != null && object.isImported() && object.isDeletedOnServer();
		}

		@Override
		public ConvertIntoLocalRole makeNewAction(Role focusedObject, Vector<Role> globalSelection, FlexoEditor editor) {
			return new ConvertIntoLocalRole(focusedObject, globalSelection, editor);
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, Role.class);
	}

	public ConvertIntoLocalRole(Role focusedObject, Vector<Role> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		Role role = getFocusedObject();
		String uri = role.getURI();
		FlexoWorkflow workflow = role.getWorkflow();
		role.getRoleList().removeFromRoles(role);
		workflow.getRoleList().addToRoles(role);
		role.setURIFromSourceObject(uri);
		role.setURI(null);
		role.setVersionURI(null);
		role.setChanged();
		role.notifyObservers(new ConvertedIntoLocalObject(role));
	}

}
