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
import org.openflexo.technologyadapter.diagram.model.DiagramSpecification;
import org.openflexo.technologyadapter.diagram.rm.DiagramSpecificationResource;
import org.openflexo.toolbox.StringUtils;

public class DiagramPatternRole extends PatternRole<View> {

	private static final Logger logger = Logger.getLogger(DiagramPatternRole.class.getPackage().getName());

	private DiagramSpecificationResource diagramSpecificationResource;
	private String diagramSpecificationURI;

	public DiagramPatternRole() {
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
			diagramSpecificationResource = getViewPoint().getDiagramSpecificationNamed(diagramSpecificationURI).getResource();
			logger.info("Looked-up " + diagramSpecificationResource);
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
