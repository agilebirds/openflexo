package org.openflexo.technologyadapter.xsd.rm;

import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.xsd.model.XSDMetaModel;

@ModelEntity
@ImplementationClass(FlexoXSDMetaModelResourceImpl.class)
@XMLElement
public interface FlexoXSDMetaModelResource extends FlexoResource<XSDMetaModel> {

}
