package org.openflexo.technologyadapter.xsd.model;

import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.technologyadapter.xsd.XSDTechnologyAdapter;

public class XSDTechnologyContextManager extends TechnologyContextManager {

	public XSDTechnologyContextManager(XSDTechnologyAdapter adapter, FlexoResourceCenterService resourceCenterService) {
		super(adapter, resourceCenterService);
	}

	@Override
	public XSDTechnologyAdapter getTechnologyAdapter() {
		return (XSDTechnologyAdapter) super.getTechnologyAdapter();
	}

}
