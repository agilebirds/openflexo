package org.openflexo.fge.layout;

import org.openflexo.fge.GraphicalRepresentation;

public class ForceDirectedPlacementLayout extends Layout{
	
	private int area = 500; //{W * L are the width and length of the frame};
	private int gravity = 5;
	private double deplacementX;
	private double deplacementY;
	float maxDisplace = (float) (Math.sqrt(area) / 30f);  
	float k = 50*((float) Math.sqrt(area) / (1f + getLayoutedGraph().getNodes().size()));	
	
	public ForceDirectedPlacementLayout(GraphicalRepresentation<?> graphicalRp) {
		super(graphicalRp);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Attractive forces
	 * @param d : distance between two vertices
	 * @param k : optimal distance between two vertices
	 * @return
	 */
	private static double attractiveForceFunction(double d,double k){
		return Math.pow(d, 2)/k;
	}
	
	/**
	 * Repulsive forces
	 * @param k
	 * @param d
	 * @return
	 */
	private static double repulsiveForceFunction(double k,double d){
		return (Math.pow(k, 2)/d);
	}
	
	@Override
	public void runLayout() {
			
		//for(int i=0;i<100;i++)
		//{
			// Apply repulsive forces
			repulsiveForcesComputation();
			// Apply attractive forces
			attractiveForcesComputation();
			// gravity
	       // gravityForcesComputation();
	        // Apply layout
	        applyLayout();
		//}
	}
	
	
	
	private void repulsiveForcesComputation(){
		// Repulsive forces
		for(LayoutedNode nodeSource : getLayoutedGraph().getNodes()){
			for(LayoutedNode nodeTarget:getLayoutedGraph().getNodes()){
				if(nodeSource!=nodeTarget){
					// Calculate distance between nodes
					double deltaX = (nodeSource.getGraphicalRepresentation().getX()) - (nodeTarget.getGraphicalRepresentation().getX());
					double deltaY = (nodeSource.getGraphicalRepresentation().getY()) - (nodeTarget.getGraphicalRepresentation().getY());
					double distance =  Math.sqrt(deltaX * deltaX + deltaY * deltaY);
					if(distance>0)
					{
						// calculate repulsive force
						deplacementX = (deltaX/distance)*repulsiveForceFunction(k,distance);
						deplacementY = (deltaY/distance)*repulsiveForceFunction(k,distance);
						// Apply repulsive forces
						nodeSource.setDeplacementX(nodeSource.getDeplacementX()+deplacementX);
						nodeSource.setDeplacementY(nodeSource.getDeplacementY()+deplacementY);
					}
				}
			}
		}
	}
	
	private void attractiveForcesComputation(){
		//attractive forces
		for(LayoutedEdge edge : getLayoutedGraph().getEgdes()){
			LayoutedNode nodeSource = edge.getSource();
			LayoutedNode nodeTarget = edge.getTarget();
			// Calculate distance between nodes
			double deltaX = (nodeSource.getGraphicalRepresentation().getX()) - (nodeTarget.getGraphicalRepresentation().getX());
			double deltaY = (nodeSource.getGraphicalRepresentation().getY()) - (nodeTarget.getGraphicalRepresentation().getY());
			double distance =  Math.sqrt(deltaX * deltaX + deltaY * deltaY);
			if(distance>0){
				// calculate attractive force
				deplacementX= (deltaX/distance)*attractiveForceFunction(distance,k);
				deplacementY= (deltaY/distance)*attractiveForceFunction(distance,k);
				// Apply attractive forces
				nodeSource.setDeplacementX(nodeSource.getDeplacementX()-deplacementX);
				nodeSource.setDeplacementY(nodeSource.getDeplacementY()-deplacementY);
				nodeTarget.setDeplacementX(nodeTarget.getDeplacementX()+deplacementX);
				nodeTarget.setDeplacementY(nodeTarget.getDeplacementY()+deplacementY);
			}
		}
	}
	
	private void gravityForcesComputation(){
		for (LayoutedNode n : getLayoutedGraph().getNodes()) {
            float d = (float) Math.sqrt(n.getGraphicalRepresentation().getX() * n.getGraphicalRepresentation().getX() + n.getGraphicalRepresentation().getY() * n.getGraphicalRepresentation().getY());
            float gf = 0.01f * k * (float) gravity * d;
            n.setDeplacementX(n.getDeplacementX()-gf*(n.getGraphicalRepresentation().getX())/d);
            n.setDeplacementY(n.getDeplacementY()-gf*(n.getGraphicalRepresentation().getY())/d);
        }
	}
	
	private void applyLayout(){
		 for (LayoutedNode n : getLayoutedGraph().getNodes()) {
	            double xDist = n.getDeplacementX();
	            double yDist = n.getDeplacementY();
	            float dist = (float) Math.sqrt(xDist * xDist + yDist * yDist);
	            if (dist > 0 ) {
	                float limitedDist = Math.min(maxDisplace, dist);
	                n.getGraphicalRepresentation().setX(n.getGraphicalRepresentation().getX() + xDist / dist * limitedDist);
	                n.getGraphicalRepresentation().setY(n.getGraphicalRepresentation().getY() + yDist / dist * limitedDist);
	            }
	      }
	}
}
