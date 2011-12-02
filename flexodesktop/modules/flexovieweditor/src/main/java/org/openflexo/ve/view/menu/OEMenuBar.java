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
package org.openflexo.ve.view.menu;

/*
 * Created on <date> by <yourname>
 *
 * Flexo Application Suite
 * (c) Denali 2003-2006
 */
import org.openflexo.module.Module;
import org.openflexo.ve.controller.OEController;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.menu.EditMenu;
import org.openflexo.view.menu.FileMenu;
import org.openflexo.view.menu.FlexoMenuBar;
import org.openflexo.view.menu.ToolsMenu;
import org.openflexo.view.menu.WindowMenu;

/**
 * Class representing menus related to WorkflowEditor window
 * 
 * @author benoit, yourname
 */
public class OEMenuBar extends FlexoMenuBar {

	private OEFileMenu _fileMenu;
	private OEEditMenu _editMenu;
	private OEToolsMenu _toolsMenu;

	public OEMenuBar(OEController controller) {
		super(controller, Module.XXX_MODULE);
	}

	/**
	 * Build if required and return WKF 'File' menu. This method overrides the default one defined on superclass
	 * 
	 * @param controller
	 * @return a XXXFileMenu instance
	 */
	@Override
	public FileMenu getFileMenu(FlexoController controller) {
		if (_fileMenu == null) {
			_fileMenu = new OEFileMenu((OEController) controller);
		}
		return _fileMenu;
	}

	/**
	 * Build if required and return WKF 'Edit' menu. This method overrides the default one defined on superclass
	 * 
	 * @param controller
	 * @return a XXXEditMenu instance
	 */
	@Override
	public EditMenu getEditMenu(FlexoController controller) {
		if (_editMenu == null) {
			_editMenu = new OEEditMenu((OEController) controller);
		}
		return _editMenu;
	}

	/**
	 * Build if required and return WKF 'Window' menu. This method overrides the default one defined on superclass
	 * 
	 * @param controller
	 * @return a XXXWindowMenu instance
	 */
	@Override
	public WindowMenu getWindowMenu(FlexoController controller, Module module) {
		if (_windowMenu == null) {
			_windowMenu = new OEWindowMenu((OEController) controller);
		}
		return _windowMenu;
	}

	/**
	 * Build if required and return WKF 'Tools' menu. This method overrides the default one defined on superclass
	 * 
	 * @param controller
	 * @return a XXXToolsMenu instance
	 */
	public ToolsMenu getToolsMenu(FlexoController controller, Module module) {
		if (_toolsMenu == null) {
			_toolsMenu = new OEToolsMenu((OEController) controller);
		}
		return _toolsMenu;
	}

}
