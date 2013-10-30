package org.openflexo.fge.drawingeditor;

import java.io.File;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.RootNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.drawingeditor.model.Connector;
import org.openflexo.fge.drawingeditor.model.Diagram;
import org.openflexo.fge.drawingeditor.model.DiagramFactory;
import org.openflexo.fge.drawingeditor.model.Shape;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.model.undo.CompoundEdit;

/**
 * This test is actually testing PAMELA undo/redo features applied to Diana model<br>
 * We also test here the coupling of Diana abstract representation layer with the PAMELA model on which undo/redo are applied
 * 
 * @author sylvain
 * 
 */
public class TestUndoRedoWithDrawing extends TestCase {

	private static DiagramFactory factory;

	private static Diagram diagram;
	private static DiagramDrawing diagramDrawing;

	private static CompoundEdit initDiagram;
	private static CompoundEdit addFirstShape;
	private static CompoundEdit addSecondShape;
	private static CompoundEdit addConnector;

	// private ModelContext modelContext;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		new File("/tmp").mkdirs();
		factory = new DiagramFactory();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Override
	@Before
	public void setUp() throws Exception {
		if (factory == null) {
			factory = new DiagramFactory();
		}
	}

	@Override
	@After
	public void tearDown() throws Exception {
	}

	private static RootNode<Diagram> root;
	private static ShapeNode<Shape> node1_1;
	private static ShapeNode<Shape> node2_1;
	private static ConnectorNode<Connector> connector_1;

	@Test
	public void test1Do() throws Exception {

		initDiagram = factory.getUndoManager().startRecording("Initialize new Diagram");
		diagram = factory.newInstance(Diagram.class);
		diagramDrawing = new DiagramDrawing(diagram, factory);
		diagramDrawing.updateGraphicalObjectsHierarchy();
		factory.getUndoManager().stopRecording(initDiagram);

		assertTrue(diagram instanceof Diagram);
		assertFalse(factory.getUndoManager().isBeeingRecording());
		System.out.println("PERFORMED: " + initDiagram.getPresentationName());
		System.out.println("edits nb=" + initDiagram.getEdits().size());
		System.out.println(initDiagram.describe());
		assertEquals(17, initDiagram.getEdits().size());

		addFirstShape = factory.getUndoManager().startRecording("Create first shape");
		Shape shape1 = factory.makeNewShape(ShapeType.RECTANGLE, new FGEPoint(100, 100), diagram);
		diagram.addToShapes(shape1);
		factory.getUndoManager().stopRecording(addFirstShape);

		assertTrue(shape1 instanceof Shape);
		assertFalse(factory.getUndoManager().isBeeingRecording());
		System.out.println("PERFORMED: " + addFirstShape.getPresentationName());
		System.out.println("edits nb=" + addFirstShape.getEdits().size());
		assertEquals(99, addFirstShape.getEdits().size());

		addSecondShape = factory.getUndoManager().startRecording("Create second shape");
		Shape shape2 = factory.makeNewShape(ShapeType.RECTANGLE, new FGEPoint(200, 100), diagram);
		diagram.addToShapes(shape2);
		factory.getUndoManager().stopRecording(addSecondShape);

		assertTrue(shape2 instanceof Shape);
		assertFalse(factory.getUndoManager().isBeeingRecording());
		System.out.println("PERFORMED: " + addSecondShape.getPresentationName());
		System.out.println("edits nb=" + addSecondShape.getEdits().size());
		assertEquals(99, addSecondShape.getEdits().size());

		addConnector = factory.getUndoManager().startRecording("Add connector");
		Connector connector1 = factory.makeNewConnector(shape1, shape2, diagram);
		diagram.addToConnectors(connector1);
		factory.getUndoManager().stopRecording(addConnector);

		assertTrue(connector1 instanceof Connector);
		assertFalse(factory.getUndoManager().isBeeingRecording());
		System.out.println("PERFORMED: " + addConnector.getPresentationName());
		System.out.println("edits nb=" + addConnector.getEdits().size());
		assertEquals(19, addConnector.getEdits().size());

		root = diagramDrawing.getRoot();
		assertEquals(3, root.getChildNodes().size());
		node1_1 = (ShapeNode<Shape>) root.getChildNodes().get(0);
		node2_1 = (ShapeNode<Shape>) root.getChildNodes().get(1);
		connector_1 = (ConnectorNode<Connector>) root.getChildNodes().get(2);

		assertNotNull(node1_1);
		assertNotNull(node2_1);
		assertNotNull(connector_1);
	}

	private static ShapeNode<Shape> node1_2;
	private static ShapeNode<Shape> node2_2;

	// UNDO create connector
	@Test
	public void test2Undo1() throws Exception {

		assertEquals(2, diagram.getShapes().size());
		assertEquals(1, diagram.getConnectors().size());
		Shape shape1 = diagram.getShapes().get(0);
		Shape shape2 = diagram.getShapes().get(1);
		Connector connector = diagram.getConnectors().get(0);

		assertNotNull(shape1);
		assertNotNull(shape2);
		assertNotNull(connector);

		assertTrue(factory.getUndoManager().canUndo());
		assertEquals(addConnector, factory.getUndoManager().editToBeUndone());

		factory.getUndoManager().undo();

		assertEquals(2, diagram.getShapes().size());
		assertEquals(0, diagram.getConnectors().size());
		assertTrue(connector.isDeleted());

		assertEquals(root, diagramDrawing.getRoot());
		assertEquals(2, root.getChildNodes().size());
		node1_2 = (ShapeNode<Shape>) root.getChildNodes().get(0);
		node2_2 = (ShapeNode<Shape>) root.getChildNodes().get(1);

		assertNotNull(node1_2);
		assertSame(node1_1, node1_2);
		assertNotNull(node2_2);
		assertSame(node2_1, node2_2);
		assertTrue(connector_1.isDeleted());

	}

