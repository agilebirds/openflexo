package org.openflexo.technologyadapter.emf.rm;

import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.emf.model.EMFMetaModel;

@ModelEntity
@ImplementationClass(FlexoEMFMetaModelResourceImpl.class)
@XMLElement
public interface FlexoEMFMetaModelResource extends FlexoResource<EMFMetaModel> {

}
