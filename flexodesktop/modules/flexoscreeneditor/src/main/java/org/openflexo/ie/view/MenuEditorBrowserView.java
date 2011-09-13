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
package org.openflexo.ie.view;


import org.openflexo.ch.FCH;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.dnd.TreeDropTarget;
import org.openflexo.components.browser.view.BrowserView;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ie.menu.FlexoItemMenu;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.ie.view.controller.dnd.MenuTreeDropTarget;


public class MenuEditorBrowserView extends BrowserView
{

    // ==========================================================================
    // ============================= Variables
    // ==================================
    // ==========================================================================

    protected IEController _controller;

    // ==========================================================================
    // ============================= Constructor
    // ================================
    // ==========================================================================

    public MenuEditorBrowserView(IEController controller)
    {
        super(controller.getMenuEditorBrowser(), controller.getKeyEventListener(), controller.getEditor());
        _controller = controller;
        FCH.setHelpItem(this,"menu-browser");
   }

    @Override
    protected TreeDropTarget createTreeDropTarget(FlexoJTree treeView2,
    		ProjectBrowser _browser2) {
    	return new MenuTreeDropTarget(treeView2, _browser2);
    }
    
    @Override
	public void treeSingleClick(FlexoModelObject object)
    {

        if (object instanceof FlexoItemMenu) {
            _controller.setSelectedMenu((FlexoItemMenu) object);
        }
        // If this object is inspectable, inspect it.
        /*if (object instanceof InspectableObject) {
            _controller.setCurrentInspectedObject((InspectableObject) object);
        }*/

    }

    @Override
	public void treeDoubleClick(FlexoModelObject object)
    {
        if (object instanceof FlexoItemMenu) {
            _controller.setSelectedMenu((FlexoItemMenu) object);
        }
    }

}
