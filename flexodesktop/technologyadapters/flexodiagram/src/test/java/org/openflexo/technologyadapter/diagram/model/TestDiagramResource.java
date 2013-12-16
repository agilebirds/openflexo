package org.openflexo.technologyadapter.diagram.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Color;
import java.io.File;

import org.junit.Test;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.foundation.TestFlexoServiceManager;
import org.openflexo.model.ModelEntity;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.toolbox.FileResource;

/**
 * Test DiagramResource in Openflexo ResourceManager
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

		try {
			TestFlexoServiceManager applicationContext = new TestFlexoServiceManager(new FileResource(
					new File("src/test/resources").getAbsolutePath()));
			DiagramTechnologyAdapter technologicalAdapter = applicationContext.getTechnologyAdapterService().getTechnologyAdapter(
					DiagramTechnologyAdapter.class);

			FlexoResourceCenter<?> resourceCenter = applicationContext.getResourceCenterService().getResourceCenters().get(2);
		}
	}
}
