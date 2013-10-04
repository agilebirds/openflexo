package org.openflexo.fge;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.model.exceptions.ModelDefinitionException;

public class TestGraphDrawing {

	private static FGEModelFactory FACTORY;
	private static Graph graph;
	private static GraphNode node1, node2, node3, node4;
	private static GraphDrawing1 graphDrawing1;
	private static GraphDrawing2 graphDrawing2;
	private static GraphDrawing3 graphDrawing3;

	@BeforeClass
	public static void beforeClass() throws ModelDefinitionException {
		FACTORY = new FGEModelFactory();
	}

	@AfterClass
	public static void afterClass() {
		// FACTORY.delete();
	}

	@Before
	public void before() {
	}

	@After
	public void after() {
	}

	@Test
	public void initGraph() {
		graph = new Graph();
		node1 = new GraphNode("node1", graph);
		node2 = new GraphNode("node2", graph);
		node3 = new GraphNode("node3", graph);
		node1.connectTo(node2);
		node1.connectTo(node3);
		node3.connectTo(node2);
		assertEquals(0, node1.getInputEdges().size());
		assertEquals(2, node1.getOutputEdges().size());
		assertEquals(2, node2.getInputEdges().size());
		assertEquals(0, node2.getOutputEdges().size());
		assertEquals(1, node3.getInputEdges().size());
		assertEquals(1, node3.getOutputEdges().size());
	}

	@Test
	public void initGraphDrawing1() {
		System.out.println("INIT graph drawing 1 ********************** ");
		graphDrawing1 = new GraphDrawing1(graph, FACTORY);
		assertNotNull(graphDrawing1.getRoot());
		System.out.println("Root = " + graphDrawing1.getRoot());
		System.out.println("GR = " + graphDrawing1.getRoot().getGraphicalRepresentation());
	}

	@Test
	public void initGraphicalObjectHierarchyForGraphDrawing1() {
		ShapeNode<GraphNode> graphNode1 = (ShapeNode<GraphNode>) graphDrawing1.getRoot().getChildNodes().get(0);
		assertNotNull(graphNode1);
		assertEquals(node1, graphNode1.getDrawable());
		ShapeNode<GraphNode> graphNode2 = (ShapeNode<GraphNode>) graphDrawing1.getRoot().getChildNodes().get(1);
		assertNotNull(graphNode2);
		assertEquals(node2, graphNode2.getDrawable());
		ShapeNode<GraphNode> graphNode3 = (ShapeNode<GraphNode>) graphDrawing1.getRoot().getChildNodes().get(2);
		assertNotNull(graphNode3);
		assertEquals(node3, graphNode3.getDrawable());
		ConnectorNode<Edge> connectorNode1 = (ConnectorNode<Edge>) graphDrawing1.getRoot().getChildNodes().get(3);
		assertNotNull(connectorNode1);
		assertEquals(node1.getOutputEdges().get(0), connectorNode1.getDrawable());
		assertEquals(node2.getInputEdges().get(0), connectorNode1.getDrawable());
		ConnectorNode<Edge> connectorNode2 = (ConnectorNode<Edge>) graphDrawing1.getRoot().getChildNodes().get(4);
		assertNotNull(connectorNode2);
		assertEquals(node1.getOutputEdges().get(1), connectorNode2.getDrawable());
		assertEquals(node3.getInputEdges().get(0), connectorNode2.getDrawable());
		ConnectorNode<Edge> connectorNode3 = (ConnectorNode<Edge>) graphDrawing1.getRoot().getChildNodes().get(5);
		assertNotNull(connectorNode3);
		assertEquals(node3.getOutputEdges().get(0), connectorNode3.getDrawable());
		assertEquals(node2.getInputEdges().get(1), connectorNode3.getDrawable());

	}

