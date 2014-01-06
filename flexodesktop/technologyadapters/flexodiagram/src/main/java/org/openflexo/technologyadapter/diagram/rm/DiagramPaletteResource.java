package org.openflexo.technologyadapter.diagram.rm;

import org.openflexo.foundation.resource.PamelaResource;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.diagram.fml.DiagramPaletteFactory;
import org.openflexo.technologyadapter.diagram.metamodel.DiagramPalette;

@ModelEntity
@ImplementationClass(DiagramPaletteResourceImpl.class)
@XMLElement
public interface DiagramPaletteResource extends PamelaResource<DiagramPalette, DiagramPaletteFactory> {

	/**
	 * Return diagram palette stored by this resource<br>
	 * Load the resource data when unloaded
	 */
	public DiagramPalette getDiagramPalette();

	/**
	 * Return diagram palette stored by this resource<br>
	 * Do not force load the resource data
	 * 
	 * @return
	 */
	public DiagramPalette getLoadedDiagramPalette();

	@Override
	public DiagramSpecificationResource getContainer();

}
