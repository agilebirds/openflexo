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
package org.openflexo.dre.view;

import java.awt.BorderLayout;
import java.util.logging.Logger;

import org.openflexo.components.AskParametersPanel;
import org.openflexo.components.browser.view.BrowserView;
import org.openflexo.dre.controller.DREController;
import org.openflexo.foundation.FlexoModelObject;


/**
 * Represents the view for the browser of this module
 * 
 * @author yourname
 * 
 */
public class DREBrowserView extends BrowserView
{

    @SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DREBrowserView.class.getPackage().getName());

    // ==========================================================================
    // ============================= Variables
    // ==================================
    // ==========================================================================

    protected DREController _controller;

    // ==========================================================================
    // ============================= Constructor
    // ================================
    // ==========================================================================

    public DREBrowserView(DREController controller)
    {
        super(controller.getDREBrowser(), controller.getKeyEventListener(),controller.getEditor());
        _controller = controller;
        AskParametersPanel languageSelector = new AskParametersPanel(null,controller.getDREBrowser().getAvailableLanguages());
        add(languageSelector,BorderLayout.NORTH);
    }

    @Override
	public void treeSingleClick(FlexoModelObject object)
    {
        // Try to display object in view
        _controller.selectAndFocusObject(object);
    }

    @Override
	public void treeDoubleClick(FlexoModelObject object)
    {
        // Try to display object in view
        _controller.selectAndFocusObject(object);
    }

}
