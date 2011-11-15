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
package org.openflexo.dm.view.controller;

import java.awt.Dimension;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;
import javax.swing.SwingUtilities;

import org.openflexo.FlexoCst;
import org.openflexo.dm.DMFrame;
import org.openflexo.dm.view.DMEOEntityView;
import org.openflexo.dm.view.DMEOModelView;
import org.openflexo.dm.view.DMEORepositoryView;
import org.openflexo.dm.view.DMEntityView;
import org.openflexo.dm.view.DMMainPane;
import org.openflexo.dm.view.DMModelView;
import org.openflexo.dm.view.DMPackageView;
import org.openflexo.dm.view.DMRepositoryFolderView;
import org.openflexo.dm.view.DMRepositoryView;
import org.openflexo.dm.view.DMView;
import org.openflexo.dm.view.EOPrototypeRepositoryView;
import org.openflexo.dm.view.controller.action.DMControllerActionInitializer;
import org.openflexo.dm.view.listener.DMKeyEventListener;
import org.openflexo.dm.view.menu.DMMenuBar;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.DMPackage;
import org.openflexo.foundation.dm.DMRepository;
import org.openflexo.foundation.dm.DMRepositoryFolder;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.dm.eo.DMEOModel;
import org.openflexo.foundation.dm.eo.DMEORepository;
import org.openflexo.foundation.dm.eo.EOPrototypeRepository;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.FlexoModule;
import org.openflexo.selection.SelectionManager;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.ConsistencyCheckingController;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.InteractiveFlexoEditor;
import org.openflexo.view.controller.SelectionManagingController;
import org.openflexo.view.menu.FlexoMenuBar;

/**
 * Controller for the DataModel Editor module
 * 
 * @author sguerin
 */
public class DMController extends FlexoController implements SelectionManagingController, ConsistencyCheckingController {

	@SuppressWarnings("hiding")
	private static final Logger logger = Logger.getLogger(DMController.class.getPackage().getName());

	public final RepositoryPerspective REPOSITORY_PERSPECTIVE;
	public final PackagePerspective PACKAGE_PERSPECTIVE;
	public final HierarchyPerspective HIERARCHY_PERSPECTIVE;
	public/*final*/DiagramPerspective DIAGRAM_PERSPECTIVE;

	protected DMMenuBar _dmMenuBar;

	protected DMFrame _dmFrame;

	protected DMKeyEventListener _dmKeyEventListener;

	// private DMBrowser _dmBrowser;

	private final DMSelectionManager _selectionManager;

