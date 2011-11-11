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

import org.openflexo.doceditor.controller.DEController;
import org.openflexo.doceditor.controller.browser.DEBrowser;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.toc.TOCEntry;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.FlexoMainPane;
import org.openflexo.view.ModuleView;

/**
 * @author gpolet
 * 
 */
public class DEMainPane extends FlexoMainPane {

	// This variable can be null!
	protected DEBrowserView deBrowserView;

	/**
	 * @param moduleView
	 * @param mainFrame
	 * @param controller
	 */
	public DEMainPane(DEController controller, ModuleView moduleView, FlexoFrame mainFrame) {
		super(moduleView, mainFrame, controller);
		deBrowserView = new DEBrowserView(controller, new DEBrowser(controller));
		_controller = controller;
		setLeftView(deBrowserView);
	}

	/**
	 * Overrides getParentObject
	 * 
	 * @see org.openflexo.view.FlexoMainPane#getParentObject(org.openflexo.foundation.FlexoModelObject)
	 */
	@Override
	protected FlexoModelObject getParentObject(FlexoModelObject object) {
		if (object instanceof TOCEntry)
			if (((TOCEntry) object).getParent() != null)
				return ((TOCEntry) object).getParent();
			else
				((TOCEntry) object).getRepository();
		return null;
	}

	@Override
	public DEController getController() {
		return (DEController) _controller;
	}

}
