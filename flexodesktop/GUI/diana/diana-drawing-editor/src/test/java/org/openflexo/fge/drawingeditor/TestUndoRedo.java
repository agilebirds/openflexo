package org.openflexo.fge.drawingeditor;

import java.io.File;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openflexo.fge.drawingeditor.model.Connector;
import org.openflexo.fge.drawingeditor.model.Diagram;
import org.openflexo.fge.drawingeditor.model.DiagramFactory;
import org.openflexo.fge.drawingeditor.model.Shape;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.model.undo.CompoundEdit;

/**
 * This test is actually testing PAMELA undo/redo features applied to Diana model
 * 
 * @author sylvain
 * 
 */
public class TestUndoRedo extends TestCase {

	private static DiagramFactory factory;

	private static Diagram diagram;

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

	@Test
	public void test1Do() throws Exception {

		initDiagram = factory.getUndoManager().startRecording("Initialize new Diagram");
		diagram = factory.newInstance(Diagram.class);
		factory.getUndoManager().stopRecording(initDiagram);

		assertTrue(diagram instanceof Diagram);
		assertFalse(factory.getUndoManager().isBeeingRecording());
		System.out.println("PERFORMED: " + initDiagram.getPresentationName());
		System.out.println("edits nb=" + initDiagram.getEdits().size());
		assertEquals(1, initDiagram.getEdits().size());

		addFirstShape = factory.getUndoManager().startRecording("Create first shape");
		Shape shape1 = factory.makeNewShape(ShapeType.RECTANGLE, new FGEPoint(100, 100), diagram);
		diagram.addToShapes(shape1);
		factory.getUndoManager().stopRecording(addFirstShape);

		assertTrue(shape1 instanceof Shape);
		assertFalse(factory.getUndoManager().isBeeingRecording());
		System.out.println("PERFORMED: " + addFirstShape.getPresentationName());
		System.out.println("edits nb=" + addFirstShape.getEdits().size());
		assertEquals(326, addFirstShape.getEdits().size());

		addSecondShape = factory.getUndoManager().startRecording("Create second shape");
		Shape shape2 = factory.makeNewShape(ShapeType.RECTANGLE, new FGEPoint(200, 100), diagram);
		diagram.addToShapes(shape2);
		factory.getUndoManager().stopRecording(addSecondShape);

		assertTrue(shape2 instanceof Shape);
		assertFalse(factory.getUndoManager().isBeeingRecording());
		System.out.println("PERFORMED: " + addSecondShape.getPresentationName());
		System.out.println("edits nb=" + addSecondShape.getEdits().size());
		assertEquals(326, addSecondShape.getEdits().size());

		addConnector = factory.getUndoManager().startRecording("Add connector");
		Connector connector1 = factory.makeNewConnector(shape1, shape2, diagram);
		diagram.addToConnectors(connector1);
		factory.getUndoManager().stopRecording(addConnector);

		assertTrue(connector1 instanceof Connector);
		assertFalse(factory.getUndoManager().isBeeingRecording());
		System.out.println("PERFORMED: " + addConnector.getPresentationName());
		System.out.println("edits nb=" + addConnector.getEdits().size());
		assertEquals(28, addConnector.getEdits().size());

	}

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
	}

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
	}

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

	}

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
