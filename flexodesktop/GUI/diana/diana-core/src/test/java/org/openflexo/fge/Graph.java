package org.openflexo.fge;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Graph extends Observable {

	private List<GraphNode> nodes;
	private GraphNode rootNode;

	public Graph() {
		nodes = new ArrayList<GraphNode>();
	}

	public List<GraphNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<GraphNode> nodes) {
		this.nodes = nodes;
		setChanged();
		notifyObservers();
	}

	public void addToNodes(GraphNode aNode) {
		nodes.add(aNode);
		setChanged();
		notifyObservers();
	}

	public void removeFromNodes(GraphNode aNode) {
		nodes.remove(aNode);
		setChanged();
		notifyObservers();
	}

	public GraphNode getRootNode() {
		return rootNode;
	}

	public void setRootNode(GraphNode rootNode) {
		this.rootNode = rootNode;
		setChanged();
		notifyObservers();
	}

}
