package org.openflexo.technologyadapter.xsd;

import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.technologyadapter.xsd.metamodel.XSDMetaModel;
import org.openflexo.technologyadapter.xsd.model.XMLModel;

public class XSDTechnologyContextManager extends TechnologyContextManager<XMLModel, XSDMetaModel> {

	public XSDTechnologyContextManager(XSDTechnologyAdapter adapter, FlexoResourceCenterService resourceCenterService) {
		super(adapter, resourceCenterService);
	}

	@Override
	public XSDTechnologyAdapter getTechnologyAdapter() {
		return (XSDTechnologyAdapter) super.getTechnologyAdapter();
	}

}
