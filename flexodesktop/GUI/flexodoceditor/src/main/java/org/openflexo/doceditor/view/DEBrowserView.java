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
package org.openflexo.doceditor.view;

import java.awt.Dimension;

import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.dnd.TreeDropTarget;
import org.openflexo.components.browser.view.BrowserView;
import org.openflexo.doceditor.DECst;
import org.openflexo.doceditor.controller.DEController;
import org.openflexo.doceditor.controller.browser.DEBrowser;
import org.openflexo.doceditor.controller.browser.TOCTreeDropTarget;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.toc.TOCEntry;

/**
 * @author gpolet
 * 
 */
public class DEBrowserView extends BrowserView {

	/**
	 * @param browser
	 * @param kl
	 */
	public DEBrowserView(DEController controller, ProjectBrowser browser) {
		super(browser, controller);
		setMinimumSize(new Dimension(DECst.MINIMUM_BROWSER_VIEW_WIDTH, DECst.MINIMUM_BROWSER_VIEW_HEIGHT));

	}

	@Override
	protected TreeDropTarget createTreeDropTarget(FlexoJTree treeView2, ProjectBrowser _browser2) {
		return new TOCTreeDropTarget(treeView2, _browser2);
	}

	@Override
	public DEBrowser getBrowser() {
		return (DEBrowser) super.getBrowser();
	}

	/**
	 * Overrides treeSingleClick
	 * 
	 * @see org.openflexo.components.browser.view.BrowserView#treeSingleClick(org.openflexo.foundation.FlexoModelObject)
	 */
	@Override
	public void treeSingleClick(FlexoModelObject object) {
		if (object instanceof GenerationRepository) {
			getController().setCurrentEditedObjectAsModuleView(object);
		} else if (object instanceof TOCEntry) {
			getController().setCurrentEditedObjectAsModuleView(object);
		}
	}

	/**
	 * Overrides treeDoubleClick
	 * 
	 * @see org.openflexo.components.browser.view.BrowserView#treeDoubleClick(org.openflexo.foundation.FlexoModelObject)
	 */
	@Override
	public void treeDoubleClick(FlexoModelObject object) {

	}

}