	/**
	 * Default constructor
	 * 
	 * @param workflowFile
	 * @throws Exception
	 */
	public DMController(InteractiveFlexoEditor projectEditor, FlexoModule module) throws Exception {
		super(projectEditor, module);
		_dmMenuBar = (DMMenuBar) createAndRegisterNewMenuBar();
		_dmKeyEventListener = new DMKeyEventListener(this);
		_dmFrame = new DMFrame(FlexoCst.BUSINESS_APPLICATION_VERSION_NAME, this, _dmKeyEventListener, _dmMenuBar);
		init(_dmFrame, _dmKeyEventListener, _dmMenuBar);

		_selectionManager = new DMSelectionManager(this);

		addToPerspectives(REPOSITORY_PERSPECTIVE = new RepositoryPerspective(this));
		addToPerspectives(PACKAGE_PERSPECTIVE = new PackagePerspective(this));
		addToPerspectives(HIERARCHY_PERSPECTIVE = new HierarchyPerspective(this));
		addToPerspectives(DIAGRAM_PERSPECTIVE = new DiagramPerspective(this));

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				switchToPerspective(REPOSITORY_PERSPECTIVE);
			}
		});
	}

	@Override
	public ControllerActionInitializer createControllerActionInitializer() {
		return new DMControllerActionInitializer(this);
	}

	/**
	 * Creates a new instance of MenuBar for the module this controller refers to
	 * 
	 * @return
	 */
	@Override
	protected FlexoMenuBar createNewMenuBar() {
		return new DMMenuBar(this);
	}

	public void loadRelativeWindows() {
		if (getDocInspectorPanel() != null) {
			getDocInspectorPanel().setPreferredSize(new Dimension(300, 300));
			getMainPane().setRightView(getDocInspectorPanel());
		}
	}

	/**
     *
     */
	@Override
	public void initInspectors() {
		super.initInspectors();
		getDMSelectionManager().addObserver(getSharedInspectorController());
		getDMSelectionManager().addObserver(getDocInspectorController());
	}

	public DMModel getDataModel() {
		return getProject().getDataModel();
	}

	@Override
	public ValidationModel getDefaultValidationModel() {
		return getProject().getDMValidationModel();
	}

	public DMKeyEventListener getKeyEventListener() {
		return _dmKeyEventListener;
	}

	public DMFrame getMainFrame() {
		return _dmFrame;
	}

	public DMMenuBar getEditorMenuBar() {
		return _dmMenuBar;
	}

	public void showDataModelBrowser() {
		if (getMainPane() != null) {
			getMainPane().showDataModelBrowser();
		}
	}

	public void hideDataModelBrowser() {
		if (getMainPane() != null) {
			getMainPane().hideDataModelBrowser();
		}
	}

	public void setCurrentEditedObject(DMObject object) {
		if (object != getCurrentDisplayedObjectAsModuleView()) {
			setCurrentEditedObjectAsModuleView(object);
		}
	}

	public DMObject getCurrentEditedObject() {
		// return _currentEditedObject;
		return (DMObject) getCurrentDisplayedObjectAsModuleView();
	}

	public ModuleView<? extends FlexoModelObject> getCurrentEditedObjectView() {
		if (getCurrentEditedObject() != null) {
			return viewForObject(getCurrentEditedObject());
		}
		return null;
	}

	public ModuleView<? extends FlexoModelObject> viewForObject(DMObject object) {
		return moduleViewForObject(object);
	}

	@Override
	protected DMMainPane createMainPane() {
		return new DMMainPane(getEmptyPanel(), getMainFrame(), this);
	}

	@Override
	public DMMainPane getMainPane() {
		return (DMMainPane) super.getMainPane();
	}

	@SuppressWarnings("unchecked")
	protected <O extends DMObject> DMView<O> createDMView(O object) {
		// Not allowed anymore
		// if (object instanceof DMProperty)
		// object = ((DMProperty) object).getEntity();
		if (object instanceof DMModel) {
			return (DMView<O>) new DMModelView((DMModel) object, this);
		} else if (object instanceof DMRepositoryFolder) {
			return (DMView<O>) new DMRepositoryFolderView((DMRepositoryFolder) object, this);
		} else if (object instanceof DMEOEntity) {
			return (DMView<O>) new DMEOEntityView((DMEOEntity) object, this);
		} else if (object instanceof DMEOModel) {
			return (DMView<O>) new DMEOModelView((DMEOModel) object, this);
		} else if (object instanceof EOPrototypeRepository) {
			return (DMView<O>) new EOPrototypeRepositoryView((EOPrototypeRepository) object, this);
		} else if (object instanceof DMEORepository) {
			return (DMView<O>) new DMEORepositoryView((DMEORepository) object, this);
		} else if (object instanceof DMEntity) {
			return (DMView<O>) new DMEntityView((DMEntity) object, this);
		} else if (object instanceof DMPackage) {
			return (DMView<O>) new DMPackageView((DMPackage) object, this);
		} else if (object instanceof DMRepository) {
			return (DMView<O>) new DMRepositoryView((DMRepository) object, this);
		} else {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Cannot create view for a " + object.getClass().getName());
			}
			return null;
		}
	}

	// ==========================================================================
	// ============================= Browsers ==================================
	// ==========================================================================

	/*public DMBrowser getDMBrowser()
	{
	    return _dmBrowser;
	}*/

	// =========================================================
	// ================ Selection management ===================
	// =========================================================

	@Override
	public SelectionManager getSelectionManager() {
		return getDMSelectionManager();
	}

	public DMSelectionManager getDMSelectionManager() {
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
		// TODO: implements view switching
		logger.warning("Implement view switching here");
		getSelectionManager().setSelectedObject(object);
	}


	// ==========================================================================
	// =================== Inspectable Exceptions handling
	// ======================
	// ==========================================================================

	/**
	 * Tries to handle an exception raised during object inspection.<br>
	 * 
	 * @param inspectable
	 *            the object on which exception was raised
	 * @param propertyName
	 *            the concerned property name
	 * @param value
	 *            the value that raised an exception
	 * @param exception
	 *            the exception that was raised
	 * @return a boolean indicating if this handler has handled this exception, or not
	 */
	@Override
	public boolean handleException(InspectableObject inspectable, String propertyName, Object value, Throwable exception) {
		if (exception instanceof InvalidNameException) {
			showError(FlexoLocalization.localizedForKey("the_value_you_entered_is_invalid"));
			return true;
		}
		if (exception instanceof IllegalArgumentException && propertyName.equals("name")) {
			showError(FlexoLocalization.localizedForKey("this_name_is_already_used"));
			return true;
		}
		// See above
		return super.handleException(inspectable, propertyName, value, exception);
	}

	@Override
	public FlexoPerspective<DMObject> getDefaultPespective() {
		return getCurrentPerspective();
	}

	@Override
	public FlexoPerspective<DMObject> getCurrentPerspective() {
		return (FlexoPerspective<DMObject>) super.getCurrentPerspective();
	}

	@Override
	public void switchToPerspective(FlexoPerspective perspective) {
		// logger.info("Selection="+getSelectionManager().getSelection());
		Vector<FlexoModelObject> selection = (Vector<FlexoModelObject>) getSelectionManager().getSelection().clone();
		super.switchToPerspective(perspective);
		// getMainPane().switchToPerspective(perspective);
		getSelectionManager().setSelectedObjects(selection);
		// logger.info("Selection="+getSelectionManager().getSelection());
	}

	@Override
	public String getWindowTitleforObject(FlexoModelObject object) {
		if (object instanceof DMObject) {
			return ((DMObject) object).getLocalizedName();
		}
		return null;
	}

}