	@Test
	public void testUpdateGraphicalObjectHierarchyForGraphDrawing1() {
		ShapeNode<GraphNode> graphNode1 = (ShapeNode<GraphNode>) graphDrawing1.getRoot().getChildNodes().get(0);
		ShapeNode<GraphNode> graphNode2 = (ShapeNode<GraphNode>) graphDrawing1.getRoot().getChildNodes().get(1);
		ShapeNode<GraphNode> graphNode3 = (ShapeNode<GraphNode>) graphDrawing1.getRoot().getChildNodes().get(2);
		ConnectorNode<Edge> connectorNode1 = (ConnectorNode<Edge>) graphDrawing1.getRoot().getChildNodes().get(3);
		ConnectorNode<Edge> connectorNode2 = (ConnectorNode<Edge>) graphDrawing1.getRoot().getChildNodes().get(4);
		ConnectorNode<Edge> connectorNode3 = (ConnectorNode<Edge>) graphDrawing1.getRoot().getChildNodes().get(5);
		graphDrawing1.updateGraphicalObjectsHierarchy();
		ShapeNode<GraphNode> graphNode1bis = (ShapeNode<GraphNode>) graphDrawing1.getRoot().getChildNodes().get(0);
		ShapeNode<GraphNode> graphNode2bis = (ShapeNode<GraphNode>) graphDrawing1.getRoot().getChildNodes().get(1);
		ShapeNode<GraphNode> graphNode3bis = (ShapeNode<GraphNode>) graphDrawing1.getRoot().getChildNodes().get(2);
		ConnectorNode<Edge> connectorNode1bis = (ConnectorNode<Edge>) graphDrawing1.getRoot().getChildNodes().get(3);
		ConnectorNode<Edge> connectorNode2bis = (ConnectorNode<Edge>) graphDrawing1.getRoot().getChildNodes().get(4);
		ConnectorNode<Edge> connectorNode3bis = (ConnectorNode<Edge>) graphDrawing1.getRoot().getChildNodes().get(5);
		assertEquals(graphNode1, graphNode1bis);
		assertEquals(graphNode2, graphNode2bis);
		assertEquals(graphNode3, graphNode3bis);
		assertEquals(connectorNode1, connectorNode1bis);
		assertEquals(connectorNode2, connectorNode2bis);
		assertEquals(connectorNode3, connectorNode3bis);
	}

	@Test
	public void initGraphDrawing2() {
		System.out.println("INIT graph drawing 2 ********************** ");
		graphDrawing2 = new GraphDrawing2(graph, FACTORY);
		assertNotNull(graphDrawing2.getRoot());
		System.out.println("Root = " + graphDrawing2.getRoot());
		System.out.println("GR = " + graphDrawing2.getRoot().getGraphicalRepresentation());
	}

	@Test
	public void initGraphicalObjectHierarchyForGraphDrawing2() {
		ShapeNode<GraphNode> graphNode1 = (ShapeNode<GraphNode>) graphDrawing2.getRoot().getChildNodes().get(0);
		assertNotNull(graphNode1);
		assertEquals(node1, graphNode1.getDrawable());
		ShapeNode<GraphNode> graphNode2 = (ShapeNode<GraphNode>) graphDrawing2.getRoot().getChildNodes().get(1);
		assertNotNull(graphNode2);
		assertEquals(node2, graphNode2.getDrawable());
		ShapeNode<GraphNode> graphNode3 = (ShapeNode<GraphNode>) graphDrawing2.getRoot().getChildNodes().get(2);
		assertNotNull(graphNode3);
		assertEquals(node3, graphNode3.getDrawable());
		ConnectorNode<Edge> connectorNode1 = (ConnectorNode<Edge>) graphDrawing2.getRoot().getChildNodes().get(3);
		assertNotNull(connectorNode1);
		assertEquals(node1.getOutputEdges().get(0), connectorNode1.getDrawable());
		assertEquals(node2.getInputEdges().get(0), connectorNode1.getDrawable());
		ConnectorNode<Edge> connectorNode2 = (ConnectorNode<Edge>) graphDrawing2.getRoot().getChildNodes().get(4);
		assertNotNull(connectorNode2);
		assertEquals(node1.getOutputEdges().get(1), connectorNode2.getDrawable());
		assertEquals(node3.getInputEdges().get(0), connectorNode2.getDrawable());
		ConnectorNode<Edge> connectorNode3 = (ConnectorNode<Edge>) graphDrawing2.getRoot().getChildNodes().get(5);
		assertNotNull(connectorNode3);
		assertEquals(node3.getOutputEdges().get(0), connectorNode3.getDrawable());
		assertEquals(node2.getInputEdges().get(1), connectorNode3.getDrawable());

	}

