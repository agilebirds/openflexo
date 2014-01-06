package org.openflexo.fge.drawingeditor;

import java.awt.Color;
import java.io.File;
import java.util.List;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openflexo.fge.drawingeditor.model.Connector;
import org.openflexo.fge.drawingeditor.model.Diagram;
import org.openflexo.fge.drawingeditor.model.DiagramFactory;
import org.openflexo.fge.drawingeditor.model.Shape;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.model.factory.Clipboard;

/**
 * This test is actually testing PAMELA copy/paste features applied to Diana model
 * 
 * @author sylvain
 * 
 */
public class TestCopyPaste extends TestCase {

	private DiagramFactory factory;

	// private ModelContext modelContext;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Override
	@Before
	public void setUp() throws Exception {
		new File("/tmp").mkdirs();
		// modelContext = new ModelContext(FlexoProcess.class);
		factory = new DiagramFactory();
	}

	@Override
	@After
	public void tearDown() throws Exception {
	}

	public void testCopyPaste() throws Exception {

		Diagram diagram = factory.newInstance(Diagram.class);
		assertTrue(diagram instanceof Diagram);

		Shape shape1 = factory.makeNewShape(ShapeType.RECTANGLE, new FGEPoint(100, 100), diagram);
		shape1.getGraphicalRepresentation().setForeground(factory.makeForegroundStyle(Color.RED));
		shape1.getGraphicalRepresentation().setBackground(factory.makeColoredBackground(Color.BLUE));
		assertTrue(shape1 instanceof Shape);
		Shape shape2 = factory.makeNewShape(ShapeType.RECTANGLE, new FGEPoint(200, 100), diagram);
		shape2.getGraphicalRepresentation().setForeground(factory.makeForegroundStyle(Color.BLUE));
		shape2.getGraphicalRepresentation().setBackground(factory.makeColoredBackground(Color.WHITE));
		assertTrue(shape2 instanceof Shape);

		Connector connector1 = factory.makeNewConnector(shape1, shape2, diagram);
		assertTrue(connector1 instanceof Connector);

		diagram.addToShapes(shape1);
		diagram.addToShapes(shape2);
		diagram.addToConnectors(connector1);

		Clipboard clipboard = factory.copy(shape1, shape2, connector1);
		assertFalse(clipboard.isSingleObject());
		assertEquals(3, clipboard.getMultipleContents().size());
		Shape shape3 = (Shape) clipboard.getMultipleContents().get(0);
		Shape shape4 = (Shape) clipboard.getMultipleContents().get(1);
		Connector connector2 = (Connector) clipboard.getMultipleContents().get(2);

		assertNotSame(shape1, shape3);
		assertNotSame(shape2, shape4);
		assertNotSame(connector1, connector2);

		assertFalse(shape1.equals(shape3));
		assertFalse(shape2.equals(shape4));
		assertFalse(connector1.equals(connector2));

		assertEquals(connector2.getStartShape(), shape3);
		assertEquals(connector2.getEndShape(), shape4);

		Object pasted = factory.paste(clipboard, diagram);

		assertEquals(4, diagram.getShapes().size());
		assertEquals(2, diagram.getConnectors().size());

		assertTrue(diagram.getShapes().contains(shape1));
		assertTrue(diagram.getShapes().contains(shape2));
		assertTrue(diagram.getShapes().contains(shape3));
		assertTrue(diagram.getShapes().contains(shape4));
		assertTrue(diagram.getConnectors().contains(connector1));
		assertTrue(diagram.getConnectors().contains(connector2));

		assertEquals(((List<?>) pasted).get(0), shape3);
		assertEquals(((List<?>) pasted).get(1), shape4);
		assertEquals(((List<?>) pasted).get(2), connector2);

		assertEquals(connector1.getStartShape(), shape1);
		assertEquals(connector1.getEndShape(), shape2);

		assertEquals(connector2.getStartShape(), shape3);
		assertEquals(connector2.getEndShape(), shape4);

		assertFalse(clipboard.isSingleObject());
		assertEquals(3, clipboard.getMultipleContents().size());
		Shape shape5 = (Shape) clipboard.getMultipleContents().get(0);
		Shape shape6 = (Shape) clipboard.getMultipleContents().get(1);
		Connector connector3 = (Connector) clipboard.getMultipleContents().get(2);

		assertNotSame(shape3, shape5);
		assertNotSame(shape4, shape6);
		assertNotSame(connector2, connector3);

		assertFalse(shape3.equals(shape5));
		assertFalse(shape4.equals(shape6));
		assertFalse(connector2.equals(connector3));

		assertEquals(connector3.getStartShape(), shape5);
		assertEquals(connector3.getEndShape(), shape6);

		assertNotSame(shape1.getGraphicalRepresentation().getForeground(), shape3.getGraphicalRepresentation().getForeground());
		assertEquals(shape1.getGraphicalRepresentation().getForeground(), shape1.getGraphicalRepresentation().getForeground());
		assertNotSame(shape1.getGraphicalRepresentation().getSelectedForeground(), shape3.getGraphicalRepresentation()
				.getSelectedForeground());
		assertEquals(shape1.getGraphicalRepresentation().getSelectedForeground(), shape1.getGraphicalRepresentation()
				.getSelectedForeground());
		assertNotSame(shape1.getGraphicalRepresentation().getFocusedForeground(), shape3.getGraphicalRepresentation()
				.getFocusedForeground());
		assertEquals(shape1.getGraphicalRepresentation().getFocusedForeground(), shape1.getGraphicalRepresentation().getFocusedForeground());

		assertNotSame(shape1.getGraphicalRepresentation().getBackground(), shape3.getGraphicalRepresentation().getBackground());
		assertEquals(shape1.getGraphicalRepresentation().getBackground(), shape1.getGraphicalRepresentation().getBackground());
		assertNotSame(shape1.getGraphicalRepresentation().getSelectedBackground(), shape3.getGraphicalRepresentation()
				.getSelectedBackground());
		assertEquals(shape1.getGraphicalRepresentation().getSelectedBackground(), shape1.getGraphicalRepresentation()
				.getSelectedBackground());
		assertNotSame(shape1.getGraphicalRepresentation().getFocusedBackground(), shape3.getGraphicalRepresentation()
				.getFocusedBackground());
		assertEquals(shape1.getGraphicalRepresentation().getFocusedBackground(), shape1.getGraphicalRepresentation().getFocusedBackground());

	}

}
