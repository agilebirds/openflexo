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
package org.openflexo.sgmodule.view.menu;

/*
 * Created on <date> by <yourname>
 *
 * Flexo Application Suite
 * (c) Denali 2003-2006
 */
import java.util.logging.Logger;

import org.openflexo.sgmodule.controller.SGController;
import org.openflexo.view.menu.FileMenu;

/**
 * 'File' menu for this Module
 * 
 * @author yourname
 */
public class SGFileMenu extends FileMenu {

	private static final Logger logger = Logger.getLogger(SGFileMenu.class.getPackage().getName());

	// ==========================================================================
	// ============================= Instance Variables
	// =========================
	// ==========================================================================

	protected SGController _sgController;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public SGFileMenu(SGController controller) {
		super(controller);
		_sgController = controller;
		// Put your actions here
	}

	public SGController getSGController() {
		return _sgController;
	}
}
