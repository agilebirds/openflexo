package org.openflexo.fge.layout;

import org.openflexo.fge.drawingeditor.MyShapeGraphicalRepresentation;

public class LayoutedNode {
	private double deplacementX;
	
	public double getDeplacementX() {
		return deplacementX;
	}

	public void setDeplacementX(double deplacementX) {
		this.deplacementX = deplacementX;
	}

	public double getDeplacementY() {
		return deplacementY;
	}

	public void setDeplacementY(double deplacementY) {
		this.deplacementY = deplacementY;
	}

	private double deplacementY;
	
	private MyShapeGraphicalRepresentation graphicalRepresentation;
	
	public LayoutedNode(int deplacement,
			MyShapeGraphicalRepresentation graphicalRepresentation) {
		super();
		this.graphicalRepresentation = graphicalRepresentation;
	}

	public MyShapeGraphicalRepresentation getGraphicalRepresentation() {
		return graphicalRepresentation;
	}

	public void setGraphicalRepresentation(
			MyShapeGraphicalRepresentation graphicalRepresentation) {
		this.graphicalRepresentation = graphicalRepresentation;
	}
	
}
