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
package org.openflexo.ie.view;

import java.awt.Dimension;

import javax.swing.JSplitPane;

import org.openflexo.ie.IECst;
import org.openflexo.ie.menu.IEMenuBar;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.ie.view.listener.IEKeyEventListener;
import org.openflexo.ie.view.palette.IEPaletteWindow;
import org.openflexo.view.FlexoFrame;

/**
 * The main window of the IE Module
 * 
 * @author bmangez, sguerin
 */
public class IEFrame extends FlexoFrame {

	protected IEController _ieController;

	protected IEMenuBar _ieMenuBar;

	protected IEKeyEventListener _ieKeyEventListener;

	public JSplitPane splitPane;

	public IEFrame(String title, IEController controller, IEKeyEventListener ieKeyEventListener, IEMenuBar menuBar) {
		super(title, controller, ieKeyEventListener, menuBar);
		_ieController = controller;
		_ieMenuBar = menuBar;
		_ieKeyEventListener = ieKeyEventListener;

		getContentPane().setSize(new Dimension(IECst.IE_WINDOW_WIDTH, IECst.IE_WINDOW_HEIGHT));
		setSize(new Dimension(IECst.IE_WINDOW_WIDTH, IECst.IE_WINDOW_HEIGHT));
	}

	public IEPaletteWindow getIEPaletteWindow() {
		return _ieController.getIEPaletteWindow();
	}

	// ==========================================================================
	// ============================ MyCellRenderer
	// ==============================
	// ==========================================================================

}
