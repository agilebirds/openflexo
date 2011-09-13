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
package org.openflexo.ie.menu;

/*
 * EditorMenuBar.java
 * Project WorkflowEditor
 * 
 * Created by benoit on Mar 10, 2004
 */

import org.openflexo.ie.view.controller.IEController;
import org.openflexo.module.Module;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.menu.EditMenu;
import org.openflexo.view.menu.FileMenu;
import org.openflexo.view.menu.FlexoMenuBar;
import org.openflexo.view.menu.ToolsMenu;


/**
 * TODO : Description for this file
 * 
 * @author sguerin
 */
public class IEMenuBar extends FlexoMenuBar
{

    public IEFileMenu _fileMenu;
    public IEEditMenu _editMenu;
    public IEToolsMenu _toolsMenu;

    public IEMenuBar(IEController controller)
    {
        super(controller, Module.IE_MODULE);
    }
    
    /**
     * Build if required and return IE 'File' menu.
     * This method overrides the default one defined on superclass
     * 
     * @param controller
     * @return a IEFileMenu instance
     */
    @Override
	public FileMenu getFileMenu(FlexoController controller)
    {
        if (_fileMenu == null) {
            _fileMenu = new IEFileMenu((IEController)controller);
        }
        return _fileMenu;
    }

    /**
     * Build if required and return IE 'Edit' menu.
     * This method overrides the default one defined on superclass
     * 
     * @param controller
     * @return a IEEditMenu instance
     */
     @Override
	public EditMenu getEditMenu(FlexoController controller)
    {
        if (_editMenu == null) {
            _editMenu = new IEEditMenu((IEController)controller);
        }
        return _editMenu;
    }

     /**
      * Build if required and return IE 'Tools' menu.
      * This method overrides the default one defined on superclass
      * 
      * @param controller
      * @return a IEToolsMenu instance
      */
      @Override
	public ToolsMenu getToolsMenu(FlexoController controller)
     {
         if (_toolsMenu == null) {
             _toolsMenu = new IEToolsMenu((IEController)controller);
         }
         return _toolsMenu;
     }


 
}
