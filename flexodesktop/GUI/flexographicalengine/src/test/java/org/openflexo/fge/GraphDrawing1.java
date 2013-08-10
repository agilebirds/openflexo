package org.openflexo.fge;

import java.awt.Color;

import org.openflexo.fge.GRBinding.ConnectorGRBinding;
import org.openflexo.fge.GRBinding.DrawingGRBinding;
import org.openflexo.fge.GRBinding.ShapeGRBinding;
import org.openflexo.fge.GRProvider.ConnectorGRProvider;
import org.openflexo.fge.GRProvider.DrawingGRProvider;
import org.openflexo.fge.GRProvider.ShapeGRProvider;
import org.openflexo.fge.GraphicalRepresentation.Parameters;
import org.openflexo.fge.connectors.ConnectorSpecification.ConnectorType;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;

public class GraphDrawing1 extends DrawingImpl<Graph> {

	private DrawingGraphicalRepresentation graphRepresentation;
	private ShapeGraphicalRepresentation nodeRepresentation;
	private ConnectorGraphicalRepresentation edgeRepresentation;

	public GraphDrawing1(Graph graph, FGEModelFactory factory) {
		super(graph, factory);
	}

	@Override
	public void init() {
		graphRepresentation = getFactory().makeDrawingGraphicalRepresentation(this);
		graphRepresentation.setBackgroundColor(Color.RED);
		nodeRepresentation = getFactory().makeShapeGraphicalRepresentation(ShapeType.CIRCLE, this);
		// nodeRepresentation.setX(50);
		// nodeRepresentation.setY(50);
		nodeRepresentation.setWidth(20);
		nodeRepresentation.setHeight(20);
		edgeRepresentation = getFactory().makeConnectorGraphicalRepresentation(ConnectorType.CURVE, this);

		final DrawingGRBinding<Graph> graphBinding = bindDrawing(Graph.class, "graph", new DrawingGRProvider<Graph>() {
			@Override
			public DrawingGraphicalRepresentation provideGR(Graph drawable, FGEModelFactory factory) {
				return graphRepresentation;
			}
		});
		final ShapeGRBinding<GraphNode> nodeBinding = bindShape(GraphNode.class, "node", new ShapeGRProvider<GraphNode>() {
			@Override
			public ShapeGraphicalRepresentation provideGR(GraphNode drawable, FGEModelFactory factory) {
				return nodeRepresentation;
			}
		});
		final ConnectorGRBinding<Edge> edgeBinding = bindConnector(Edge.class, "edge", nodeBinding, nodeBinding, graphBinding,
				new ConnectorGRProvider<Edge>() {
					@Override
					public ConnectorGraphicalRepresentation provideGR(Edge drawable, FGEModelFactory factory) {
						return edgeRepresentation;
					}
				});

		graphBinding.addToWalkers(new GRStructureWalker<Graph>() {

			@Override
			public void walk(Graph graph) {
				for (GraphNode node : graph.getNodes()) {
					drawShape(nodeBinding, node);
				}
			}
		});

		nodeBinding.addToWalkers(new GRStructureWalker<GraphNode>() {
			@Override
			public void walk(GraphNode node) {
				System.out.println("Walking for edges ");
				for (Edge edge : node.getInputEdges()) {
					drawConnector(edgeBinding, edge, edge.getStartNode(), edge.getEndNode(), node.getGraph());
				}
				for (Edge edge : node.getOutputEdges()) {
					drawConnector(edgeBinding, edge, edge.getStartNode(), edge.getEndNode(), node.getGraph());
				}
			}
		});

		nodeBinding.addDynamicPropertyValue(Parameters.text, "drawable.name");

	}
}
