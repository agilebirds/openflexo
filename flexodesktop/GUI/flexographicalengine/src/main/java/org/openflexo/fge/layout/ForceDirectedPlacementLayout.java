package org.openflexo.fge.layout;

import org.openflexo.fge.GraphicalRepresentation;

public class ForceDirectedPlacementLayout extends Layout{

	private int gravity = 30;
	private int temperature = 100;
	
	private boolean computeAttractiveForces = true;
	private boolean computeRepulsiveForces = true;
	private boolean computeGravityForces=true;
	
	private double deplacementX;
	private double deplacementY;
	
	float maxDisplace = (float) (Math.sqrt(getArea()) / 0.1f);  
	float k = 10*((float) Math.sqrt(getArea()) / (1f + getLayoutedGraph().getNodes().size()));	
	
	public ForceDirectedPlacementLayout(GraphicalRepresentation<?> graphicalRp) {
		super(graphicalRp);
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

		for(int currentTemp = 0;currentTemp<getStepNumber();currentTemp++)
		{
			// Apply repulsive forces
			if(computeRepulsiveForces)repulsiveForcesComputation(currentTemp);
			// Apply attractive forces
			if(computeAttractiveForces)attractiveForcesComputation(currentTemp);
			// gravity
			if(computeGravityForces)gravityForcesComputation(currentTemp);
			// Apply layout
			maxDisplace(currentTemp);
		}
		
	    return LayoutStatus.COMPLETE;
	}
	
	private void repulsiveForcesComputation(int currentStep){
		// Repulsive forces
		double deltaX;
		double deltaY;
		float distance;
		
		for(LayoutedNode nodeSource : getLayoutedGraph().getNodes()){
			for(LayoutedNode nodeTarget:getLayoutedGraph().getNodes()){
				if(nodeSource!=nodeTarget){
					// Calculate distance between nodes
					deltaX = (nodeSource.getXForStep(currentStep)) - (nodeTarget.getXForStep(currentStep));
					deltaY = (nodeSource.getYForStep(currentStep)) - (nodeTarget.getYForStep(currentStep));
					distance = distance(deltaX,deltaY);

					if(distance<0)
					{
						// calculate repulsive force
						deplacementX = (deltaX/distance)*repulsiveForceFunction(k,distance);
						deplacementY = (deltaY/distance)*repulsiveForceFunction(k,distance);
					}
					else{
						deplacementX = 0;
						deplacementY = 0;	
					}
					nodeSource.setDeplacementX(nodeSource.getDeplacementX()+deplacementX);
					nodeSource.setDeplacementY(nodeSource.getDeplacementY()+deplacementY);
				}
			}
		}
	}
	
	private void attractiveForcesComputation(int currentStep){
		//attractive forces
		double deltaX;
		double deltaY;
		float distance;
		
		for(LayoutedEdge edge : getLayoutedGraph().getEgdes()){
			LayoutedNode nodeSource = edge.getSource();
			LayoutedNode nodeTarget = edge.getTarget();
			// Calculate distance between nodes
			deltaX = (nodeSource.getXForStep(currentStep)) - (nodeTarget.getXForStep(currentStep));
			deltaY = (nodeSource.getYForStep(currentStep)) - (nodeTarget.getYForStep(currentStep));
			distance = distance(deltaX,deltaY);
			
			if(distance>0){
				// calculate attractive force
				deplacementX= (deltaX/distance)*attractiveForceFunction(distance,k);
				deplacementY= (deltaY/distance)*attractiveForceFunction(distance,k);
			}
			else{
				deplacementX= 0;
				deplacementY= 0;
			}
			// Apply attractive forces
			nodeSource.setDeplacementX(nodeSource.getDeplacementX()-deplacementX);
			nodeSource.setDeplacementY(nodeSource.getDeplacementY()-deplacementY);
			nodeTarget.setDeplacementX(nodeTarget.getDeplacementX()+deplacementX);
			nodeTarget.setDeplacementY(nodeTarget.getDeplacementY()+deplacementY);
		}
	}
	
	private void gravityForcesComputation(int currentStep){
		float dist;
		float gf;
		for (LayoutedNode n : getLayoutedGraph().getNodes()) {
			dist = distance(n.getXForStep(currentStep),n.getYForStep(currentStep));
            gf = 0.01f * k * (float) gravity * dist;
            n.setDeplacementX(n.getDeplacementX()-gf*(n.getXForStep(currentStep))/dist);
            n.setDeplacementY(n.getDeplacementY()-gf*(n.getYForStep(currentStep))/dist);
        }
	}
	
	public void maxDisplace(int currentStep){
		float dist;
		float limitedDist;
		for (LayoutedNode n : getLayoutedGraph().getNodes()) {
	        dist = distance(n.getDeplacementX(),n.getDeplacementY());
	        if (dist > 0 ) {
	            limitedDist = Math.min(maxDisplace, dist);
	            n.setDeplacementX(n.getDeplacementX()/dist*limitedDist);
	            n.setDeplacementY(n.getDeplacementY()/dist*limitedDist);

	            n.updatePosition(currentStep, 
	            		n.getXForStep(currentStep)+n.getDeplacementX(), 
	            		n.getYForStep(currentStep)+n.getDeplacementY());
	            // create the next position;
	            if(currentStep<(getStepNumber()-1))n.updatePosition(currentStep+1, n.getXForStep(currentStep), n.getYForStep(currentStep));
	        }
		}
	}
	
	@Override
	public LayoutType getLayoutType() {
		return LayoutType.FORCE_DIRECTED_PLACEMENT;
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
	
	public int getTemperature() {
		return temperature;
	}

	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}

	
}
