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
package org.openflexo.foundation.wkf;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.DataFlexoObserver;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.action.FlexoActionizer;
import org.openflexo.foundation.imported.dm.RoleAlreadyImportedException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.utils.FlexoColor;
import org.openflexo.foundation.utils.FlexoIndexManager;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.wkf.action.AddRole;
import org.openflexo.foundation.wkf.action.DeleteRole;
import org.openflexo.foundation.wkf.dm.RoleInserted;
import org.openflexo.foundation.wkf.dm.RoleRemoved;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.foundation.xml.FlexoWorkflowBuilder;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.EmptyVector;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.ws.client.PPMWebService.PPMRole;

/**
 * Represents the list of roles for the workflow
 * 
 * @author sguerin
 * 
 */
public final class RoleList extends WorkflowModelObject implements DataFlexoObserver, Serializable, InspectableObject {

	private static final Logger logger = Logger.getLogger(RoleList.class.getPackage().getName());

	private Vector<Role> _roles;
	public static FlexoActionizer<AddRole, WorkflowModelObject, WorkflowModelObject> addRoleActionizer;
	public static FlexoActionizer<DeleteRole, Role, WorkflowModelObject> deleteRoleActionizer;

	public Role importRole(PPMRole role) throws RoleAlreadyImportedException {
		Role fir = getImportedObjectWithURI(role.getUri());
		if (fir != null) {
			throw new RoleAlreadyImportedException(role, fir);
		}
		fir = Role.createImportedRoleFromRole(this, role);
		try {
			addToRoles(fir);
		} catch (DuplicateRoleException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.log(Level.SEVERE, "Duplicate exception while importing role: " + role + " this should never happen!", e);
			}
		}
		return fir;
	}

	/**
	 * Constructor used during deserialization
	 */
	public RoleList(FlexoWorkflowBuilder builder) {
		this(builder.getProject(), builder.workflow);
		initializeDeserialization(builder);
	}

	/**
	 * Constructor used during deserialization
	 * 
	 * @deprecated (used before version 1.2.1)
	 */
	@Deprecated
	public RoleList(FlexoProcessBuilder builder) {
		this(builder.getProject(), null);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public RoleList(FlexoProject project, FlexoWorkflow workflow) {
		super(project, workflow);
		_roles = new Vector<Role>();
	}

	@Override
	public boolean isImported() {
		return isImportedRoleList();
	}

	public boolean isImportedRoleList() {
		return getWorkflow() != null && getWorkflow().getImportedRoleList() == this;
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "role_list";
	}

	@Override
	public String getFullyQualifiedName() {
		return getProject().getFullyQualifiedName() + ".ROLE_LIST";
	}

	public Role roleWithName(String aName) {
		for (Enumeration e = getRoles().elements(); e.hasMoreElements();) {
			Role temp = (Role) e.nextElement();
			if (temp.getName() != null && temp.getName().equals(aName)) {
				return temp;
			}
		}
		// if (logger.isLoggable(Level.WARNING)) logger.warning ("Could not find
		// role named "+aName);
		return null;
	}

	/**
	 * Default inspector name
	 */
	@Override
	public String getInspectorName() {
		return Inspectors.WKF.ROLE_LIST_INSPECTOR;
	}

	public Vector<Role> getRoles() {
		return _roles;
	}

	public void setRoles(Vector<Role> roles) {
		_roles = roles;
	}

	public int size() {
		return getRoles().size();
	}

	public Role getImportedObjectWithURI(String uri) {
		return getObjectWithURI(getRoles(), uri);
	}

	public void addToRoles(Role aRole) throws DuplicateRoleException {
		addToRoles(aRole, false);
	}

	public void addToRoles(Role aRole, boolean replaceExisting) throws DuplicateRoleException {
		if (!aRole.isImported() && !isImportedRoleList()) {
			if (aRole.getName() == null) {
				aRole.setName(aRole.getIsSystemRole() ? getNextNewSystemRoleName() : getNextNewUserRoleName());
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("addToRoles with " + aRole.getFullyQualifiedName());
			}
			Role roleWithName = roleWithName(aRole.getName());
			if (roleWithName != null) {
				if (roleWithName == aRole) {
					return;
				}
				if (!replaceExisting) {
					if (isDeserializing()) {
						aRole.setName(aRole.getName() + "-1");
						addToRoles(aRole);
						return;
					}
					throw new DuplicateRoleException(aRole.getName());
				} else {
					_roles.remove(roleWithName);
				}
			}
		}
		if (!_roles.contains(aRole)) {
			_roles.add(aRole);
			aRole.setRoleList(this);
			if (!isDeserializing()) {
				notifyRoleAdded(aRole);
				if (getWorkflow() != null) {
					getWorkflow().clearAssignableRolesCache();
				}
			}
		}
	}

	public void removeFromRoles(Role aRole) {
		if (_roles.contains(aRole)) {
			_roles.remove(aRole);
			aRole.setRoleList(null);
			if (!isDeserializing()) {
				notifyRoleRemoved(aRole);
				if (getWorkflow() != null) {
					getWorkflow().clearAssignableRolesCache();
				}
			}
		}
	}

	/**
	 * @return
	 */
	public Enumeration<Role> getSortedRoles() {
		disableObserving();
		Role[] o = FlexoIndexManager.sortArray(getRoles().toArray(new Role[0]));
		enableObserving();
		return ToolBox.getEnumeration(o);
	}

	public Vector<Role> getSortedRolesVector() {
		disableObserving();
		Vector<Role> v = new Vector<Role>(getRoles());
		Collections.sort(v, FlexoIndexManager.INDEX_COMPARATOR);
		enableObserving();
		return v;
	}

	public Vector<Role> getTopRolesInTopDownHierachy() {
		Vector<Role> reply = new Vector<Role>();
		for (Enumeration<Role> e = getSortedRoles(); e.hasMoreElements();) {
			Role r = e.nextElement();
			if (r.getRoleSpecializations().size() == 0) {
				reply.add(r);
			}
		}
		return reply;
	}

	/**
	 * used by velocity
	 * 
	 * @return the list of roles with no incoming arrow.
	 */
	public ArrayList<Role> getRoots() {
		ArrayList<Role> reply = new ArrayList<Role>();
		for (Enumeration<Role> e = getSortedRoles(); e.hasMoreElements();) {
			Role r = e.nextElement();
			if (r.getInverseRoleSpecializations().size() == 0) {
				reply.add(r);
			}
		}
		Role.sort(reply);
		return reply;
	}

	/**
	 * @param role
	 * @param process
	 */
	private void notifyRoleAdded(Role role) {
		setChanged();
		notifyObservers(new RoleInserted(role));
	}

	/**
	 * @param role
	 * @param process
	 */
	private void notifyRoleRemoved(Role role) {
		setChanged();
		notifyObservers(new RoleRemoved(role));
	}

	public Role createNewRole() {
		String newRoleName = getNextNewUserRoleName();
		Role newRole = new Role(getWorkflow(), newRoleName);
		try {
			addToRoles(newRole);
		} catch (DuplicateRoleException e) {
			// should never happen
			e.printStackTrace();
		}
		return newRole;
	}

	public String getNextNewUserRoleName() {
		String baseName = FlexoLocalization.localizedForKey("role");
		String newRoleName = baseName;
		int inc = 0;
		while (roleWithName(newRoleName) != null) {
			inc++;
			newRoleName = baseName + inc;
		}
		return newRoleName;
	}

	public String getNextNewSystemRoleName() {
		String baseName = FlexoLocalization.localizedForKey("system");
		String newRoleName = baseName;
		int inc = 0;
		while (roleWithName(newRoleName) != null) {
			inc++;
			newRoleName = baseName + inc;
		}
		return newRoleName;
	}

	public void deleteRole(Role aRole) {
		removeFromRoles(aRole);
	}

	public boolean isRoleDeletable(Role aRole) {
		return true;
	}

	private Role defaultRole = null;

	public Role getDefaultRole() {
		if (defaultRole == null || defaultRole.isDeleted()) {
			defaultRole = null;
		}
		return defaultRole;
	}

	public void setDefaultRole(Role role) {
		defaultRole = role;
	}

	protected void assertDefaultRoleHasBeenCreated() {
		if (getDefaultRole() == null && !isDeserializing()) {
			// Create a default system role
			String defaultRoleName = FlexoLocalization.localizedForKey("no_role");
			Role newRole = new Role(getWorkflow(), defaultRoleName);
			newRole.setColor(Color.DARK_GRAY);
			newRole.setIsSystemRole(true);
			defaultRole = newRole;
		}
	}

	/**
	 * Return a Vector of all embedded WKFObjects
	 * 
	 * @return a Vector of WKFObject instances
	 */
	@Override
	public Vector<Validable> getAllEmbeddedValidableObjects() {
		Vector<Validable> returned = new Vector<Validable>();
		returned.add(this);
		returned.addAll(getRoles());
		return returned;
	}

	public void performAddRole() {
		if (addRoleActionizer != null) {
			addRoleActionizer.run(this, EMPTY_VECTOR);
		}
	}

	public void performDeleteRole(Role object) {
		if (deleteRoleActionizer != null) {
			deleteRoleActionizer.run(object, EmptyVector.EMPTY_VECTOR(WorkflowModelObject.class));
		}
	}

	public Color getNewRoleColor() {
		Vector<Color> v = new Vector<Color>();
		for (Role role : getRoles()) {
			if (role.getColor() != null) {
				v.add(role.getColor());
			}
		}
		return FlexoColor.getRandomColor(v);
	}

	// ==========================================================================
	// ================================= Delete ===============================
	// ==========================================================================

	@Override
	public final void delete() {
		_roles.clear();
		super.delete();
		deleteObservers();
	}

	// ===================================================================
	// =========================== FlexoObserver =========================
	// ===================================================================

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		// TODO Auto-generated method stub

	}
}
