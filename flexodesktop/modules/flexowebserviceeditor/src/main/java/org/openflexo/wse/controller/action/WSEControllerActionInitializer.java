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
package org.openflexo.wse.controller.action;

import java.util.logging.Logger;

import org.openflexo.selection.SelectionManager;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.wse.controller.WSEController;

/**
 * 
 * Action initializing for this module
 * 
 * @author yourname
 */
public class WSEControllerActionInitializer extends ControllerActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	private WSEController _wseController;

	public WSEControllerActionInitializer(WSEController controller) {
		super(controller);
		_wseController = controller;
	}

	protected WSEController getWSEController() {
		return _wseController;
	}

	protected SelectionManager getWSESelectionManager() {
		return getWSEController().getSelectionManager();
	}

	@Override
	public void initializeActions() {
		super.initializeActions();

		new WSESetPropertyInitializer(this);

		new WSDeleteInitializer(this);
		new ImportWsdlInitializer(this);
		new CreateNewWebServiceInitializer(this);

	}

}
