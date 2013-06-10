package org.openflexo.fge.layout;

import java.util.HashMap;

import org.openflexo.fge.ShapeGraphicalRepresentation;


public class LayoutedNode {
	
	// The set of positions a layouted Node can have through the time.
	private HashMap<Integer,LayoutedNodePosition> positions;
	
	// The represented shape for this layouted node
	private ShapeGraphicalRepresentation<?> graphicalRepresentation;
	
	private double deplacementX;
	
	private double deplacementY;

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

	/**
	 * Get the X value at a particular step of the algorithm
	 * @param step
	 * @return
	 */
	public double getXForStep(int step){
		LayoutedNodePosition position = positions.get(step);
		return position.getX();
	}
	
	/**
	 * Get the Y value at a particular step of the algorithm
	 * @param step
	 * @return
	 */
	public double getYForStep(int step){
		LayoutedNodePosition position = positions.get(step);
		return position.getY();
	}
	
	public LayoutedNode(int numberOfSteps,
			ShapeGraphicalRepresentation<?> graphicalRepresentation) {
		super();
		this.graphicalRepresentation = graphicalRepresentation;
		positions = new HashMap<Integer,LayoutedNodePosition>();
		
		
		// Create the initial position
		LayoutedNodePosition position = new LayoutedNodePosition();
		position.setX(graphicalRepresentation.getX());
		position.setY(graphicalRepresentation.getY());
		positions.put(0, position);
		
		for(int step=1;step<numberOfSteps;step++){
			createPosition(step, graphicalRepresentation.getX(), graphicalRepresentation.getY());
		}
		
		setDeplacementX(0);
		setDeplacementY(0);
	}

	public ShapeGraphicalRepresentation<?> getGraphicalRepresentation() {
		return graphicalRepresentation;
	}

	/**
	 * 
	 * @param graphicalRepresentation
	 */
	public void setGraphicalRepresentation(
			ShapeGraphicalRepresentation<?> graphicalRepresentation) {
		this.graphicalRepresentation = graphicalRepresentation;
	}
	
	/**
	 * Update the postition for a particular step
	 * @param step
	 * @param x
	 * @param y
	 */
	public void updatePosition(int step, double x, double y){
		LayoutedNodePosition position = positions.get(step);
		position.setX(x);
		position.setY(y);
		positions.put(step, position);
	}
	
	/**
	 * Create a new position for this node and a particular step. 
	 */
	public void createPosition(int step, double x, double y){
		LayoutedNodePosition position = new LayoutedNodePosition();
		position.setX(x);
		position.setY(y);
		positions.put(step, position);
	}
	
}
