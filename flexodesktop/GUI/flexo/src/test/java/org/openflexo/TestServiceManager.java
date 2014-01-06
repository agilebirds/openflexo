package org.openflexo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.logging.Logger;

import org.junit.Test;
import org.openflexo.foundation.OpenflexoTestCase;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.module.ModuleLoader;
import org.openflexo.module.ProjectLoader;
import org.openflexo.view.controller.TechnologyAdapterControllerService;

public class TestServiceManager extends OpenflexoTestCase {

	protected static final Logger logger = Logger.getLogger(TestServiceManager.class.getPackage().getName());

	private static ApplicationContext testApplicationContext;
	private static FlexoResourceCenter resourceCenter;

	/**
	 * Instanciate test ApplicationContext
	 */
	@Test
	public void test0UseTestApplicationContext() {
		log("test0UseTestApplicationContext()");
		testApplicationContext = new TestApplicationContext();
		resourceCenter = testApplicationContext.getResourceCenterService().getResourceCenters().get(0);

		logger.info("services: " + testApplicationContext.getRegisteredServices());

		assertNotNull(testApplicationContext.getService(ProjectLoader.class));
		assertNotNull(testApplicationContext.getService(ModuleLoader.class));
		assertNotNull(testApplicationContext.getService(FlexoResourceCenterService.class));
		assertNotNull(testApplicationContext.getService(TechnologyAdapterService.class));
		assertNotNull(testApplicationContext.getService(TechnologyAdapterControllerService.class));

	}

	/**
	 * Try to load a module
	 */
	@Test
	public void test1ModuleLoading() {
		ModuleLoader moduleLoader = testApplicationContext.getModuleLoader();
		assertEquals(moduleLoader, testApplicationContext.getService(ModuleLoader.class));

		/*UserType.setCurrentUserType(UserType.MAINTAINER);

		try {
			ExternalModule loadedModule = moduleLoader.getVPMModuleInstance();
			if (loadedModule != null) {
				fail();
			}
			// This module is not in the classpath, normal
		} catch (ModuleLoadingException e) {
			fail();
		}*/
	}
}
