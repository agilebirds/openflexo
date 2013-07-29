package org.openflexo.fge;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class GraphNode extends Observable {

	private String name;
	private List<Edge> inputEdges;
	private List<Edge> outputEdges;

	public GraphNode(String name) {
		inputEdges = new ArrayList<Edge>();
		outputEdges = new ArrayList<Edge>();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Edge> getInputEdges() {
		return inputEdges;
	}

	public void setInputEdges(List<Edge> inputEdges) {
		this.inputEdges = inputEdges;
		setChanged();
		notifyObservers();
	}

	public void addToInputEdges(Edge aNode) {
		inputEdges.add(aNode);
		setChanged();
		notifyObservers();
	}

	public void removeFromInputEdges(Edge aNode) {
		inputEdges.remove(aNode);
		setChanged();
		notifyObservers();
	}

	public List<Edge> getOutputEdges() {
		return outputEdges;
	}

	public void setOutputEdges(List<Edge> outputEdges) {
		this.outputEdges = outputEdges;
		setChanged();
		notifyObservers();
	}

	public void addToOutputEdges(Edge aNode) {
		outputEdges.add(aNode);
		setChanged();
		notifyObservers();
	}

	public void removeFromOutputEdges(Edge aNode) {
		outputEdges.remove(aNode);
		setChanged();
		notifyObservers();
	}

	public void connectTo(GraphNode toNode) {
		Edge newEdge = new Edge(this, toNode);
		addToOutputEdges(newEdge);
		toNode.addToInputEdges(newEdge);
	}

}
