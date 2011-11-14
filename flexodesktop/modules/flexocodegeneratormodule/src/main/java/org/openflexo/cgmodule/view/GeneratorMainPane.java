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
package org.openflexo.cgmodule.view;

import org.openflexo.cgmodule.controller.GeneratorController;
import org.openflexo.cgmodule.controller.browser.GeneratorBrowser;
import org.openflexo.components.browser.view.BrowserView;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.FlexoMainPane;
import org.openflexo.view.ModuleView;

/**
 * @author gpolet
 * 
 */
public class GeneratorMainPane extends FlexoMainPane {

	private BrowserView browserView;

	private GeneratorController _controller;

	/**
	 * @param moduleView
	 * @param mainFrame
	 * @param controller
	 */
	public GeneratorMainPane(GeneratorController controller, ModuleView moduleView, FlexoFrame mainFrame) {
		super(moduleView, mainFrame, controller);
		browserView = new GeneratorBrowserView(controller, new GeneratorBrowser(controller));
		this._controller = controller;
		setLeftView(browserView);
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
		return null;
	}

	@Override
	public GeneratorController getController() {
		return _controller;
	}

	public BrowserView getBrowserView() {
		return browserView;
	}

}
