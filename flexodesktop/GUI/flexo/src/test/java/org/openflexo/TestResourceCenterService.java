package org.openflexo;

import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;

/**
 * Implementation of a {@link FlexoResourceCenterService} with a unique {@link FlexoResourceCenter} declared as a file-system based resource
 * center (test purposes)
 * 
 * @author sylvain
 * 
 */
public abstract class TestResourceCenterService implements FlexoResourceCenterService {

	public static TestResourceCenterService getNewInstance() {
		try {
			ModelFactory factory = new ModelFactory().importClass(FlexoResourceCenterService.class);
			factory.setImplementingClassForInterface(TestResourceCenterService.class, FlexoResourceCenterService.class);
			TestResourceCenterService returned = (TestResourceCenterService) factory.newInstance(FlexoResourceCenterService.class);
			return returned;
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void registerTechnologyAdapterService(TechnologyAdapterService technologyAdapterService) {
		for (FlexoResourceCenter rc : getResourceCenters()) {
			rc.initialize(technologyAdapterService);
		}
	}

}