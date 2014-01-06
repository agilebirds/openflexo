package org.openflexo.technologyadapter.powerpoint.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hslf.model.Slide;
import org.openflexo.technologyadapter.powerpoint.PowerpointTechnologyAdapter;

/**
 * Represents an Excel sheet, implemented as a wrapper of a POI sheet
 * 
 * @author vincent, sylvain
 * 
 */
public class PowerpointSlide extends PowerpointObject {
	private Slide slide;
	private PowerpointSlideshow powerpointSlideshow;
	private List<PowerpointShape> powerpointShapes;
	private int slideNumber;

	public Slide getSlide() {
		return slide;
	}

	public PowerpointSlide(Slide slide, PowerpointSlideshow powerpointSlideshow, PowerpointTechnologyAdapter adapter) {
		super(adapter);
		this.slide = slide;
		this.powerpointSlideshow = powerpointSlideshow;
		powerpointShapes = new ArrayList<PowerpointShape>();
	}

	@Override
	public String getName() {
		if(slide.getTitle()==null){
			return Integer.toString(slide.getSlideNumber());
		}
		else{
			return slide.getTitle();
		}
	}

	public PowerpointSlideshow getSlideshow() {
		return powerpointSlideshow;
	}

	public List<PowerpointShape> getPowerpointShapes() {
		return powerpointShapes;
	}

	public void setPowerpointShapes(List<PowerpointShape> powerpointShapes) {
		this.powerpointShapes = powerpointShapes;
	}

	public void addToPowerpointShapes(PowerpointShape powerpointShapes) {
		this.powerpointShapes.add(powerpointShapes);
	}

	public void removeFromPowerpointShapes(PowerpointShape powerpointShapes) {
		this.powerpointShapes.remove(powerpointShapes);
	}

	@Override
	public String getUri() {
		String uri = getSlideshow().getUri()+"Slide="+getName();
		return uri;
	}

	public int getSlideNumber() {
		return slideNumber;
	}

	public void setSlideNumber(int slideNumber) {
		this.slideNumber = slideNumber;
	}

}
