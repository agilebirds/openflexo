package org.openflexo.technologyadapter.xsd.rm;

import org.openflexo.foundation.resource.FlexoFileResource;
import org.openflexo.foundation.technologyadapter.FlexoMetaModelResource;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterResource;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.xsd.metamodel.XSDMetaModel;
import org.openflexo.technologyadapter.xsd.model.XMLXSDModel;

@ModelEntity
@ImplementationClass(XSDMetaModelResourceImpl.class)
@XMLElement
public interface XSDMetaModelResource extends FlexoFileResource<XSDMetaModel>, TechnologyAdapterResource<XSDMetaModel>, FlexoMetaModelResource<XMLXSDModel, XSDMetaModel> {

	@Override
	public XSDMetaModel getMetaModelData();
}
