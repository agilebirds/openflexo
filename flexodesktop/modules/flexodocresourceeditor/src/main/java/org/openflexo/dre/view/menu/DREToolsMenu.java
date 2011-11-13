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
package org.openflexo.dre.view.menu;

/*
 * Created on <date> by <yourname>
 *
 * Flexo Application Suite
 * (c) Denali 2003-2006
 */
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;

import org.openflexo.dre.controller.DREController;
import org.openflexo.view.menu.FlexoMenuItem;
import org.openflexo.view.menu.ToolsMenu;

/**
 * 'Tools' menu for this Module
 * 
 * @author yourname
 */
public class DREToolsMenu extends ToolsMenu {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DREToolsMenu.class.getPackage().getName());

	public DREToolsMenu(DREController controller) {
		super(controller);
	}

	public DREController getDREController() {
		return (DREController) getController();
	}

	@Override
	public void addSpecificItems() {
		add(new CheckDocumentationConsistencyItem());
		addSeparator();
	}

	// ==========================================================================
	// ======================= CheckWorkflowConsistency
	// =========================
	// ==========================================================================

	public class CheckDocumentationConsistencyItem extends FlexoMenuItem {

		public CheckDocumentationConsistencyItem() {
			super(new CheckDocumentationConsistencyAction(), "check_documentation_consistency", null, getDREController(), true);
		}

	}

	public class CheckDocumentationConsistencyAction extends AbstractAction {
		public CheckDocumentationConsistencyAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			getDREController().consistencyCheck(getDREController().getDocResourceManager().getDocResourceCenter());
		}

	}

}
