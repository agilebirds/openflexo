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
package org.openflexo.dm.view;

import org.openflexo.ch.FCH;
import org.openflexo.components.browser.view.BrowserView;
import org.openflexo.dm.view.controller.DMBrowser;
import org.openflexo.dm.view.controller.DMController;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dm.DMObject;


/**
 * Represents the view for DM Browser
 * 
 * @author sguerin
 * 
 */
public class DMBrowserView extends BrowserView
{


    protected DMController _controller;

    public DMBrowserView(DMBrowser dmBrowser, DMController controller)
    {
        super(dmBrowser, controller.getKeyEventListener(), controller.getEditor());
        _controller = controller;
        FCH.setHelpItem(this,"dm-browser");
    }

    @Override
	public void treeSingleClick(FlexoModelObject object)
    {
    }

    @Override
	public void treeDoubleClick(FlexoModelObject object)
    {
        if (object instanceof DMObject) {
            _controller.setCurrentEditedObject((DMObject) object);
        }
    }

}
