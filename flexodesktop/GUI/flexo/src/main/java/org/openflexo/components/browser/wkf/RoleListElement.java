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
package org.openflexo.components.browser.wkf;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.tree.TreePath;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ExpansionSynchronizedElement;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.RoleList;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.localization.FlexoLocalization;

/**
 * Browser element representing all the roles of a process
 * 
 * @author sguerin
 * 
 */
public class RoleListElement extends BrowserElement implements ExpansionSynchronizedElement {

	private HashSet<Role> observedRole;

	public RoleListElement(RoleList roleList, ProjectBrowser browser, BrowserElement parent) {
		super(roleList, BrowserElementType.ROLE_LIST, browser, parent);
	}

	@Override
	public TreePath getTreePath() {
		return super.getTreePath();
	}

	@Override
	public Icon getIcon() {
		if (getRoleList().isImportedRoleList()) {
			return WKFIconLibrary.IMPORTED_ROLE_LIBRARY_ICON;
		} else {
			return super.getIcon();
		}
	}

	@Override
	protected void buildChildrenVector() {
		if (observedRole == null) {
			observedRole = new HashSet<Role>();
		}
		clearObserving();
		// We add the roles
		if (!getRoleList().isImportedRoleList()) {
			switch (getProjectBrowser().getRoleViewMode()) {
			case TOP_DOWN:
				for (Enumeration<Role> e = getRoleList().getSortedRoles(); e.hasMoreElements();) {
					Role r = e.nextElement();
					if (r.getRoleSpecializations().size() == 0) {
						observeRole(r);
						addToChilds(r);
					}
				}
				break;
			case BOTTOM_UP:
				for (Enumeration<Role> e = getRoleList().getSortedRoles(); e.hasMoreElements();) {
					Role r = e.nextElement();
					Vector<Role> v = r.getRolesSpecializingMyself();
					if (v.size() == 0 || (v.size() == 1 && v.firstElement() == r)) {
						observeRole(r);
						addToChilds(r);
					}
				}
				break;
			case FLAT:
				for (Enumeration<Role> e = getRoleList().getSortedRoles(); e.hasMoreElements();) {
					Role r = e.nextElement();
					addToChilds(r);
				}
				break;
			}
		} else {
			Vector<FlexoModelObject> roles = new Vector<FlexoModelObject>(getRoleList().getRoles());
			Collections.sort(roles, FlexoModelObject.NAME_COMPARATOR);
			for (FlexoModelObject role : roles) {
				addToChilds(role);
			}
		}
	}

	private void clearObserving() {
		if (observedRole == null) {
			observedRole = new HashSet<Role>();
		}
		for (Role r : observedRole) {
			r.deleteObserver(this);
		}
		observedRole.clear();
	}

	private void observeRole(Role role) {
		if (observedRole == null) {
			observedRole = new HashSet<Role>();
		}
		if (!observedRole.contains(role)) {
			role.addObserver(this);
			observedRole.add(role);
		}
	}

	@Override
	public void delete() {
		super.delete();
		clearObserving();
		observedRole = null;
	}

	protected RoleList getRoleList() {
		return (RoleList) getObject();
	}

	@Override
	protected BrowserElementType getFilteredElementType() {
		return BrowserElementType.ROLE;
	}

	@Override
	public String getName() {
		if (getRoleList().isImportedRoleList()) {
			return FlexoLocalization.localizedForKey("imported_role_library");
		} else {
			return super.getName();
		}
	}

	@Override
	public void collapse() {

	}

	@Override
	public void expand() {

	}

	@Override
	public boolean isExpanded() {
		return true;
	}

	@Override
	public boolean isExpansionSynchronizedWithData() {
		return true;
	}

	@Override
	public boolean requiresExpansionFor(BrowserElement next) {
		return true;
	}

}
