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
package org.openflexo.dm.view.menu;

/*
 * DMMenuEdit.java
 * Project DataModelEditor
 * 
 * Created by sylvain on Aug 9, 2005
 */
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import org.openflexo.FlexoCst;
import org.openflexo.dm.view.controller.DMController;
import org.openflexo.foundation.dm.action.DMCopy;
import org.openflexo.foundation.dm.action.DMCut;
import org.openflexo.foundation.dm.action.DMDelete;
import org.openflexo.foundation.dm.action.DMPaste;
import org.openflexo.foundation.dm.action.DMSelectAll;
import org.openflexo.view.menu.EditMenu;
import org.openflexo.view.menu.FlexoMenuItem;

/**
 * Menu edit for DM module
 * 
 * @author sylvain
 */
public class DMEditMenu extends EditMenu {

	protected static final Logger logger = Logger.getLogger(DMEditMenu.class.getPackage().getName());

	// ==========================================================================
	// ============================= Instance Variables
	// =========================
	// ==========================================================================

	protected DMController _controller;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public DMEditMenu(DMController controller) {
		super(controller);
		_controller = controller;
		add(cutItem = new FlexoMenuItem(DMCut.actionType, getController()));
		add(copyItem = new FlexoMenuItem(DMCopy.actionType, getController()));
		add(pasteItem = new FlexoMenuItem(DMPaste.actionType, getController()));
		add(deleteItem = new FlexoMenuItem(DMDelete.actionType, getController()));
		deleteItem.getInputMap().put(KeyStroke.getKeyStroke((char) FlexoCst.DELETE_KEY_CODE), "doClick");
		add(selectAllItem = new FlexoMenuItem(DMSelectAll.actionType, getController()));
	}

	// ==========================================================================
	// ============================= Accessors
	// ==================================
	// ==========================================================================

	public DMController getDMController() {
		return _controller;
	}

}
