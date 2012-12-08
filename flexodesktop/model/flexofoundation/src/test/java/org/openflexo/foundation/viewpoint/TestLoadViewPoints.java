package org.openflexo.foundation.viewpoint;

import java.util.logging.Logger;

import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.resource.DefaultResourceCenterService;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.resource.UserResourceCenter;
import org.openflexo.toolbox.FileResource;

public class TestLoadViewPoints extends FlexoTestCase {

	protected static final Logger logger = Logger.getLogger(TestLoadViewPoints.class.getPackage().getName());

	private static UserResourceCenter resourceCenter;

	public TestLoadViewPoints(String name) {
		super(name);
	}

	/**
	 * Instantiate test resource center
	 */
	public void test0InstantiateResourceCenter() {

		log("test0UseTestApplicationContext()");

		FlexoResourceCenterService rcService = DefaultResourceCenterService.getNewInstance();
		rcService.addToResourceCenters(resourceCenter = new UserResourceCenter(new FileResource("src/test/resources/TestResourceCenter")));

		logger.info("ResourceCenter:" + rcService.getResourceCenters());

		resourceCenter.retrieveViewPointLibrary();

	}

}
