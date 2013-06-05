package org.openflexo.fge.layout;

import java.util.Random;

import org.openflexo.fge.GraphicalRepresentation;

public class RandomPlacementLayout extends Layout{
	
	private LayoutStatus layoutStatus;
	
	public void setLayoutStatus(LayoutStatus layoutStatus) {
		this.layoutStatus = layoutStatus;
	}

	public RandomPlacementLayout(GraphicalRepresentation<?> graphicalRp) {
		super(graphicalRp);
	}

	@Override
	public LayoutStatus runLayout() {
		// Find the place for the others
		placeNodes();
		// Apply the layout
		applyLayout(getLayoutedGraph());
		
		setLayoutStatus(LayoutStatus.COMPLETE);
		
	    return layoutStatus;
	}
	
	private void placeNodes(){
		Random r = new Random();
		for(LayoutedNode nodeSource : getLayoutedGraph().getNodes()){
			//nodeSource.setDeplacementX(deplacementX)
		}
	}
	
	@Override
	public LayoutType getLayoutType() {
		return LayoutType.FORCE_DIRECTED_PLACEMENT;
	}

	@Override
	public LayoutStatus getStatus() {
		return layoutStatus;
	}
}
