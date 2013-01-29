package org.openflexo.foundation.view.diagram.rm;

import org.openflexo.foundation.resource.FlexoXMLFileResource;
import org.openflexo.foundation.rm.DiagramSpecificationResource;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagram;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(ExampleDiagramResourceImpl.class)
@XMLElement
public interface ExampleDiagramResource extends FlexoXMLFileResource<ExampleDiagram> {

	public static final String VIEW_POINT_LIBRARY = "viewPointLibrary";

	public ExampleDiagram getExampleDiagram();

	@Getter(value = VIEW_POINT_LIBRARY, ignoreType = true)
	public ViewPointLibrary getViewPointLibrary();

	@Setter(VIEW_POINT_LIBRARY)
	public void setViewPointLibrary(ViewPointLibrary viewPointLibrary);

	@Override
	public DiagramSpecificationResource getContainer();
}
