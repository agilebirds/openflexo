package org.openflexo.fge.drawingeditor;

import java.io.File;

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

public class DrawingEditorTest extends TestCase {

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

	public void test1() throws Exception {

		Diagram diagram = factory.newInstance(Diagram.class);
		assertTrue(diagram instanceof Diagram);

		Shape shape1 = factory.makeNewShape(ShapeType.RECTANGLE, new FGEPoint(100, 100), null);
		assertTrue(shape1 instanceof Shape);
		Shape shape2 = factory.makeNewShape(ShapeType.RECTANGLE, new FGEPoint(200, 100), null);
		assertTrue(shape2 instanceof Shape);

		Connector connector1 = factory.makeNewConnector(shape1, shape2, null);
		assertTrue(connector1 instanceof Connector);

		/*FlexoProcess process = factory.newInstance(FlexoProcess.class);
		assertTrue(process instanceof FlexoProcess);
		try {
			process.getName();
			fail("getName() should not be invokable until init() has been called");
		} catch (UnitializedEntityException e) {
			// OK this on purpose.
		}
		process.init("234XX");
		System.out.println("process=" + process);
		System.out.println("Id=" + process.getFlexoID());
		process.setName("NewProcess");
		process.setFoo(8);
		assertEquals("NewProcess", process.getName());
		assertEquals("234XX", process.getFlexoID());
		assertEquals(8, process.getFoo());

		ActivityNode activityNode = factory.newInstance(ActivityNode.class);
		activityNode.init();
		assertTrue(activityNode instanceof ActivityNode);
		assertEquals("0000", activityNode.getFlexoID());

		activityNode.setName("MyActivity");
		process.addToNodes(activityNode);

		System.out.println("activityNode=" + activityNode);
		assertEquals("MyActivity", activityNode.getName());
		assertTrue(process.getNodes().contains(activityNode));
		assertEquals(process, activityNode.getProcess());
		System.out.println("process: " + activityNode.getProcess());

		StartNode startNode = factory.newInstance(StartNode.class);
		startNode.setName("Start");
		process.addToNodes(startNode);

		EndNode endNode = factory.newInstance(EndNode.class);
		endNode.init();
		endNode.setName("End");
		process.addToNodes(endNode);

		System.out.println("process=" + process);

		TokenEdge edge1 = (TokenEdge) factory.newInstance(TokenEdge.class).init(startNode, activityNode);
		edge1.setName("edge1");
		// startNode.addToOutgoingEdges(edge1);
		// activityNode.addToIncomingEdges(edge1);
		System.out.println("edge1=" + edge1);
		assertEquals(process, edge1.getProcess());

		TokenEdge edge2 = factory.newInstance(TokenEdge.class, "edge2", activityNode, endNode);
		// edge2.setStartNode(activityNode);
		// edge2.setEndNode(endNode);

		System.out.println("edge2=" + edge2);
		assertEquals(process, edge2.getProcess());

		try {
			FileOutputStream fos = new FileOutputStream("/tmp/TestFile.xml");
			factory.serialize(process, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		}*/

	}

}
