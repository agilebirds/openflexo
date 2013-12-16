package org.openflexo.technologyadapter.diagram.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Color;

import org.junit.Test;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.model.ModelEntity;
import org.openflexo.model.exceptions.ModelDefinitionException;

/**
 * Test Diagram model
 * 
 * @author sylvain
 * 
 */
public class TestDiagram {

	/**
	 * Test the diagram factory
	 */
	@Test
	public void testDiagramFactory() {

		try {
			DiagramFactory factory = new DiagramFactory();

			ModelEntity<Diagram> diagramEntity = factory.getModelContext().getModelEntity(Diagram.class);
			ModelEntity<DiagramShape> shapeEntity = factory.getModelContext().getModelEntity(DiagramShape.class);
			ModelEntity<DiagramConnector> connectorEntity = factory.getModelContext().getModelEntity(DiagramConnector.class);

			assertNotNull(diagramEntity);
			assertNotNull(shapeEntity);
			assertNotNull(connectorEntity);

		} catch (ModelDefinitionException e) {
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Test the diagram factory
	 */
	@Test
	public void testInstanciateDiagram() throws Exception {

		DiagramFactory factory = new DiagramFactory();

		Diagram diagram = factory.newInstance(Diagram.class);
		assertTrue(diagram instanceof Diagram);

		DiagramShape shape1 = factory.makeNewShape("Shape1", ShapeType.RECTANGLE, new FGEPoint(100, 100), diagram);
		shape1.getGraphicalRepresentation().setForeground(factory.makeForegroundStyle(Color.RED));
		shape1.getGraphicalRepresentation().setBackground(factory.makeColoredBackground(Color.BLUE));
		assertTrue(shape1 instanceof DiagramShape);
		DiagramShape shape2 = factory.makeNewShape("Shape2", ShapeType.RECTANGLE, new FGEPoint(200, 100), diagram);
		shape2.getGraphicalRepresentation().setForeground(factory.makeForegroundStyle(Color.BLUE));
		shape2.getGraphicalRepresentation().setBackground(factory.makeColoredBackground(Color.WHITE));
		assertTrue(shape2 instanceof DiagramShape);

		DiagramConnector connector1 = factory.makeNewConnector("Connector", shape1, shape2, diagram);
		assertTrue(connector1 instanceof DiagramConnector);

	}

}
