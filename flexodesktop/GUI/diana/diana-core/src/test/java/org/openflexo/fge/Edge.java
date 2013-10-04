package org.openflexo.fge;

import java.util.Observable;

public class Edge extends Observable {

	private GraphNode startNode;
	private GraphNode endNode;

	public Edge(GraphNode startNode, GraphNode endNode) {
		super();
		this.startNode = startNode;
		this.endNode = endNode;
	}

	public GraphNode getStartNode() {
		return startNode;
	}

	public GraphNode getEndNode() {
		return endNode;
	}
}
