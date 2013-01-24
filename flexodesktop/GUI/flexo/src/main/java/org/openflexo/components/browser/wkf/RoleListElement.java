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

import javax.swing.Icon;
import javax.swing.tree.TreePath;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ExpansionSynchronizedElement;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.RoleList;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconLibrary;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.localization.FlexoLocalization;

/**
 * Browser element representing all the roles of a process
 * 
 * @author sguerin
 * 
 */
public class RoleListElement extends BrowserElement implements ExpansionSynchronizedElement {

	public RoleListElement(RoleList roleList, ProjectBrowser browser, BrowserElement parent) {
		super(roleList, BrowserElementType.ROLE_LIST, browser, parent);
	}

	@Override
	public TreePath getTreePath() {
		return super.getTreePath();
	}

	@Override
	public Icon getIcon() {
		if (getRoleList().isImportedRoleList() || !isRoot() && getParent().getElementType() == BrowserElementType.ROLE_LIST) {
			if (getRoleList().isImportedRoleList()) {
				return IconFactory.getImageIcon(WKFIconLibrary.IMPORTED_ROLE_LIBRARY_ICON, IconLibrary.WARNING);
			} else {
				return WKFIconLibrary.IMPORTED_ROLE_LIBRARY_ICON;
			}
		} else {
			return super.getIcon();
		}
	}

	@Override
	protected void buildChildrenVector() {
		/*if (getRoleList().getProject().getProjectData() != null) {
			for (FlexoProjectReference ref : getRoleList().getProject().getProjectData().getImportedProjects()) {
				if (ref.getReferredProject() != null && ref.getReferredProject().getFlexoWorkflow(false) != null) {
					addToChilds(ref.getReferredProject().getWorkflow().getRoleList());
				}

			}
		}*/
		// We add the roles
		for (Role role : getRoleList().getSortedRolesVector()) {
			addToChilds(role);
		}
	}

	@Override
	public void delete() {
		super.delete();
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
