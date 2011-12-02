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
package org.openflexo.fps.controller.browser;

import java.util.logging.Logger;

import org.openflexo.components.browser.BrowserConfiguration;
import org.openflexo.components.browser.ConfigurableProjectBrowser;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.fps.CVSExplorable;
import org.openflexo.fps.CVSExplorer;
import org.openflexo.fps.CVSExplorerListener;
import org.openflexo.fps.FlexoAuthentificationException;
import org.openflexo.fps.action.FlexoUnknownHostException;
import org.openflexo.fps.controller.FPSController;

/**
 * Browser for Code Generator module
 * 
 * @author sguerin
 * 
 */
public abstract class FPSBrowser extends ConfigurableProjectBrowser implements FlexoObserver, CVSExplorerListener {

	private static final Logger logger = Logger.getLogger(FPSBrowser.class.getPackage().getName());

	// ==========================================================================
	// ============================= Variables
	// ==================================
	// ==========================================================================

	private FPSController _controller;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public FPSBrowser(BrowserConfiguration configuration, FPSController controller) {
		super(configuration, controller.getFPSSelectionManager());
		_controller = controller;
	}

	public FPSBrowser(BrowserConfiguration configuration) {
		super(configuration);
	}

	public FPSController getController() {
		return _controller;
	}

	@Override
	public void exploringFailed(CVSExplorable explorable, CVSExplorer explorer, Exception exception) {
		if (exception instanceof FlexoAuthentificationException) {
			getController().handleAuthenticationException((FlexoAuthentificationException) exception);
		} else if (exception instanceof FlexoUnknownHostException) {
			getController().handleUnknownHostException((FlexoUnknownHostException) exception);
		}
	}

	@Override
	public void exploringSucceeded(CVSExplorable explorable, CVSExplorer explorer) {
		logger.info("Exploring " + explorable + " was successfull");
		focusOn((FlexoModelObject) explorer.getExplorable());
	}

}
