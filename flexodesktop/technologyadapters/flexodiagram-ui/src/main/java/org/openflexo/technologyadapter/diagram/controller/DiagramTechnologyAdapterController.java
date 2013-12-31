package org.openflexo.technologyadapter.diagram.controller;

import javax.swing.ImageIcon;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.foundation.viewpoint.DeleteAction;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.EditionPatternInstancePatternRole;
import org.openflexo.foundation.viewpoint.EditionScheme;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconLibrary;
import org.openflexo.icon.VEIconLibrary;
import org.openflexo.technologyadapter.diagram.DiagramTechnologyAdapter;
import org.openflexo.technologyadapter.diagram.fml.ConnectorPatternRole;
import org.openflexo.technologyadapter.diagram.fml.DiagramPatternRole;
import org.openflexo.technologyadapter.diagram.fml.DropScheme;
import org.openflexo.technologyadapter.diagram.fml.LinkScheme;
import org.openflexo.technologyadapter.diagram.fml.ShapePatternRole;
import org.openflexo.technologyadapter.diagram.fml.editionaction.AddConnector;
import org.openflexo.technologyadapter.diagram.fml.editionaction.AddDiagram;
import org.openflexo.technologyadapter.diagram.fml.editionaction.AddShape;
import org.openflexo.technologyadapter.diagram.fml.editionaction.GraphicalAction;
import org.openflexo.technologyadapter.diagram.gui.DiagramIconLibrary;
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
		return DiagramIconLibrary.DIAGRAM_ICON;
	}

	/**
	 * Return icon representing underlying technology
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getTechnologyIcon() {
		return DiagramIconLibrary.DIAGRAM_ICON;
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
	public ImageIcon getIconForPatternRole(Class<? extends PatternRole<?>> patternRoleClass) {
		if (DiagramPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return DiagramIconLibrary.DIAGRAM_ICON;
		} else if (ShapePatternRole.class.isAssignableFrom(patternRoleClass)) {
			return DiagramIconLibrary.SHAPE_ICON;
		} else if (ConnectorPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return DiagramIconLibrary.CONNECTOR_ICON;
		} else if (EditionPatternInstancePatternRole.class.isAssignableFrom(patternRoleClass)) {
			return VEIconLibrary.EDITION_PATTERN_INSTANCE_ICON;
		}
		return null;
	}

	@Override
	public ImageIcon getIconForEditionAction(Class<? extends EditionAction<?, ?>> editionActionClass) {
		if (AddDiagram.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(DiagramIconLibrary.DIAGRAM_ICON, IconLibrary.DUPLICATE);
		} else if (AddShape.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(DiagramIconLibrary.SHAPE_ICON, IconLibrary.DUPLICATE);
		} else if (AddConnector.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(DiagramIconLibrary.CONNECTOR_ICON, IconLibrary.DUPLICATE);
		} else if (GraphicalAction.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(DiagramIconLibrary.GRAPHICAL_ACTION_ICON);
		} else if (DeleteAction.class.isAssignableFrom(editionActionClass)) {
			return VEIconLibrary.DELETE_ICON;
		}
		return super.getIconForEditionAction(editionActionClass);
	}

	@Override
	public ImageIcon getIconForEditionScheme(Class<? extends EditionScheme> editionSchemeClass) {
		if (DropScheme.class.isAssignableFrom(editionSchemeClass)) {
			return DiagramIconLibrary.DROP_SCHEME_ICON;
		} else if (LinkScheme.class.isAssignableFrom(editionSchemeClass)) {
			return DiagramIconLibrary.LINK_SCHEME_ICON;
		}
		return super.getIconForEditionScheme(editionSchemeClass);
	}

	@Override
	public boolean hasModuleViewForObject(TechnologyObject object) {
		// TODO not applicable
		return false;
	}

	@Override
	public String getWindowTitleforObject(TechnologyObject object) {
		return object.toString();
	}

	@Override
	public <T extends FlexoObject> ModuleView<T> createModuleViewForObject(T object, FlexoController controller,
			FlexoPerspective perspective) {
		// TODO not applicable
		return new EmptyPanel<T>(controller, perspective, object);
	}

}
