package org.openflexo.fge.layout;

import java.util.Vector;


import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;

public abstract class Layout implements ILayout{
	
	private GraphicalRepresentation<?> graphicalRp; 
	
	private LayoutedGraph layoutedGraph;
	
	public LayoutedGraph getLayoutedGraph() {
		return layoutedGraph;
	}

	public void setLayoutedGraph(LayoutedGraph layoutedGraph) {
		this.layoutedGraph = layoutedGraph;
	}

	public Layout(GraphicalRepresentation<?> graphicalRp) {
		this.graphicalRp = graphicalRp;
		layoutedGraph = new LayoutedGraph(new Vector<LayoutedNode>(),new Vector<LayoutedEdge>());
		fillLayoutGraph(layoutedGraph);
	}
	
	/**
	 * Create a graph with layouted elements
	 * @param g
	 */
	private void fillLayoutGraph(LayoutedGraph g) {
		for(GraphicalRepresentation<?> subGraphicalRp : this.graphicalRp.getOrderedContainedGraphicalRepresentations()){
			if(subGraphicalRp instanceof ShapeGraphicalRepresentation){
				LayoutedNode n = new LayoutedNode(0,(ShapeGraphicalRepresentation<?>)subGraphicalRp);
				n.setDeplacementX(0);
				n.setDeplacementY(0);
				g.getNodes().add(n);
			}
			else{
				ConnectorGraphicalRepresentation<?> con = (ConnectorGraphicalRepresentation<?>)subGraphicalRp;
				g.getEgdes().add(new LayoutedEdge((ConnectorGraphicalRepresentation<?>)subGraphicalRp,g.getNode(con.getStartObject()),g.getNode(con.getEndObject())));
			}
		}
	}


}
