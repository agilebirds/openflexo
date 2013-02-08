package org.openflexo.foundation.view.diagram;

import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.technologyadapter.DeclareEditionAction;
import org.openflexo.foundation.technologyadapter.DeclareEditionActions;
import org.openflexo.foundation.technologyadapter.DeclarePatternRole;
import org.openflexo.foundation.technologyadapter.DeclarePatternRoles;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration;
import org.openflexo.foundation.view.diagram.action.CreateDiagram;
import org.openflexo.foundation.view.diagram.model.Diagram;
import org.openflexo.foundation.view.diagram.viewpoint.ConnectorPatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramSpecification;
import org.openflexo.foundation.view.diagram.viewpoint.ShapePatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.editionaction.AddConnector;
import org.openflexo.foundation.view.diagram.viewpoint.editionaction.AddDiagram;
import org.openflexo.foundation.view.diagram.viewpoint.editionaction.AddShape;
import org.openflexo.foundation.view.diagram.viewpoint.editionaction.GraphicalAction;
import org.openflexo.foundation.viewpoint.DeleteAction;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.VirtualModel.VirtualModelBuilder;
import org.openflexo.foundation.viewpoint.VirtualModelModelSlot;

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
public class DiagramModelSlot extends VirtualModelModelSlot<Diagram, DiagramSpecification> {

	private static final Logger logger = Logger.getLogger(DiagramModelSlot.class.getPackage().getName());

	public DiagramModelSlot(ViewPoint viewPoint, DiagramTechnologyAdapter adapter) {
		super(viewPoint, adapter);
	}

	public DiagramModelSlot(DiagramSpecification diagramSpecification, DiagramTechnologyAdapter adapter) {
		super(diagramSpecification, adapter);
	}

	public DiagramModelSlot(VirtualModelBuilder builder) {
		super(builder);
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
	public BindingVariable makePatternRolePathElement(PatternRole<?> pr, Bindable container) {
		/*if (pr instanceof ShapePatternRole) {
			return new ShapePatternRolePathElement((ShapePatternRole) pr, container);
		} else if (pr instanceof ConnectorPatternRole) {
			return new ConnectorPatternRolePathElement((ConnectorPatternRole) pr, container);
		} else {
			return null;
		}*/
		return null;
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

	@Override
	public <EA extends EditionAction<?, ?, ?>> EA makeEditionAction(Class<EA> editionActionClass) {
		if (AddDiagram.class.isAssignableFrom(editionActionClass)) {
			return (EA) new AddDiagram(null);
		} else if (AddShape.class.isAssignableFrom(editionActionClass)) {
			return (EA) new AddShape(null);
		} else if (AddConnector.class.isAssignableFrom(editionActionClass)) {
			return (EA) new AddConnector(null);
		} else if (GraphicalAction.class.isAssignableFrom(editionActionClass)) {
			return (EA) new GraphicalAction(null);
		} else if (DeleteAction.class.isAssignableFrom(editionActionClass)) {
			return (EA) new DeleteAction(null);
		} else {
			return super.makeEditionAction(editionActionClass);
		}
	}

	@Override
	public boolean getIsRequired() {
		return true;
	}

	@Override
	public ModelSlotInstanceConfiguration<? extends VirtualModelModelSlot<Diagram, DiagramSpecification>> createConfiguration(
			CreateVirtualModelInstance<?> action) {
		return new DiagramModelSlotInstanceConfiguration(this, (CreateDiagram) action);
	}
}
