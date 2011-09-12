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
package org.openflexo.ie.view.controller;

import java.util.logging.Logger;

import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ie.cl.ComponentDefinition;


/**
 * Browser for WKF module
 * 
 * @author sguerin
 * 
 */
public class ComponentBrowser extends ProjectBrowser
{

    protected static final Logger logger = Logger.getLogger(ComponentBrowser.class
            .getPackage().getName());

    // ==========================================================================
    // ============================= Variables
    // ==================================
    // ==========================================================================

    protected IEController _controller;

    protected ComponentDefinition _currentComponent;

    // ==========================================================================
    // ============================= Constructor
    // ================================
    // ==========================================================================

    public ComponentBrowser(IEController controller)
    {
        super(controller.getEditor(), controller.getIESelectionManager());
        _controller = controller;
        update();
    }

    @Override
	public void configure()
    {
        setFilterStatus(BrowserElementType.REUSABLE_COMPONENT, BrowserFilterStatus.SHOW);
        setFilterStatus(BrowserElementType.COMPONENT_FOLDER, BrowserFilterStatus.SHOW);
        setFilterStatus(BrowserElementType.TAB_COMPONENT, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
        setFilterStatus(BrowserElementType.BLOC, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN,true);
        setFilterStatus(BrowserElementType.LABEL, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
        setFilterStatus(BrowserElementType.STRING, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
        setFilterStatus(BrowserElementType.HYPERLINK, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
        setFilterStatus(BrowserElementType.DROPDOWN, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
        setFilterStatus(BrowserElementType.BUTTON, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
        setFilterStatus(BrowserElementType.TEXTFIELD, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
        setFilterStatus(BrowserElementType.TEXTAREA, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
    }

     @Override
	public FlexoModelObject getDefaultRootObject()
    {
        return _currentComponent;
    }

    public ComponentDefinition getCurrentComponent()
    {
        return _currentComponent;
    }

    public void setCurrentComponent(ComponentDefinition component)
    {
        _currentComponent = component;
        update();
    }


}
