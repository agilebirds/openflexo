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
package org.openflexo.dre.view.menu;

/*
 * Created on <date> by <yourname>
 *
 * Flexo Application Suite
 * (c) Denali 2003-2006
 */
import java.util.logging.Logger;

import org.openflexo.dre.controller.DREController;
import org.openflexo.drm.action.GenerateHelpSet;
import org.openflexo.drm.action.ImportDocSubmissionReport;
import org.openflexo.drm.action.SaveDocumentationCenter;
import org.openflexo.view.menu.FileMenu;
import org.openflexo.view.menu.FlexoMenuItem;


/**
 * 'File' menu for this Module
 * 
 * @author yourname
 */
public class DREFileMenu extends FileMenu
{

    @SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DREFileMenu.class.getPackage().getName());


    public DREFileMenu(DREController controller)
    {
        super(controller);
    }

    @Override
	public void addSpecificItems()
    {
        add(new FlexoMenuItem(SaveDocumentationCenter.actionType, getController()));
        add(new FlexoMenuItem(GenerateHelpSet.actionType, getController()));
        add(new FlexoMenuItem(ImportDocSubmissionReport.actionType, getController()));
        addSeparator();
   }

    public DREController getDREController()
    {
        return (DREController)getController();
    }
}
