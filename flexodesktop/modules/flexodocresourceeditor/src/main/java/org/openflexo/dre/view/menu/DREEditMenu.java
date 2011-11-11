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
import java.util.logging.Logger;

import org.openflexo.dre.controller.DREController;
import org.openflexo.view.menu.EditMenu;

/**
 * 'Edit' menu for this module
 * 
 * @author yourname
 */
public class DREEditMenu extends EditMenu {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DREEditMenu.class.getPackage().getName());

	public DREEditMenu(DREController controller) {
		super(controller);
		addSeparator();
		// Add actions here
		// add(deleteItem = new FlexoMenuItem(DREDelete.actionType, getController()));
		// add(cutItem = new FlexoMenuItem(DRECut.actionType, getController()));
		// add(copyItem = new FlexoMenuItem(DRECopy.actionType, getController()));
		// add(pasteItem = new FlexoMenuItem(DREPaste.actionType, getController()));
		// add(selectAllItem = new FlexoMenuItem(DRESelectAll.actionType, getController()));
	}

}
