package org.openflexo.foundation.view.diagram.rm;

import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.rm.VirtualModelInstanceResource;
import org.openflexo.foundation.view.diagram.model.Diagram;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

/**
 * This is the {@link FlexoResource} encoding a {@link Diagram}
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(DiagramResourceImpl.class)
@XMLElement
public interface DiagramResource extends VirtualModelInstanceResource<Diagram> {

	public static final String DIAGRAM_SUFFIX = ".diagram";

	public Diagram getDiagram();
}
