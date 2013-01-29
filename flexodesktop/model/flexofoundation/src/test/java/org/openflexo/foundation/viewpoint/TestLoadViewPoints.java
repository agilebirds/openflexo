package org.openflexo.foundation.viewpoint;

import java.util.logging.Logger;

import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.rm.ViewPointResource;
import org.openflexo.foundation.rm.VirtualModelResource;
import org.openflexo.foundation.view.diagram.rm.DiagramPaletteResource;
import org.openflexo.foundation.view.diagram.rm.ExampleDiagramResource;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPalette;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagram;

public class TestLoadViewPoints extends FlexoTestCase {

	protected static final Logger logger = Logger.getLogger(TestLoadViewPoints.class.getPackage().getName());

	public TestLoadViewPoints(String name) {
		super(name);
	}

	/**
	 * Instantiate test resource center
	 */
	public void test0InstantiateResourceCenter() {

		log("test0InstantiateResourceCenter()");

		instanciateTestServiceManager();
	}

	private void testLoadViewPoint(String viewPointURI) {

		log("Testing ViewPoint loading: " + viewPointURI);

		ViewPointResource vpRes = resourceCenter.getViewPointRepository().getResource(viewPointURI);

		assertNotNull(vpRes);
		assertFalse(vpRes.isLoaded());

		ViewPoint vp = vpRes.getViewPoint();
		assertNotNull(vp);
		assertTrue(vpRes.isLoaded());

		for (FlexoResource<?> r : vpRes.getContents()) {
			assertTrue(r instanceof VirtualModelResource);
			VirtualModelResource<?> vmRes = (VirtualModelResource<?>) r;
			assertFalse(vmRes.isLoaded());
			VirtualModel<?> vm = vmRes.getVirtualModel();
			assertNotNull(vm);
			assertTrue(vmRes.isLoaded());
			for (FlexoResource<?> r2 : vmRes.getContents()) {
				assertTrue(r2 instanceof ExampleDiagramResource || r2 instanceof DiagramPaletteResource);
				if (r2 instanceof ExampleDiagramResource) {
					ExampleDiagramResource edRes = (ExampleDiagramResource) r2;
					assertFalse(edRes.isLoaded());
					ExampleDiagram ed = edRes.getExampleDiagram();
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
