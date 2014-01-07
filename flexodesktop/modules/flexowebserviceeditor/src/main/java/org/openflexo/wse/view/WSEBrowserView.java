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
package org.openflexo.wse.view;

import java.util.logging.Logger;

import org.openflexo.components.browser.view.BrowserView;
import org.openflexo.drm.FCH;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.wse.controller.WSEController;

/**
 * Represents the view for the browser of this module
 * 
 * @author yourname
 * 
 */
public class WSEBrowserView extends BrowserView {

	private static final Logger logger = Logger.getLogger(WSEBrowserView.class.getPackage().getName());

	public WSEBrowserView(WSEController controller) {
		super(controller.getWSEBrowser(), controller);
		FCH.setHelpItem(this, "ws-browser");
	}

	@Override
	public void treeSingleClick(FlexoObject object) {
	}

	@Override
	public void treeDoubleClick(FlexoObject object) {
		// Try to display object in view
		getController().selectAndFocusObject(object);
	}

}
