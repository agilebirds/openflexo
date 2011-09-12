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
package org.openflexo.cgmodule.menu;

import org.openflexo.cgmodule.controller.GeneratorController;
import org.openflexo.module.Module;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.menu.FileMenu;
import org.openflexo.view.menu.FlexoMenuBar;
import org.openflexo.view.menu.ToolsMenu;


/**
 * @author sguerin
 */
public class GeneratorMenuBar extends FlexoMenuBar
{

    public GeneratorFileMenu _fileMenu;
    
    public GeneratorToolsMenu toolsMenu;

    public GeneratorMenuBar(GeneratorController controller)
    {
        super(controller, Module.CG_MODULE);
     }

    /**
     * Build if required and return CG 'File' menu.
     * This method overrides the default one defined on superclass
     * 
     * @param controller
     * @return a GeneratorFileMenu instance
     */
    @Override
	public FileMenu getFileMenu(FlexoController controller)
    {
        if (_fileMenu == null) {
            _fileMenu = new GeneratorFileMenu((GeneratorController)controller);
        }
        return _fileMenu;
    }

    /**
     * Overrides getToolsMenu
     * @see org.openflexo.view.menu.FlexoMenuBar#getToolsMenu(org.openflexo.view.controller.FlexoController)
     */
    @Override
    public ToolsMenu getToolsMenu(FlexoController controller)
    {
        if (toolsMenu==null)
            toolsMenu = new GeneratorToolsMenu(controller);
        return toolsMenu;
    }

}
