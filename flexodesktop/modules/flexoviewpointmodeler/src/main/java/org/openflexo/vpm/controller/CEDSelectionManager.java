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
package org.openflexo.vpm.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.selection.SelectionManager;
import org.openflexo.vpm.view.menu.CEDMenuBar;


/**
 * Selection manager dedicated to this module
 */
public class CEDSelectionManager extends SelectionManager
{

    protected static final Logger logger = Logger.getLogger(CEDSelectionManager.class.getPackage().getName());

    public CEDSelectionManager(CEDController controller)
    {
        super(controller);
        CEDMenuBar menuBar = controller.getEditorMenuBar();
        _clipboard = new CEDClipboard(this, menuBar.getEditMenu(controller).copyItem, menuBar.getEditMenu(controller).pasteItem, menuBar.getEditMenu(controller).cutItem);
        _contextualMenuManager = new CEDContextualMenuManager(this,controller.getEditor(),controller);
   }

    public CEDController getCEDController()
    {
        return (CEDController) getController();
    }

    @Override
	public boolean performSelectionSelectAll()
    {
        if (logger.isLoggable(Level.WARNING))
            logger.warning("'Select All' not implemented yet in this module");
        return false;
    }

    /**
     * Returns the root object that can be currently edited
     * 
     * @return FlexoModelObject
     */
    @Override
	public FlexoModelObject getRootFocusedObject()
    {
        return getCEDController().getCurrentDisplayedObjectAsModuleView();
    }

    @Override
	public FlexoModelObject getPasteContext()
    {
        // TODO please implement this
        return null;
    }

}
