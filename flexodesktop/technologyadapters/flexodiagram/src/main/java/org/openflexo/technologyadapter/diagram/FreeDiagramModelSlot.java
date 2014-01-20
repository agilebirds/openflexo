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
import org.openflexo.technologyadapter.diagram.fml.ConnectorPatternRole;
import org.openflexo.technologyadapter.diagram.fml.DiagramPatternRole;
import org.openflexo.technologyadapter.diagram.fml.ShapePatternRole;
import org.openflexo.technologyadapter.diagram.fml.editionaction.AddConnector;
import org.openflexo.technologyadapter.diagram.fml.editionaction.AddDiagram;
import org.openflexo.technologyadapter.diagram.fml.editionaction.AddShape;
import org.openflexo.technologyadapter.diagram.fml.editionaction.GraphicalAction;
import org.openflexo.technologyadapter.diagram.metamodel.DiagramSpecification;
import org.openflexo.technologyadapter.diagram.model.Diagram;
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
public interface FreeDiagramModelSlot extends FreeModelSlot<Diagram>, DiagramModelSlot {

	public abstract class FreeDiagramModelSlotImpl extends FreeModelSlotImpl<Diagram> implements FreeDiagramModelSlot {

		private static final Logger logger = Logger.getLogger(FreeDiagramModelSlot.class.getPackage().getName());

		@Override
		public String getStringRepresentation() {
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

	}

}
