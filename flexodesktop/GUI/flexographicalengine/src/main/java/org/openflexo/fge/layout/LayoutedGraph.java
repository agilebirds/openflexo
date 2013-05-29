package org.openflexo.fge.layout;

import java.util.Vector;

import org.openflexo.fge.ShapeGraphicalRepresentation;

public class LayoutedGraph {

	private Vector<LayoutedNode> nodes;
	
	private Vector<LayoutedEdge> egdes;

	public LayoutedGraph(Vector<LayoutedNode> nodes, Vector<LayoutedEdge> egdes) {
		super();
		this.nodes = nodes;
		this.egdes = egdes;
	}

	public Vector<LayoutedNode> getNodes() {
		return nodes;
	}

	public void setNodes(Vector<LayoutedNode> nodes) {
		this.nodes = nodes;
	}

	public Vector<LayoutedEdge> getEgdes() {
		return egdes;
	}

	public void setEgdes(Vector<LayoutedEdge> egdes) {
		this.egdes = egdes;
	}
	
	public boolean areLinked(LayoutedNode source, LayoutedNode target){
		for(LayoutedEdge edge : egdes){
			if((edge.getGraphicalRepresentation().getStartObject()).equals(source.getGraphicalRepresentation())
					&&(edge.getGraphicalRepresentation().getEndObject().equals(target.getGraphicalRepresentation()))){
				return true;
			}
			if((edge.getGraphicalRepresentation().getStartObject()).equals(target.getGraphicalRepresentation())
					&&(edge.getGraphicalRepresentation().getEndObject().equals(source.getGraphicalRepresentation()))){
				return true;
			}
		}
		return false;
	}
	
	public LayoutedNode getNode(ShapeGraphicalRepresentation<?> shape){
		for(LayoutedNode node : nodes){
			if(node.getGraphicalRepresentation().equals(shape))return node;
		}
		return null;
	}
	
}
