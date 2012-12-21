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
package org.openflexo.wse.controller;

/*
 * Created in March 06 by Denis VANVYVE Flexo Application Suite (c) Denali 2003-2006
 */
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.ws.AbstractMessageDefinition;
import org.openflexo.foundation.wkf.ws.FlexoPort;
import org.openflexo.foundation.wkf.ws.ServiceInterface;
import org.openflexo.foundation.wkf.ws.ServiceOperation;
import org.openflexo.foundation.ws.WSObject;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.module.FlexoModule;
import org.openflexo.selection.SelectionManager;
import org.openflexo.view.FlexoMainPane;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;
import org.openflexo.view.menu.FlexoMenuBar;
import org.openflexo.wse.controller.action.WSEControllerActionInitializer;
import org.openflexo.wse.view.WSEMainPane;
import org.openflexo.wse.view.menu.WSEMenuBar;

/**
 * Controller for this module
 * 
 * @author yourname
 */
public class WSEController extends FlexoController {

	static final Logger logger = Logger.getLogger(WSEController.class.getPackage().getName());

	public FlexoPerspective WSE_PERSPECTIVE;

	private WSEBrowser _browser;

	// ================================================
	// ================ Constructor ===================
	// ================================================

	/**
	 * Default constructor
	 */
	public WSEController(FlexoModule module) {
		super(module);
	}

	@Override
	protected void initializePerspectives() {
		_browser = new WSEBrowser(this);
		addToPerspectives(WSE_PERSPECTIVE = new WSEPerspective(this));
	}

	@Override
	protected SelectionManager createSelectionManager() {
		return new WSESelectionManager(this);
	}

	@Override
	public ControllerActionInitializer createControllerActionInitializer() {
		return new WSEControllerActionInitializer(this);
	}

	/**
	 * Creates a new instance of MenuBar for the module this controller refers to
	 * 
	 * @return
	 */
	@Override
	protected FlexoMenuBar createNewMenuBar() {
		return new WSEMenuBar(this);
	}

	@Override
	protected FlexoMainPane createMainPane() {
		return new WSEMainPane(this);
	}

	public WSEBrowser getWSEBrowser() {
		return _browser;
	}

	@Override
	public FlexoModelObject getDefaultObjectToSelect(FlexoProject project) {
		return project;
	}

	@Override
	public boolean handleException(InspectableObject inspectable, String propertyName, Object value, Throwable exception) {
		// TODO: Handles here exceptions that may be thrown through the inspector
		return super.handleException(inspectable, propertyName, value, exception);
	}

	@Override
	public String getWindowTitleforObject(FlexoObject object) {
		if (object instanceof DMObject) {
			return ((DMObject) object).getLocalizedName();
		} else if (object instanceof WSObject) {
			return ((WSObject) object).getLocalizedName();
		} else if (object instanceof FlexoProcess) {
			return ((FlexoProcess) object).getName();
		} else if (object instanceof FlexoPort) {
			return ((FlexoPort) object).getName();
		} else if (object instanceof AbstractMessageDefinition) {
			return ((AbstractMessageDefinition) object).getName();
		} else if (object instanceof ServiceInterface) {
			return ((ServiceInterface) object).getName();
		} else if (object instanceof ServiceOperation) {
			return ((ServiceOperation) object).getName();
		}
		return null;
	}

	@Override
	protected void updateEditor(FlexoEditor from, FlexoEditor to) {
		super.updateEditor(from, to);
		_browser.setRootObject(to != null && to.getProject() != null ? to.getProject().getFlexoWSLibrary() : null);
	}

}