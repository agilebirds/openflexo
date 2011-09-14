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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.openflexo.dm.view.controller.DMController;
import org.openflexo.view.menu.FlexoMenuItem;
import org.openflexo.view.menu.ToolsMenu;


public class DMToolsMenu extends ToolsMenu
{

    public CheckDataModelConsistencyItem checkDataModelItem;

    protected DMController _controller;

    public DMToolsMenu(DMController controller)
    {
        super(controller);
        _controller = controller;
 
    }

    public DMController getDMController()
    {
        return _controller;
    }

    @Override
	public void addSpecificItems()
    {
        add(checkDataModelItem = new CheckDataModelConsistencyItem());
        addSeparator();
    }
    
    public class CheckDataModelConsistencyItem extends FlexoMenuItem
    {

        public CheckDataModelConsistencyItem()
        {
            super(new CheckDMConsistencyAction(), "check_datamodel_consistency", null, getDMController(), true);
        }

    }

    public class CheckDMConsistencyAction extends AbstractAction
    {
        public CheckDMConsistencyAction()
        {
            super();
        }

        @Override
		public void actionPerformed(ActionEvent event)
        {
            getDMController().consistencyCheck(getDMController().getDataModel());
        }

    }
}
