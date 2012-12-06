package org.openflexo.technologyadapter.emf.rm;

import org.openflexo.foundation.resource.FlexoFileResource;
import org.openflexo.foundation.technologyadapter.FlexoMetaModelResource;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.model.EMFModel;

@ModelEntity
@ImplementationClass(EMFMetaModelResourceImpl.class)
@XMLElement
public interface EMFMetaModelResource extends FlexoFileResource<EMFMetaModel>, FlexoMetaModelResource<EMFModel, EMFMetaModel> {

	public EMFMetaModel getMetaModel();
}
