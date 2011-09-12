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
package org.openflexo.dm.view.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.dm.view.menu.DMMenuBar;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.selection.SelectionManager;


/**
 * Selection manager dedicated to Data Model Editor module
 * 
 * @author sguerin
 */
public class DMSelectionManager extends SelectionManager
{

    protected static final Logger logger = Logger.getLogger(DMSelectionManager.class.getPackage().getName());

    public DMSelectionManager(DMController controller)
    {
        super(controller);
        DMMenuBar menuBar = controller.getEditorMenuBar();
        _clipboard = new DMClipboard(this, menuBar.getEditMenu(controller).copyItem, menuBar.getEditMenu(controller).pasteItem, menuBar.getEditMenu(controller).cutItem);
        _contextualMenuManager = new DMContextualMenuManager(this,controller.getEditor());
   }

    public DMController getDMController()
    {
        return (DMController) getController();
    }

    @Override
	public boolean performSelectionSelectAll()
    {
        if (logger.isLoggable(Level.WARNING))
            logger.warning("'Select All' not implemented yet in Interface Editor");
        return false;
    }

    // ==================================================
    // ========= Selection Management public API ========
    // ==================================================
    

    /**
     * Returns the root object that can be currently edited
     * 
     * @return FlexoObservable
     */
    @Override
	public FlexoModelObject getRootFocusedObject()
    {
        return getDMController().getCurrentEditedObject();
    }

    @Override
	public FlexoModelObject getPasteContext() 
    {
        return getFocusedObject();
    }

}
