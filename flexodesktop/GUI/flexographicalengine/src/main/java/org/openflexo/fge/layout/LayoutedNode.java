package org.openflexo.fge.layout;

import java.util.HashMap;

import org.openflexo.fge.ShapeGraphicalRepresentation;


public class LayoutedNode {
	
	private double nextX;
	private double nextY;
	private double deplacementX;
	private double deplacementY;
	
	private HashMap<Integer,Position> positions;
	
	private ShapeGraphicalRepresentation<?> graphicalRepresentation;
	
	public int getXForStep(int step){
		Position position = positions.get(step);
		return position.getX();
	}
	
	public int getYForStep(int step){
		Position position = positions.get(step);
		return position.getY();
	}
	
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
	
	public LayoutedNode(int deplacement,
			ShapeGraphicalRepresentation<?> graphicalRepresentation) {
		super();
		this.graphicalRepresentation = graphicalRepresentation;
		positions = new HashMap<Integer,Position>();
	}

	public ShapeGraphicalRepresentation<?> getGraphicalRepresentation() {
		return graphicalRepresentation;
	}
	
	public double getCurrentX(){
		return graphicalRepresentation.getX();
	}
	
	public double getCurrentY(){
		return graphicalRepresentation.getY();
	}

	public void setGraphicalRepresentation(
			ShapeGraphicalRepresentation<?> graphicalRepresentation) {
		this.graphicalRepresentation = graphicalRepresentation;
	}
	
	public void computeNewPosition(){
		nextX = this.graphicalRepresentation.getX() + deplacementX;
		nextY = this.graphicalRepresentation.getY() + deplacementY;
	}
	
	public void applyNewPosition(){
		this.graphicalRepresentation.setX(nextX);
		this.graphicalRepresentation.setY(nextY);
	}
	
}
