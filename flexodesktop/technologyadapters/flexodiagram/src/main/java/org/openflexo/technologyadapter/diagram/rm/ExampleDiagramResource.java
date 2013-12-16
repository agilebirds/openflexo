package org.openflexo.technologyadapter.diagram.rm;

import org.openflexo.foundation.resource.FlexoXMLFileResource;
import org.openflexo.foundation.resource.PamelaResource;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.diagram.fml.ExampleDiagram;
import org.openflexo.technologyadapter.diagram.fml.ExampleDiagramFactory;

@ModelEntity
@ImplementationClass(ExampleDiagramResourceImpl.class)
@XMLElement
public interface ExampleDiagramResource extends FlexoXMLFileResource<ExampleDiagram>, PamelaResource<ExampleDiagram, ExampleDiagramFactory> {

	public static final String VIEW_POINT_LIBRARY = "viewPointLibrary";

	/**
	 * Return example diagram stored by this resource<br>
	 * Load the resource data when unloaded
	 */
	public ExampleDiagram getExampleDiagram();

	/**
	 * Return example diagram stored by this resource<br>
	 * Do not force load the resource data
	 * 
	 * @return
	 */
	public ExampleDiagram getLoadedExampleDiagram();

	@Getter(value = VIEW_POINT_LIBRARY, ignoreType = true)
	public ViewPointLibrary getViewPointLibrary();

	@Setter(VIEW_POINT_LIBRARY)
	public void setViewPointLibrary(ViewPointLibrary viewPointLibrary);

	@Override
	public DiagramSpecificationResource getContainer();
}