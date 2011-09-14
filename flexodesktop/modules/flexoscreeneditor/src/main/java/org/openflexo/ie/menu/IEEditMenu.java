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

import java.util.logging.Logger;

import javax.swing.KeyStroke;

import org.openflexo.FlexoCst;
import org.openflexo.foundation.ie.action.IECopy;
import org.openflexo.foundation.ie.action.IECut;
import org.openflexo.foundation.ie.action.IEDelete;
import org.openflexo.foundation.ie.action.IEPaste;
import org.openflexo.foundation.ie.action.IESelectAll;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.view.menu.EditMenu;
import org.openflexo.view.menu.FlexoMenuItem;


/**
 * TODO : Description for this file
 * 
 * @author benoit
 */
public class IEEditMenu extends EditMenu
{

    protected static final Logger logger = Logger.getLogger(IEEditMenu.class.getPackage().getName());

    // ==========================================================================
    // ============================= Instance Variables
    // =========================
    // ==========================================================================

     protected IEController _controller;

    // ==========================================================================
    // ============================= Constructor
    // ================================
    // ==========================================================================

    public IEEditMenu(IEController controller)
    {
        super(controller);
        _controller = controller;
         addSeparator();
        add(cutItem = new FlexoMenuItem(IECut.actionType, getController()));
        add(copyItem = new FlexoMenuItem(IECopy.actionType, getController()));
        add(pasteItem = new FlexoMenuItem(IEPaste.actionType, getController()));
        add(deleteItem = new FlexoMenuItem(IEDelete.actionType, getController()));
        deleteItem.getInputMap().put(KeyStroke.getKeyStroke((char)FlexoCst.DELETE_KEY_CODE),"doClick");
        add(selectAllItem = new FlexoMenuItem(IESelectAll.actionType, getController()));
        selectAllItem.setEnabled(false);
    }

    // ==========================================================================
    // ============================= Accessors
    // ==================================
    // ==========================================================================

    public IEController getIEController()
    {
        return _controller;
    }

}