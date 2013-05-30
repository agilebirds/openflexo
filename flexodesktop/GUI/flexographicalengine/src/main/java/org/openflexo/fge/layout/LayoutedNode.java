package org.openflexo.fge.layout;

import org.openflexo.fge.ShapeGraphicalRepresentation;


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
	
	private ShapeGraphicalRepresentation<?> graphicalRepresentation;
	
	public LayoutedNode(int deplacement,
			ShapeGraphicalRepresentation<?> graphicalRepresentation) {
		super();
		this.graphicalRepresentation = graphicalRepresentation;
	}

	public ShapeGraphicalRepresentation<?> getGraphicalRepresentation() {
		return graphicalRepresentation;
	}

	public void setGraphicalRepresentation(
			ShapeGraphicalRepresentation<?> graphicalRepresentation) {
		this.graphicalRepresentation = graphicalRepresentation;
	}
	
}
