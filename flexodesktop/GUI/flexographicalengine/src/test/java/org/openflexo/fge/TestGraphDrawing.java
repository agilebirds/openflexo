package org.openflexo.fge;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.model.exceptions.ModelDefinitionException;

public class TestGraphDrawing {

	private static FGEModelFactory FACTORY;
	private static Graph graph;
	private static GraphDrawing graphDrawing;

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
		GraphNode node1 = new GraphNode("node1");
		GraphNode node2 = new GraphNode("node2");
		GraphNode node3 = new GraphNode("node3");
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
	public void initGraphDrawing() {
		graphDrawing = new GraphDrawing(graph, FACTORY);
		assertNotNull(graphDrawing.getRoot());
		System.out.println("Root = " + graphDrawing.getRoot());
		System.out.println("GR = " + graphDrawing.getRoot().getGraphicalRepresentation());
		DrawingController<Graph> controller = new DrawingController<Graph>(graphDrawing, FACTORY);
	}
}
