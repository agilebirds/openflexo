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

import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.foundation.FlexoModelObject;



/**
 * Browser for DM module
 * 
 * @author sguerin
 * 
 */
public class DMBrowser extends ProjectBrowser
{
	
    protected DMController _controller;

    public DMBrowser(DMController controller)
    {
        this(controller,true);
    }

    public DMBrowser(DMController controller, boolean syncWithSelectionManager)
    {
        super(controller.getEditor(), (syncWithSelectionManager ? controller.getDMSelectionManager() : null));
        _controller = controller;
    }

    @Override
	public void configure()
    {
    	setFilterStatus(BrowserElementType.JDK_REPOSITORY,BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
    	setFilterStatus(BrowserElementType.WO_REPOSITORY,BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
    	setFilterStatus(BrowserElementType.EXTERNAL_REPOSITORY,BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
    	setFilterStatus(BrowserElementType.DM_EOPROTOTYPES_REPOSITORY,BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
    	setFilterStatus(BrowserElementType.DM_EXECUTION_MODEL_REPOSITORY,BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
    }

    @Override
	public FlexoModelObject getDefaultRootObject()
    {
        if (_controller != null) {
        	if (getDMViewMode() == DMViewMode.Repositories)
        		return _controller.getDataModel();
        	else if (getDMViewMode() == DMViewMode.Packages)
        		return _controller.getDataModel();
        	else if (getDMViewMode() == DMViewMode.Diagrams)
        		return _controller.getDataModel();
        	else if (getDMViewMode() == DMViewMode.Hierarchy)
        		return _controller.getDataModel().getEntityNamed("java.lang.Object");
        }
        return null;
    }

}
