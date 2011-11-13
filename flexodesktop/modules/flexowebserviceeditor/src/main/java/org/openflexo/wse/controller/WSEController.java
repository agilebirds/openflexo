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
import java.util.Hashtable;
import java.util.logging.Logger;

import org.openflexo.FlexoCst;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.validation.ValidationModel;
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
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.InteractiveFlexoEditor;
import org.openflexo.view.controller.SelectionManagingController;
import org.openflexo.view.menu.FlexoMenuBar;
import org.openflexo.wse.controller.action.WSEControllerActionInitializer;
import org.openflexo.wse.view.WSEFrame;
import org.openflexo.wse.view.WSEMainPane;
import org.openflexo.wse.view.listener.WSEKeyEventListener;
import org.openflexo.wse.view.menu.WSEMenuBar;

/**
 * Controller for this module
 * 
 * @author yourname
 */
public class WSEController extends FlexoController implements SelectionManagingController {// , ConsistencyCheckingController {

	static final Logger logger = Logger.getLogger(WSEController.class.getPackage().getName());

	public final FlexoPerspective<FlexoModelObject> WSE_PERSPECTIVE = new WSEPerspective();

	protected WSEMenuBar _WSEMenuBar;

	protected WSEFrame _frame;

	protected WSEKeyEventListener _WSEKeyEventListener;

	private WSESelectionManager _selectionManager;

	private WSEBrowser _browser;

	// ================================================
	// ================ Constructor ===================
	// ================================================

	/**
	 * Default constructor
	 */
	public WSEController(InteractiveFlexoEditor projectEditor, FlexoModule module) throws Exception {
		super(projectEditor, module);
		addToPerspectives(WSE_PERSPECTIVE);
		setDefaultPespective(WSE_PERSPECTIVE);
		_WSEMenuBar = (WSEMenuBar) createAndRegisterNewMenuBar();
		_WSEKeyEventListener = new WSEKeyEventListener(this);
		_frame = new WSEFrame(FlexoCst.BUSINESS_APPLICATION_VERSION_NAME, this, _WSEKeyEventListener, _WSEMenuBar);
		init(_frame, _WSEKeyEventListener, _WSEMenuBar);

		// At this point the InspectorController is not yet loaded
		_selectionManager = new WSESelectionManager(this);

		_browser = new WSEBrowser(this);
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

	/**
	 * Init inspectors
	 */
	@Override
	public void initInspectors() {
		super.initInspectors();
		_selectionManager.addObserver(getSharedInspectorController());
	}

	public void loadRelativeWindows() {
		// Build eventual relative windows
	}

	// ================================================
	// ============== Instance method =================
	// ================================================

	public ValidationModel getDefaultValidationModel() {
		// If there is a ValidationModel associated to this module, put it here
		return null;
	}

	public WSEFrame getMainFrame() {
		return _frame;
	}

	public WSEMenuBar getEditorMenuBar() {
		return _WSEMenuBar;
	}

	public void showBrowser() {
		if (getMainPane() != null) {
			((WSEMainPane) getMainPane()).showBrowser();
		}
	}

	public void hideBrowser() {
		if (getMainPane() != null) {
			((WSEMainPane) getMainPane()).hideBrowser();
		}
	}

	@Override
	protected FlexoMainPane createMainPane() {
		return new WSEMainPane(getEmptyPanel(), getMainFrame(), this);
	}

	public WSEBrowser getWSEBrowser() {
		return _browser;
	}

	public WSEKeyEventListener getKeyEventListener() {
		return _WSEKeyEventListener;
	}

	// ================================================
	// ============ Selection management ==============
	// ================================================

	@Override
	public SelectionManager getSelectionManager() {
		return getWSESelectionManager();
	}

	public WSESelectionManager getWSESelectionManager() {
		return _selectionManager;
	}

	/**
	 * Select the view representing supplied object, if this view exists. Try all to really display supplied object, even if required view
	 * is not the current displayed view
	 * 
	 * @param object
	 *            : the object to focus on
	 */
	@Override
	public void selectAndFocusObject(FlexoModelObject object) {
		// TODO: Implements this
		setCurrentEditedObjectAsModuleView(object);
	}

	// ================================================
	// ============ Exception management ==============
	// ================================================

	@Override
	public boolean handleException(InspectableObject inspectable, String propertyName, Object value, Throwable exception) {
		// TODO: Handles here exceptions that may be thrown through the inspector
		return super.handleException(inspectable, propertyName, value, exception);
	}

	// VIEWS
	@Override
	public Hashtable getLoadedViews() {
		return super.getLoadedViews();
	}

	@Override
	public String getWindowTitleforObject(FlexoModelObject object) {
		if (object instanceof DMObject)
			return ((DMObject) object).getLocalizedName();
		else if (object instanceof WSObject)
			return ((WSObject) object).getLocalizedName();
		else if (object instanceof FlexoProcess)
			return ((FlexoProcess) object).getName();
		else if (object instanceof FlexoPort)
			return ((FlexoPort) object).getName();
		else if (object instanceof AbstractMessageDefinition) {
			return ((AbstractMessageDefinition) object).getName();
		} else if (object instanceof ServiceInterface) {
			return ((ServiceInterface) object).getName();
		} else if (object instanceof ServiceOperation) {
			return ((ServiceOperation) object).getName();
		}
		return null;
	}

}