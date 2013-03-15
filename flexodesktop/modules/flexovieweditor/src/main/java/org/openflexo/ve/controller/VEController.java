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
package org.openflexo.ve.controller;

/*
 * Created on <date> by <yourname>
 *
 * Flexo Application Suite
 * (c) Denali 2003-2006
 */
import java.util.logging.Logger;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.module.FlexoModule;
import org.openflexo.selection.SelectionManager;
import org.openflexo.ve.controller.action.VEControllerActionInitializer;
import org.openflexo.ve.view.VEMainPane;
import org.openflexo.ve.view.menu.OEMenuBar;
import org.openflexo.view.FlexoMainPane;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.menu.FlexoMenuBar;

/**
 * Controller for this module
 * 
 * @author yourname
 */
public class VEController extends FlexoController {

	private static final Logger logger = Logger.getLogger(VEController.class.getPackage().getName());

	public ViewLibraryPerspective VIEW_LIBRARY_PERSPECTIVE;

	public InformationSpacePerspective INFORMATION_SPACE_PERSPECTIVE;

	@Override
	public boolean useNewInspectorScheme() {
		return true;
	}

	@Override
	public boolean useOldInspectorScheme() {
		return false;
	}

	/**
	 * Default constructor
	 */
	public VEController(FlexoModule module) {
		super(module);
	}

	@Override
	protected void initializePerspectives() {
		addToPerspectives(VIEW_LIBRARY_PERSPECTIVE = new ViewLibraryPerspective(this));
		addToPerspectives(INFORMATION_SPACE_PERSPECTIVE = new InformationSpacePerspective(this));
	}

	@Override
	protected SelectionManager createSelectionManager() {
		return new VESelectionManager(this);
	}

	@Override
	public ControllerActionInitializer createControllerActionInitializer() {
		return new VEControllerActionInitializer(this);
	}

	/**
	 * Creates a new instance of MenuBar for the module this controller refers to
	 * 
	 * @return
	 */
	@Override
	protected FlexoMenuBar createNewMenuBar() {
		return new OEMenuBar(this);
	}

	@Override
	public void updateEditor(FlexoEditor from, FlexoEditor to) {
		super.updateEditor(from, to);
		FlexoProject project = to != null ? to.getProject() : null;
		if (project != null) {
			project.getStringEncoder()._addConverter(GraphicalRepresentation.POINT_CONVERTER);
			project.getStringEncoder()._addConverter(GraphicalRepresentation.RECT_POLYLIN_CONVERTER);
		}
		VIEW_LIBRARY_PERSPECTIVE.setProject(project);
		// ONTOLOGY_PERSPECTIVE.setProject(project);
	}

	@Override
	public FlexoObject getDefaultObjectToSelect(FlexoProject project) {
		return project.getViewLibrary();
	}

	/**
	 * Init inspectors
	 */
	@Override
	public void initInspectors() {
		super.initInspectors();
		if (useNewInspectorScheme()) {
			loadInspectorGroup("Ontology");
		}

	}

	@Override
	protected FlexoMainPane createMainPane() {
		return new VEMainPane(this);
	}

	@Override
	public boolean handleException(InspectableObject inspectable, String propertyName, Object value, Throwable exception) {
		// TODO: Handles here exceptions that may be thrown through the inspector
		return super.handleException(inspectable, propertyName, value, exception);
	}

	@Override
	public String getWindowTitleforObject(FlexoObject object) {
		if (getCurrentPerspective() == VIEW_LIBRARY_PERSPECTIVE) {
			return VIEW_LIBRARY_PERSPECTIVE.getWindowTitleforObject(object);
		}
		/*if (getCurrentPerspective() == ONTOLOGY_PERSPECTIVE) {
			return ONTOLOGY_PERSPECTIVE.getWindowTitleforObject(object);
		}*/
		return object.getFullyQualifiedName();
	}

}
