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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;
import javax.swing.SwingUtilities;

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
import org.openflexo.dm.view.menu.DMMenuBar;
import org.openflexo.foundation.FlexoEditor;
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
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.FlexoModule;
import org.openflexo.selection.SelectionManager;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;
import org.openflexo.view.menu.FlexoMenuBar;

/**
 * Controller for the DataModel Editor module
 * 
 * @author sguerin
 */
public class DMController extends FlexoController {

	@SuppressWarnings("hiding")
	private static final Logger logger = Logger.getLogger(DMController.class.getPackage().getName());

	public final RepositoryPerspective REPOSITORY_PERSPECTIVE;
	public final PackagePerspective PACKAGE_PERSPECTIVE;
	public final HierarchyPerspective HIERARCHY_PERSPECTIVE;
	public final DiagramPerspective DIAGRAM_PERSPECTIVE;

	@Override
	public boolean useNewInspectorScheme() {
		return true;
	}

	@Override
	public boolean useOldInspectorScheme() {
		return true;
	}

	/**
	 * Default constructor
	 * 
	 * @param workflowFile
	 * @throws Exception
	 */
	public DMController(FlexoModule module) {
		super(module);
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
	protected SelectionManager createSelectionManager() {
		return new DMSelectionManager(this);
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

	/**
	 * Overrides getDefaultObjectToSelect
	 * 
	 * @see org.openflexo.module.FlexoModule#getDefaultObjectToSelect(FlexoProject)
	 */
	@Override
	public FlexoModelObject getDefaultObjectToSelect(FlexoProject project) {
		return project.getDataModel();
	}

	public DMModel getDataModel() {
		if (getProject() != null) {
			return getProject().getDataModel();
		} else {
			return null;
		}
	}

	@Override
	public ValidationModel getDefaultValidationModel() {
		if (getProject() != null) {
			return getProject().getDMValidationModel();
		}
		return null;
	}

	public void setCurrentEditedObject(DMObject object) {
		if (object != getCurrentDisplayedObjectAsModuleView()) {
			setCurrentEditedObjectAsModuleView(object);
		}
	}

	public DMObject getCurrentEditedObject() {
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
		return new DMMainPane(this);
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

	/**
	 * Select the view representing supplied object, if this view exists. Try all to really display supplied object, even if required view
	 * is not the current displayed view
	 * 
	 * @param object
	 *            : the object to focus on
	 */
	@Override
	public void selectAndFocusObject(FlexoModelObject object) {
		super.selectAndFocusObject(object);
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
	public void switchToPerspective(FlexoPerspective perspective) {
		List<FlexoModelObject> selection = new ArrayList<FlexoModelObject>(getSelectionManager().getSelection());
		super.switchToPerspective(perspective);
		getSelectionManager().setSelectedObjects(selection);
	}

	@Override
	public String getWindowTitleforObject(FlexoModelObject object) {
		if (object instanceof DMObject) {
			return ((DMObject) object).getLocalizedName();
		}
		return null;
	}

	@Override
	protected void updateEditor(FlexoEditor from, FlexoEditor to) {
		super.updateEditor(from, to);
		DIAGRAM_PERSPECTIVE.setProject(to != null ? to.getProject() : null);
	}

}
