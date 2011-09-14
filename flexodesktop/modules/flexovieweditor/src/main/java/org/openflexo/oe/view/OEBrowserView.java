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
package org.openflexo.oe.view;

import java.util.logging.Logger;

import org.openflexo.components.browser.view.BrowserView;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.oe.controller.OEBrowser;
import org.openflexo.oe.controller.OEController;


/**
 * Represents the view for the browser of this module
 * 
 * @author yourname
 * 
 */
public class OEBrowserView extends BrowserView
{

    private static final Logger logger = Logger.getLogger(OEBrowserView.class.getPackage().getName());

    // ==========================================================================
    // ============================= Variables
    // ==================================
    // ==========================================================================

    protected OEController _controller;

    // ==========================================================================
    // ============================= Constructor
    // ================================
    // ==========================================================================

    public OEBrowserView(OEBrowser browser, OEController controller, SelectionPolicy selectionPolicy)
    {
        super(browser, controller.getKeyEventListener(), controller.getEditor(), selectionPolicy);
        _controller = controller;
    }

    @Override
	public void treeSingleClick(FlexoModelObject object)
    {
    }

    @Override
	public void treeDoubleClick(FlexoModelObject object)
    {
    	if (_controller.getCurrentPerspective().hasModuleViewForObject(object)) {
    		// Try to display object in view
    		_controller.selectAndFocusObject(object);
    	}
    }

}
