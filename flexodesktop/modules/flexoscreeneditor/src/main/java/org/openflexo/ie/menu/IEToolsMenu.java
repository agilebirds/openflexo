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
 * MenuFile.java
 * Project WorkflowEditor
 * 
 * Created by benoit on Mar 10, 2004
 */
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.openflexo.ie.view.controller.IEController;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.menu.FlexoMenuItem;
import org.openflexo.view.menu.ToolsMenu;


/**
 * IE Module tools menu
 * 
 * @author sguerin
 */
public class IEToolsMenu extends ToolsMenu
{

    // ==========================================================================
    // ============================= Instance Variables
    // =========================
    // ==========================================================================

    public CheckLibraryConsistencyItem checkLibraryConsistencyItem;

    public CheckComponentConsistencyItem checkComponentConsistencyItem;
    
    public CheckDKVConsistencyItem checkDKVConsistencyItem;

    protected IEController _controller;

    // ==========================================================================
    // ============================= Constructor
    // ================================
    // ==========================================================================

    public IEToolsMenu(IEController controller)
    {
        super(controller);
        _controller = controller;
    }

    @Override
	public void addSpecificItems()
    {
        add(checkLibraryConsistencyItem = new CheckLibraryConsistencyItem());
        add(checkComponentConsistencyItem = new CheckComponentConsistencyItem());
        add(checkDKVConsistencyItem = new CheckDKVConsistencyItem());
        addSeparator();
    }

    // ==========================================================================
    // ============================= Accessors
    // ==================================
    // ==========================================================================

    public IEController getIEController()
    {
        return _controller;
    }

    // ==========================================================================
    // ============================= CheckLibraryConsistency ===============================
    // ==========================================================================

    public class CheckLibraryConsistencyItem extends FlexoMenuItem
    {

        public CheckLibraryConsistencyItem()
        {
            super(new CheckLibraryConsistencyAction(), "check_component_library_consistency", null, getIEController(), true);
        }

    }

    public class CheckLibraryConsistencyAction extends AbstractAction
    {
        public CheckLibraryConsistencyAction()
        {
            super();
        }

        @Override
		public void actionPerformed(ActionEvent arg0)
        {
            getIEController().consistencyCheck(getIEController().getProject().getFlexoComponentLibrary());
        }

    }

    // ==========================================================================
    // ======================= CheckComponentConsistency =========================
    // ==========================================================================

    public class CheckComponentConsistencyItem extends FlexoMenuItem
    {

        public CheckComponentConsistencyItem()
        {
            super(new CheckComponentConsistencyAction(), "check_component_consistency", null, getIEController(), true);
        }

        @Override
        public void itemWillShow() {
        	setEnabled(getIEController().getCurrentEditedComponent() != null); 
        	super.itemWillShow();
        }
    }

    public class CheckComponentConsistencyAction extends AbstractAction
    {
        public CheckComponentConsistencyAction()
        {
            super();
        }

        @Override
		public void actionPerformed(ActionEvent arg0)
        {
            if (getIEController().getCurrentEditedComponent() != null) {
	            try {
	                getIEController().consistencyCheck(getIEController().getCurrentEditedComponent().getComponentDefinition());
	            } catch (ClassCastException e) {
	                FlexoController.showError(getIEController().getCurrentEditedComponent().getClass() + " is not Validable !");
	
	            }
            }
        }

    }
    
    
    public class CheckDKVConsistencyItem extends FlexoMenuItem
    {

        public CheckDKVConsistencyItem()
        {
            super(new CheckDKVConsistencyAction(), "check_dkv_consistency", null, getIEController(), true);
        }

    }

    public class CheckDKVConsistencyAction extends AbstractAction
    {
        public CheckDKVConsistencyAction()
        {
            super();
        }

        @Override
		public void actionPerformed(ActionEvent arg0)
        {
                getIEController().consistencyCheck(getIEController().getProject().getDKVModel());
        }

    }

}
