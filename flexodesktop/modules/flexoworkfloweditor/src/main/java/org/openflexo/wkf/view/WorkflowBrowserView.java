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

import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.dnd.TreeDropTarget;
import org.openflexo.components.browser.view.BrowserView;
import org.openflexo.drm.FCH;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.wkf.controller.WKFController;
import org.openflexo.wkf.controller.WKFTreeDropTarget;
import org.openflexo.wkf.controller.WorkflowBrowser;

/**
 * Represents the view for WKF Browser
 * 
 * @author sguerin
 * 
 */
public class WorkflowBrowserView extends BrowserView {

	private static final Logger logger = Logger.getLogger(WorkflowBrowserView.class.getPackage().getName());

	public WorkflowBrowserView(WorkflowBrowser browser, WKFController controller) {
		super(browser, controller);
	}

	public WorkflowBrowserView(WKFController controller) {
		this(controller.getWorkflowBrowser(), controller);
		FCH.setHelpItem(this, "workflow-browser");
	}

	@Override
	protected TreeDropTarget createTreeDropTarget(FlexoJTree treeView2, ProjectBrowser _browser2) {
		return new WKFTreeDropTarget(treeView2, _browser2);
	}

	@Override
	public void treeSingleClick(FlexoObject object) {

	}

	@Override
	public void treeDoubleClick(FlexoObject object) {
		if (object instanceof WKFObject) {
			FlexoProcess objectProcess = ((WKFObject) object).getProcess();
			getController().selectAndFocusObject(objectProcess);

			treeSingleClick(object);
		} else if (object instanceof ComponentDefinition) {
			getEditor().focusOn(((ComponentDefinition) object).getDummyComponentInstance());
		} else {
		}
	}

}
