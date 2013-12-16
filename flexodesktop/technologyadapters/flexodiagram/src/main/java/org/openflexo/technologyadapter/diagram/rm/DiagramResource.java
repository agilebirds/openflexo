package org.openflexo.technologyadapter.diagram.rm;

import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.PamelaResource;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.diagram.model.Diagram;
import org.openflexo.technologyadapter.diagram.model.DiagramFactory;

/**
 * This is the {@link FlexoResource} encoding a {@link Diagram}
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(DiagramResourceImpl.class)
@XMLElement
public interface DiagramResource extends PamelaResource<Diagram, DiagramFactory> {

	public static final String DIAGRAM_SUFFIX = ".diagram";

	public Diagram getDiagram();
}
