package org.openflexo;

import java.util.logging.Logger;

import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.module.ModuleLoader;
import org.openflexo.module.ModuleLoadingException;
import org.openflexo.module.ProjectLoader;
import org.openflexo.module.UserType;
import org.openflexo.module.external.ExternalModule;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.controller.TechnologyAdapterControllerService;

public class TestServiceManagerFull extends FlexoTestCase {

	protected static final Logger logger = Logger.getLogger(TestServiceManagerFull.class.getPackage().getName());

	private static ApplicationContext testApplicationContext;
	private static FlexoResourceCenter resourceCenter;
	private static ModuleLoader moduleLoader;

	public TestServiceManagerFull(String name) {
		super(name);
	}

	/**
	 * Instanciate test ApplicationContext
	 */
	public void test0UseTestApplicationContext() {
		log("test0UseTestApplicationContext()");
		testApplicationContext = new TestApplicationContext(new FileResource("src/test/resources/TestResourceCenter"));
		resourceCenter = testApplicationContext.getResourceCenterService().getResourceCenters().get(0);

		logger.info("services: " + testApplicationContext.getRegisteredServices());

		assertNotNull(testApplicationContext.getService(ProjectLoader.class));
		assertNotNull(testApplicationContext.getService(ModuleLoader.class));
		assertNotNull(testApplicationContext.getService(FlexoResourceCenterService.class));
		assertNotNull(testApplicationContext.getService(TechnologyAdapterService.class));
		assertNotNull(testApplicationContext.getService(TechnologyAdapterControllerService.class));

		moduleLoader = testApplicationContext.getModuleLoader();
		assertEquals(moduleLoader, testApplicationContext.getService(ModuleLoader.class));

		UserType.setCurrentUserType(UserType.MAINTAINER);

	}

	/**
	 * Try to load WKF module
	 */
	public void test1WKFModuleLoading() {
		log("test1WKFModuleLoading()");

		try {
			ExternalModule loadedModule = moduleLoader.getWKFModuleInstance();
			if (loadedModule == null) {
				fail();
			}
			// This module is not in the classpath, normal
		} catch (ModuleLoadingException e) {
			fail();
		}

		assertNotNull(testApplicationContext.getService(TechnologyAdapterControllerService.class));

	}

	/**
	 * Try to load a module
	 */
	public void test2VPMModuleLoading() {
		log("test2VPMModuleLoading()");

		try {
			ExternalModule loadedModule = moduleLoader.getVPMModuleInstance();
			if (loadedModule == null) {
				fail();
			}
			// This module is not in the classpath, normal
		} catch (ModuleLoadingException e) {
			fail();
		}

		assertNotNull(testApplicationContext.getService(TechnologyAdapterControllerService.class));

	}
}
