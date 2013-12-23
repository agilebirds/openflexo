package org.openflexo.technologyadapter.diagram;

import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.technologyadapter.DeclareEditionAction;
import org.openflexo.foundation.technologyadapter.DeclareEditionActions;
import org.openflexo.foundation.technologyadapter.DeclareFetchRequests;
import org.openflexo.foundation.technologyadapter.DeclarePatternRole;
import org.openflexo.foundation.technologyadapter.DeclarePatternRoles;
import org.openflexo.foundation.technologyadapter.FlexoMetaModelResource;
import org.openflexo.foundation.technologyadapter.TypeAwareModelSlot;
import org.openflexo.foundation.view.TypeAwareModelSlotInstance;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.viewpoint.DeleteAction;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.FetchRequest;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.technologyadapter.diagram.fml.ConnectorPatternRole;
import org.openflexo.technologyadapter.diagram.fml.DiagramPatternRole;
import org.openflexo.technologyadapter.diagram.fml.FMLDiagramPaletteElementBinding;
import org.openflexo.technologyadapter.diagram.fml.ShapePatternRole;
import org.openflexo.technologyadapter.diagram.fml.editionaction.AddConnector;
import org.openflexo.technologyadapter.diagram.fml.editionaction.AddDiagram;
import org.openflexo.technologyadapter.diagram.fml.editionaction.AddShape;
import org.openflexo.technologyadapter.diagram.fml.editionaction.GraphicalAction;
import org.openflexo.technologyadapter.diagram.metamodel.DiagramSpecification;
import org.openflexo.technologyadapter.diagram.model.Diagram;
import org.openflexo.technologyadapter.diagram.model.action.CreateDiagram;
import org.openflexo.technologyadapter.diagram.rm.DiagramResource;

/**
 * Implementation of the ModelSlot class for the Openflexo built-in diagram technology adapter<br>
 * 
 * We modelize here the access to a {@link Diagram} conform to a given {@link DiagramSpecification}.
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
public class TypedDiagramModelSlot extends TypeAwareModelSlot<Diagram, DiagramSpecification> implements DiagramModelSlot {

	private static final Logger logger = Logger.getLogger(TypedDiagramModelSlot.class.getPackage().getName());

	private List<FMLDiagramPaletteElementBinding> paletteElementBindings;

	public TypedDiagramModelSlot(VirtualModel virtualModel, DiagramTechnologyAdapter adapter) {
		super(virtualModel, adapter);
	}

	public TypedDiagramModelSlot(VirtualModel virtualModel, DiagramSpecification diagramSpecification, DiagramTechnologyAdapter adapter) {
		this(virtualModel, adapter);
		setMetaModelResource(diagramSpecification.getResource());
	}

	/*public DiagramModelSlot(ViewPointBuilder builder) {
		super(builder);
	}*/

	@Override
	public String getStringRepresentation() {
		return "TypedDiagramModelSlot";
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
			return (PR) new DiagramPatternRole();
		} else if (ShapePatternRole.class.isAssignableFrom(patternRoleClass)) {
			return (PR) new ShapePatternRole();
		} else if (ConnectorPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return (PR) new ConnectorPatternRole();
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
			return (EA) new AddDiagram();
		} else if (AddShape.class.isAssignableFrom(editionActionClass)) {
			return (EA) new AddShape();
		} else if (AddConnector.class.isAssignableFrom(editionActionClass)) {
			return (EA) new AddConnector();
		} else if (GraphicalAction.class.isAssignableFrom(editionActionClass)) {
			return (EA) new GraphicalAction();
		} else if (DeleteAction.class.isAssignableFrom(editionActionClass)) {
			return (EA) new DeleteAction();
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
	public TypedDiagramModelSlotInstanceConfiguration createConfiguration(CreateVirtualModelInstance<?> action) {
		if (action instanceof CreateDiagram) {
			return new TypedDiagramModelSlotInstanceConfiguration(this, (CreateDiagram) action);
		} else {
			logger.warning("Unexpected " + action);
			return null;
		}
	}

	@Override
	public DiagramResource createProjectSpecificEmptyModel(View view, String filename, String modelUri,
			FlexoMetaModelResource<Diagram, DiagramSpecification, ?> metaModelResource) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DiagramResource createSharedEmptyModel(FlexoResourceCenter<?> resourceCenter, String relativePath, String filename,
			String modelUri, FlexoMetaModelResource<Diagram, DiagramSpecification, ?> metaModelResource) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getURIForObject(
			TypeAwareModelSlotInstance<Diagram, DiagramSpecification, ? extends TypeAwareModelSlot<Diagram, DiagramSpecification>> msInstance,
			Object o) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object retrieveObjectWithURI(
			TypeAwareModelSlotInstance<Diagram, DiagramSpecification, ? extends TypeAwareModelSlot<Diagram, DiagramSpecification>> msInstance,
			String objectURI) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isStrictMetaModelling() {
		// TODO Auto-generated method stub
		return false;
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

	public List<FMLDiagramPaletteElementBinding> getPaletteElementBindings() {
		return paletteElementBindings;
	}

	public void setPaletteElementBindings(List<FMLDiagramPaletteElementBinding> paletteElementBindings) {
		this.paletteElementBindings = paletteElementBindings;
	}

	public void addToPaletteElementBindings(FMLDiagramPaletteElementBinding paletteElementBinding) {
		paletteElementBindings.add(paletteElementBinding);
	}

	public void removeFromPaletteElementBindings(FMLDiagramPaletteElementBinding paletteElementBinding) {
		paletteElementBindings.remove(paletteElementBinding);
	}

}
