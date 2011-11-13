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

import org.openflexo.module.Module;
import org.openflexo.sgmodule.controller.SGController;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.menu.FlexoMenuBar;
import org.openflexo.view.menu.ToolsMenu;
import org.openflexo.view.menu.WindowMenu;

/**
 * Class representing menus related to this module
 * 
 * @author sylvain
 */
public class SGMenuBar extends FlexoMenuBar {

	private SGFileMenu _fileMenu;
	private SGEditMenu _editMenu;
	private SGToolsMenu _toolsMenu;

	// private SGWindowMenu _windowMenu;

	public SGMenuBar(SGController controller) {
		super(controller, Module.SG_MODULE);
	}

	/**
	 * Build if required and return SG 'File' menu. This method overrides the default one defined on superclass
	 * 
	 * @param controller
	 * @return a SGFileMenu instance
	 */
	@Override
	public SGFileMenu getFileMenu(FlexoController controller) {
		if (_fileMenu == null) {
			_fileMenu = new SGFileMenu((SGController) controller);
		}
		return _fileMenu;
	}

	/**
	 * Build if required and return SG 'Edit' menu. This method overrides the default one defined on superclass
	 * 
	 * @param controller
	 * @return a SGEditMenu instance
	 */
	@Override
	public SGEditMenu getEditMenu(FlexoController controller) {
		if (_editMenu == null) {
			_editMenu = new SGEditMenu((SGController) controller);
		}
		return _editMenu;
	}

	/**
	 * Build if required and return SG 'Window' menu. This method overrides the default one defined on superclass
	 * 
	 * @param controller
	 * @return a SGWindowMenu instance
	 */
	@Override
	public WindowMenu getWindowMenu(FlexoController controller, Module module) {
		if (_windowMenu == null) {
			_windowMenu = new SGWindowMenu((SGController) controller);
		}
		return _windowMenu;
	}

	/**
	 * Build if required and return SG 'Tools' menu. This method overrides the default one defined on superclass
	 * 
	 * @param controller
	 * @return a SGToolsMenu instance
	 */
	public ToolsMenu getToolsMenu(FlexoController controller, Module module) {
		if (_toolsMenu == null) {
			_toolsMenu = new SGToolsMenu((SGController) controller);
		}
		return _toolsMenu;
	}

}
