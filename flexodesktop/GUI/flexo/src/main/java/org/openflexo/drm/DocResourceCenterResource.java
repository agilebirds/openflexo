package org.openflexo.drm;

import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.PamelaResource;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

/**
 * This is the {@link FlexoResource} encoding a {@link DocResourceCenter}
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(DocResourceCenterResourceImpl.class)
@XMLElement
public interface DocResourceCenterResource extends PamelaResource<DocResourceCenter, DRMModelFactory> {

	public static final String DIAGRAM_SUFFIX = ".diagram";

	public DocResourceCenter getDocResourceCenter();

}
