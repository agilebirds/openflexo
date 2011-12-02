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

import java.rmi.RemoteException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoRemoteException;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.imported.DeltaStatus;
import org.openflexo.foundation.imported.FlexoImportedRoleLibraryDelta;
import org.openflexo.foundation.imported.FlexoImportedRoleLibraryDelta.RoleDelta;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.RoleList;
import org.openflexo.foundation.wkf.WorkflowModelObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.ws.client.PPMWebService.PPMRole;

public class RefreshImportedRoleAction extends
		RefreshImportedObjectAction<RefreshImportedRoleAction, WorkflowModelObject, WorkflowModelObject> {

	private final class RefreshRoleDeltaVisitor implements FlexoImportedRoleLibraryDelta.DeltaVisitor {
		private final RoleList lib;

		private StringBuilder report;

		protected RefreshRoleDeltaVisitor(RoleList lib) {
			this.lib = lib;
			this.report = new StringBuilder();
		}

		@Override
		public void visit(RoleDelta delta) {
			PPMRole role = delta.getPPMRole();
			DeltaStatus status = delta.getStatus();
			switch (status) {
			case UNCHANGED:
				break;
			case DELETED:
				if (!delta.getFiRole().isDeletedOnServer()) {
					if (report.length() > 0) {
						report.append("\n");
					}
					report.append(FlexoLocalization.localizedForKey("the_role")).append(" ").append(delta.getFiRole().getName())
							.append(" ").append(FlexoLocalization.localizedForKey("has_been_removed_from_server"));
				}
				delta.getFiRole().markAsDeletedOnServer();
				break;
			case UPDATED:
				Role rip = lib.getImportedObjectWithURI(role.getUri());
				if (report.length() > 0) {
					report.append("\n");
				}
				report.append(FlexoLocalization.localizedForKey("the_role")).append(" ").append(rip.getName()).append(" ")
						.append(FlexoLocalization.localizedForKey("has_been_updated"));
				rip.updateFromObject(role);
				break;
			case NEW:
				// We have received a new role-->we import it
				ImportRolesAction importRoles = ImportRolesAction.actionType.makeNewEmbeddedAction(lib, null,
						RefreshImportedRoleAction.this);
				Vector<PPMRole> v = new Vector<PPMRole>();
				v.add(role);
				importRoles.setRolesToImport(v);
				importRoles.doAction();
				break;
			default:
				break;
			}
		}

		public String getReport() {
			if (report.length() == 0) {
				return FlexoLocalization.localizedForKey("there_are_no_changes");
			}
			return report.toString();
		}
	}

	private static final Logger logger = FlexoLogger.getLogger(RefreshImportedRoleAction.class.getPackage().getName());

	public static final FlexoActionType<RefreshImportedRoleAction, WorkflowModelObject, WorkflowModelObject> actionType = new FlexoActionType<RefreshImportedRoleAction, WorkflowModelObject, WorkflowModelObject>(
			"refresh_imported_roles") {

		@Override
		protected boolean isEnabledForSelection(WorkflowModelObject object, Vector<WorkflowModelObject> globalSelection) {
			return object != null;
		}

		@Override
		protected boolean isVisibleForSelection(WorkflowModelObject object, Vector<WorkflowModelObject> globalSelection) {
			return object != null && object.isImported();
		}

		@Override
		public RefreshImportedRoleAction makeNewAction(WorkflowModelObject focusedObject, Vector<WorkflowModelObject> globalSelection,
				FlexoEditor editor) {
			return new RefreshImportedRoleAction(focusedObject, globalSelection, editor);
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, RoleList.class);
		FlexoModelObject.addActionForClass(actionType, Role.class);
	}

	protected RefreshImportedRoleAction(WorkflowModelObject focusedObject, Vector<WorkflowModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	private FlexoImportedRoleLibraryDelta libraryDelta;

	private RefreshRoleDeltaVisitor visitor;

	@Override
	protected void doAction(Object context) throws FlexoException {
		FlexoProject project = getFocusedObject().getProject();
		final RoleList lib = project.getFlexoWorkflow().getImportedRoleList();
		Vector<Role> roles = lib.getRoles();
		String[] uris = new String[roles.size()];
		int i = 0;
		for (Role role : roles) {
			uris[i] = role.getURI();
			i++;
		}
		PPMRole[] updated;
		try {
			updated = getWebService().refreshRoles(getLogin(), getMd5Password(), uris);
		} catch (RemoteException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, "Remote exception: " + e.getMessage(), e);
			}
			throw new FlexoRemoteException(null, e);
		}
		if (updated != null) {
			libraryDelta = new FlexoImportedRoleLibraryDelta(lib, updated);
			visitor = new RefreshRoleDeltaVisitor(lib);
			libraryDelta.visit(visitor);
		}
	}

	public FlexoImportedRoleLibraryDelta getLibraryDelta() {
		return libraryDelta;
	}

	public String getReport() {
		if (visitor != null) {
			return visitor.getReport();
		}
		return FlexoLocalization.localizedForKey("refresh_has_not_been_performed");
	}

}
