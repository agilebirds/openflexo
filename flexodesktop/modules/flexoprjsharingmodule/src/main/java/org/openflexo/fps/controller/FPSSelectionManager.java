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
package org.openflexo.fps.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.fps.view.menu.FPSMenuBar;
import org.openflexo.selection.SelectionManager;

/**
 * Selection manager dedicated to this module
 * 
 * @author yourname
 */
public class FPSSelectionManager extends SelectionManager {

	protected static final Logger logger = Logger.getLogger(FPSSelectionManager.class.getPackage().getName());

	public FPSSelectionManager(FPSController controller) {
		super(controller);
		FPSMenuBar menuBar = controller.getEditorMenuBar();
		_clipboard = new FPSClipboard(this, menuBar.getEditMenu(controller).copyItem, menuBar.getEditMenu(controller).pasteItem,
				menuBar.getEditMenu(controller).cutItem);
		_contextualMenuManager = new FPSContextualMenuManager(this, controller.getEditor(), controller);
	}

	public FPSController getFPSController() {
		return (FPSController) getController();
	}

	@Override
	public boolean performSelectionSelectAll() {
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("'Select All' not implemented yet in this module");
		}
		return false;
	}

	// ==========================================================================
	// ============================= Deletion
	// ===================================
	// ==========================================================================

	/**
	 * Returns the root object that can be currently edited
	 * 
	 * @return FlexoModelObject
	 */
	@Override
	public FlexoModelObject getRootFocusedObject() {
		return getFPSController().getCurrentDisplayedObjectAsModuleView();
	}

	@Override
	public FlexoModelObject getPasteContext() {
		// TODO please implement this
		return null;
	}

}
