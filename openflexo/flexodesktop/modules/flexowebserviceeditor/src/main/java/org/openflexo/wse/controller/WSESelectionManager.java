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
package org.openflexo.wse.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.selection.SelectionManager;
import org.openflexo.wse.view.menu.WSEMenuBar;


/**
 * Selection manager dedicated to this module
 * 
 * Note that we don't select the data, but the views representing data
 * This will be refactored, for FlexoModelObject instances to be managed,
 * instead of views (bad design)
 * 
 * @author yourname
 */
public class WSESelectionManager extends SelectionManager
{

    protected static final Logger logger = Logger.getLogger(WSESelectionManager.class.getPackage().getName());

    public WSESelectionManager(WSEController controller)
    {
        super(controller);
        WSEMenuBar menuBar = controller.getEditorMenuBar();
        _clipboard = new WSEClipboard(this, menuBar.getEditMenu(controller).copyItem, menuBar.getEditMenu(controller).pasteItem, menuBar.getEditMenu(controller).cutItem);
        _contextualMenuManager = new WSEContextualMenuManager(this,controller.getEditor(),controller);
   }

    public WSEController getWSEController()
    {
        return (WSEController) getController();
    }

    @Override
	public boolean performSelectionSelectAll()
    {
        if (logger.isLoggable(Level.WARNING))
            logger.warning("'Select All' not implemented yet in this module");
        return false;
    }

    // ==================================================
    // ========= Selection Management public API ========
    // ==================================================
    


    // ==========================================================================
    // ============================= Deletion
    // ===================================
    // ==========================================================================


    /**
     * Returns the root object that can be currently edited
     * 
     * @return FlexoModelObject
     */
    @Override
	public FlexoModelObject getRootFocusedObject()
    {
        return getWSEController().getCurrentDisplayedObjectAsModuleView();
    }

    @Override
	public FlexoModelObject getPasteContext()
    {
        // TODO please implement this
        return null;
    }
   
 }
