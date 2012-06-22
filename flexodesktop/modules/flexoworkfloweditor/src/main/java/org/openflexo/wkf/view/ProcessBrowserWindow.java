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

/*
 * FlexoPalette.java
 * Project WorkflowEditor
 * 
 * Created by benoit on Mar 1, 2004
 */

import java.awt.BorderLayout;

import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.FlexoRelativeWindow;
import org.openflexo.wkf.WKFCst;
import org.openflexo.wkf.controller.WKFController;

/**
 * The WKF Browser window
 * 
 * @author sguerin
 */
public class ProcessBrowserWindow extends FlexoRelativeWindow {

	private ProcessBrowserView _processBrowserView;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public ProcessBrowserWindow(FlexoFrame mainFrame) {
		super(mainFrame);
		getContentPane().setLayout(new BorderLayout());

		_processBrowserView = new ProcessBrowserView(((WKFController) getController()).getExternalProcessBrowser(),
				(WKFController) getController());
		getContentPane().add(_processBrowserView, BorderLayout.CENTER);

		setTitle(FlexoLocalization.localizedForKey(getName()));
		setSize(WKFCst.DEFAULT_PROCESS_BROWSER_WINDOW_WIDTH, WKFCst.DEFAULT_PROCESS_BROWSER_WINDOW_HEIGHT);
		setLocation(WKFCst.DEFAULT_MAINFRAME_WIDTH + 2, WKFCst.DEFAULT_PALETTE_HEIGHT + 25 + WKFCst.DEFAULT_WORKFLOW_BROWSER_WINDOW_HEIGHT);

		validate();
		pack();
	}

	@Override
	public void dispose() {
		_processBrowserView = null;
		super.dispose();
	}

	@Override
	public String getName() {
		return WKFCst.DEFAULT_PROCESS_BROWSER_WINDOW_TITLE;
	}

}
