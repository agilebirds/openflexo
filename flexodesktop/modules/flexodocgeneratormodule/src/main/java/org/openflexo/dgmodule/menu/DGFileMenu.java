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
package org.openflexo.dgmodule.menu;

import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;

import org.openflexo.dgmodule.controller.DGController;
import org.openflexo.doceditor.menu.DEFileMenu;
import org.openflexo.view.menu.FlexoMenuItem;

/**
 * @author gpolet
 */
public class DGFileMenu extends DEFileMenu {

	static final Logger logger = Logger.getLogger(DGFileMenu.class.getPackage().getName());

	public GenerateFilesItem generateFilesItem;

	public RefreshItem refreshItem;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public DGFileMenu(DGController controller) {
		super(controller);
	}

	@Override
	public DGController getGeneratorController() {
		return (DGController) _controller;
	}

	@Override
	public void addSpecificItems() {
		add(generateFilesItem = new GenerateFilesItem());
		add(refreshItem = new RefreshItem());
		addSeparator();
	}

	public class GenerateFilesItem extends FlexoMenuItem {
		public GenerateFilesItem() {
			super(new GenerateFilesAction(), "generate_files", null, getGeneratorController(), true);
		}

	}

	public class GenerateFilesAction extends AbstractAction {
		public GenerateFilesAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			logger.warning("Not implemented yet");
		}
	}

	public class RefreshItem extends FlexoMenuItem {
		public RefreshItem() {
			super(new RefreshAction(), "refresh", null, getGeneratorController(), true);
		}
	}

	public class RefreshAction extends AbstractAction {
		public RefreshAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Refresh not implemented");
				// getGeneratorController().rebuildGeneratorWindow();
			}
		}
	}
}
