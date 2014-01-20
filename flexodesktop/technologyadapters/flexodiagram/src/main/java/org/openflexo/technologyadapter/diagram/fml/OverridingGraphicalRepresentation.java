package org.openflexo.technologyadapter.diagram.fml;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.foundation.viewpoint.FMLRepresentationContext;
import org.openflexo.foundation.viewpoint.NamedViewPointObject;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.diagram.metamodel.DiagramPalette;

@ModelEntity(isAbstract = true)
@ImplementationClass(OverridingGraphicalRepresentation.OverridingGraphicalRepresentationImpl.class)
@Imports({ @Import(OverridingGraphicalRepresentation.ShapeOverridingGraphicalRepresentation.class),
		@Import(OverridingGraphicalRepresentation.ConnectorOverridingGraphicalRepresentation.class) })
public interface OverridingGraphicalRepresentation<GR extends GraphicalRepresentation> extends NamedViewPointObject {

	@PropertyIdentifier(type = FMLDiagramPaletteElementBinding.class)
	public static final String PALETTE_ELEMENT_BINDING_KEY = "paletteElementBinding";
	@PropertyIdentifier(type = GraphicalElementPatternRole.class)
	public static final String PATTERN_ROLE_KEY = "patternRole";
	@PropertyIdentifier(type = GraphicalRepresentation.class)
	public static final String GRAPHICAL_REPRESENTATION_KEY = "graphicalRepresentation";

	@Getter(value = PALETTE_ELEMENT_BINDING_KEY, inverse = FMLDiagramPaletteElementBinding.OVERRIDING_GRAPHICAL_REPRESENTATIONS_KEY)
	public FMLDiagramPaletteElementBinding getDiagramPaletteElementBinding();

	@Setter(PALETTE_ELEMENT_BINDING_KEY)
	public void setDiagramPaletteElementBinding(FMLDiagramPaletteElementBinding diagramPaletteElementBinding);

	@Getter(value = PATTERN_ROLE_KEY)
	@XMLAttribute
	public GraphicalElementPatternRole<?, GR> getPatternRole();

	@Setter(PATTERN_ROLE_KEY)
	public void setPatternRole(GraphicalElementPatternRole<?, GR> aPatternRole);

	@Getter(value = GRAPHICAL_REPRESENTATION_KEY)
	@XMLElement
	public GR getGraphicalRepresentation();

	@Setter(GRAPHICAL_REPRESENTATION_KEY)
	public void setGraphicalRepresentation(GR graphicalRepresentation);

	public abstract class OverridingGraphicalRepresentationImpl<GR extends GraphicalRepresentation> extends NamedViewPointObjectImpl
			implements OverridingGraphicalRepresentation<GR> {

		// FMLDiagramPaletteElementBinding paletteElementBinding;
		// private String patternRoleName;

		// Do not use, required for deserialization
		public OverridingGraphicalRepresentationImpl() {
			super();
		}

		// Do not use, required for deserialization
		public OverridingGraphicalRepresentationImpl(GraphicalElementPatternRole<?, GR> patternRole) {
			super();
			setPatternRole(patternRole);
			// patternRoleName = patternRole.getPatternRoleName();
		}

		@Override
		public BindingModel getBindingModel() {
			if (getDiagramPaletteElementBinding() != null) {
				return getDiagramPaletteElementBinding().getBindingModel();
			}
			return null;
		}

		/*public FMLDiagramPaletteElementBinding getPaletteElementBinding() {
			return paletteElementBinding;
		}*/

		/*@Override
		public String getPatternRoleName() {
			return patternRoleName;
		}

		@Override
		public void setPatternRoleName(String patternRoleName) {
			this.patternRoleName = patternRoleName;
		}*/

		@Override
		public String getFMLRepresentation(FMLRepresentationContext context) {
			return "<not_implemented:" + getStringRepresentation() + ">";
		}

		public DiagramPalette getPalette() {
			return getDiagramPaletteElementBinding().getPaletteElement().getPalette();
		}

		public VirtualModel getVirtualModel() {
			return getDiagramPaletteElementBinding().getVirtualModel();
		}

		@Override
		public String getURI() {
			return null;
		}

		@Override
		public ViewPoint getViewPoint() {
			return getVirtualModel().getViewPoint();
		}

	}

	@ModelEntity
	@XMLElement
	public static interface ShapeOverridingGraphicalRepresentation extends OverridingGraphicalRepresentation<ShapeGraphicalRepresentation> {
	}

	@ModelEntity
	@XMLElement
	public static interface ConnectorOverridingGraphicalRepresentation extends
			OverridingGraphicalRepresentation<ConnectorGraphicalRepresentation> {
	}

}