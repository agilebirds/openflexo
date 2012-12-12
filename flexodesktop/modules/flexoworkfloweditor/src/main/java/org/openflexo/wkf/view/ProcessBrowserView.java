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
package org.openflexo.wkf.view;

import java.util.logging.Logger;

import org.openflexo.ch.FCH;
import org.openflexo.components.browser.view.BrowserView;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.wkf.controller.ProcessBrowser;
import org.openflexo.wkf.controller.WKFController;

/**
 * Represents the view for WKF Browser
 * 
 * @author sguerin
 * 
 */
public class ProcessBrowserView extends BrowserView {

	private static final Logger logger = Logger.getLogger(ProcessBrowserView.class.getPackage().getName());

	public ProcessBrowserView(ProcessBrowser processBrowser, WKFController controller) {
		super(processBrowser, controller);
		FCH.setHelpItem(this, "process-browser");
	}

	@Override
	public void treeSingleClick(FlexoObject object) {

	}

	@Override
	public void treeDoubleClick(FlexoObject object) {
		if (object instanceof WKFObject) {
			treeSingleClick(object);
		} else if (object instanceof ComponentDefinition) {
			getEditor().focusOn(((ComponentDefinition) object).getDummyComponentInstance());
		}
	}

}
