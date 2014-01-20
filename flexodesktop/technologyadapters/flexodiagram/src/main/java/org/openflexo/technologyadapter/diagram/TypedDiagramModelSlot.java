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
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;
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
@ModelEntity
@ImplementationClass(TypedDiagramModelSlot.TypedDiagramModelSlotImpl.class)
@XMLElement(xmlTag = "DiagramModelSlot")
public interface TypedDiagramModelSlot extends TypeAwareModelSlot<Diagram, DiagramSpecification>, DiagramModelSlot {

	@PropertyIdentifier(type = List.class)
	public static final String PALETTE_ELEMENTS_BINDING_KEY = "paletteElementBindings";

	@Getter(
			value = PALETTE_ELEMENTS_BINDING_KEY,
			cardinality = Cardinality.LIST,
			inverse = FMLDiagramPaletteElementBinding.DIAGRAM_MODEL_SLOT_KEY)
	@XMLElement
	public List<FMLDiagramPaletteElementBinding> getPaletteElementBindings();

	@Setter(PALETTE_ELEMENTS_BINDING_KEY)
	public void setPaletteElementBindings(List<FMLDiagramPaletteElementBinding> paletteElementBindings);

	@Adder(PALETTE_ELEMENTS_BINDING_KEY)
	public void addToPaletteElementBindings(FMLDiagramPaletteElementBinding paletteElementBinding);

	@Remover(PALETTE_ELEMENTS_BINDING_KEY)
	public void removeFromPaletteElementBindings(FMLDiagramPaletteElementBinding paletteElementBinding);

	public static abstract class TypedDiagramModelSlotImpl extends TypeAwareModelSlotImpl<Diagram, DiagramSpecification> implements
			TypedDiagramModelSlot {

		private static final Logger logger = Logger.getLogger(TypedDiagramModelSlot.class.getPackage().getName());

		private List<FMLDiagramPaletteElementBinding> paletteElementBindings;

		public TypedDiagramModelSlotImpl(VirtualModel virtualModel, DiagramTechnologyAdapter adapter) {
			super(virtualModel, adapter);
		}

		public TypedDiagramModelSlotImpl(VirtualModel virtualModel, DiagramSpecification diagramSpecification,
				DiagramTechnologyAdapter adapter) {
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

		@Override
		public boolean getIsRequired() {
			return true;
		}

		@Override
		public TypedDiagramModelSlotInstanceConfiguration createConfiguration(CreateVirtualModelInstance<?> action) {
			return new TypedDiagramModelSlotInstanceConfiguration(this, action);
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
		public List<FMLDiagramPaletteElementBinding> getPaletteElementBindings() {
			return paletteElementBindings;
		}

		@Override
		public void setPaletteElementBindings(List<FMLDiagramPaletteElementBinding> paletteElementBindings) {
			this.paletteElementBindings = paletteElementBindings;
		}

		@Override
		public void addToPaletteElementBindings(FMLDiagramPaletteElementBinding paletteElementBinding) {
			paletteElementBindings.add(paletteElementBinding);
		}

		@Override
		public void removeFromPaletteElementBindings(FMLDiagramPaletteElementBinding paletteElementBinding) {
			paletteElementBindings.remove(paletteElementBinding);
		}

	}
}
