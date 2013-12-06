package org.openflexo.foundation.view.diagram.viewpoint;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.foundation.rm.DiagramSpecificationResource;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.ModelObjectActorReference;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.viewpoint.EditionPatternInstanceType;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.ViewPointObject.FMLRepresentationContext.FMLRepresentationOutput;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.view.diagram.model.Diagram;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.StringUtils;

public class DiagramPatternRole extends PatternRole<Diagram> {

	private static final Logger logger = Logger.getLogger(DiagramPatternRole.class.getPackage().getName());

	private DiagramSpecificationResource diagramSpecificationResource;
	private String diagramSpecificationURI;

	public DiagramPatternRole(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
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
		return DiagramType.getDiagramType(this.getDiagramSpecification());
	}

	@Override
	public boolean getIsPrimaryRole() {
		return false;
	}

	@Override
	public void setIsPrimaryRole(boolean isPrimary) {
		// Not relevant
	}

	public DiagramSpecificationResource getDiagramSpecificationResource() {
		if (diagramSpecificationResource == null && StringUtils.isNotEmpty(diagramSpecificationURI)) {
			DiagramSpecification diagramSpec = getViewPoint().getDiagramSpecificationNamed(diagramSpecificationURI);
			if (diagramSpec != null){
				diagramSpecificationResource = getViewPoint().getDiagramSpecificationNamed(diagramSpecificationURI).getResource();
				logger.info("Looked-up " + diagramSpecificationResource);
			}
			else {
				logger.warning("Enable to load Resource for DiagramSpec URI: " + diagramSpecificationURI);
			}
		}
		return diagramSpecificationResource;
	}

	public void setDiagramSpecificationResource(DiagramSpecificationResource diagramSpecificationResource) {
		this.diagramSpecificationResource = diagramSpecificationResource;
	}

	public String getDiagramSpecificationURI() {
		if (diagramSpecificationResource != null) {
			return diagramSpecificationResource.getURI();
		}
		return diagramSpecificationURI;
	}

	public void setDiagramSpecificationURI(String diagramSpecificationURI) {
		this.diagramSpecificationURI = diagramSpecificationURI;
	}

	public DiagramSpecification getDiagramSpecification() {
		DiagramSpecification diagramSpec = getViewPoint().getDiagramSpecificationNamed(diagramSpecificationURI);
		return diagramSpec;
	}

	public void setDiagramSpecification(DiagramSpecification diagramSpecification) {
		diagramSpecificationResource = diagramSpecification.getResource();
	}

	@Override
	public boolean defaultBehaviourIsToBeDeleted() {
		return false;
	}

	@Override
	public ModelObjectActorReference<Diagram> makeActorReference(Diagram object, EditionPatternInstance epi) {
		return new ModelObjectActorReference<Diagram>(object, this, epi);
	}
}
