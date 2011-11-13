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

import org.openflexo.dgmodule.controller.DGController;
import org.openflexo.doceditor.menu.DEMenuBar;
import org.openflexo.module.Module;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.menu.FileMenu;

/**
 * @author gpolet
 */
public class DGMenuBar extends DEMenuBar {

	public DGFileMenu _fileMenu;

	public DGMenuBar(DGController controller) {
		super(controller, Module.DG_MODULE);
	}

	/**
	 * Build if required and return CG 'File' menu. This method overrides the default one defined on superclass
	 * 
	 * @param controller
	 * @return a GeneratorFileMenu instance
	 */
	@Override
	public FileMenu getFileMenu(FlexoController controller) {
		if (_fileMenu == null) {
			_fileMenu = new DGFileMenu((DGController) controller);
		}
		return _fileMenu;
	}

}
