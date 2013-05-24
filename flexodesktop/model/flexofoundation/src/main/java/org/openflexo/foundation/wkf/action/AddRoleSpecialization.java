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
import org.openflexo.foundation.wkf.DuplicateRoleException;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.RoleSpecialization;
import org.openflexo.foundation.wkf.WorkflowModelObject;

public class AddRoleSpecialization extends FlexoAction<AddRoleSpecialization, Role, WorkflowModelObject> {

	private static final Logger logger = Logger.getLogger(AddRoleSpecialization.class.getPackage().getName());

	public static FlexoActionType<AddRoleSpecialization, Role, WorkflowModelObject> actionType = new FlexoActionType<AddRoleSpecialization, Role, WorkflowModelObject>(
			"add_a_specialized_role", FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public AddRoleSpecialization makeNewAction(Role focusedObject, Vector<WorkflowModelObject> globalSelection, FlexoEditor editor) {
			return new AddRoleSpecialization(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(Role role, Vector<WorkflowModelObject> globalSelection) {
			return role != null && !role.isImported();
		}

		@Override
		public boolean isEnabledForSelection(Role role, Vector<WorkflowModelObject> globalSelection) {
			return role != null && !role.isDefaultRole() && !role.isImported() && role.getAvailableRolesForSpecialization().size() > 0;
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, Role.class);
	}

	private Role _newParentRole;
	private String annotation;
	private RoleSpecialization _newRoleSpecialization;
	private boolean automaticallyCreateRoleSpecialization = false;

	AddRoleSpecialization(Role focusedObject, Vector<WorkflowModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws DuplicateRoleException {
		logger.info("Add parent role");
		if (getFocusedObject() != null && getNewParentRole() != null) {
			_newRoleSpecialization = new RoleSpecialization(getFocusedObject(), getNewParentRole());
			_newRoleSpecialization.setAnnotation(annotation);
			getFocusedObject().addToRoleSpecializations(_newRoleSpecialization);
		} else {
			logger.warning("Focused role is null !");
		}
	}

	public Role getNewParentRole() {
		return _newParentRole;
	}

	public void setNewParentRole(Role aParentRole) {
		_newParentRole = aParentRole;
	}

	public RoleSpecialization getNewRoleSpecialization() {
		return _newRoleSpecialization;
	}

	public boolean getRoleSpecializationAutomaticallyCreated() {
		return automaticallyCreateRoleSpecialization;
	}

	public void setRoleSpecializationAutomaticallyCreated(boolean automaticallyCreateRole) {
		this.automaticallyCreateRoleSpecialization = automaticallyCreateRole;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

}
