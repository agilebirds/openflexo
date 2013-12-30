package org.openflexo.foundation.viewpoint;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;

import org.junit.Test;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.viewpoint.rm.ViewPointResource;
import org.openflexo.foundation.viewpoint.rm.VirtualModelResource;
import org.openflexo.technologyadapter.diagram.metamodel.DiagramPalette;
import org.openflexo.technologyadapter.diagram.model.Diagram;
import org.openflexo.technologyadapter.diagram.rm.DiagramPaletteResource;
import org.openflexo.technologyadapter.diagram.rm.DiagramResource;

public class TestLoadViewPoints extends ViewPointTestCase {

	protected static final Logger logger = Logger.getLogger(TestLoadViewPoints.class.getPackage().getName());

	/**
	 * Instantiate test resource center
	 */
	@Test
	public void test0InstantiateResourceCenter() {

		log("test0InstantiateResourceCenter()");

		// TODO: create a project where all those tests don't need a manual import of projects
		// TODO: copy all test VP in tmp dir and work with those VP instead of polling GIT workspace
		instanciateTestServiceManager();
	}

	private void testLoadViewPoint(String viewPointURI) {

		log("Testing ViewPoint loading: " + viewPointURI);

		System.out.println("resourceCenter=" + resourceCenter);
		System.out.println("resourceCenter.getViewPointRepository()=" + resourceCenter.getViewPointRepository());

		ViewPointResource vpRes = resourceCenter.getViewPointRepository().getResource(viewPointURI);

		assertNotNull(vpRes);
		assertFalse(vpRes.isLoaded());

		ViewPoint vp = vpRes.getViewPoint();
		assertNotNull(vp);
		assertTrue(vpRes.isLoaded());

		for (FlexoResource<?> r : vpRes.getContents()) {
			assertTrue(r instanceof VirtualModelResource);
			VirtualModelResource vmRes = (VirtualModelResource) r;
			VirtualModel vm = vmRes.getVirtualModel();
			assertNotNull(vm);
			assertTrue(vmRes.isLoaded());
			for (FlexoResource<?> r2 : vmRes.getContents()) {
				assertTrue(r2 instanceof DiagramResource || r2 instanceof DiagramPaletteResource);
				if (r2 instanceof DiagramResource) {
					DiagramResource edRes = (DiagramResource) r2;
					assertFalse(edRes.isLoaded());
					Diagram ed = edRes.getDiagram();
					assertNotNull(ed);
					assertTrue(edRes.isLoaded());
				}
				if (r2 instanceof DiagramPaletteResource) {
					DiagramPaletteResource dpRes = (DiagramPaletteResource) r2;
					assertFalse(dpRes.isLoaded());
					DiagramPalette dp = dpRes.getDiagramPalette();
					assertNotNull(dp);
					assertTrue(dpRes.isLoaded());
				}
			}
		}

	}

	@Test
	public void test1LoadBasicOntology() {
		testLoadViewPoint("http://www.agilebirds.com/openflexo/ViewPoints/Basic/BasicOntology.owl");
	}

	@Test
	public void test2LoadBDN() {
		testLoadViewPoint("http://www.agilebirds.com/openflexo/ViewPoints/ScopeDefinition/BenefitDependancyNetwork.owl");
	}

	@Test
	public void test3LoadOC() {
		testLoadViewPoint("http://www.agilebirds.com/openflexo/ViewPoints/ScopeDefinition/OrganizationalChart.owl");
	}

	@Test
	public void test4LoadOM() {
		testLoadViewPoint("http://www.agilebirds.com/openflexo/ViewPoints/ScopeDefinition/OrganizationalMap.owl");
	}

	@Test
	public void test5LoadOUD() {
		testLoadViewPoint("http://www.agilebirds.com/openflexo/ViewPoints/ScopeDefinition/OrganizationalUnitDefinition.owl");
	}

	@Test
	public void test6LoadSKOS() {
		testLoadViewPoint("http://www.agilebirds.com/openflexo/ViewPoints/SKOS/SKOSThesaurusEditor.owl");
	}

	@Test
	public void test7LoadUMLPackage() {
		testLoadViewPoint("http://www.agilebirds.com/openflexo/ViewPoints/UML/PackageDiagram.owl");
	}

	@Test
	public void test8LoadUMLUseCases() {
		testLoadViewPoint("http://www.agilebirds.com/openflexo/ViewPoints/UML/UseCaseDiagram.owl");
	}
}
