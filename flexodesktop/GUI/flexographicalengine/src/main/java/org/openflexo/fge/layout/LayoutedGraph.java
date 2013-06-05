package org.openflexo.fge.layout;

import java.util.ArrayList;

import org.openflexo.fge.ShapeGraphicalRepresentation;

public class LayoutedGraph {

	private ArrayList<LayoutedNode> nodes;
	
	private ArrayList<LayoutedEdge> egdes;

	public LayoutedGraph(ArrayList<LayoutedNode> nodes, ArrayList<LayoutedEdge> egdes) {
		super();
		this.nodes = nodes;
		this.egdes = egdes;
	}

	public ArrayList<LayoutedNode> getNodes() {
		return nodes;
	}

	public void setNodes(ArrayList<LayoutedNode> nodes) {
		this.nodes = nodes;
	}

	public ArrayList<LayoutedEdge> getEgdes() {
		return egdes;
	}

	public void setEgdes(ArrayList<LayoutedEdge> egdes) {
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
