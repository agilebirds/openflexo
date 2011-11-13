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
package org.openflexo.wkf.view.menu;

/*
 * MenuFile.java
 * Project WorkflowEditor
 * 
 * Created by benoit on Mar 10, 2004
 */
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;

import org.openflexo.view.menu.FlexoMenuItem;
import org.openflexo.view.menu.ToolsMenu;
import org.openflexo.wkf.controller.WKFController;

/**
 * 'Tools' menu for WKF Module
 * 
 * @author sguerin
 */
public class WKFToolsMenu extends ToolsMenu {

	private static final Logger logger = Logger.getLogger(WKFToolsMenu.class.getPackage().getName());

	// ==========================================================================
	// ============================= Instance Variables
	// =========================
	// ==========================================================================

	public FlexoMenuItem checkWorkflowConsistencyItem;

	public FlexoMenuItem checkProcessConsistencyItem;

	protected WKFController _wkfController;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public WKFToolsMenu(WKFController controller) {
		super(controller);
		_wkfController = controller;
	}

	public WKFController getWKFController() {
		return _wkfController;
	}

	@Override
	public void addSpecificItems() {
		add(checkWorkflowConsistencyItem = new CheckWorkflowConsistencyItem());
		add(checkProcessConsistencyItem = new CheckProcessConsistencyItem());
		addSeparator();
	}

	// ==========================================================================
	// ======================= CheckWorkflowConsistency
	// =========================
	// ==========================================================================

	public class CheckWorkflowConsistencyItem extends FlexoMenuItem {

		public CheckWorkflowConsistencyItem() {
			super(new CheckWorkflowConsistencyAction(), "check_workflow_consistency", null, getWKFController(), true);
		}

	}

	public class CheckWorkflowConsistencyAction extends AbstractAction {
		public CheckWorkflowConsistencyAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			getWKFController().consistencyCheck(getWKFController().getProject().getFlexoWorkflow());
		}

	}

	// ==========================================================================
	// ======================= CheckProcessConsistency =========================
	// ==========================================================================

	public class CheckProcessConsistencyItem extends FlexoMenuItem {

		public CheckProcessConsistencyItem() {
			super(new CheckProcessConsistencyAction(), "check_process_consistency", null, getWKFController(), true);
		}

	}

	public class CheckProcessConsistencyAction extends AbstractAction {
		public CheckProcessConsistencyAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			getWKFController().consistencyCheck(getWKFController().getCurrentFlexoProcess());
		}

	}

}
