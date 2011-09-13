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
package org.openflexo.wkf.view.menu;

/*
 * MenuFile.java
 * Project WorkflowEditor
 * 
 * Created by benoit on Mar 10, 2004
 */
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import org.openflexo.FlexoCst;
import org.openflexo.foundation.wkf.action.WKFCopy;
import org.openflexo.foundation.wkf.action.WKFCut;
import org.openflexo.foundation.wkf.action.WKFDelete;
import org.openflexo.foundation.wkf.action.WKFPaste;
import org.openflexo.foundation.wkf.action.WKFSelectAll;
import org.openflexo.view.menu.EditMenu;
import org.openflexo.view.menu.FlexoMenuItem;
import org.openflexo.wkf.controller.WKFController;


/**
 * 'Edit' menu
 * 
 * @author sguerin
 */
public class WKFEditMenu extends EditMenu
{

    private static final Logger logger = Logger.getLogger(WKFEditMenu.class.getPackage().getName());

    // ==========================================================================
    // ============================= Instance Variables
    // =========================
    // ==========================================================================

    protected WKFController _wkfController;

    // ==========================================================================
    // ============================= Constructor
    // ================================
    // ==========================================================================

    public WKFEditMenu(WKFController controller)
    {
        super(controller);
        _wkfController = controller;
        //addSeparator();
        add(cutItem = new FlexoMenuItem(WKFCut.actionType, getController()));
        add(copyItem = new FlexoMenuItem(WKFCopy.actionType, getController()));
        add(pasteItem = new FlexoMenuItem(WKFPaste.actionType, getController()));
        add(deleteItem = new FlexoMenuItem(WKFDelete.actionType, getController()));
        deleteItem.getInputMap().put(KeyStroke.getKeyStroke((char)FlexoCst.DELETE_KEY_CODE),"doClick");
        add(selectAllItem = new FlexoMenuItem(WKFSelectAll.actionType, getController()));
    }

}
