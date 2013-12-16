package org.openflexo.technologyadapter.diagram.rm;

import java.util.List;

import org.openflexo.foundation.resource.PamelaResource;
import org.openflexo.foundation.technologyadapter.FlexoMetaModelResource;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterResource;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.diagram.DiagramTechnologyAdapter;
import org.openflexo.technologyadapter.diagram.model.Diagram;
import org.openflexo.technologyadapter.diagram.model.DiagramSpecification;
import org.openflexo.technologyadapter.diagram.model.DiagramSpecificationFactory;

@ModelEntity
@ImplementationClass(DiagramSpecificationResourceImpl.class)
@XMLElement
public interface DiagramSpecificationResource extends PamelaResource<DiagramSpecification, DiagramSpecificationFactory>,
		TechnologyAdapterResource<DiagramSpecification, DiagramTechnologyAdapter>,
		FlexoMetaModelResource<Diagram, DiagramSpecification, DiagramTechnologyAdapter> {

	/**
	 * Return virtual model stored by this resource<br>
	 * Load the resource data when unloaded
	 */
	public DiagramSpecification getDiagramSpecification();

	/**
	 * Return virtual model stored by this resource when loaded<br>
	 * Do not force the resource data to be loaded
	 */
	public DiagramSpecification getLoadedDiagramSpecification();

	public List<ExampleDiagramResource> getExampleDiagramResources();

	public List<DiagramPaletteResource> getDiagramPaletteResources();

}