	private static ShapeNode<Shape> node1_3;

	// UNDO create shape2
	@Test
	public void test3Undo2() throws Exception {

		assertEquals(2, diagram.getShapes().size());
		assertEquals(0, diagram.getConnectors().size());
		Shape shape1 = diagram.getShapes().get(0);
		Shape shape2 = diagram.getShapes().get(1);

		assertNotNull(shape1);
		assertNotNull(shape2);

		assertTrue(factory.getUndoManager().canUndo());
		assertEquals(addSecondShape, factory.getUndoManager().editToBeUndone());

		factory.getUndoManager().undo();

		assertEquals(1, diagram.getShapes().size());
		assertEquals(0, diagram.getConnectors().size());
		assertTrue(shape2.isDeleted());

		assertEquals(root, diagramDrawing.getRoot());
		assertEquals(1, root.getChildNodes().size());
		node1_3 = (ShapeNode<Shape>) root.getChildNodes().get(0);

		assertNotNull(node1_3);
		assertSame(node1_1, node1_2);
		assertSame(node1_2, node1_3);

		assertTrue(node2_2.isDeleted());

	}

	// UNDO create shape1
	@Test
	public void test4Undo3() throws Exception {

		assertEquals(1, diagram.getShapes().size());
		assertEquals(0, diagram.getConnectors().size());
		Shape shape1 = diagram.getShapes().get(0);

		assertNotNull(shape1);

		assertTrue(factory.getUndoManager().canUndo());
		assertEquals(addFirstShape, factory.getUndoManager().editToBeUndone());

		factory.getUndoManager().undo();

		assertEquals(0, diagram.getShapes().size());
		assertEquals(0, diagram.getConnectors().size());
		assertTrue(shape1.isDeleted());

		assertEquals(root, diagramDrawing.getRoot());
		assertEquals(0, root.getChildNodes().size());

		assertTrue(node1_1.isDeleted());

	}

	private static ShapeNode<Shape> node1_5;

	// REDO create shape1
	@Test
	public void test5Redo1() throws Exception {

		assertEquals(0, diagram.getShapes().size());
		assertEquals(0, diagram.getConnectors().size());

		assertTrue(factory.getUndoManager().canRedo());
		assertEquals(addFirstShape, factory.getUndoManager().editToBeRedone());

		factory.getUndoManager().redo();

		assertEquals(1, diagram.getShapes().size());
		assertEquals(0, diagram.getConnectors().size());
		Shape shape1 = diagram.getShapes().get(0);

		assertNotNull(shape1);
		assertFalse(shape1.isDeleted());

		assertEquals(root, diagramDrawing.getRoot());
		assertEquals(1, root.getChildNodes().size());
		node1_5 = (ShapeNode<Shape>) root.getChildNodes().get(0);

		assertNotNull(node1_5);
		assertNotSame(node1_3, node1_5);
		assertSame(shape1, node1_5.getDrawable());

	}

	private static ShapeNode<Shape> node1_6;
	private static ShapeNode<Shape> node2_6;

	// REDO create shape2
	@Test
	public void test6Redo2() throws Exception {

		assertEquals(1, diagram.getShapes().size());
		assertEquals(0, diagram.getConnectors().size());
		Shape shape1 = diagram.getShapes().get(0);

		assertNotNull(shape1);
		assertFalse(shape1.isDeleted());

		assertTrue(factory.getUndoManager().canRedo());
		assertEquals(addSecondShape, factory.getUndoManager().editToBeRedone());

		factory.getUndoManager().redo();

		assertEquals(2, diagram.getShapes().size());
		assertEquals(0, diagram.getConnectors().size());
		Shape shape2 = diagram.getShapes().get(1);

		assertNotNull(shape2);
		assertFalse(shape2.isDeleted());

		assertEquals(root, diagramDrawing.getRoot());
		assertEquals(2, root.getChildNodes().size());
		node1_6 = (ShapeNode<Shape>) root.getChildNodes().get(0);
		node2_6 = (ShapeNode<Shape>) root.getChildNodes().get(1);

		assertNotNull(node1_6);
		assertSame(node1_5, node1_6);
		assertNotSame(node1_3, node1_6);
		assertSame(shape1, node1_6.getDrawable());

		assertNotNull(node2_6);
		assertNotSame(node2_2, node2_6);
		assertSame(shape2, node2_6.getDrawable());
	}

	// REDO create connector
	@Test
	public void test7Redo3() throws Exception {

		assertEquals(2, diagram.getShapes().size());
		assertEquals(0, diagram.getConnectors().size());
		Shape shape1 = diagram.getShapes().get(0);
		Shape shape2 = diagram.getShapes().get(1);

		assertNotNull(shape1);
		assertNotNull(shape2);
		assertFalse(shape1.isDeleted());
		assertFalse(shape2.isDeleted());

		assertTrue(factory.getUndoManager().canRedo());
		assertEquals(addConnector, factory.getUndoManager().editToBeRedone());

		factory.getUndoManager().redo();

		assertEquals(2, diagram.getShapes().size());
		assertEquals(1, diagram.getConnectors().size());
		Connector connector = diagram.getConnectors().get(0);

		assertNotNull(connector);
		assertFalse(connector.isDeleted());

	}

}