	@Test
	public void testUpdateGraphicalObjectHierarchyForGraphDrawing2() {
		ShapeNode<GraphNode> graphNode1 = (ShapeNode<GraphNode>) graphDrawing2.getRoot().getChildNodes().get(0);
		ShapeNode<GraphNode> graphNode2 = (ShapeNode<GraphNode>) graphDrawing2.getRoot().getChildNodes().get(1);
		ShapeNode<GraphNode> graphNode3 = (ShapeNode<GraphNode>) graphDrawing2.getRoot().getChildNodes().get(2);
		ConnectorNode<Edge> connectorNode1 = (ConnectorNode<Edge>) graphDrawing2.getRoot().getChildNodes().get(3);
		ConnectorNode<Edge> connectorNode2 = (ConnectorNode<Edge>) graphDrawing2.getRoot().getChildNodes().get(4);
		ConnectorNode<Edge> connectorNode3 = (ConnectorNode<Edge>) graphDrawing2.getRoot().getChildNodes().get(5);
		graphDrawing2.updateGraphicalObjectsHierarchy();
		ShapeNode<GraphNode> graphNode1bis = (ShapeNode<GraphNode>) graphDrawing2.getRoot().getChildNodes().get(0);
		ShapeNode<GraphNode> graphNode2bis = (ShapeNode<GraphNode>) graphDrawing2.getRoot().getChildNodes().get(1);
		ShapeNode<GraphNode> graphNode3bis = (ShapeNode<GraphNode>) graphDrawing2.getRoot().getChildNodes().get(2);
		ConnectorNode<Edge> connectorNode1bis = (ConnectorNode<Edge>) graphDrawing2.getRoot().getChildNodes().get(3);
		ConnectorNode<Edge> connectorNode2bis = (ConnectorNode<Edge>) graphDrawing2.getRoot().getChildNodes().get(4);
		ConnectorNode<Edge> connectorNode3bis = (ConnectorNode<Edge>) graphDrawing2.getRoot().getChildNodes().get(5);
		assertEquals(graphNode1, graphNode1bis);
		assertEquals(graphNode2, graphNode2bis);
		assertEquals(graphNode3, graphNode3bis);
		assertEquals(connectorNode1, connectorNode1bis);
		assertEquals(connectorNode2, connectorNode2bis);
		assertEquals(connectorNode3, connectorNode3bis);
	}

	@Test
	public void initGraphDrawing3() {
		System.out.println("INIT graph drawing 3 ********************** ");
		graphDrawing3 = new GraphDrawing3(graph, FACTORY);
		assertNotNull(graphDrawing3.getRoot());
		System.out.println("Root = " + graphDrawing3.getRoot());
		System.out.println("GR = " + graphDrawing3.getRoot().getGraphicalRepresentation());
	}

