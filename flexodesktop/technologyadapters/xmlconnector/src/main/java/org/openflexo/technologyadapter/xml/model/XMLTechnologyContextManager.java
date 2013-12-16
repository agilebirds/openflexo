package org.openflexo.technologyadapter.xml.model;

import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.technologyadapter.xml.XMLTechnologyAdapter;

public class XMLTechnologyContextManager extends TechnologyContextManager<XMLTechnologyAdapter> {

	public XMLTechnologyContextManager(XMLTechnologyAdapter adapter, FlexoResourceCenterService resourceCenterService) {
		super(adapter, resourceCenterService);
	}

	@Override
	public XMLTechnologyAdapter getTechnologyAdapter() {
		return (XMLTechnologyAdapter) super.getTechnologyAdapter();
	}

}
