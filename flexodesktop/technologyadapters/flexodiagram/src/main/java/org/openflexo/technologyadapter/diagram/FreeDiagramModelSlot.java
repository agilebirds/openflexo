package org.openflexo.technologyadapter.diagram;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.technologyadapter.DeclareEditionAction;
import org.openflexo.foundation.technologyadapter.DeclareEditionActions;
import org.openflexo.foundation.technologyadapter.DeclareFetchRequests;
import org.openflexo.foundation.technologyadapter.DeclarePatternRole;
import org.openflexo.foundation.technologyadapter.DeclarePatternRoles;
import org.openflexo.foundation.technologyadapter.FreeModelSlot;
import org.openflexo.foundation.view.FreeModelSlotInstance;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.viewpoint.DeleteAction;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.FetchRequest;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.VirtualModel.VirtualModelBuilder;
import org.openflexo.technologyadapter.diagram.fml.ConnectorPatternRole;
import org.openflexo.technologyadapter.diagram.fml.DiagramPatternRole;
import org.openflexo.technologyadapter.diagram.fml.ShapePatternRole;
import org.openflexo.technologyadapter.diagram.fml.editionaction.AddConnector;
import org.openflexo.technologyadapter.diagram.fml.editionaction.AddDiagram;
import org.openflexo.technologyadapter.diagram.fml.editionaction.AddShape;
import org.openflexo.technologyadapter.diagram.fml.editionaction.GraphicalAction;
import org.openflexo.technologyadapter.diagram.model.Diagram;
import org.openflexo.technologyadapter.diagram.model.DiagramSpecification;
import org.openflexo.technologyadapter.diagram.rm.DiagramResource;

/**
 * Implementation of the ModelSlot class for the Openflexo built-in diagram technology adapter<br>
 * 
 * We modelize here the access to a free {@link Diagram} (no conformance to any {@link DiagramSpecification}).
 * 
 * @author sylvain
 * 
 */
@DeclarePatternRoles({ // All pattern roles available through this model slot
@DeclarePatternRole(FML = "Diagram", patternRoleClass = DiagramPatternRole.class), // Diagrams
		@DeclarePatternRole(FML = "ShapeSpecification", patternRoleClass = ShapePatternRole.class), // Shapes
		@DeclarePatternRole(FML = "ConnectorSpecification", patternRoleClass = ConnectorPatternRole.class), // Connectors
})
@DeclareEditionActions({ // All edition actions available through this model slot
@DeclareEditionAction(FML = "AddDiagram", editionActionClass = AddDiagram.class),
		@DeclareEditionAction(FML = "AddShape", editionActionClass = AddShape.class),
		@DeclareEditionAction(FML = "AddConnector", editionActionClass = AddConnector.class),
		@DeclareEditionAction(FML = "GraphicalAction", editionActionClass = GraphicalAction.class) })
@DeclareFetchRequests({ // All requests available through this model slot
})
public class FreeDiagramModelSlot extends FreeModelSlot<Diagram> {

	private static final Logger logger = Logger.getLogger(FreeDiagramModelSlot.class.getPackage().getName());

	public FreeDiagramModelSlot(VirtualModel virtualModel, DiagramTechnologyAdapter adapter) {
		super(virtualModel, adapter);
	}

	public FreeDiagramModelSlot(VirtualModelBuilder builder) {
		super(builder);
	}

	/*public DiagramModelSlot(ViewPointBuilder builder) {
		super(builder);
	}*/

	@Override
	public String getFullyQualifiedName() {
		return "FreeDiagramModelSlot";
	}

	@Override
	public Class<DiagramTechnologyAdapter> getTechnologyAdapterClass() {
		return DiagramTechnologyAdapter.class;
	}

	@Override
	public DiagramTechnologyAdapter getTechnologyAdapter() {
		return (DiagramTechnologyAdapter) super.getTechnologyAdapter();
	}

	@SuppressWarnings("unchecked")
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
			logger.warning("Unexpected EditionAction: " + editionActionClass.getName());
			return null;
		}
	}

	@Override
	public boolean getIsRequired() {
		return true;
	}

	@Override
	public FreeDiagramModelSlotInstanceConfiguration createConfiguration(CreateVirtualModelInstance<?> action) {
		return new FreeDiagramModelSlotInstanceConfiguration(this, action);
	}

	@Override
	public DiagramResource createProjectSpecificEmptyResource(View view, String filename, String modelUri) {
		return null;
	}

	@Override
	public DiagramResource createSharedEmptyResource(FlexoResourceCenter<?> resourceCenter, String relativePath, String filename,
			String modelUri) {
		return null;
	}

	@Override
	public String getURIForObject(FreeModelSlotInstance<Diagram, ? extends FreeModelSlot<Diagram>> msInstance, Object o) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object retrieveObjectWithURI(FreeModelSlotInstance<Diagram, ? extends FreeModelSlot<Diagram>> msInstance, String objectURI) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <FR extends FetchRequest<?, ?>> FR makeFetchRequest(Class<FR> fetchRequestClass) {
		// TODO Auto-generated method stub
		return null;
	}

}
