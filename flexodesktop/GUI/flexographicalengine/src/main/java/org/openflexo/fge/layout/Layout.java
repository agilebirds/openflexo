package org.openflexo.fge.layout;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;


import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.kvc.KVCObject;
import org.openflexo.xmlcode.XMLSerializable;

public abstract class Layout extends KVCObject implements XMLSerializable, Cloneable, ILayout {
	private static final Logger logger = Logger.getLogger(Layout.class.getPackage().getName());
	
	// Size of the area to perform the layout
	private int area = 300;
	
	private int stepNumber;
	
	private int nodesNumber;
	
	private Layout layout;

	public Layout getLayout() {
		return layout;
	}

	public void setLayout(Layout layout) {
		this.layout = layout;
	}

	// The graphical representation that maintain this layout
	private GraphicalRepresentation<?> graphicalRp; 
	
	private LayoutedGraph layoutedGraph;
	
	// Return the type of the layout
	public abstract LayoutType getLayoutType();

	// The possible type of layout
	public static enum LayoutType {
		NONE, FORCE_DIRECTED_PLACEMENT, HIERARCHICAL_PLACEMENT, RANDOM_PLACEMENT, CIRCULAR_PLACEMENT
	}

	@Override
	public Layout clone() {
		try {
			return (Layout) super.clone();
		} catch (CloneNotSupportedException e) {
			// cannot happen since we are clonable
			e.printStackTrace();
			return null;
		}
	}
	
	public LayoutedGraph getLayoutedGraph() {
		return layoutedGraph;
	}

	public void setLayoutedGraph(LayoutedGraph layoutedGraph) {
		this.layoutedGraph = layoutedGraph;
	}

	
	public Layout(){
		layoutedGraph = new LayoutedGraph(new ArrayList<LayoutedNode>(),new ArrayList<LayoutedEdge>());
	}
	
	/**
	 * A layout is applied on a set of graphical representations
	 * A layout can be set on all the sub graphical representations of a graphical representation
	 * @param graphicalRp
	 */
	public Layout(GraphicalRepresentation<?> graphicalRp) {
		this.graphicalRp = graphicalRp;
		layoutedGraph = new LayoutedGraph(new ArrayList<LayoutedNode>(),new ArrayList<LayoutedEdge>());
		if(this.graphicalRp!=null)setNodesNumber(graphicalRp.getContainedGraphicalRepresentations().size()) ;
	}

	
	/**
	 * Create a graph with layouted elements
	 * @param g
	 */
	public void fillLayoutGraph(LayoutedGraph g) {
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
	
	/**
	 * Create a graph with layouted elements
	 * @param g
	 */
	public void fillLayoutGraph(List<GraphicalRepresentation> grs) {
		for(GraphicalRepresentation<?> subGraphicalRp : grs){
			if(subGraphicalRp instanceof ShapeGraphicalRepresentation){
				LayoutedNode n = new LayoutedNode(0,(ShapeGraphicalRepresentation<?>)subGraphicalRp);
				n.setDeplacementX(0);
				n.setDeplacementY(0);
				layoutedGraph.getNodes().add(n);
			}
			else{
				ConnectorGraphicalRepresentation<?> con = (ConnectorGraphicalRepresentation<?>)subGraphicalRp;
				layoutedGraph.getEgdes().add(new LayoutedEdge((ConnectorGraphicalRepresentation<?>)subGraphicalRp,layoutedGraph.getNode(con.getStartObject()),layoutedGraph.getNode(con.getEndObject())));
			}
		}
	}


	/**
	 * Instanciate a layout for a given type and for a given graphical representation set
	 * @param type
	 * @param aGraphicalRepresentation
	 * @return
	 */
	public static Layout makeLayout(LayoutType type, ShapeGraphicalRepresentation<?> aGraphicalRepresentation) {
		if (type == LayoutType.FORCE_DIRECTED_PLACEMENT) {
			return new ForceDirectedPlacementLayout(aGraphicalRepresentation);
		} else if (type == LayoutType.HIERARCHICAL_PLACEMENT) {
			return new HierarchicalPlacementLayout(aGraphicalRepresentation);
		} else if (type == LayoutType.NONE) {
			return new NoneLayout(aGraphicalRepresentation);
		} else if (type == LayoutType.RANDOM_PLACEMENT) {
			return new RandomPlacementLayout(aGraphicalRepresentation);
		} else if (type == LayoutType.CIRCULAR_PLACEMENT) {
			return new CircularPlacementLayout(aGraphicalRepresentation);
		} 
		return null;
	}
	
	/**
	 * Apply the layout graphically.
	 * @param layoutedGraph
	 * @param steps
	 */
	public void applyLayout(LayoutedGraph layoutedGraph, int steps){
		for(int i=0;i<steps;i++){
			for (LayoutedNode n : layoutedGraph.getNodes()) {
				if(n.move){
					 n.getGraphicalRepresentation().setX(n.getXForStep(step));
				     n.getGraphicalRepresentation().setY(n.getYForStep(step));
				}    
			}
		}
	}

	public int getArea() {
		return area;
	}

	public void setArea(int area) {
		this.area = area;
	}

	public int getNodesNumber() {
		return nodesNumber;
	}

	public void setNodesNumber(int nodesNumber) {
		this.nodesNumber = nodesNumber;
	}
	
	public float distance(double n1, double n2){
		  float dist = (float) Math.sqrt(n1 * n1 + n2 * n2);
		  return dist;
	}
}
