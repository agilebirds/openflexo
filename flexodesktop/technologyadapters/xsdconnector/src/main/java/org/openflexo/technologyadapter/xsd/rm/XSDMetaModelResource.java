package org.openflexo.technologyadapter.xsd.rm;

import org.openflexo.foundation.resource.FlexoFileResource;
import org.openflexo.foundation.technologyadapter.FlexoMetaModelResource;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.xsd.model.XMLModel;
import org.openflexo.technologyadapter.xsd.model.XSDMetaModel;

@ModelEntity
@ImplementationClass(XSDMetaModelResourceImpl.class)
@XMLElement
public interface XSDMetaModelResource extends FlexoFileResource<XSDMetaModel>, FlexoMetaModelResource<XMLModel, XSDMetaModel> {

	public XSDMetaModel getMetaModel();
}
