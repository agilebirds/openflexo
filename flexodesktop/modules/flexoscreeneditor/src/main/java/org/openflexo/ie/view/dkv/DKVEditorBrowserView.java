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
package org.openflexo.ie.view.dkv;

import org.openflexo.ch.FCH;
import org.openflexo.components.browser.view.BrowserView;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dkv.DKVObject;
import org.openflexo.ie.view.controller.IEController;

/**
 * @author gpolet
 * 
 */
public class DKVEditorBrowserView extends BrowserView {

	/**
	 * @param browser
	 * @param kl
	 */
	public DKVEditorBrowserView(IEController controller) {
		super(controller.getDkvEditorBrowser(), controller);
		FCH.setHelpItem(this, "dkv-browser");
	}

	/**
	 * Overrides treeSingleClick
	 * 
	 * @see org.openflexo.components.browser.view.BrowserView#treeSingleClick(org.openflexo.foundation.FlexoModelObject)
	 */
	@Override
	public void treeSingleClick(FlexoModelObject object) {
		if (object instanceof DKVObject) {
			getController().selectAndFocusObject(object);
		}
		// If this object is inspectable, inspect it.
		/*if (object instanceof InspectableObject) {
		    _controller.setCurrentInspectedObject((InspectableObject) object);
		}*/

	}

	/**
	 * Overrides treeDoubleClick
	 * 
	 * @see org.openflexo.components.browser.view.BrowserView#treeDoubleClick(org.openflexo.foundation.FlexoModelObject)
	 */
	@Override
	public void treeDoubleClick(FlexoModelObject object) {
		if (object instanceof DKVObject) {
			getController().selectAndFocusObject(object);
		}
	}

}
