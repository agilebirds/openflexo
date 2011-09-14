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

import org.openflexo.dm.view.controller.DMController;
import org.openflexo.module.Module;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.menu.EditMenu;
import org.openflexo.view.menu.FileMenu;
import org.openflexo.view.menu.FlexoMenuBar;
import org.openflexo.view.menu.ToolsMenu;
import org.openflexo.view.menu.WindowMenu;


public class DMMenuBar extends FlexoMenuBar
{

    public DMFileMenu _fileMenu;
    public DMEditMenu _editMenu;
    public DMToolsMenu _toolsMenu;

    public DMMenuBar(DMController controller)
    {
        super(controller, Module.DM_MODULE);
     }

    /**
     * Build if required and return DM 'File' menu.
     * This method overrides the default one defined on superclass
     * 
     * @param controller
     * @return a WKFFileMenu instance
     */
    @Override
	public FileMenu getFileMenu(FlexoController controller)
    {
        if (_fileMenu == null) {
            _fileMenu = new DMFileMenu((DMController)controller);
        }
        return _fileMenu;
    }

    /**
     * Build if required and return DM 'Edit' menu.
     * This method overrides the default one defined on superclass
     * 
     * @param controller
     * @return a WKFEditMenu instance
     */
     @Override
	public EditMenu getEditMenu(FlexoController controller)
    {
        if (_editMenu == null) {
            _editMenu = new DMEditMenu((DMController)controller);
        }
        return _editMenu;
    }

     /**
      * Build if required and return DM 'Windows' menu.
      * This method overrides the default one defined on superclass
      * 
      * @param controller
      * @return a WKFEditMenu instance
      */
      @Override
	public WindowMenu getWindowMenu(FlexoController controller, Module module)
     {
         if (_windowMenu == null) {
             _windowMenu = new DMWindowMenu((DMController)controller,module);
         }
         return _windowMenu;
     }

  /**
    * Build if required and return DM 'Tools' menu.
    * This method overrides the default one defined on superclass
    * 
    * @param controller
    * @return a WKFEditMenu instance
    */
    @Override
	public ToolsMenu getToolsMenu(FlexoController controller)
   {
       if (_toolsMenu == null) {
           _toolsMenu = new DMToolsMenu((DMController)controller);
       }
       return _toolsMenu;
   }


}
