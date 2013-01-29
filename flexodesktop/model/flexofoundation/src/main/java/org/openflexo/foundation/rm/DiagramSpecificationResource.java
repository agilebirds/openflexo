package org.openflexo.foundation.rm;

import org.openflexo.foundation.view.diagram.viewpoint.DiagramSpecification;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(DiagramSpecificationResourceImpl.class)
@XMLElement
public interface DiagramSpecificationResource extends VirtualModelResource<DiagramSpecification> {

	public DiagramSpecification getDiagramSpecification();

}
