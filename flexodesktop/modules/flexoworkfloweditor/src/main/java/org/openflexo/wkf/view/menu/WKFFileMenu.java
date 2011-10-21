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
import java.awt.event.ActionEvent;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.AbstractAction;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.imported.action.ImportProcessesAction;
import org.openflexo.foundation.imported.action.ImportRolesAction;
import org.openflexo.icon.IconLibrary;
import org.openflexo.view.menu.FileMenu;
import org.openflexo.view.menu.FlexoMenuItem;
import org.openflexo.wkf.controller.WKFController;
import org.openflexo.wkf.view.print.PrintProcessAction;


/**
 * 'File' menu for WKF Module
 * 
 * @author benoit
 */
public class WKFFileMenu extends FileMenu
{

    private static final Logger logger = Logger.getLogger(WKFFileMenu.class.getPackage().getName());

    // ==========================================================================
    // ============================= Instance Variables
    // =========================
    // ==========================================================================

    protected WKFController _wkfController;
    public ImportRoleItem importRoleItem;
    public ImportProcessItem importProcessItem;
    // ==========================================================================
    // ============================= Constructor
    // ================================
    // ==========================================================================

    public WKFFileMenu(WKFController controller)
    {
        super(controller);
        _wkfController = controller;
      }

    public WKFController getWKFController()
    {
        return _wkfController;
    }


    @Override
	public void addPrintItems()
    {
        add(new FlexoMenuItem(PrintProcessAction.actionType, IconLibrary.PRINT_ICON, getController()) {
            @Override
			public FlexoModelObject getFocusedObject() 
            {
                return _wkfController.getCurrentFlexoProcess();
            }           
            @Override
			public Vector getGlobalSelection() 
            {
                return null;
            }
        });
    }
    
    @Override
    protected boolean addImportItems() {
    	super.addImportItems();
    	addToImportItems(importRoleItem = new ImportRoleItem());
    	addToImportItems(importProcessItem = new ImportProcessItem());
    	return true;
    }
    
    public class ImportRoleItem extends FlexoMenuItem
    {
        public ImportRoleItem()
        {
            super(new ImportRoleAction(), "import_roles_from_ppm", null, getController(), true);
        }
    }

    public class ImportRoleAction extends AbstractAction
    {
        public ImportRoleAction()
        {
            super();
        }

        @Override
		public void actionPerformed(ActionEvent arg0)
        {
        	ImportRolesAction importRoles = ImportRolesAction.actionType.makeNewAction(null, null,getController().getEditor());
        	importRoles.doAction();
        }
    }
    
    public class ImportProcessItem extends FlexoMenuItem
    {
        public ImportProcessItem()
        {
            super(new ImportProcessAction(), "import_processes_from_ppm", null, getController(), true);
        }
    }

    public class ImportProcessAction extends AbstractAction
    {
        public ImportProcessAction()
        {
            super();
        }

        @Override
		public void actionPerformed(ActionEvent arg0)
        {
        	ImportProcessesAction importRoles = ImportProcessesAction.actionType.makeNewAction(null, null,getController().getEditor());
        	importRoles.doAction();
        }
    }
 
}
