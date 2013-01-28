package org.openflexo.foundation.rm;

import org.openflexo.foundation.resource.FlexoProjectResource;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoXMLFileResource;
import org.openflexo.foundation.view.View;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

/**
 * This is the {@link FlexoResource} encoding a {@link View}
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(ViewResourceImpl.class)
@XMLElement
public interface ViewResource extends FlexoXMLFileResource<View>, FlexoProjectResource<View> {

	public View getView();

}
