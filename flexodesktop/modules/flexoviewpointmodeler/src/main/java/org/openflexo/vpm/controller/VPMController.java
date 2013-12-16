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
package org.openflexo.vpm.controller;

/*
 * Created on <date> by <yourname>
 *
 * Flexo Application Suite
 * (c) Denali 2003-2006
 */
import java.util.logging.Logger;

import org.openflexo.fge.swing.control.SwingToolFactory;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPalette;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagram;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.EditionPatternObject;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.foundation.viewpoint.ViewPointObject;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.FlexoModule;
import org.openflexo.selection.SelectionManager;
import org.openflexo.view.FlexoMainPane;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.menu.FlexoMenuBar;
import org.openflexo.vpm.controller.action.VPMControllerActionInitializer;
import org.openflexo.vpm.view.EditionPatternView;
import org.openflexo.vpm.view.VPMMainPane;
import org.openflexo.vpm.view.menu.VPMMenuBar;

/**
 * This is the controller of ViewPointModeller module
 * 
 * @author sylvain
 */
public class VPMController extends FlexoController {

	private static final Logger logger = Logger.getLogger(VPMController.class.getPackage().getName());

	public ViewPointPerspective VIEW_POINT_PERSPECTIVE;
	public InformationSpacePerspective INFORMATION_SPACE_PERSPECTIVE;

	private SwingToolFactory toolFactory;

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
	public VPMController(FlexoModule module) {
		super(module);
	}

	public SwingToolFactory getToolFactory() {
		return toolFactory;
	}

	@Override
	protected void initializePerspectives() {
		toolFactory = new SwingToolFactory(getFlexoFrame());
		addToPerspectives(VIEW_POINT_PERSPECTIVE = new ViewPointPerspective(this));
		addToPerspectives(INFORMATION_SPACE_PERSPECTIVE = new InformationSpacePerspective(this));
	}

	@Override
	protected SelectionManager createSelectionManager() {
		return new VPMSelectionManager(this);
	}

	@Override
	public ControllerActionInitializer createControllerActionInitializer() {
		return new VPMControllerActionInitializer(this);
	}

	/**
	 * Creates a new instance of MenuBar for the module this controller refers to
	 * 
	 * @return
	 */
	@Override
	protected FlexoMenuBar createNewMenuBar() {
		return new VPMMenuBar(this);
	}

	/**
	 * Init inspectors
	 */
	@Override
	public void initInspectors() {
		super.initInspectors();
		if (useNewInspectorScheme()) {
			loadInspectorGroup("IFlexoOntology");
		}

	}

	@Override
	protected FlexoMainPane createMainPane() {
		return new VPMMainPane(this);
	}

	/**
	 * Return the ViewPointLibrary
	 * 
	 * @return
	 */
	public ViewPointLibrary getViewPointLibrary() {
		return getApplicationContext().getService(ViewPointLibrary.class);
	}

	@Override
	public FlexoObject getDefaultObjectToSelect(FlexoProject project) {
		return getViewPointLibrary();
	}

	/**
	 * Select the view representing supplied object, if this view exists. Try all to really display supplied object, even if required view
	 * is not the current displayed view
	 * 
	 * @param object
	 *            : the object to focus on
	 */
	@Override
	public void selectAndFocusObject(FlexoObject object) {
		if (object != null) {
			logger.info("selectAndFocusObject " + object + "of " + object.getClass().getSimpleName());
			if (object instanceof EditionPatternObject) {
				setCurrentEditedObjectAsModuleView(((EditionPatternObject) object).getEditionPattern());
			} else {
				setCurrentEditedObjectAsModuleView(object);
			}
			if (getCurrentPerspective() == VIEW_POINT_PERSPECTIVE) {
				if (object instanceof ViewPointLibrary) {
					/*ViewPointLibrary cl = (ViewPointLibrary) object;
					if (cl.getViewPoints().size() > 0) {
						getSelectionManager().setSelectedObject(cl.getViewPoints().firstElement());
					}*/
				} /*else if (object instanceof OWLMetaModel) {
					OWLMetaModel ontology = (OWLMetaModel) object;
					VIEW_POINT_PERSPECTIVE.focusOnOntology(ontology);
					if (ontology.getClasses().size() > 0) {
						getSelectionManager().setSelectedObject(ontology.getClasses().firstElement());
					}
					}*/else if (object instanceof ExampleDiagram) {
					VIEW_POINT_PERSPECTIVE.focusOnExampleDiagram((ExampleDiagram) object);
				} else if (object instanceof DiagramPalette) {
					VIEW_POINT_PERSPECTIVE.focusOnPalette((DiagramPalette) object);
				} else if (object instanceof ViewPoint) {
					ViewPoint viewPoint = (ViewPoint) object;
					VIEW_POINT_PERSPECTIVE.focusOnViewPoint(viewPoint);
				} else if (object instanceof VirtualModel) {
					VirtualModel virtualModel = (VirtualModel) object;
					VIEW_POINT_PERSPECTIVE.focusOnVirtualModel(virtualModel);
				} else if (object instanceof EditionPattern) {
					EditionPattern pattern = (EditionPattern) object;
					if (pattern.getEditionSchemes().size() > 0) {
						getSelectionManager().setSelectedObject(pattern.getEditionSchemes().firstElement());
					}
				} else if (object instanceof EditionPatternObject) {
					if (getCurrentModuleView() instanceof EditionPatternView) {
						((EditionPatternView) getCurrentModuleView()).tryToSelect((EditionPatternObject) object);
					}
				}
			}
			getSelectionManager().setSelectedObject(object);
		} else {
			logger.warning("Cannot set focus on a NULL object");
		}
	}

	// ================================================
	// ============ Exception management ==============
	// ================================================

	@Override
	public boolean handleException(InspectableObject inspectable, String propertyName, Object value, Throwable exception) {
		// TODO: Handles here exceptions that may be thrown through the inspector
		return super.handleException(inspectable, propertyName, value, exception);
	}

	@Override
	public String getWindowTitleforObject(FlexoObject object) {
		// System.out.println("getWindowTitleforObject() "+object+" perspective="+getCurrentPerspective());
		if (object instanceof ViewPointLibrary) {
			return FlexoLocalization.localizedForKey("view_point_library");
		}
		/*if (object instanceof OntologyLibrary) {
			return FlexoLocalization.localizedForKey("ontology_library");
		}*/
		if (getCurrentPerspective() == VIEW_POINT_PERSPECTIVE) {
			return VIEW_POINT_PERSPECTIVE.getWindowTitleforObject(object);
		}
		if (getCurrentPerspective() == INFORMATION_SPACE_PERSPECTIVE) {
			return INFORMATION_SPACE_PERSPECTIVE.getWindowTitleforObject(object);
		}
		return object.getFullyQualifiedName();
	}

	public ViewPoint getCurrentViewPoint() {
		if (getCurrentDisplayedObjectAsModuleView() instanceof ViewPointObject) {
			return ((ViewPointObject) getCurrentDisplayedObjectAsModuleView()).getViewPoint();
		}
		return null;
	}

	@Override
	public ValidationModel getDefaultValidationModel() {
		return ViewPointLibrary.VALIDATION_MODEL;
	}
}
