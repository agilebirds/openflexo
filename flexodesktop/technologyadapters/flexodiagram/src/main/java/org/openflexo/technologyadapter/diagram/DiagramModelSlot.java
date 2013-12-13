package org.openflexo.technologyadapter.diagram;

import java.util.logging.Logger;

import org.openflexo.foundation.technologyadapter.DeclareEditionAction;
import org.openflexo.foundation.technologyadapter.DeclareEditionActions;
import org.openflexo.foundation.technologyadapter.DeclareFetchRequest;
import org.openflexo.foundation.technologyadapter.DeclareFetchRequests;
import org.openflexo.foundation.technologyadapter.DeclarePatternRole;
import org.openflexo.foundation.technologyadapter.DeclarePatternRoles;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.viewpoint.AddEditionPatternInstance;
import org.openflexo.foundation.viewpoint.DeleteAction;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.EditionPatternInstancePatternRole;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.SelectEditionPatternInstance;
import org.openflexo.foundation.viewpoint.VirtualModel.VirtualModelBuilder;
import org.openflexo.foundation.viewpoint.VirtualModelModelSlot;
import org.openflexo.technologyadapter.diagram.fml.ConnectorPatternRole;
import org.openflexo.technologyadapter.diagram.fml.DiagramPatternRole;
import org.openflexo.technologyadapter.diagram.fml.DiagramSpecification;
import org.openflexo.technologyadapter.diagram.fml.ShapePatternRole;
import org.openflexo.technologyadapter.diagram.fml.editionaction.AddConnector;
import org.openflexo.technologyadapter.diagram.fml.editionaction.AddDiagram;
import org.openflexo.technologyadapter.diagram.fml.editionaction.AddShape;
import org.openflexo.technologyadapter.diagram.fml.editionaction.GraphicalAction;
import org.openflexo.technologyadapter.diagram.model.Diagram;
import org.openflexo.technologyadapter.diagram.model.action.CreateDiagram;

/**
 * Implementation of the ModelSlot class for the Openflexo built-in diagram technology adapter
 * 
 * @author sylvain
 * 
 */
@DeclarePatternRoles({ // All pattern roles available through this model slot
@DeclarePatternRole(FML = "Diagram", patternRoleClass = DiagramPatternRole.class), // Diagrams
		@DeclarePatternRole(FML = "ShapeSpecification", patternRoleClass = ShapePatternRole.class), // Shapes
		@DeclarePatternRole(FML = "ConnectorSpecification", patternRoleClass = ConnectorPatternRole.class), // Connectors
		@DeclarePatternRole(FML = "EditionPatternInstance", patternRoleClass = EditionPatternInstancePatternRole.class) // EditionPatternInstance
})
@DeclareEditionActions({ // All edition actions available through this model slot
@DeclareEditionAction(FML = "AddDiagram", editionActionClass = AddDiagram.class),
		@DeclareEditionAction(FML = "AddShape", editionActionClass = AddShape.class),
		@DeclareEditionAction(FML = "AddConnector", editionActionClass = AddConnector.class),
		@DeclareEditionAction(FML = "GraphicalAction", editionActionClass = GraphicalAction.class),
		@DeclareEditionAction(FML = "AddEditionPatternInstance", editionActionClass = AddEditionPatternInstance.class) })
@DeclareFetchRequests({ // All requests available through this model slot
@DeclareFetchRequest(FML = "SelectEditionPatternInstance", fetchRequestClass = SelectEditionPatternInstance.class) })
public class DiagramModelSlot extends VirtualModelModelSlot<Diagram, DiagramSpecification> {

	private static final Logger logger = Logger.getLogger(DiagramModelSlot.class.getPackage().getName());

	/*public DiagramModelSlot(ViewPoint viewPoint, DiagramTechnologyAdapter adapter) {
		super(viewPoint, adapter);
	}*/

	public DiagramModelSlot(DiagramSpecification diagramSpecification, DiagramTechnologyAdapter adapter) {
		super(diagramSpecification, adapter);
	}

	public DiagramModelSlot(VirtualModelBuilder builder) {
		super(builder);
	}

	/*public DiagramModelSlot(ViewPointBuilder builder) {
		super(builder);
	}*/

	@Override
	public String getFullyQualifiedName() {
		return "DiagramModelSlot";
	}

	@Override
	public Class<DiagramTechnologyAdapter> getTechnologyAdapterClass() {
		return DiagramTechnologyAdapter.class;
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
		PR returned = super.makePatternRole(patternRoleClass);
		if (returned != null) {
			return returned;
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
	public <EA extends EditionAction<?, ?>> EA makeEditionAction(Class<EA> editionActionClass) {
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
	public DiagramModelSlotInstanceConfiguration createConfiguration(CreateVirtualModelInstance<?> action) {
		if (action instanceof CreateDiagram) {
			return new DiagramModelSlotInstanceConfiguration(this, (CreateDiagram) action);
		} else {
			logger.warning("Unexpected " + action);
			return null;
		}
	}
}
