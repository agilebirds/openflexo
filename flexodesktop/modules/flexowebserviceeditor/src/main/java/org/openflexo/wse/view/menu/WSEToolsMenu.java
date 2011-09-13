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
package org.openflexo.wse.view.menu;

/*
 * Created in March 06 by Denis VANVYVE
 *
 * Flexo Application Suite
 * (c) Denali 2003-2006
 */
import java.util.logging.Logger;

import org.openflexo.view.menu.ToolsMenu;
import org.openflexo.wse.controller.WSEController;


/**
 * 'Tools' menu for this Module
 * 
 * @author yourname
 */
public class WSEToolsMenu extends ToolsMenu
{

    private static final Logger logger = Logger.getLogger(WSEToolsMenu.class.getPackage().getName());

    // ==========================================================================
    // ============================= Instance Variables
    // =========================
    // ==========================================================================

      protected WSEController _wseController;

    // ==========================================================================
    // ============================= Constructor
    // ================================
    // ==========================================================================

    public WSEToolsMenu(WSEController controller)
    {
        super(controller);
        _wseController = controller;
        // Put your actions here
    }

    public WSEController getWSEController()
    {
        return _wseController;
    }
}
