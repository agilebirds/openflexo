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
package org.openflexo.dgmodule.view;

import org.openflexo.dgmodule.controller.DGController;
import org.openflexo.dgmodule.controller.browser.DGBrowser;
import org.openflexo.doceditor.view.DEMainPane;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGFile;

/**
 * @author gpolet
 * 
 */
public class DGMainPane extends DEMainPane {

	private DGBrowserView dgBrowserView;

	/**
	 * @param moduleView
	 * @param mainFrame
	 * @param controller
	 */
	public DGMainPane(DGController controller) {
		super(controller);
		dgBrowserView = new DGBrowserView(controller, new DGBrowser(controller));
		setLeftView(dgBrowserView);
		if (deBrowserView.getParent() != null) {
			deBrowserView.getParent().remove(deBrowserView);
		}
		deBrowserView.getBrowser().delete();
		deBrowserView = null;
	}

	/**
	 * Overrides getParentObject
	 * 
	 * @see org.openflexo.view.FlexoMainPane#getParentObject(org.openflexo.foundation.FlexoModelObject)
	 */
	@Override
	protected FlexoModelObject getParentObject(FlexoModelObject object) {
		if (object instanceof CGFile) {
			return ((CGFile) object).getRepository();
		}
		return super.getParentObject(object);
	}

	@Override
	public DGController getController() {
		return (DGController) _controller;
	}

	public DGBrowserView getDgBrowserView() {
		return dgBrowserView;
	}

}
