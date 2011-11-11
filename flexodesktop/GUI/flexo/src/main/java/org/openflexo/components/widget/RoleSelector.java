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
package org.openflexo.components.widget;

import java.awt.Dimension;

import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.Role;

/**
 * Widget allowing to select a Process while browsing the workflow
 * 
 * @author sguerin
 * 
 */
public class RoleSelector extends AbstractBrowserSelector<Role> {

	protected static final String EMPTY_STRING = "";

	public RoleSelector(FlexoProject project, Role role) {
		super(project, role, Role.class);
	}

	public RoleSelector(FlexoProject project, Role role, int cols) {
		super(project, role, Role.class, cols);
	}

	@Override
	protected RoleSelectorPanel makeCustomPanel(Role editedObject) {
		return new RoleSelectorPanel();
	}

	@Override
	public String renderedString(Role editedObject) {
		if (editedObject != null) {
			return (editedObject).getName();
		}
		return EMPTY_STRING;
	}

	protected class RoleSelectorPanel extends AbstractSelectorPanel<Role> {
		protected RoleSelectorPanel() {
			super(RoleSelector.this);
		}

		@Override
		protected ProjectBrowser createBrowser(FlexoProject project) {
			return new RoleBrowser();
		}

		@Override
		public Dimension getDefaultSize() {
			Dimension returned = _browserView.getDefaultSize();
			returned.width = returned.width;
			returned.height = returned.height - 100;
			return returned;
		}
	}

	protected class RoleBrowser extends ProjectBrowser {

		protected RoleBrowser() {
			super(RoleSelector.this.getProject(), false);
			init();
		}

		@Override
		public void configure() {
			for (BrowserElementType browserElementType : BrowserElementType.values())
				setFilterStatus(browserElementType, BrowserFilterStatus.HIDE);

			setFilterStatus(BrowserElementType.WORKFLOW, BrowserFilterStatus.SHOW);
			setFilterStatus(BrowserElementType.ROLE, BrowserFilterStatus.SHOW);
		}

		@Override
		public FlexoModelObject getDefaultRootObject() {
			return getProject();
		}

		@Override
		public boolean showRootNode() {
			return false;
		}
	}

}
