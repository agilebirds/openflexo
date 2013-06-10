package org.openflexo.fge.layout;

import java.util.Random;

import org.openflexo.fge.GraphicalRepresentation;

public class HierarchicalPlacementLayout extends Layout{
	
	private int sequenceNumberValue;
	
	private int sequenceGapValue;
	
	private int numberOfLayers;
	
	private LayoutedNode rootNode;
	
	public int getNumberOfLayers() {
		return numberOfLayers;
	}

	public void setNumberOfLayers(int numberOfLayers) {
		this.numberOfLayers = numberOfLayers;
	}

	private LayoutStatus layoutStatus;
	
	public void setLayoutStatus(LayoutStatus layoutStatus) {
		this.layoutStatus = layoutStatus;
	}

	public HierarchicalPlacementLayout(GraphicalRepresentation<?> graphicalRp) {
		super(graphicalRp);
	}

	
	
	@Override
	public LayoutStatus runLayout() {
		// Find the root node
		setRootNode(findRootNode());
		// Find the place for the others
		placeNodes();
		// Apply the layout
		applyLayout(getLayoutedGraph());
		
		setLayoutStatus(LayoutStatus.COMPLETE);
		
	    return layoutStatus;
	}
	
	
	public LayoutedNode getRootNode() {
		return rootNode;
	}

	public void setRootNode(LayoutedNode rootNode) {
		this.rootNode = rootNode;
	}

	private void placeNodes(){
		// Place the Root Node
		rootNode.setDeplacementX(getArea()/2);
		rootNode.setDeplacementY(getArea()/sequenceNumberValue);
		
		/*for(LayoutedEdge edge : getLayoutedGraph().getEgdes()){
			if(edge.getSource().equals(rootNode)){
				LayoutedNode targetNode = edge.getSource().equals(rootNode);
			}
		}*/
	}
	
	private LayoutedNode findRootNode(){
		if(rootNode==null){
			Random r = new Random();
			int rootNode = r.nextInt(getNodesNumber());
			int nodeCounter = 0;
			for(LayoutedNode nodeSource : getLayoutedGraph().getNodes()){
				if(rootNode == nodeCounter)return nodeSource;
			}
		}
		else{
			return rootNode;
		}
		return null;
	}
	

	@Override
	public LayoutType getLayoutType() {
		return LayoutType.HIERARCHICAL_PLACEMENT;
	}

	@Override
	public LayoutStatus getStatus() {
		return layoutStatus;
	}

}
