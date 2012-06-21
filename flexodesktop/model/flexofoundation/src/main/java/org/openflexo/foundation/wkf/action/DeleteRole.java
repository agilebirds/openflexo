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
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.WorkflowModelObject;

public class DeleteRole extends FlexoAction<DeleteRole, Role, WorkflowModelObject> {

	private static final Logger logger = Logger.getLogger(DeleteRole.class.getPackage().getName());

	public static FlexoActionType<DeleteRole, Role, WorkflowModelObject> actionType = new FlexoActionType<DeleteRole, Role, WorkflowModelObject>(
			"delete_role", FlexoActionType.editGroup, FlexoActionType.DELETE_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DeleteRole makeNewAction(Role focusedObject, Vector<WorkflowModelObject> globalSelection, FlexoEditor editor) {
			return new DeleteRole(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(Role role, Vector<WorkflowModelObject> globalSelection) {
			return role != null && !role.isDefaultRole();
		}

		@Override
		public boolean isEnabledForSelection(Role role, Vector<WorkflowModelObject> globalSelection) {
			return ((role != null && !role.isDefaultRole()) || (globalSelection != null && globalSelection.size() > 0));
		}

	};

	DeleteRole(Role focusedObject, Vector<WorkflowModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		logger.info("Delete role(s)");
		for (Role r : getRoleToDelete()) {
			if (!r.isDefaultRole()) {
				r.delete();
			}
		}
	}

	public Vector<Role> getRoleToDelete() {
		Vector<Role> roleToDelete = new Vector<Role>();
		if (getGlobalSelection() != null) {
			for (FlexoModelObject o : getGlobalSelection()) {
				if (o instanceof Role) {
					roleToDelete.add((Role) o);
				}
			}
		}
		if (!roleToDelete.contains(getFocusedObject())) {
			roleToDelete.add(getFocusedObject());
		}
		return roleToDelete;
	}

	public Role getRole() {
		return getFocusedObject();
	}

}
