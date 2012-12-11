package org.openflexo;

import java.util.logging.Logger;

import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.module.FlexoModule;
import org.openflexo.module.Module;
import org.openflexo.module.ModuleLoader;
import org.openflexo.module.ModuleLoadingException;
import org.openflexo.module.ProjectLoader;
import org.openflexo.module.UserType;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.controller.TechnologyAdapterControllerService;

public class TestLoadAllModules extends FlexoTestCase {

	protected static final Logger logger = Logger.getLogger(TestLoadAllModules.class.getPackage().getName());

	private static ApplicationContext testApplicationContext;
	private static FlexoResourceCenter resourceCenter;
	private static ModuleLoader moduleLoader;

	public TestLoadAllModules(String name) {
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
			FlexoModule loadedModule = moduleLoader.getModuleInstance(Module.WKF_MODULE);
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
	 * Try to load IE module
	 */
	public void test2IEModuleLoading() {
		log("test2IEModuleLoading()");

		try {
			FlexoModule loadedModule = moduleLoader.getModuleInstance(Module.IE_MODULE);
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
	 * Try to load DM module
	 */
	public void test3DMModuleLoading() {
		log("test3DMModuleLoading()");

		try {
			FlexoModule loadedModule = moduleLoader.getModuleInstance(Module.DM_MODULE);
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
	 * Try to load VE module
	 */
	public void test4VEModuleLoading() {
		log("test4VEModuleLoading()");

		try {
			FlexoModule loadedModule = moduleLoader.getModuleInstance(Module.VE_MODULE);
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
	 * Try to load CG module
	 */
	public void test5CGModuleLoading() {
		log("test5CGModuleLoading()");

		try {
			FlexoModule loadedModule = moduleLoader.getModuleInstance(Module.CG_MODULE);
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
	 * Try to load SG module
	 */
	public void test6SGModuleLoading() {
		log("test6SGModuleLoading()");

		try {
			FlexoModule loadedModule = moduleLoader.getModuleInstance(Module.SG_MODULE);
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
	 * Try to load DG module
	 */
	public void test7DGModuleLoading() {
		log("test7DGModuleLoading()");

		try {
			FlexoModule loadedModule = moduleLoader.getModuleInstance(Module.DG_MODULE);
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
	 * Try to load VPM module
	 */
	public void test8VPMModuleLoading() {
		log("test8VPMModuleLoading()");

		try {
			FlexoModule loadedModule = moduleLoader.getModuleInstance(Module.VPM_MODULE);
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
