package org.openflexo.technologyadapter.powerpoint.model;

import org.apache.poi.hslf.model.Shape;
import org.openflexo.technologyadapter.powerpoint.PowerpointTechnologyAdapter;

/**
 * Represents an Excel row, implemented as a wrapper of a POI row
 * 
 * @author vincent, sylvain
 * 
 */
abstract public class PowerpointShape extends PowerpointObject {

	private Shape shape;
	private PowerpointSlide powerpointSlide;

	public Shape getShape() {
		return shape;
	}

	public PowerpointShape(Shape shape, PowerpointSlide powerpointSlide, PowerpointTechnologyAdapter adapter) {
		super(adapter);
		this.shape = shape;
		this.powerpointSlide = powerpointSlide;
	}

	public PowerpointSlide getPowerpointSlide() {
		return powerpointSlide;
	}

	@Override
	public String getName() {
		return shape.getShapeName();
	}

	public double getX(){
		return shape.getAnchor().getX();
	}
	
	public double getY(){
		return shape.getAnchor().getY();
	}
	
	public double getWidth(){
		return shape.getAnchor().getWidth();
	}
	
	public double getHeight(){
		return shape.getAnchor().getHeight();
	}
	
	@Override
	public String getUri() {
		return getPowerpointSlide().getUri()+getName();
	}
	
	

}
