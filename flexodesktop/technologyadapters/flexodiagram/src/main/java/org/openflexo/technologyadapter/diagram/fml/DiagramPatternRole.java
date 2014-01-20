package org.openflexo.technologyadapter.diagram.fml;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.ModelObjectActorReference;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.viewpoint.FMLRepresentationContext;
import org.openflexo.foundation.viewpoint.FMLRepresentationContext.FMLRepresentationOutput;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.diagram.metamodel.DiagramSpecification;
import org.openflexo.technologyadapter.diagram.rm.DiagramSpecificationResource;
import org.openflexo.toolbox.StringUtils;

// TODO: change View to Diagram
@ModelEntity
@ImplementationClass(DiagramPatternRole.DiagramPatternRoleImpl.class)
@XMLElement
public interface DiagramPatternRole extends PatternRole<View> {

	@PropertyIdentifier(type = String.class)
	public static final String DIAGRAM_SPECIFICATION_URI_KEY = "diagramSpecificationURI";

	@Getter(value = DIAGRAM_SPECIFICATION_URI_KEY)
	@XMLAttribute
	public String getDiagramSpecificationURI();

	@Setter(DIAGRAM_SPECIFICATION_URI_KEY)
	public void setDiagramSpecificationURI(String diagramSpecificationURI);

	public static abstract class DiagramPatternRoleImpl extends PatternRoleImpl<View> implements DiagramPatternRole {

		private static final Logger logger = Logger.getLogger(DiagramPatternRole.class.getPackage().getName());

		private DiagramSpecificationResource diagramSpecificationResource;
		private String diagramSpecificationURI;

		public DiagramPatternRoleImpl() {
			super();
		}

		@Override
		public String getPreciseType() {
			if (getDiagramSpecification() != null) {
				return getDiagramSpecification().getName();
			}
			return FlexoLocalization.localizedForKey("diagram");
		}

		@Override
		public String getFMLRepresentation(FMLRepresentationContext context) {
			FMLRepresentationOutput out = new FMLRepresentationOutput(context);
			out.append("PatternRole " + getName() + " as Diagram conform to " + getDiagramSpecificationURI() + ";", context);
			return out.toString();
		}

		@Override
		public Type getType() {
			return View.class;
		}

		public DiagramSpecificationResource getDiagramSpecificationResource() {
			if (diagramSpecificationResource == null && StringUtils.isNotEmpty(diagramSpecificationURI)) {
				diagramSpecificationResource = (DiagramSpecificationResource) getModelSlot().getTechnologyAdapter()
						.getTechnologyContextManager().getResourceWithURI(diagramSpecificationURI);
				logger.info("Looked-up " + diagramSpecificationResource);
			}
			return diagramSpecificationResource;
		}

		public void setDiagramSpecificationResource(DiagramSpecificationResource diagramSpecificationResource) {
			this.diagramSpecificationResource = diagramSpecificationResource;
		}

		@Override
		public String getDiagramSpecificationURI() {
			if (diagramSpecificationResource != null) {
				return diagramSpecificationResource.getURI();
			}
			return diagramSpecificationURI;
		}

		@Override
		public void setDiagramSpecificationURI(String diagramSpecificationURI) {
			this.diagramSpecificationURI = diagramSpecificationURI;
		}

		public DiagramSpecification getDiagramSpecification() {
			if (getDiagramSpecificationResource() != null) {
				return getDiagramSpecificationResource().getDiagramSpecification();
			}
			return null;
		}

		public void setDiagramSpecification(DiagramSpecification diagramSpecification) {
			diagramSpecificationResource = diagramSpecification.getResource();
		}

		@Override
		public boolean defaultBehaviourIsToBeDeleted() {
			return false;
		}

		@Override
		public ModelObjectActorReference<View> makeActorReference(View object, EditionPatternInstance epi) {
			return new ModelObjectActorReference<View>(object, this, epi);
		}
	}
}
