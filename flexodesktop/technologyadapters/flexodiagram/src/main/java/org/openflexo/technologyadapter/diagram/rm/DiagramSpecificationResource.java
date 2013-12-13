package org.openflexo.technologyadapter.diagram.rm;

import java.util.List;

import org.openflexo.foundation.rm.VirtualModelResource;
import org.openflexo.foundation.view.diagram.rm.DiagramPaletteResource;
import org.openflexo.foundation.view.diagram.rm.ExampleDiagramResource;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramSpecification;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(DiagramSpecificationResourceImpl.class)
@XMLElement
public interface DiagramSpecificationResource extends VirtualModelResource<DiagramSpecification> {

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
