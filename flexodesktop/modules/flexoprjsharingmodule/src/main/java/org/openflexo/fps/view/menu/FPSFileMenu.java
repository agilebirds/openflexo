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
package org.openflexo.fps.view.menu;

/*
 * Created on <date> by <yourname>
 *
 * Flexo Application Suite
 * (c) Denali 2003-2006
 */
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Enumeration;

import javax.swing.AbstractAction;
import javax.swing.JMenu;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.fps.FPSPreferences;
import org.openflexo.fps.action.OpenSharedProject;
import org.openflexo.fps.action.ShareProject;
import org.openflexo.fps.controller.FPSController;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.menu.FileMenu;
import org.openflexo.view.menu.FlexoMenuItem;

/**
 * 'File' menu for this Module
 * 
 * @author yourname
 */
public class FPSFileMenu extends FileMenu {

	// ==========================================================================
	// ============================= Instance Variables
	// =========================
	// ==========================================================================

	protected FPSController _fpsController;
	private JMenu fpsRecentProjectItem;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public FPSFileMenu(FPSController controller) {
		super(controller, false);
		_fpsController = controller;
	}

	public FPSController getFPSController() {
		return _fpsController;
	}

	@Override
	public void addSpecificItems() {
		add(new FlexoMenuItem(ShareProject.actionType, getController()) {
			@Override
			public FlexoObject getFocusedObject() {
				return _fpsController.getRepositories();
			}
		});
		add(new FlexoMenuItem(OpenSharedProject.actionType, getController()) {
			@Override
			public FlexoObject getFocusedObject() {
				return _fpsController.getRepositories();
			}
		});

		add(fpsRecentProjectItem = new JMenu());
		fpsRecentProjectItem.setText(FlexoLocalization.localizedForKey("recent_projects", fpsRecentProjectItem));
		Enumeration<File> en = FPSPreferences.getLastOpenedProjects().elements();
		while (en.hasMoreElements()) {
			File f = en.nextElement();
			fpsRecentProjectItem.add(new RecentSharedProjectItem(f));
		}

		addSeparator();
	}

	public class RecentSharedProjectItem extends FlexoMenuItem {
		public RecentSharedProjectItem(File project) {
			super(new RecentSharedProjectAction(project), project.getName() + " - " + project.getParentFile().getAbsolutePath(), null,
					getController(), false);
		}
	}

	public class RecentSharedProjectAction extends AbstractAction {
		private File projectDirectory;

		public RecentSharedProjectAction(File projectDirectory) {
			super();
			this.projectDirectory = projectDirectory;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			OpenSharedProject newAction = OpenSharedProject.actionType.makeNewAction(((FPSController) getController()).getRepositories(),
					null, getController().getEditor());
			newAction.setProjectDirectory(projectDirectory);
			newAction.doAction();
		}
	}

}
