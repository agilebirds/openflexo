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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.imported.dm.RoleAlreadyImportedException;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.RoleList;
import org.openflexo.foundation.wkf.WorkflowModelObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.ws.client.PPMWebService.PPMRole;

public class ImportRolesAction extends FlexoAction<ImportRolesAction, WorkflowModelObject, WorkflowModelObject> {

	private static final Logger logger = FlexoLogger.getLogger(ImportRolesAction.class.getPackage().getName());

	public static final FlexoActionType<ImportRolesAction, WorkflowModelObject, WorkflowModelObject> actionType = new FlexoActionType<ImportRolesAction, WorkflowModelObject, WorkflowModelObject>(
			"import_roles", FlexoActionType.defaultGroup, FlexoActionType.importMenu, FlexoActionType.ADD_ACTION_TYPE) {

		@Override
		public ImportRolesAction makeNewAction(WorkflowModelObject focusedObject, Vector<WorkflowModelObject> globalSelection,
				FlexoEditor editor) {
			return new ImportRolesAction(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(WorkflowModelObject object, Vector<WorkflowModelObject> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(WorkflowModelObject object, Vector<WorkflowModelObject> globalSelection) {
			return true;
		}
	};

	static {
		FlexoModelObject.addActionForClass(actionType, RoleList.class);
		FlexoModelObject.addActionForClass(actionType, Role.class);
	}

	private RoleImportReport importReport;

	private Vector<PPMRole> rolesToImport;

	public Vector<PPMRole> getRolesToImport() {
		return rolesToImport;
	}

	public void setRolesToImport(Vector<PPMRole> rolesToImport) {
		this.rolesToImport = rolesToImport;
	}

	public RoleImportReport getImportReport() {
		return importReport;
	}

	protected ImportRolesAction(WorkflowModelObject focusedObject, Vector<WorkflowModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		RoleList lib = getEditor().getProject().getWorkflow().getImportedRoleList();
		importReport = new RoleImportReport();
		for (PPMRole p : getRolesToImport()) {
			if (!isValid(p))
				importReport.addToInvalidRoles(p);
			else {
				try {
					Role fip = lib.importRole(p);
					importReport.addToProperlyImportedRoles(p, fip);
				} catch (RoleAlreadyImportedException e) {
					importReport.addToAlreadyImportedRoles(p);
					if (logger.isLoggable(Level.FINE))
						logger.log(Level.FINE, "Role " + p.getName() + " was already imported", e);
				}
			}
		}

	}

	private boolean isValid(PPMRole role) {
		return role.getUri() != null && role.getVersionUri() != null;
	}

	public static class RoleImportReport {
		private LinkedHashMap<PPMRole, Role> properlyImported;
		private Vector<PPMRole> invalidRoles;
		private Vector<PPMRole> alreadyImportedRoles;

		public RoleImportReport() {
			properlyImported = new LinkedHashMap<PPMRole, Role>();
			invalidRoles = new Vector<PPMRole>();
			alreadyImportedRoles = new Vector<PPMRole>();
		}

		public LinkedHashMap<PPMRole, Role> getProperlyImported() {
			return properlyImported;
		}

		public void setProperlyImported(LinkedHashMap<PPMRole, Role> properlyImported) {
			this.properlyImported = properlyImported;
		}

		public void addToProperlyImportedRoles(PPMRole processToImport, Role matchingImportedRole) {
			properlyImported.put(processToImport, matchingImportedRole);
		}

		public Vector<PPMRole> getInvalidRoles() {
			return invalidRoles;
		}

		public void setInvalidRoles(Vector<PPMRole> invalidRole) {
			this.invalidRoles = invalidRole;
		}

		public void addToInvalidRoles(PPMRole process) {
			invalidRoles.add(process);
		}

		public Vector<PPMRole> getAlreadyImportedRoles() {
			return alreadyImportedRoles;
		}

		public void setAlreadyImportedRoles(Vector<PPMRole> invalidRole) {
			this.alreadyImportedRoles = invalidRole;
		}

		public void addToAlreadyImportedRoles(PPMRole process) {
			alreadyImportedRoles.add(process);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			// 1. Properly imported
			Iterator<PPMRole> i = getProperlyImported().keySet().iterator();
			append(sb, i, FlexoLocalization.localizedForKey("the_following_roles_have_been_properly_imported"));
			// 2. Invalid roles
			i = getInvalidRoles().iterator();
			append(sb, i, FlexoLocalization.localizedForKey("the_following_roles_were_not_valid"));
			// 3. Already imported
			i = getAlreadyImportedRoles().iterator();
			append(sb, i, FlexoLocalization.localizedForKey("the_following_roles_were_already_imported"));
			if (sb.length() == 0)
				return FlexoLocalization.localizedForKey("nothing_has_been_imported");
			else
				sb.append("</html>");
			return sb.toString();
		}

		private void append(StringBuilder sb, Iterator<PPMRole> i, String title) {
			if (sb.length() == 0)
				sb.append("<html>");
			boolean needsClosingUl = false;
			if (i.hasNext()) {
				sb.append(title).append(':').append("<ul>");
				needsClosingUl = true;
			}
			while (i.hasNext()) {
				PPMRole p = i.next();
				sb.append("<li>").append(p.getName()).append("</li>");
			}
			if (needsClosingUl) {
				sb.append("</ul>");
				needsClosingUl = false;
			}
		}

	}

}
