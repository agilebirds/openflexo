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
package org.openflexo.wkf.view.menu;

/*
 * MenuFile.java
 * Project WorkflowEditor
 *
 * Created by benoit on Mar 10, 2004
 */
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.module.Module;
import org.openflexo.view.menu.FlexoMenuItem;
import org.openflexo.view.menu.WindowMenu;
import org.openflexo.wkf.controller.WKFController;

/**
 * 'Edit' menu
 * 
 * @author benoit
 */
public class WKFWindowMenu extends WindowMenu {

	private static final Logger logger = Logger.getLogger(WKFWindowMenu.class.getPackage().getName());

	// ==========================================================================
	// ============================= Instance Variables
	// =========================
	// ==========================================================================

	public FlexoMenuItem openInNewWindow;

	protected WKFController _controller;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public WKFWindowMenu(WKFController controller) {
		super(controller, Module.WKF_MODULE);
		_controller = controller;
		addSeparator();

		add(new FlexoMenuItem(org.openflexo.wkf.controller.OpenProcessInNewWindow.actionType, getController()) {
			@Override
			public FlexoModelObject getFocusedObject() {
				return _controller.getCurrentFlexoProcess();
			}

			@Override
			public Vector getGlobalSelection() {
				return null;
			}
		});

	}

}
