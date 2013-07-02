package org.openflexo.diagram.controller;

import javax.swing.ImageIcon;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.foundation.view.diagram.DiagramTechnologyAdapter;
import org.openflexo.foundation.view.diagram.viewpoint.ConnectorPatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.ShapePatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.editionaction.AddConnector;
import org.openflexo.foundation.view.diagram.viewpoint.editionaction.AddDiagram;
import org.openflexo.foundation.view.diagram.viewpoint.editionaction.AddShape;
import org.openflexo.foundation.view.diagram.viewpoint.editionaction.GraphicalAction;
import org.openflexo.foundation.viewpoint.DeleteAction;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.EditionPatternInstancePatternRole;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconLibrary;
import org.openflexo.icon.VEIconLibrary;
import org.openflexo.view.EmptyPanel;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.TechnologyAdapterController;
import org.openflexo.view.controller.model.FlexoPerspective;

public class DiagramTechnologyAdapterController extends TechnologyAdapterController<DiagramTechnologyAdapter> {

	@Override
	public Class<DiagramTechnologyAdapter> getTechnologyAdapterClass() {
		return DiagramTechnologyAdapter.class;
	}

	@Override
	public void initializeActions(ControllerActionInitializer actionInitializer) {
	}

	@Override
	public ImageIcon getTechnologyBigIcon() {
		// TODO
		return VEIconLibrary.DIAGRAM_ICON;
	}

	/**
	 * Return icon representing underlying technology
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getTechnologyIcon() {
		return VEIconLibrary.DIAGRAM_ICON;
	}

	/**
	 * Return icon representing a model of underlying technology
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getModelIcon() {
		return VEIconLibrary.VIEW_ICON;
	}

	/**
	 * Return icon representing a model of underlying technology
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getMetaModelIcon() {
		return VEIconLibrary.VIEW_ICON;
	}

	/**
	 * Return icon representing supplied ontology object
	 * 
	 * @param object
	 * @return
	 */
	@Override
	public ImageIcon getIconForTechnologyObject(Class<? extends TechnologyObject> objectClass) {
		return null;
	}

	/**
	 * Return icon representing supplied pattern role
	 * 
	 * @param object
	 * @return
	 */
	@Override
	public ImageIcon getIconForPatternRole(Class<? extends PatternRole> patternRoleClass) {
		if (DiagramPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return VEIconLibrary.VIEW_ICON;
		} else if (ShapePatternRole.class.isAssignableFrom(patternRoleClass)) {
			return VEIconLibrary.SHAPE_ICON;
		} else if (ConnectorPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return VEIconLibrary.CONNECTOR_ICON;
		} else if (EditionPatternInstancePatternRole.class.isAssignableFrom(patternRoleClass)) {
			return VEIconLibrary.EDITION_PATTERN_INSTANCE_ICON;
		}
		return null;
	}

	@Override
	public ImageIcon getIconForEditionAction(Class<? extends EditionAction> editionActionClass) {
		if (AddDiagram.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(VEIconLibrary.VIEW_ICON, IconLibrary.DUPLICATE);
		} else if (AddShape.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(VEIconLibrary.SHAPE_ICON, IconLibrary.DUPLICATE);
		} else if (AddConnector.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(VEIconLibrary.CONNECTOR_ICON, IconLibrary.DUPLICATE);
		} else if (GraphicalAction.class.isAssignableFrom(editionActionClass)) {
			return null;
		} else if (DeleteAction.class.isAssignableFrom(editionActionClass)) {
			return VEIconLibrary.DELETE_ICON;
		}
		return super.getIconForEditionAction(editionActionClass);
	}

	@Override
	public boolean hasModuleViewForObject(FlexoObject object) {
		// TODO not applicable
		return false;
	}

	@Override
	public <T extends FlexoObject> ModuleView<T> createModuleViewForObject(T object, FlexoController controller,
			FlexoPerspective perspective) {
		// TODO not applicable
		return new EmptyPanel<T>(controller, perspective, object);
	}

}
