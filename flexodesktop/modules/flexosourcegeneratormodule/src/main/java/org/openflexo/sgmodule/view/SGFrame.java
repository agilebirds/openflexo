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
package org.openflexo.sgmodule.view;

/*
 * Created on <date> by <yourname>
 *
 * Flexo Application Suite
 * (c) Denali 2003-2006
 */
import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.sgmodule.SGCst;
import org.openflexo.sgmodule.controller.SGController;
import org.openflexo.sgmodule.controller.SGKeyEventListener;
import org.openflexo.sgmodule.view.menu.SGMenuBar;
import org.openflexo.view.FlexoFrame;

/**
 * The main window of this module
 * 
 * @author sylvain
 */
public class SGFrame extends FlexoFrame {

	private static final Logger logger = Logger.getLogger(SGFrame.class.getPackage().getName());

	// ==========================================================================
	// ============================= Instance variables
	// =========================
	// ==========================================================================

	protected SGController _sgController;

	protected SGMenuBar _sgMenuBar;

	protected SGKeyEventListener _sgKeyEventListener;

	// ================================================
	// ================ Constructor ===================
	// ================================================

	/**
	 * Constructor for XXXFrame
	 */
	public SGFrame(String title, SGController controller, SGKeyEventListener wkfKeyEventListener, SGMenuBar menuBar)
			throws HeadlessException {
		super(title, controller, wkfKeyEventListener, menuBar);
		_sgController = controller;
		_sgMenuBar = menuBar;
		_sgKeyEventListener = wkfKeyEventListener;
		setSize(SGCst.DEFAULT_MAINFRAME_WIDTH, SGCst.DEFAULT_MAINFRAME_HEIGHT);
		updateTitle();
		getContentPane().setLayout(new BorderLayout());
		// You may observe here some model objects
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		super.update(observable, dataModification);
	}

	@Override
	public String getName() {
		return SGCst.SG_MODULE_NAME;
	}

	/**
	 * @return Returns the controller.
	 */
	public SGController getSGController() {
		return _sgController;
	}

}
