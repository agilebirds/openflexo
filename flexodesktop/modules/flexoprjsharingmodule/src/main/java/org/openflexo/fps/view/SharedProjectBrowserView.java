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
package org.openflexo.fps.view;

import org.openflexo.components.browser.view.BrowserView;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.fps.controller.FPSController;

/**
 * Represents the view for the browser of this module
 * 
 * @author yourname
 * 
 */
public class SharedProjectBrowserView extends BrowserView {

	public SharedProjectBrowserView(FPSController controller) {
		super(controller.getSharedProjectBrowser(), controller);
	}

	@Override
	public void treeSingleClick(FlexoModelObject object) {
	}

	@Override
	public void treeDoubleClick(FlexoModelObject object) {
		// Try to display object in view
		getController().selectAndFocusObject(object);
	}

}
