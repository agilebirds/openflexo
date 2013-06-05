package org.openflexo.fge.layout;

import org.openflexo.fge.GraphicalRepresentation;

public class ForceDirectedPlacementLayout extends Layout{

	private int gravity = 2;
	private int temperature = 30;
	
	private LayoutStatus layoutStatus;
	
	public void setLayoutStatus(LayoutStatus layoutStatus) {
		this.layoutStatus = layoutStatus;
	}

	public int getGravity() {
		return gravity;
	}

	public void setGravity(int gravity) {
		this.gravity = gravity;
	}

	public float getMaxDisplace() {
		return maxDisplace;
	}

	public void setMaxDisplace(float maxDisplace) {
		this.maxDisplace = maxDisplace;
	}

	public float getK() {
		return k;
	}

	public void setK(float k) {
		this.k = k;
	}

	private double deplacementX;
	private double deplacementY;
	float maxDisplace = (float) (Math.sqrt(getArea()) / 5f);  
	float k = 30*((float) Math.sqrt(getArea()) / (1f + getLayoutedGraph().getNodes().size()));	
	
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
	public LayoutStatus runLayout() {
		
		// Apply repulsive forces
		repulsiveForcesComputation();
		// Apply attractive forces
		attractiveForcesComputation();
		// gravity
		gravityForcesComputation();
		// Apply layout
		   
		applyLayout(getLayoutedGraph());
		decreaseTemperature();
		if(temperature<=0)setLayoutStatus(LayoutStatus.COMPLETE);
		else{setLayoutStatus(LayoutStatus.PROGRESS);}
	    return layoutStatus;
	}
	
	
	public int getTemperature() {
		return temperature;
	}

	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}

	private void decreaseTemperature(){
		temperature = temperature - 1;
	}
	
	private void repulsiveForcesComputation(){
		// Repulsive forces
		for(LayoutedNode nodeSource : getLayoutedGraph().getNodes()){
			for(LayoutedNode nodeTarget:getLayoutedGraph().getNodes()){
				if(nodeSource!=nodeTarget){
					// Calculate distance between nodes
					double deltaX = (nodeSource.getCurrentX()) - (nodeTarget.getCurrentX());
					double deltaY = (nodeSource.getCurrentY()) - (nodeTarget.getCurrentY());
					float distance = distance(deltaX,deltaY);
					if(distance>20)
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
			double deltaX = (nodeSource.getCurrentX()) - (nodeTarget.getCurrentX());
			double deltaY = (nodeSource.getCurrentY()) - (nodeTarget.getCurrentY());
			float distance = distance(deltaX,deltaY);
			if(distance>20){
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
			float dist = distance(n.getCurrentX(),n.getCurrentY());
            float gf = 0.01f * k * (float) gravity * dist;
            n.setDeplacementX(n.getDeplacementX()-gf*(n.getCurrentX())/dist);
            n.setDeplacementY(n.getDeplacementY()-gf*(n.getCurrentY())/dist);
        }
	}
	
	@Override
	public void applyLayout(LayoutedGraph layoutedGraph){
		for (LayoutedNode n : layoutedGraph.getNodes()) {
	        float dist = distance(n.getDeplacementX(),n.getDeplacementY());
	        if (dist > 20 ) {
	            float limitedDist = Math.min(maxDisplace, dist);
	            n.setDeplacementX(n.getDeplacementX()/dist*limitedDist);
	            n.setDeplacementY(n.getDeplacementY()/dist*limitedDist);
	            n.computeNewPosition();
	            n.applyNewPosition();
	        }
		}
	}

	private float distance(double n1, double n2){
		  float dist = (float) Math.sqrt(n1 * n1 + n2 * n2);
		  return dist;
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
