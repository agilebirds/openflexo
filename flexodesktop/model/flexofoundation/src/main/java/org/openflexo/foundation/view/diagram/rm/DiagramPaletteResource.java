package org.openflexo.foundation.view.diagram.rm;

import org.openflexo.foundation.resource.FlexoXMLFileResource;
import org.openflexo.foundation.resource.PamelaResource;
import org.openflexo.foundation.rm.DiagramSpecificationResource;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPalette;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPaletteFactory;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(DiagramPaletteResourceImpl.class)
@XMLElement
public interface DiagramPaletteResource extends FlexoXMLFileResource<DiagramPalette>, PamelaResource<DiagramPalette, DiagramPaletteFactory> {

	public static final String VIEW_POINT_LIBRARY = "viewPointLibrary";

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

	@Getter(value = VIEW_POINT_LIBRARY, ignoreType = true)
	public ViewPointLibrary getViewPointLibrary();

	@Setter(VIEW_POINT_LIBRARY)
	public void setViewPointLibrary(ViewPointLibrary viewPointLibrary);

	@Override
	public DiagramSpecificationResource getContainer();

}
