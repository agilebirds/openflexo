package org.openflexo.technologyadapter.diagram.model;

import java.io.File;

import org.junit.Test;
import org.openflexo.foundation.TestFlexoServiceManager;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.technologyadapter.diagram.DiagramTechnologyAdapter;
import org.openflexo.toolbox.FileResource;

/**
 * Test ProjectDataResource in Openflexo ResourceManager
 * 
 * @author sylvain
 * 
 */
public class TestDiagramResource {

	/**
	 * Test the diagram resource
	 */
	@Test
	public void testDiagramResource() {

		// try {
		TestFlexoServiceManager applicationContext = new TestFlexoServiceManager(new FileResource(
				new File("src/test/resources").getAbsolutePath()));
		DiagramTechnologyAdapter technologicalAdapter = applicationContext.getTechnologyAdapterService().getTechnologyAdapter(
				DiagramTechnologyAdapter.class);

		FlexoResourceCenter<?> resourceCenter = applicationContext.getResourceCenterService().getResourceCenters().get(2);
		// }
	}
}
