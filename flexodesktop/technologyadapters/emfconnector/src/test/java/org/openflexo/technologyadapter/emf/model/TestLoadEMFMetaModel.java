package org.openflexo.technologyadapter.emf.model;

import org.junit.Test;
import org.openflexo.ApplicationContext;
import org.openflexo.TestApplicationContext;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.toolbox.FileResource;

public class TestLoadEMFMetaModel {

	@Test
	public void test() {
		ApplicationContext applicationContext = new TestApplicationContext(new FileResource("src/test/resources/EMF"));

		FlexoResourceCenter resourceCenter = applicationContext.getResourceCenterService().getResourceCenters().get(0);
		EMFTechnologyAdapter technologicalAdapter = (EMFTechnologyAdapter) applicationContext.getTechnologyAdapterService()
				.getTechnologyAdapters().get(0);
		// EMFMetaModelRepository metaModelRepository = (EMFMetaModelRepository)
		// resourceCenter.getMetaModelRepository(technologicalAdapter);
		// Collection<EMFMetaModelResource> metaModelResources = metaModelRepository.getAllResources();
	}
}