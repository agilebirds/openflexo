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
import org.openflexo.foundation.wkf.RoleSpecialization;
import org.openflexo.foundation.wkf.WorkflowModelObject;

public class DeleteRoleSpecialization extends FlexoAction<DeleteRoleSpecialization, RoleSpecialization, WorkflowModelObject> {

	private static final Logger logger = Logger.getLogger(DeleteRoleSpecialization.class.getPackage().getName());

	public static FlexoActionType<DeleteRoleSpecialization, RoleSpecialization, WorkflowModelObject> actionType = new FlexoActionType<DeleteRoleSpecialization, RoleSpecialization, WorkflowModelObject>(
			"delete_role_specialization", FlexoActionType.editGroup, FlexoActionType.DELETE_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DeleteRoleSpecialization makeNewAction(RoleSpecialization focusedObject, Vector<WorkflowModelObject> globalSelection,
				FlexoEditor editor) {
			return new DeleteRoleSpecialization(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(RoleSpecialization role, Vector<WorkflowModelObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(RoleSpecialization role, Vector<WorkflowModelObject> globalSelection) {
			return role != null || globalSelection != null && globalSelection.size() > 0;
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, RoleSpecialization.class);
	}

	DeleteRoleSpecialization(RoleSpecialization focusedObject, Vector<WorkflowModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		logger.info("Delete role specialization(s)");
		for (RoleSpecialization r : getRoleSpecializationToDelete()) {
			r.delete();
		}
	}

	public Vector<RoleSpecialization> getRoleSpecializationToDelete() {
		Vector<RoleSpecialization> roleSpecializationToDelete = new Vector<RoleSpecialization>();
		if (getGlobalSelection() != null) {
			for (FlexoModelObject o : getGlobalSelection()) {
				if (o instanceof RoleSpecialization) {
					roleSpecializationToDelete.add((RoleSpecialization) o);
				}
			}
		}
		if (!roleSpecializationToDelete.contains(getFocusedObject())) {
			roleSpecializationToDelete.add(getFocusedObject());
		}
		return roleSpecializationToDelete;
	}

	public RoleSpecialization getRoleSpecialization() {
		return getFocusedObject();
	}

}
