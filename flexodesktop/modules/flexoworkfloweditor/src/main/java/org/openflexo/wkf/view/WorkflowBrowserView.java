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
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.dnd.TreeDropTarget;
import org.openflexo.components.browser.view.BrowserView;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.module.ModuleLoader;
import org.openflexo.module.external.ExternalIEModule;
import org.openflexo.view.listener.FlexoKeyEventListener;
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

	// ==========================================================================
	// ============================= Variables
	// ==================================
	// ==========================================================================

	protected WKFController _controller;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public WorkflowBrowserView(WorkflowBrowser browser, FlexoKeyEventListener listener, FlexoEditor editor) {
		super(browser, listener, editor);
	}

	public WorkflowBrowserView(WKFController controller) {
		this(controller.getWorkflowBrowser(), controller.getKeyEventListener(), controller.getEditor());
		_controller = controller;
		FCH.setHelpItem(this, "workflow-browser");
	}

	@Override
	protected TreeDropTarget createTreeDropTarget(FlexoJTree treeView2, ProjectBrowser _browser2) {
		return new WKFTreeDropTarget(treeView2, _browser2);
	}

	@Override
	public void treeSingleClick(FlexoModelObject object) {

	}

	@Override
	public void treeDoubleClick(FlexoModelObject object) {
		if (object instanceof WKFObject) {
			FlexoProcess objectProcess = ((WKFObject) object).getProcess();
			_controller.setCurrentFlexoProcess(objectProcess);
			treeSingleClick(object);
		} else if (object instanceof ComponentDefinition) {
			ExternalIEModule ieModule = ModuleLoader.getIEModule();
			if (ieModule == null) {
				return;
			}
			ieModule.focusOn();
			ieModule.showScreenInterface(((ComponentDefinition) object).getDummyComponentInstance());
		} else {
			// System.out.println ("Pas d'idee de truc a faire non plus !");
		}
	}

}
