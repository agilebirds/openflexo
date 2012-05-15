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
package org.openflexo.vpm.view.menu;

/*
 * Created on <date> by <yourname>
 *
 * Flexo Application Suite
 * (c) Denali 2003-2006
 */
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;

import org.openflexo.view.menu.FlexoMenuItem;
import org.openflexo.view.menu.ToolsMenu;
import org.openflexo.vpm.controller.VPMController;

/**
 * 'Tools' menu for this Module
 * 
 * @author yourname
 */
public class VPMToolsMenu extends ToolsMenu {

	private static final Logger logger = Logger.getLogger(VPMToolsMenu.class.getPackage().getName());

	// ==========================================================================
	// ============================= Instance Variables
	// =========================
	// ==========================================================================

	public FlexoMenuItem checkViewPointLibraryConsistencyItem;
	public FlexoMenuItem checkViewPointConsistencyItem;

	protected VPMController _vpmController;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public VPMToolsMenu(VPMController controller) {
		super(controller);
		_vpmController = controller;
		// Put your actions here
	}

	public VPMController getVPMController() {
		return _vpmController;
	}

	@Override
	public void addSpecificItems() {
		add(checkViewPointLibraryConsistencyItem = new CheckViewPointLibraryConsistencyItem());
		add(checkViewPointConsistencyItem = new CheckViewPointConsistencyItem());
		addSeparator();
	}

	// ==========================================================================
	// ======================= CheckWorkflowConsistency
	// =========================
	// ==========================================================================

	public class CheckViewPointLibraryConsistencyItem extends FlexoMenuItem {

		public CheckViewPointLibraryConsistencyItem() {
			super(new CheckViewPointLibraryConsistencyAction(), "check_all_viewpoint_consistency", null, getVPMController(), true);
		}

	}

	public class CheckViewPointLibraryConsistencyAction extends AbstractAction {
		public CheckViewPointLibraryConsistencyAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			logger.info("Check consistency for " + getVPMController().getViewPointLibrary());
			getVPMController().consistencyCheck(getVPMController().getViewPointLibrary());
		}

	}

	// ==========================================================================
	// ======================= CheckProcessConsistency =========================
	// ==========================================================================

	public class CheckViewPointConsistencyItem extends FlexoMenuItem {

		public CheckViewPointConsistencyItem() {
			super(new CheckViewPointConsistencyAction(), "check_viewpoint_consistency", null, getVPMController(), true);
		}

	}

	public class CheckViewPointConsistencyAction extends AbstractAction {
		public CheckViewPointConsistencyAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (getVPMController().getCurrentViewPoint() != null) {
				logger.info("Check consistency for " + getVPMController().getCurrentViewPoint());
				getVPMController().consistencyCheck(getVPMController().getCurrentViewPoint());
			}
		}

	}

}