	@Test
	public void initGraphicalObjectHierarchyForGraphDrawing3() {
		System.out.println("all nodes = " + graphDrawing3.getRoot().getChildNodes());
		ShapeNode<GraphNode> graphNode1 = (ShapeNode<GraphNode>) graphDrawing3.getRoot().getChildNodes().get(0);
		assertNotNull(graphNode1);
		assertEquals(node1, graphNode1.getDrawable());
		ShapeNode<GraphNode> graphNode2 = (ShapeNode<GraphNode>) graphDrawing3.getRoot().getChildNodes().get(1);
		assertNotNull(graphNode2);
		assertEquals(node2, graphNode2.getDrawable());
		ShapeNode<GraphNode> graphNode3 = (ShapeNode<GraphNode>) graphDrawing3.getRoot().getChildNodes().get(2);
		assertNotNull(graphNode3);
		assertEquals(node3, graphNode3.getDrawable());
		ConnectorNode<Edge> connectorNode1 = (ConnectorNode<Edge>) graphDrawing3.getRoot().getChildNodes().get(3);
		assertNotNull(connectorNode1);
		assertEquals(node1.getOutputEdges().get(0), connectorNode1.getDrawable());
		assertEquals(node2.getInputEdges().get(0), connectorNode1.getDrawable());
		ConnectorNode<Edge> connectorNode2 = (ConnectorNode<Edge>) graphDrawing3.getRoot().getChildNodes().get(4);
		assertNotNull(connectorNode2);
		assertEquals(node1.getOutputEdges().get(1), connectorNode2.getDrawable());
		assertEquals(node3.getInputEdges().get(0), connectorNode2.getDrawable());
		ConnectorNode<Edge> connectorNode3 = (ConnectorNode<Edge>) graphDrawing3.getRoot().getChildNodes().get(5);
		assertNotNull(connectorNode3);
		assertEquals(node3.getOutputEdges().get(0), connectorNode3.getDrawable());
		assertEquals(node2.getInputEdges().get(1), connectorNode3.getDrawable());
	}

	@Test
	public void testUpdateGraphicalObjectHierarchyForGraphDrawing3() {
		ShapeNode<GraphNode> graphNode1 = (ShapeNode<GraphNode>) graphDrawing3.getRoot().getChildNodes().get(0);
		ShapeNode<GraphNode> graphNode2 = (ShapeNode<GraphNode>) graphDrawing3.getRoot().getChildNodes().get(1);
		ShapeNode<GraphNode> graphNode3 = (ShapeNode<GraphNode>) graphDrawing3.getRoot().getChildNodes().get(2);
		ConnectorNode<Edge> connectorNode1 = (ConnectorNode<Edge>) graphDrawing3.getRoot().getChildNodes().get(3);
		ConnectorNode<Edge> connectorNode2 = (ConnectorNode<Edge>) graphDrawing3.getRoot().getChildNodes().get(4);
		ConnectorNode<Edge> connectorNode3 = (ConnectorNode<Edge>) graphDrawing3.getRoot().getChildNodes().get(5);
		graphDrawing3.updateGraphicalObjectsHierarchy();
		ShapeNode<GraphNode> graphNode1bis = (ShapeNode<GraphNode>) graphDrawing3.getRoot().getChildNodes().get(0);
		ShapeNode<GraphNode> graphNode2bis = (ShapeNode<GraphNode>) graphDrawing3.getRoot().getChildNodes().get(1);
		ShapeNode<GraphNode> graphNode3bis = (ShapeNode<GraphNode>) graphDrawing3.getRoot().getChildNodes().get(2);
		ConnectorNode<Edge> connectorNode1bis = (ConnectorNode<Edge>) graphDrawing3.getRoot().getChildNodes().get(3);
		ConnectorNode<Edge> connectorNode2bis = (ConnectorNode<Edge>) graphDrawing3.getRoot().getChildNodes().get(4);
		ConnectorNode<Edge> connectorNode3bis = (ConnectorNode<Edge>) graphDrawing3.getRoot().getChildNodes().get(5);
		assertEquals(graphNode1, graphNode1bis);
		assertEquals(graphNode2, graphNode2bis);
		assertEquals(graphNode3, graphNode3bis);
		assertEquals(connectorNode1, connectorNode1bis);
		assertEquals(connectorNode2, connectorNode2bis);
		assertEquals(connectorNode3, connectorNode3bis);
	}

}
