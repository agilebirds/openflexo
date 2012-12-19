package org.openflexo.foundation.view.diagram;

import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.technologyadapter.DeclareEditionAction;
import org.openflexo.foundation.technologyadapter.DeclareEditionActions;
import org.openflexo.foundation.technologyadapter.DeclarePatternRole;
import org.openflexo.foundation.technologyadapter.DeclarePatternRoles;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.view.diagram.model.View;
import org.openflexo.foundation.view.diagram.viewpoint.ConnectorPatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.ShapePatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.editionaction.AddConnector;
import org.openflexo.foundation.view.diagram.viewpoint.editionaction.AddDiagram;
import org.openflexo.foundation.view.diagram.viewpoint.editionaction.AddShape;
import org.openflexo.foundation.view.diagram.viewpoint.editionaction.GraphicalAction;
import org.openflexo.foundation.viewpoint.DeleteAction;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.binding.GraphicalElementPatternRolePathElement.ConnectorPatternRolePathElement;
import org.openflexo.foundation.viewpoint.binding.GraphicalElementPatternRolePathElement.ShapePatternRolePathElement;

/**
 * Implementation of the ModelSlot class for the Openflexo built-in diagram technology adapter
 * 
 * @author sylvain
 * 
 */
@DeclarePatternRoles({ @DeclarePatternRole(DiagramPatternRole.class), // Diagrams
		@DeclarePatternRole(ShapePatternRole.class), // Shapes
		@DeclarePatternRole(ConnectorPatternRole.class) // Connectors
})
@DeclareEditionActions({ @DeclareEditionAction(AddDiagram.class), // Add diagram
		@DeclareEditionAction(AddShape.class), // Add shape
		@DeclareEditionAction(AddConnector.class), // Add connector
		@DeclareEditionAction(GraphicalAction.class), // Graphical action
		@DeclareEditionAction(DeleteAction.class) // Delete action
})
public class DiagramModelSlot extends ModelSlot<View, DiagramMetaModel> {

	private static final Logger logger = Logger.getLogger(DiagramModelSlot.class.getPackage().getName());

	public DiagramModelSlot(ViewPoint viewPoint, DiagramTechnologyAdapter adapter) {
		super(viewPoint, adapter);
	}

	public DiagramModelSlot(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public String getFullyQualifiedName() {
		return "DiagramModelSlot";
	}

	@Override
	public Class<DiagramTechnologyAdapter> getTechnologyAdapterClass() {
		return DiagramTechnologyAdapter.class;
	}

	@Override
	public BindingVariable<?> makePatternRolePathElement(PatternRole<?> pr, Bindable container) {
		if (pr instanceof ShapePatternRole) {
			return new ShapePatternRolePathElement((ShapePatternRole) pr, container);
		} else if (pr instanceof ConnectorPatternRole) {
			return new ConnectorPatternRolePathElement((ConnectorPatternRole) pr, container);
		} else {
			return null;
		}
	}

	@Override
	public <PR extends PatternRole<?>> PR makePatternRole(Class<PR> patternRoleClass) {
		if (DiagramPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return (PR) new DiagramPatternRole(null);
		} else if (ShapePatternRole.class.isAssignableFrom(patternRoleClass)) {
			return (PR) new ShapePatternRole(null);
		} else if (ConnectorPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return (PR) new ConnectorPatternRole(null);
		}
		logger.warning("Unexpected pattern role: " + patternRoleClass.getName());
		return null;
	}

	@Override
	public <PR extends PatternRole<?>> String defaultPatternRoleName(Class<PR> patternRoleClass) {
		if (DiagramPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return "diagram";
		} else if (ShapePatternRole.class.isAssignableFrom(patternRoleClass)) {
			return "shape";
		} else if (ConnectorPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return "connector";
		}
		logger.warning("Unexpected pattern role: " + patternRoleClass.getName());
		return null;
	}

}
