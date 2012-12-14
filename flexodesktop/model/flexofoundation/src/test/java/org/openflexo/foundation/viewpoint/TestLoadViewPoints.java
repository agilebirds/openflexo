package org.openflexo.foundation.viewpoint;

import java.util.logging.Logger;

import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.resource.DefaultResourceCenterService;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.resource.UserResourceCenter;
import org.openflexo.foundation.rm.ViewPointResource;
import org.openflexo.foundation.technologyadapter.DefaultTechnologyAdapterService;
import org.openflexo.toolbox.FileResource;

public class TestLoadViewPoints extends FlexoTestCase {

	protected static final Logger logger = Logger.getLogger(TestLoadViewPoints.class.getPackage().getName());

	private static UserResourceCenter resourceCenter;
	private static ViewPointLibrary viewPointLibrary;

	public TestLoadViewPoints(String name) {
		super(name);
	}

	/**
	 * Instantiate test resource center
	 */
	public void test0InstantiateResourceCenter() {

		log("test0InstantiateResourceCenter()");

		FlexoServiceManager sm = new FlexoServiceManager();
		FlexoResourceCenterService rcService = DefaultResourceCenterService.getNewInstance();
		rcService.addToResourceCenters(resourceCenter = new UserResourceCenter(new FileResource("src/test/resources/TestResourceCenter")));
		sm.registerService(rcService);
		sm.registerService(DefaultTechnologyAdapterService.getNewInstance(rcService));
		viewPointLibrary = new ViewPointLibrary();
		sm.registerService(viewPointLibrary);

		logger.info("ResourceCenter:" + rcService.getResourceCenters());

	}

	private void testLoadViewPoint(String viewPointURI) {

		log("Testing ViewPoint loading: " + viewPointURI);

		ViewPointResource vpRes = resourceCenter.getViewPointRepository().getResource(viewPointURI);

		assertNotNull(vpRes);
		assertFalse(vpRes.isLoaded());

		ViewPoint vp = vpRes.getViewPoint();
		assertTrue(vpRes.isLoaded());

	}

	public void test1LoadBasicOntology() {
		testLoadViewPoint("http://www.agilebirds.com/openflexo/ViewPoints/Basic/BasicOntology.owl");
	}

	public void test2LoadBDN() {
		testLoadViewPoint("http://www.agilebirds.com/openflexo/ViewPoints/ScopeDefinition/BenefitDependancyNetwork.owl");
	}

	public void test3LoadOC() {
		testLoadViewPoint("http://www.agilebirds.com/openflexo/ViewPoints/ScopeDefinition/OrganizationalChart.owl");
	}

	public void test4LoadOM() {
		testLoadViewPoint("http://www.agilebirds.com/openflexo/ViewPoints/ScopeDefinition/OrganizationalMap.owl");
	}

	public void test5LoadOUD() {
		testLoadViewPoint("http://www.agilebirds.com/openflexo/ViewPoints/ScopeDefinition/OrganizationalUnitDefinition.owl");
	}

	public void test6LoadSKOS() {
		testLoadViewPoint("http://www.agilebirds.com/openflexo/ViewPoints/SKOS/SKOSThesaurusEditor.owl");
	}

	public void test7LoadUMLPackage() {
		testLoadViewPoint("http://www.agilebirds.com/openflexo/ViewPoints/UML/PackageDiagram.owl");
	}

	public void test8LoadUMLUseCases() {
		testLoadViewPoint("http://www.agilebirds.com/openflexo/ViewPoints/UML/UseCaseDiagram.owl");
	}
}
